/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.api;

import org.primefaces.component.column.ColumnBase;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EditableValueHolderState;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.ValueHolder;
import jakarta.faces.component.search.SearchExpressionHint;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;

public interface UITable<T extends UITableState> extends ColumnAware, MultiViewStateAware<T> {

    /**
     * ID of the global filter component
     */
    String GLOBAL_FILTER_COMPONENT_ID = "globalFilter";

    String getVar();

    String getClientId(FacesContext context);


    default Map<String, FilterMeta> initFilterBy(FacesContext context) {
        Map<String, FilterMeta> filterBy = new LinkedHashMap<>();

        // build columns filterBy
        forEachColumn(c -> {
            FilterMeta meta = FilterMeta.of(context, getVar(), c, isFilterNormalize());
            if (meta != null) {
                filterBy.put(meta.getColumnKey(), meta);
            }

            return true;
        });

        // merge internal filterBy with user filterBy
        Object userFilterBy = getFilterBy();
        if (userFilterBy != null) {
            updateFilterByWithUserFilterBy(context, filterBy, userFilterBy);
        }

        // build global filterBy
        updateFilterByWithGlobalFilter(context, filterBy);

        setFilterByAsMap(filterBy);

        return filterBy;
    }

    default void updateFilterByWithMVS(FacesContext context, Map<String, FilterMeta> tsFilterBy) {
        for (Map.Entry<String, FilterMeta> entry : tsFilterBy.entrySet()) {
            FilterMeta intlFilterBy = getFilterByAsMap().get(entry.getKey());
            if (intlFilterBy != null) {
                FilterMeta tsFilterMeta = entry.getValue();
                intlFilterBy.setFilterValue(tsFilterMeta.getFilterValue());
            }
            // #7325 restore global filter value
            if (FilterMeta.GLOBAL_FILTER_KEY.equals(entry.getKey())) {
                UIComponent globalFilterComponent = SearchExpressionUtils.contextlessResolveComponent(
                        context, (UIComponent) this, GLOBAL_FILTER_COMPONENT_ID);
                if (globalFilterComponent instanceof ValueHolder) {
                    ((ValueHolder) globalFilterComponent).setValue(entry.getValue().getFilterValue());
                }
            }
        }
    }

    default void updateFilterByWithUserFilterBy(FacesContext context, Map<String, FilterMeta> intlFilterBy, Object usrFilterBy) {

        Collection<FilterMeta> filterByTmp;
        if (usrFilterBy instanceof FilterMeta) {
            filterByTmp = Collections.singletonList((FilterMeta) usrFilterBy);
        }
        else if (!(usrFilterBy instanceof Collection)) {
            throw new FacesException("filterBy expects a single or a collection of FilterMeta");
        }
        else {
            filterByTmp = (Collection<FilterMeta>) usrFilterBy;
        }

        for (FilterMeta userFM : filterByTmp) {
            FilterMeta intlFM = intlFilterBy.values().stream()
                    .filter(o -> Objects.equals(o.getField(), userFM.getField()) || Objects.equals(o.getColumnKey(), userFM.getColumnKey()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userFM.getField()
                            + "' or columnKey '" + userFM.getColumnKey() + "' has been found"));

            ValueExpression filterByVE = userFM.getFilterBy();
            if (filterByVE == null) {
                filterByVE = UIColumn.createValueExpressionFromField(context, getVar(), userFM.getField());

                intlFM.setFilterBy(filterByVE);
                intlFM.setFilterByGenerated(true);
            }
            else {
                intlFM.setFilterBy(filterByVE);
            }

            intlFM.setFilterValue(userFM.getFilterValue());
            intlFM.setConstraint(userFM.getConstraint());
            intlFM.setMatchMode(userFM.getMatchMode());
        }
    }

    default void updateFilterByWithGlobalFilter(FacesContext context, Map<String, FilterMeta> filterBy) {
        // #globalFilter sets the default value, which will be assigned to the "globalFilter" input
        String globalFilterDefaultValue = getGlobalFilter();
        // if #globalFilter is set, the "globalFilter" is mandatory
        Set<SearchExpressionHint> hints = LangUtils.isBlank(globalFilterDefaultValue)
                ? SearchExpressionUtils.hintsIgnoreNoResult()
                : null;
        UIComponent globalFilterComponent = SearchExpressionUtils.contextlessResolveComponent(
                context, (UIComponent) this, GLOBAL_FILTER_COMPONENT_ID, hints);
        if (globalFilterComponent != null) {
            if (globalFilterComponent instanceof ValueHolder) {
                ((ValueHolder) globalFilterComponent).setValue(globalFilterDefaultValue);
            }
            FilterMeta globalFilterBy = FilterMeta.of(globalFilterDefaultValue, getGlobalFilterFunction(), isFilterNormalize());
            filterBy.put(globalFilterBy.getColumnKey(), globalFilterBy);
        }
    }

    default boolean isColumnFilterable(FacesContext context, UIColumn column) {
        Map<String, FilterMeta> filterBy = getFilterByAsMap();
        if (filterBy.containsKey(column.getColumnKey())) {
            return true;
        }

        // lazy init - happens in cases where the column is initially not rendered
        FilterMeta f = FilterMeta.of(context, getVar(), column, isFilterNormalize());
        if (f != null) {
            filterBy.put(f.getColumnKey(), f);
        }
        setFilterByAsMap(filterBy);

        return f != null;
    }

    default void updateFilterByValuesWithFilterRequest(FacesContext context, Map<String, FilterMeta> filterBy) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        char separator = UINamingContainer.getSeparatorChar(context);

        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        if (globalFilter != null) {
            String componentIdPrefix = ((UIComponent) this).getClientId(context) + separator + FilterMeta.GLOBAL_FILTER_KEY;
            String filterValue = params.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getKey().startsWith(componentIdPrefix))
                    .map(e -> e.getValue())
                    .findFirst().orElse(null);
            globalFilter.setFilterValue(filterValue);
        }

        forEachColumn(column -> {
            FilterMeta filterMeta = filterBy.get(column.getColumnKey());
            if (filterMeta == null || filterMeta.isGlobalFilter()) {
                return true;
            }

            if (column instanceof DynamicColumn) {
                ((DynamicColumn) column).applyModel();
            }

            EditableValueHolderState editableValueHolderState = column.getFilterValueHolder(context);

            Object filterValue;
            if (editableValueHolderState != null) {
                filterValue = editableValueHolderState.getValue();
            }
            else {
                String valueHolderClientId = column instanceof DynamicColumn
                        ? column.getContainerClientId(context) + separator + "filter"
                        : column.getClientId(context) + separator + "filter";
                filterValue = params.get(valueHolderClientId);

                try {
                    // if no custom filter provided and conversion necessary, use UIColumn#converter instead
                    filterValue = ComponentUtils.getConvertedValue(context, column.asUIComponent(), column.getConverter(), filterValue);
                }
                catch (ConverterException ex) {
                    filterValue = null;
                }
            }

            if (filterValue != null) {
                // this is not absolutely necessary, but in case the result is null, it prevents to execution of the next statement
                filterValue = FilterMeta.resetToNullIfEmpty(filterValue);
            }

            if (filterValue != null && filterValue.getClass().isArray()) {
                ValueExpression columnFilterValueVE = column.getValueExpression(ColumnBase.PropertyKeys.filterValue.toString());
                if (columnFilterValueVE != null && List.class.isAssignableFrom(columnFilterValueVE.getType(context.getELContext()))) {
                    filterValue = Arrays.asList((Object[]) filterValue);
                }
            }

            filterMeta.setFilterValue(filterValue);

            return true;
        });
    }

    default Object getFilterValue(UIColumn column) {
        return getFilterByAsMap().get(column.getColumnKey()).getFilterValue();
    }

    Object getFilterBy();

    void setFilterBy(Object filterBy);

    boolean isFilterByAsMapDefined();

    Map<String, FilterMeta> getFilterByAsMap();

    void setFilterByAsMap(Map<String, FilterMeta> sortBy);

    /**
     * Returns actives filter meta.
     * @return map with {@link FilterMeta#getField()} as key and {@link FilterMeta} as value
     */
    default Map<String, FilterMeta> getActiveFilterMeta() {
        return getFilterByAsMap().values().stream()
                .filter(FilterMeta::isActive)
                .collect(Collectors.toMap(FilterMeta::getField, Function.identity()));
    }

    String getGlobalFilter();

    void setGlobalFilter(String globalFilter);

    MethodExpression getGlobalFilterFunction();

    void setGlobalFilterFunction(MethodExpression globalFilterFunction);

    boolean isGlobalFilterOnly();

    void setGlobalFilterOnly(boolean globalFilterOnly);

    boolean isFilterNormalize();

    default Map<String, SortMeta> initSortBy(FacesContext context) {
        Map<String, SortMeta> sortBy = new LinkedHashMap<>();
        AtomicBoolean sorted = new AtomicBoolean();

        HeaderRow headerRow = getHeaderRow();
        if (headerRow != null) {
            SortMeta meta = SortMeta.of(context, getVar(), headerRow);
            sortBy.put(meta.getColumnKey(), meta);
            sorted.set(true);
        }

        forEachColumn(c -> {
            SortMeta meta = SortMeta.of(context, getVar(), c);
            if (meta != null) {
                sorted.set(sorted.get() || meta.isActive());
                sortBy.put(meta.getColumnKey(), meta);
            }

            return true;
        });

        // merge internal sortBy with user sortBy
        Object userSortBy = getSortBy();
        if (userSortBy != null) {
            updateSortByWithUserSortBy(context, sortBy, userSortBy, sorted);
        }

        setSortByAsMap(sortBy);

        return sortBy;
    }

    default void updateSortByWithMVS(Map<String, SortMeta> tsSortBy) {
        for (Map.Entry<String, SortMeta> entry : tsSortBy.entrySet()) {
            SortMeta intlSortBy = getSortByAsMap().get(entry.getKey());
            if (intlSortBy != null) {
                SortMeta tsSortMeta = entry.getValue();
                intlSortBy.setPriority(tsSortMeta.getPriority());
                intlSortBy.setOrder(tsSortMeta.getOrder());
            }
        }
    }

    default void updateSortByWithUserSortBy(FacesContext context, Map<String, SortMeta> intlSortBy, Object usrSortBy, AtomicBoolean sorted) {
        Collection<SortMeta> sortBy;
        if (usrSortBy instanceof SortMeta) {
            sortBy = Collections.singletonList((SortMeta) usrSortBy);
        }
        else if (!(usrSortBy instanceof Collection)) {
            throw new FacesException("sortBy expects a single or a collection of SortMeta");
        }
        else {
            sortBy = (Collection<SortMeta>) usrSortBy;
        }

        for (SortMeta userSM : sortBy) {
            SortMeta intlSM = intlSortBy.values().stream()
                    .filter(o -> Objects.equals(o.getField(), userSM.getField()) || Objects.equals(o.getColumnKey(), userSM.getColumnKey()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userSM.getField()
                            + "' or columnKey '" + userSM.getColumnKey() + "' has been found"));

            ValueExpression sortByVE = userSM.getSortBy();
            if (sortByVE == null) {
                sortByVE = UIColumn.createValueExpressionFromField(context, getVar(), userSM.getField());
            }

            intlSM.setPriority(userSM.getPriority());
            intlSM.setOrder(userSM.getOrder());
            intlSM.setSortBy(sortByVE);
            intlSM.setFunction(userSM.getFunction());
            sorted.set(sorted.get() || userSM.isActive());
        }
    }

    default SortMeta getHighestPriorityActiveSortMeta() {
        return getSortByAsMap().values().stream()
                .filter(SortMeta::isActive)
                .min(Comparator.comparingInt(SortMeta::getPriority))
                .orElse(null);
    }

    /**
     * Returns actives sort meta. See {@link SortMeta#compareTo(SortMeta)}
     * @return map with {@link SortMeta#getField()} as key and {@link SortMeta} as value
     */
    default Map<String, SortMeta> getActiveSortMeta() {
        return getSortByAsMap().values().stream()
                .filter(SortMeta::isActive)
                .sorted()
                .collect(Collectors.toMap(SortMeta::getField, Function.identity(), (o1, o2) -> o1, LinkedHashMap::new));
    }

    default boolean isSortingCurrentlyActive() {
        return getSortByAsMap().values().stream().anyMatch(SortMeta::isActive);
    }

    default boolean isColumnSortable(FacesContext context, UIColumn column) {
        Map<String, SortMeta> sortBy = getSortByAsMap();
        if (sortBy.containsKey(column.getColumnKey())) {
            return true;
        }

        // lazy init - happens in cases where the column is initially not rendered
        SortMeta s = SortMeta.of(context, getVar(), column);
        if (s != null) {
            sortBy.put(s.getColumnKey(), s);
        }
        setSortByAsMap(sortBy);

        return s != null;
    }

    default String getSortMetaAsString() {
        return getActiveSortMeta().values().stream()
                .map(SortMeta::getColumnKey)
                .collect(Collectors.joining("','", "['", "']"));
    }

    default boolean isSortingEnabled() {
        return !getSortByAsMap().isEmpty();
    }

    default HeaderRow getHeaderRow() {
        return null;
    }

    Map<String, SortMeta> getSortByAsMap();

    void setSortByAsMap(Map<String, SortMeta> sortBy);

    default boolean isFilteringEnabled() {
        return isFilterByAsMapDefined() && !getFilterByAsMap().isEmpty();
    }

    Object getSortBy();

    void setSortBy(Object sortBy);

    default void decodeColumnTogglerState(FacesContext context) {
        String columnTogglerStateParam = context.getExternalContext().getRequestParameterMap()
                .get(getClientId(context) + "_columnTogglerState");
        if (columnTogglerStateParam == null) {
            return;
        }

        Map<String, ColumnMeta> columMeta = getColumnMeta();
        columMeta.values().forEach(s -> s.setVisible(null));

        if (LangUtils.isNotBlank(columnTogglerStateParam)) {
            String[] columnStates = columnTogglerStateParam.split(",");
            for (String columnState : columnStates) {
                if (LangUtils.isBlank(columnState)) {
                    continue;
                }
                int seperatorIndex = columnState.lastIndexOf('_');
                String columnKey = columnState.substring(0, seperatorIndex);
                boolean visible = Boolean.parseBoolean(columnState.substring(seperatorIndex + 1));

                ColumnMeta meta = columMeta.computeIfAbsent(columnKey, ColumnMeta::new);
                meta.setVisible(visible);
            }
        }

        if (isMultiViewState()) {
            UITableState state = getMultiViewState(true);
            state.setColumnMeta(columMeta);
        }
    }

    default void decodeColumnResizeState(FacesContext context) {
        String columnResizeStateParam = context.getExternalContext().getRequestParameterMap()
                .get(getClientId(context) + "_resizableColumnState");
        if (columnResizeStateParam == null) {
            return;
        }

        Map<String, ColumnMeta> columMeta = getColumnMeta();
        columMeta.values().forEach(s -> s.setWidth(null));

        String tableWidth = null;

        if (LangUtils.isNotBlank(columnResizeStateParam)) {
            String[] columnStates = columnResizeStateParam.split(",");
            for (String columnState : columnStates) {
                if (LangUtils.isBlank(columnState)) {
                    continue;
                }

                if ((getClientId(context) + "_tableWidthState").equals(columnState)) {
                    tableWidth = columnState;
                    setWidth(tableWidth);
                }
                else {
                    int seperatorIndex = columnState.lastIndexOf('_');
                    String columnKey = columnState.substring(0, seperatorIndex);
                    String width = columnState.substring(seperatorIndex + 1);

                    ColumnMeta meta = columMeta.computeIfAbsent(columnKey, ColumnMeta::new);
                    meta.setWidth(width);
                }
            }
        }

        if (isMultiViewState()) {
            UITableState state = getMultiViewState(true);
            state.setWidth(tableWidth);
            state.setColumnMeta(columMeta);
        }
    }

    String getWidth();

    void setWidth(String width);

    default void decodeColumnDisplayOrderState(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String columnOrderParam = params.get(getClientId(context) + "_columnOrder");
        if (LangUtils.isBlank(columnOrderParam)) {
            return;
        }

        Map<String, ColumnMeta> columMeta = getColumnMeta();
        columMeta.values().forEach(s -> s.setDisplayPriority(0));

        String[] columnKeys = columnOrderParam.split(",");
        for (int i = 0; i < columnKeys.length; i++) {
            String columnKey = columnKeys[i];
            if (LangUtils.isBlank(columnKey)) {
                continue;
            }

            ColumnMeta meta = columMeta.computeIfAbsent(columnKey, ColumnMeta::new);
            meta.setDisplayPriority(i);
        }

        if (isMultiViewState()) {
            UITableState ts = getMultiViewState(true);
            ts.setColumnMeta(columMeta);
        }
    }

    default String getColumnsWidthForClientSide() {
        return getColumnMeta().entrySet()
                .stream()
                .filter(e -> LangUtils.isNotBlank(e.getValue().getWidth()))
                .map(e -> e.getKey() + '_' + e.getValue().getWidth())
                .collect(Collectors.joining(","));
    }

    default Object getFieldValue(FacesContext context, UIColumn column) {
        return UIColumn.createValueExpressionFromField(context, getVar(), column.getField()).getValue(context.getELContext());
    }

    default String getConvertedFieldValue(FacesContext context, UIColumn column) {
        Object value = UIColumn.createValueExpressionFromField(context, getVar(), column.getField()).getValue(context.getELContext());
        return ComponentUtils.getConvertedAsString(context, column.asUIComponent(), column.getConverter(), value);
    }

    default boolean isFilteringCurrentlyActive() {
        return getFilterByAsMap().values().stream().anyMatch(FilterMeta::isActive);
    }

    /**
     * Recalculates filteredValue after adding, updating or removing object to/from a filtered UITable.
     */
    void filterAndSort();

    /**
     * Resets all column related state after adding/removing/moving columns.
     */
    default void resetColumns() {
        setColumns(null);
        setSortByAsMap(null);
        setFilterByAsMap(null);
        setColumnMeta(null);
    }

    default boolean hasFooterColumn() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered() && (child instanceof UIColumn)) {
                UIColumn column = (UIColumn) child;

                if (column.getFooterText() != null || FacetUtils.shouldRenderFacet(column.getFacet("footer"))) {
                    return true;
                }
            }
        }

        return false;
    }

    default Locale resolveDataLocale(FacesContext context) {
        return LocaleUtils.resolveLocale(context, getDataLocale(), getClientId(context));
    }

    int getChildCount();

    List<UIComponent> getChildren();

    Object getDataLocale();
}
