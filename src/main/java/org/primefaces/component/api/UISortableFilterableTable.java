/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import static org.primefaces.component.datatable.DataTable.createValueExprFromVarField;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

public interface UISortableFilterableTable extends UITable {

    String getVar();



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
        String globalFilter = getGlobalFilter();
        Set<SearchExpressionHint> hint = LangUtils.isValueBlank(globalFilter)
                ? EnumSet.of(SearchExpressionHint.IGNORE_NO_RESULT)
                : Collections.emptySet();
        UIComponent globalFilterComponent = SearchExpressionFacade
                .resolveComponent(context, (UIComponent) this, "globalFilter", hint);
        if (globalFilterComponent != null) {
            if (globalFilterComponent instanceof ValueHolder) {
                ((ValueHolder) globalFilterComponent).setValue(globalFilter);
            }
            FilterMeta globalFilterBy = FilterMeta.of(filterBy.values(), globalFilter, getGlobalFilterFunction());
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

        for (FilterMeta filterMeta : filterBy.values()) {
            Object filterValue;

            if (filterMeta.isGlobalFilter()) {
                filterValue = params.get(((UIComponent) this).getClientId(context) + separator + FilterMeta.GLOBAL_FILTER_KEY);
            }
            else {
                UIColumn column = filterMeta.getColumn();
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

                if (hasCustomFilter) {
                    filterValue = ((ValueHolder) filterFacet).getLocalValue();
                }
                else {
                    String valueHolderClientId = column instanceof DynamicColumn
                            ? column.getContainerClientId(context) + separator + "filter"
                            : column.getClientId(context) + separator + "filter";
                    filterValue = params.get(valueHolderClientId);
                }
            }

            // returns null if empty string/array/object
            if (filterValue != null
                    && (filterValue instanceof String && LangUtils.isValueBlank((String) filterValue)
                    || filterValue.getClass().isArray() && Array.getLength(filterValue) == 0)) {
                filterValue = null;
            }

            filterMeta.setFilterValue(filterValue);
        }
    }

    default Object getFilterValue(UIColumn column) {
        return getFilterByAsMap().get(column.getColumnKey()).getFilterValue();
    }

    boolean isDefaultFilter();

    void setDefaultFilter(boolean defaultFilter);

    Object getFilterBy();

    public void setFilterBy(Object filterBy);

    boolean isFilterByAsMapDefined();

    Map<String, FilterMeta> getFilterByAsMap();

    void setFilterByAsMap(Map<String, FilterMeta> sortBy);

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
        return getSortByAsMap().keySet().stream()
                .collect(Collectors.joining("','", "['", "']"));
    }

    default HeaderRow getHeaderRow() {
        return null;
    }

    Map<String, SortMeta> getSortByAsMap();

    void setSortByAsMap(Map<String, SortMeta> sortBy);

    Object getSortBy();

    void setSortBy(Object sortBy);

    boolean isDefaultSort();

    void setDefaultSort(boolean defaultSort);
}
