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
package org.primefaces.component.datatable.feature;

import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.util.ComponentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.el.ELContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;

public class FilterFeature implements DataTableFeature {

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
        Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
        table.updateFilterByValuesWithFilterRequest(context, filterBy);

        // reset state
        table.setFirst(0);

        // update rows with rpp value
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        String rppValue = params.get(clientId + "_rppDD");
        if (rppValue != null && !"*".equals(rppValue)) {
            table.setRows(Integer.parseInt(rppValue));
        }

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
        if (!table.loadLazyDataIfEnabled()) {
            filter(context, table);

            // update filtered value accordingly to take account sorting
            if (table.isSortingCurrentlyActive()) {
                DataTableFeatures.sortFeature().sort(context, table);
            }
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        if (!table.isFullUpdateRequest(context)) {
            renderer.encodeTbody(context, table, true);
        }
    }

    public void filter(FacesContext context, DataTable table) {
        List<Object> filtered = new ArrayList<>();
        Locale filterLocale = table.resolveDataLocale();
        ELContext elContext = context.getELContext();
        Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        boolean hasGlobalFilterFunction = globalFilter != null && globalFilter.getConstraint() instanceof FunctionFilterConstraint;

        table.setValue(null); // reset value (instead of filtering on already filtered value)
        AtomicBoolean localMatch = new AtomicBoolean();
        AtomicBoolean globalMatch = new AtomicBoolean();

        for (int i = 0; i < table.getRowCount(); i++) {
            table.setRowIndex(i);
            Object rowData = table.getRowData();
            localMatch.set(true);
            globalMatch.set(false);

            if (hasGlobalFilterFunction) {
                globalMatch.set(globalFilter.getConstraint().isMatching(context, rowData, globalFilter.getFilterValue(), filterLocale));
            }

            final int rowIndex = i;
            table.forEachColumn(column -> {
                FilterMeta filter = filterBy.get(column.getColumnKey(table, rowIndex));
                if (filter == null || filter.isGlobalFilter()) {
                    return true;
                }

                Object columnValue = filter.getLocalValue(elContext, column);

                if (globalFilter != null && globalFilter.isActive() && !globalMatch.get() && !hasGlobalFilterFunction) {
                    FilterConstraint constraint = globalFilter.getConstraint();
                    Object filterValue = globalFilter.getFilterValue();
                    globalMatch.set(constraint.isMatching(context, columnValue, filterValue, filterLocale));
                }

                if (!filter.isActive()) {
                    return true;
                }

                FilterConstraint constraint = filter.getConstraint();
                Object filterValue = filter.getFilterValue();

                localMatch.set(constraint.isMatching(context, columnValue, filterValue, filterLocale));
                return localMatch.get();
            });

            boolean matches = localMatch.get();
            if (globalFilter != null && globalFilter.isActive()) {
                matches = matches && globalMatch.get();
            }

            if (matches) {
                filtered.add(rowData);
            }
        }

        //Metadata for callback
        if ((table.isPaginator() || table.isVirtualScroll()) && ComponentUtils.isRequestSource(table, context)) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", filtered.size());
        }

        //save filtered data
        table.setFilteredValue(filtered);
        table.setValue(DataTable.convertIntoObjectValueType(context, table, filtered));
        table.setRowIndex(-1); //reset datamodel
    }
}