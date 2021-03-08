/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

public interface UITable<T extends UITableState> extends ColumnAware, MultiViewStateAware<T> {

    /**
     * Backward compatibility for column properties (e.g sortBy, filterBy)
     * using old syntax #{car[column.property]}) instead of #{column.property}
     */
    Pattern OLD_SYNTAX_COLUMN_PROPERTY_REGEX = Pattern.compile("^#\\{\\w+\\[(.+)]}$");

    static String resolveStaticField(ValueExpression expression) {
        if (expression != null) {
            String expressionString = expression.getExpressionString();
            if (expressionString.startsWith("#{")) {
                expressionString = expressionString.substring(2, expressionString.indexOf('}')); //Remove #{}
                return expressionString.substring(expressionString.indexOf('.') + 1); //Remove var
            }
        }

        return null;
    }

    /**
     * Get bean's property value from a value expression.
     * Support old syntax (e.g #{car[column.property]}) instead of #{column.property}
     * @param context
     * @param exprVE
     * @return
     */
    static String resolveDynamicField(FacesContext context, ValueExpression exprVE) {
        if (exprVE == null) {
            return null;
        }

        ELContext elContext = context.getELContext();
        String exprStr = exprVE.getExpressionString();

        Matcher matcher = OLD_SYNTAX_COLUMN_PROPERTY_REGEX.matcher(exprStr );
        if (matcher.find()) {
            exprStr = matcher.group(1);
            exprVE = context.getApplication().getExpressionFactory()
                    .createValueExpression(elContext, "#{" + exprStr  + "}", String.class);
        }

        return (String) exprVE.getValue(elContext);
    }

    static ValueExpression createValueExprFromVarField(FacesContext context, String var, String field) {
        if (LangUtils.isValueBlank(var) || LangUtils.isValueBlank(field)) {
            throw new FacesException("Table 'var' and Column 'field' attributes must be non null.");
        }

        return context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{" + var + "." + field + "}", Object.class);
    }

    String getVar();

    String getClientId(FacesContext context);


    default Map<String, FilterMeta> initFilterBy(FacesContext context) {
        boolean invalidate = !isFilterByAsMapDefined();
        Map<String, FilterMeta> filterBy = invalidate ? new HashMap<>() : getFilterByAsMap();
        AtomicBoolean filtered = invalidate ? new AtomicBoolean() : new AtomicBoolean(isDefaultFilter());

        // build columns filterBy
        forEachColumn(c -> {
            FilterMeta f = filterBy.get(c.getColumnKey());
            if (f != null && !invalidate) {
                f.setColumn(c);
            }
            else {
                f = FilterMeta.of(context, getVar(), c);
                if (f != null) {
                    filterBy.put(f.getColumnKey(), f);
                    filtered.set(filtered.get() || f.isActive());
                }
            }

            return true;
        });

        // merge internal filterBy with user filterBy
        Object userfilterBy = getFilterBy();
        if (userfilterBy != null) {
            updateFilterByWithUserFilterBy(context, filterBy, userfilterBy, filtered);
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
                    .filter(o -> o.getField().equals(userFM.getField()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userFM.getField() + "' has been found"));

            ValueExpression filterByVE = userFM.getFilterBy();
            if (filterByVE == null) {
                filterByVE = createValueExprFromVarField(context, getVar(), userFM.getField());
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
        Set<SearchExpressionHint> hint = LangUtils.isValueBlank(globalFilterDefaultValue)
                ? SearchExpressionUtils.SET_IGNORE_NO_RESULT
                : SearchExpressionUtils.SET_NONE;
        UIComponent globalFilterComponent = SearchExpressionFacade
                .resolveComponent(context, (UIComponent) this, "globalFilter", hint);
        if (globalFilterComponent != null) {
            if (globalFilterComponent instanceof ValueHolder) {
                ((ValueHolder) globalFilterComponent).setValue(globalFilterDefaultValue);
            }
            FilterMeta globalFilterBy = FilterMeta.of(globalFilterDefaultValue, getGlobalFilterFunction());
            filterBy.put(globalFilterBy.getColumnKey(), globalFilterBy);
            filtered.set(filtered.get() || globalFilterBy.isActive());
        }
    }

    default boolean isColumnFilterable(UIColumn column) {
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

            UIComponent filterFacet = column.getFacet("filter");
            boolean hasCustomFilter = filterFacet != null;
            if (column instanceof DynamicColumn) {
                if (hasCustomFilter) {
                    ((DynamicColumn) column).applyModel();
                    // UIColumn#rendered might change after restoring p:columns state at the right index
                    hasCustomFilter = ComponentUtils.shouldRenderFacet(filterFacet);
                }
                else {
                    ((DynamicColumn) column).applyStatelessModel();
                }
            }

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

            // returns null if empty string/array/object
            if (filterValue != null
                    && (filterValue instanceof String && LangUtils.isValueBlank((String) filterValue)
                    || filterValue.getClass().isArray() && Array.getLength(filterValue) == 0)) {
                filterValue = null;
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



    default Map<String, SortMeta> initSortBy(FacesContext context) {
        Map<String, SortMeta> sortMeta = new HashMap<>();
        AtomicBoolean sorted = new AtomicBoolean();

        HeaderRow headerRow = getHeaderRow();
        if (headerRow != null) {
            SortMeta s = SortMeta.of(context, getVar(), headerRow);
            sortMeta.put(s.getColumnKey(), s);
            sorted.set(true);
        }

        forEachColumn(c -> {
            SortMeta s = SortMeta.of(context, getVar(), c);
            if (s != null) {
                sorted.set(sorted.get() || s.isActive());
                sortMeta.put(s.getColumnKey(), s);
            }

            return true;
        });

        // merge internal sortBy with user sortBy
        Object userSortBy = getSortBy();
        if (userSortBy != null) {
            updateSortByWithUserSortBy(context, sortMeta, userSortBy, sorted);
        }

        setDefaultSort(sorted.get());

        return sortMeta;
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
                    .filter(o -> o.getField().equals(userSM.getField()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userSM.getField() + "' has been found"));

            ValueExpression sortByVE = userSM.getSortBy();
            if (sortByVE == null) {
                sortByVE = createValueExprFromVarField(context, getVar(), userSM.getField());
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

        SortMeta s = SortMeta.of(context, getVar(), column);
        if (s == null) {
            return false;
        }

        // unlikely to happen, in case columns change between two ajax requests
        sortBy.put(s.getColumnKey(), s);
        setSortByAsMap(sortBy);
        return true;
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
        return !getFilterByAsMap().isEmpty();
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

        if (!LangUtils.isValueBlank(columnTogglerStateParam)) {
            String[] columnStates = columnTogglerStateParam.split(",");
            for (String columnState : columnStates) {
                if (LangUtils.isValueBlank(columnState)) {
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

        if (!LangUtils.isValueBlank(columnResizeStateParam)) {
            String[] columnStates = columnResizeStateParam.split(",");
            for (String columnState : columnStates) {
                if (LangUtils.isValueBlank(columnState)) {
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
        if (LangUtils.isValueBlank(columnOrderParam)) {
            return;
        }

        Map<String, ColumnMeta> columMeta = getColumnMeta();
        columMeta.values().stream().forEach(s -> s.setDisplayPriority(0));

        String[] columnKeys = columnOrderParam.split(",");
        for (int i = 0; i < columnKeys.length; i++) {
            String columnKey = columnKeys[i];
            if (LangUtils.isValueBlank(columnKey)) {
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
                .map(e -> e.getKey() + '_' + e.getValue().getWidth())
                .collect(Collectors.joining(","));
    }

    default String getConvertedFieldValue(FacesContext context, UIColumn column) {
        Object value = createValueExprFromVarField(context, getVar(), column.getField()).getValue(context.getELContext());
        UIComponent component = column instanceof DynamicColumn ? ((DynamicColumn) column).getColumns() : (UIComponent) column;
        return ComponentUtils.getConvertedAsString(context, component, value);
    }

    default boolean isFilteringCurrentlyActive() {
        return getFilterByAsMap().values().stream().anyMatch(FilterMeta::isActive);
    }
}
