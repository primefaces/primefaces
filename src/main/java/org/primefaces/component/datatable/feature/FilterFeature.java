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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.filter.*;
import org.primefaces.util.MapBuilder;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.IOException;
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
        .put(MatchMode.RANGE, new RangeFilterConstraint())
        .build();

    private boolean isFilterRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return context.getCurrentPhaseId() == PhaseId.PROCESS_VALIDATIONS && isFilterRequest(context, table);
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isFilterRequest(context, table);
    }

    @Override
    public void decode(FacesContext context, DataTable table) {
        // FilterMeta#column must be updated since local value
        // (from column) must be decoded by FilterFeature#decodeFilterValue
        Map<String, FilterMeta> filterBy = table.initFilterBy(context);

        table.updateFilterByValuesWithFilterRequest(context, filterBy);

        // reset state
        table.setFirst(0);

        // update rows with rpp value
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
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

            // update filtered value accordingly to take account sorting
            if (table.isSortingCurrentlyActive()) {
                SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);
                sortFeature.sort(context, table);
            }
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        if (table.isMultiViewState()) {
            DataTableState ts = table.getMultiViewState(true);
            ts.setFilterBy(filterBy);
            if (table.isPaginator()) {
                ts.setFirst(table.getFirst());
                ts.setRows(table.getRows());
            }
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        renderer.encodeTbody(context, table, true);
    }

    public void filter(FacesContext context, DataTable table) {
        List<Object> filtered = new ArrayList<>();
        Locale filterLocale = table.resolveDataLocale();
        ELContext elContext = context.getELContext();
        Map<String, FilterMeta> filterBy = table.getFilterByAsMap();

        table.setValue(null); // reset value (instead of filtering on already filtered value)

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
                filtered.add(table.getRowData());
            }
        }

        //Metadata for callback
        if (table.isPaginator() || table.isVirtualScroll()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", filtered.size());
        }

        //save filtered data
        table.updateFilteredValue(context, filtered);
        table.setValue(filtered);
        table.setRowIndex(-1);  //reset datamodel
    }
}