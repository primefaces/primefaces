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
package org.primefaces.component.datatable.feature;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.filter.*;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class FilterFeature implements DataTableFeature {

    public static final Map<MatchMode, FilterConstraint> FILTER_CONSTRAINTS = MapBuilder.<MatchMode, FilterConstraint>builder()
        .put(MatchMode.STARTS_WITH, new StartsWithFilterConstraint())
        .put(MatchMode.ENDS_WITH, new EndsWithFilterConstraint())
        .put(MatchMode.CONTAINS, new ContainsFilterConstraint())
        .put(MatchMode.EXACT, new ExactFilterConstraint())
        .put(MatchMode.LESS_THAN, new LessThanFilterConstraint())
        .put(MatchMode.LESS_THAN_EQUALS, new LessThanEqualsFilterConstraint())
        .put(MatchMode.GREATER_THAN, new GreaterThanFilterConstraint())
        .put(MatchMode.GREATER_THAN_EQUALS, new GreaterThanEqualsFilterConstraint())
        .put(MatchMode.EQUALS, new EqualsFilterConstraint())
        .put(MatchMode.IN, new InFilterConstraint())
        .build();

    private boolean isFilterRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isFilterRequest(context, table);
    }

    @Override
    public void decode(FacesContext context, DataTable table) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        // FilterMeta#column must be updated since local value
        // (from column) must be decoded by FilterFeature#decodeFilterValue
        Map<String, FilterMeta> filterBy = table.initFilterBy();
        char separator = UINamingContainer.getSeparatorChar(context);

        for (FilterMeta entry : filterBy.values()) {
            decodeFilterValue(context, table, entry, params, separator);
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        //reset state
        String clientId = table.getClientId(context);
        table.updateFilteredValue(context, null);
        table.setValue(null);
        table.setFirst(0);
        table.setRowIndex(-1);

        //update rows with rpp value
        String rppValue = params.get(clientId + "_rppDD");
        if (rppValue != null && !rppValue.equals("*")) {
            table.setRows(Integer.parseInt(rppValue));
        }

        if (table.isLazy()) {
            if (table.isLiveScroll()) {
                table.loadLazyScrollData(0, table.getScrollRows());
            }
            else if (table.isVirtualScroll()) {
                int rows = table.getRows();
                int scrollRows = table.getScrollRows();
                int virtualScrollRows = (scrollRows * 2);
                scrollRows = (rows == 0) ? virtualScrollRows : Math.min(virtualScrollRows, rows);

                table.loadLazyScrollData(0, scrollRows);
            }
            else {
                table.loadLazyData();
            }
        }
        else {
            filter(context, table);

            //sort new filtered data to restore sort state
            boolean sorted = table.isSortingCurrentlyActive();
            if (sorted) {
                SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);
                sortFeature.sort(context, table);
            }
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        renderer.encodeTbody(context, table, true);

        if (table.isMultiViewState()) {
            Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
            DataTableState ts = table.getMultiViewState(true);
            ts.setFilterBy(filterBy);
            if (table.isPaginator()) {
                ts.setFirst(table.getFirst());
                ts.setRows(table.getRows());
            }
        }
    }

    public void filter(FacesContext context, DataTable table) {
        Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
        List<Object> filteredData = new ArrayList<>();
        Locale filterLocale = table.resolveDataLocale();
        ELContext elContext = context.getELContext();

        for (int i = 0; i < table.getRowCount(); i++) {
            table.setRowIndex(i);
            boolean matching = true;

            for (FilterMeta filter : filterBy.values()) {
                if (!filter.isActive()) {
                    continue;
                }

                FilterConstraint constraint = filter.getConstraint();
                Object filterValue = filter.getFilterValue();
                Object columnValue = filter.isGlobalFilter() ? table.getRowData() : filter.getLocalValue(elContext);

                // case of default filter value using markup value (coerce into String by default)
                if (!filter.isGlobalFilter()
                        && filterValue != null
                        && columnValue != null
                        && !Objects.equals(filterValue.getClass(), columnValue.getClass())
                        && !filterValue.getClass().isArray()
                        && !(filterValue instanceof Collection)) {
                    ExpressionFactory ef = context.getApplication().getExpressionFactory();
                    filterValue = ef.coerceToType(filterValue, columnValue.getClass());
                    filter.setFilterValue(filterValue);
                }

                matching = constraint.isMatching(context, columnValue, filterValue, filterLocale);
                if (!matching) {
                    break;
                }
            }

            if (matching) {
                filteredData.add(table.getRowData());
            }
        }

        //Metadata for callback
        if (table.isPaginator() || table.isVirtualScroll()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", filteredData.size());
        }

        //save filtered data
        table.updateFilteredValue(context, filteredData);

        //update value
        table.updateValue(table.getFilteredValue());

        table.setRowIndex(-1);  //reset datamodel
    }

    protected void decodeFilterValue(FacesContext context, DataTable table, FilterMeta filterBy, Map<String, String> params, char separator) {
        Object filterValue = null;

        if (filterBy.isGlobalFilter()) {
            filterValue = params.get(table.getClientId(context) + separator + FilterMeta.GLOBAL_FILTER_KEY);
        }
        else {
            UIColumn column = filterBy.getColumn();
            if (column instanceof DynamicColumn) {
                ((DynamicColumn) column).applyStatelessModel();
            }

            UIComponent filterFacet = column.getFacet("filter");
            if (ComponentUtils.shouldRenderFacet(filterFacet)) {
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

        filterBy.setFilterValue(filterValue);
    }
}