/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.text.Collator;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.component.search.SearchExpressionHint;
import javax.faces.context.FacesContext;

import org.primefaces.component.column.ColumnBase;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

public interface UITable<T extends UITableState> extends ColumnAware, MultiViewStateAware<T> {

    /**
     * ID of the global filter component
     */
    String GLOBAL_FILTER_COMPONENT_ID = "globalFilter";

    String getVar();

    String getClientId(FacesContext context);


    default Map<String, FilterMeta> initFilterBy(FacesContext context) {
        Map<String, FilterMeta> filterBy = new LinkedHashMap<>();
        AtomicBoolean filtered = new AtomicBoolean();

        // build columns filterBy
        forEachColumn(c -> {
            FilterMeta meta = FilterMeta.of(context, getVar(), c);
            if (meta != null) {
                filterBy.put(meta.getColumnKey(), meta);
                filtered.set(filtered.get() || meta.isActive());
            }

            return true;
        });

        // merge internal filterBy with user filterBy
        Object userFilterBy = getFilterBy();
        if (userFilterBy != null) {
            updateFilterByWithUserFilterBy(context, filterBy, userFilterBy, filtered);
        }

        // build global filterBy
        updateFilterByWithGlobalFilter(context, filterBy, filtered);

        // finally set if default filtering is enabled
        setDefaultFilter(filtered.get());

        setFilterByAsMap(filterBy);

        return filterBy;
    }

    default void updateFilterByWithMVS(FacesContext context, Map<String, FilterMeta> tsFilterBy) {
        boolean defaultFilter = isDefaultFilter();
        for (Map.Entry<String, FilterMeta> entry : tsFilterBy.entrySet()) {
            FilterMeta intlFilterBy = getFilterByAsMap().get(entry.getKey());
            if (intlFilterBy != null) {
                FilterMeta tsFilterMeta = entry.getValue();
                intlFilterBy.setFilterValue(tsFilterMeta.getFilterValue());
                defaultFilter |= intlFilterBy.isActive();
            }
            // #7325 restore global filter value
            if (FilterMeta.GLOBAL_FILTER_KEY.equals(entry.getKey())) {
                UIComponent globalFilterComponent = SearchExpressionUtils.contextlessResolveComponent(
                        context, (UIComponent) this, GLOBAL_FILTER_COMPONENT_ID);
                if (globalFilterComponent != null && globalFilterComponent instanceof ValueHolder) {
                    ((ValueHolder) globalFilterComponent).setValue(entry.getValue().getFilterValue());
                }
            }
        }

        setDefaultFilter(defaultFilter);
    }

    default void updateFilterByWithUserFilterBy(FacesContext context, Map<String, FilterMeta> intlFilterBy, Object usrFilterBy,
            AtomicBoolean filtered) {

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
            }

            intlFM.setFilterValue(userFM.getFilterValue());
            intlFM.setFilterBy(filterByVE);
            intlFM.setConstraint(userFM.getConstraint());
            intlFM.setMatchMode(userFM.getMatchMode());
            filtered.set(filtered.get() || userFM.isActive());
        }
    }

    default void updateFilterByWithGlobalFilter(FacesContext context, Map<String, FilterMeta> filterBy, AtomicBoolean filtered) {
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
            FilterMeta globalFilterBy = FilterMeta.of(globalFilterDefaultValue, getGlobalFilterFunction());
            filterBy.put(globalFilterBy.getColumnKey(), globalFilterBy);
            filtered.set(filtered.get() || globalFilterBy.isActive());
        }
    }

    default boolean isColumnFilterable(FacesContext context, UIColumn column) {
        Map<String, FilterMeta> filterBy = getFilterByAsMap();
        return filterBy.containsKey(column.getColumnKey());
    }

    default void updateFilterByValuesWithFilterRequest(FacesContext context, Map<String, FilterMeta> filterBy) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        char separator = UINamingContainer.getSeparatorChar(context);

        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        if (globalFilter != null) {
            globalFilter.setFilterValue(
                    params.get(((UIComponent) this).getClientId(context) + separator + FilterMeta.GLOBAL_FILTER_KEY));
        }

        forEachColumn(column -> {
            FilterMeta filterMeta = filterBy.get(column.getColumnKey());
            if (filterMeta == null || filterMeta.isGlobalFilter()) {
                return true;
            }

            if (column instanceof DynamicColumn) {
                ((DynamicColumn) column).applyModel();
            }

            UIComponent filterFacet = column.getFilterComponent();
            boolean hasCustomFilter = ComponentUtils.shouldRenderFacet(filterFacet);

            Object filterValue;
            if (hasCustomFilter) {
                filterValue = ((ValueHolder) filterFacet).getLocalValue();
            }
            else {
                String valueHolderClientId = column instanceof DynamicColumn
                        ? column.getContainerClientId(context) + separator + "filter"
                        : column.getClientId(context) + separator + "filter";
                filterValue = params.get(valueHolderClientId);
            }

            if (filterValue != null) {
                // this is not absolutely necessary, but in case the result is null, it prevents to execution of the next statement
                filterValue = FilterMeta.resetToNullIfEmpty(filterValue);
            }

            if (filterValue != null) {
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

    boolean isDefaultFilter();

    void setDefaultFilter(boolean defaultFilter);

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

        setDefaultSort(sorted.get());

        setSortByAsMap(sortBy);

        return sortBy;
    }

    default void updateSortByWithMVS(Map<String, SortMeta> tsSortBy) {
        boolean defaultSort = isDefaultSort();
        for (Map.Entry<String, SortMeta> entry : tsSortBy.entrySet()) {
            SortMeta intlSortBy = getSortByAsMap().get(entry.getKey());
            if (intlSortBy != null) {
                SortMeta tsSortMeta = entry.getValue();
                intlSortBy.setPriority(tsSortMeta.getPriority());
                intlSortBy.setOrder(tsSortMeta.getOrder());
                defaultSort |= intlSortBy.isActive();
            }
        }

        setDefaultSort(defaultSort);
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
        return sortBy.containsKey(column.getColumnKey());
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

    boolean isDefaultSort();

    void setDefaultSort(boolean defaultSort);

    default void decodeColumnTogglerState(FacesContext context) {
        String columnTogglerStateParam = context.getExternalContext().getRequestParameterMap()
                .get(getClientId(context) + "_columnTogglerState");
        if (columnTogglerStateParam == null) {
            return;
        }

        Map<String, ColumnMeta> columMeta = getColumnMeta();
        columMeta.values().stream().forEach(s -> s.setVisible(null));

        if (LangUtils.isNotBlank(columnTogglerStateParam)) {
            String[] columnStates = columnTogglerStateParam.split(",");
            for (String columnState : columnStates) {
                if (LangUtils.isBlank(columnState)) {
                    continue;
                }
                int seperatorIndex = columnState.lastIndexOf('_');
                String columnKey = columnState.substring(0, seperatorIndex);
                boolean visible = Boolean.parseBoolean(columnState.substring(seperatorIndex + 1));

                ColumnMeta meta = columMeta.computeIfAbsent(columnKey, k -> new ColumnMeta(k));
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
        columMeta.values().stream().forEach(s -> s.setWidth(null));

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

                    ColumnMeta meta = columMeta.computeIfAbsent(columnKey, k -> new ColumnMeta(k));
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
        columMeta.values().stream().forEach(s -> s.setDisplayPriority(0));

        String[] columnKeys = columnOrderParam.split(",");
        for (int i = 0; i < columnKeys.length; i++) {
            String columnKey = columnKeys[i];
            if (LangUtils.isBlank(columnKey)) {
                continue;
            }

            ColumnMeta meta = columMeta.computeIfAbsent(columnKey, k -> new ColumnMeta(k));
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
        Object value = UIColumn.createValueExpressionFromField(context, getVar(), column.getField()).getValue(context.getELContext());
        return value;
    }

    default String getConvertedFieldValue(FacesContext context, UIColumn column) {
        Object value = UIColumn.createValueExpressionFromField(context, getVar(), column.getField()).getValue(context.getELContext());
        UIComponent component = column instanceof DynamicColumn ? ((DynamicColumn) column).getColumns() : (UIComponent) column;
        Object converter = column instanceof ColumnBase ? ((ColumnBase) column).getConverter() : null;
        return ComponentUtils.getConvertedAsString(context, component, converter, value);
    }

    default boolean isFilteringCurrentlyActive() {
        return getFilterByAsMap().values().stream().anyMatch(FilterMeta::isActive);
    }

    default int compare(FacesContext context, String var, SortMeta sortMeta, Object o1, Object o2,
            Collator collator, Locale locale) {

        try {
            ValueExpression ve = sortMeta.getSortBy();

            context.getExternalContext().getRequestMap().put(var, o1);
            Object value1 = ve.getValue(context.getELContext());

            context.getExternalContext().getRequestMap().put(var, o2);
            Object value2 = ve.getValue(context.getELContext());

            int result;

            if (sortMeta.getFunction() == null) {
                //Empty check
                if (value1 == null && value2 == null) {
                    result = 0;
                }
                else if (value1 == null) {
                    result = sortMeta.getNullSortOrder();
                }
                else if (value2 == null) {
                    result = -1 * sortMeta.getNullSortOrder();
                }
                else if (value1 instanceof String && value2 instanceof String) {
                    if (sortMeta.isCaseSensitiveSort()) {
                        result = collator.compare(value1, value2);
                    }
                    else {
                        String str1 = (((String) value1).toLowerCase(locale));
                        String str2 = (((String) value2).toLowerCase(locale));

                        result = collator.compare(str1, str2);
                    }
                }
                else {
                    result = ((Comparable<Object>) value1).compareTo(value2);
                }
            }
            else {
                result = (Integer) sortMeta.getFunction().invoke(context.getELContext(), new Object[]{value1, value2});
            }

            return sortMeta.getOrder().isAscending() ? result : -1 * result;
        }
        catch (Exception e) {
            throw new FacesException(e);
        }
    }

    /**
     * Recalculates filteredValue after adding, updating or removing object to/from a filtered UITable.
     */
    void filterAndSort();

    /**
     * Resets all column related state after adding/removing/moving columns.
     */
    default void resetColumns() {
        resetDynamicColumns();
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

                if (column.getFooterText() != null || ComponentUtils.shouldRenderFacet(column.getFacet("footer"))) {
                    return true;
                }
            }
        }

        return false;
    }

    int getChildCount();

    List<UIComponent> getChildren();
}
