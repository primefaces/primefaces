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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import static org.primefaces.component.datatable.DataTable.createValueExprFromVarField;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

public interface FilterableTable extends ColumnHolder {

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

    default void decodeFilterValue(FacesContext context, FilterMeta filterMeta) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        char separator = UINamingContainer.getSeparatorChar(context);

        Object filterValue;

        if (filterMeta.isGlobalFilter()) {
            filterValue = params.get(((UIComponent) this).getClientId(context) + separator + FilterMeta.GLOBAL_FILTER_KEY);
        }
        else {
            UIColumn column = filterMeta.getColumn();
            UIComponent filterFacet = column.getFacet("filter");
            boolean hasCustomFilter = ComponentUtils.shouldRenderFacet(filterFacet);
            if (column instanceof DynamicColumn) {
                if (hasCustomFilter) {
                    ((DynamicColumn) column).applyModel();
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

    String getVar();
}
