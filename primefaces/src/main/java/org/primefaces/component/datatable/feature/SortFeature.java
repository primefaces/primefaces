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
import org.primefaces.event.data.PostSortEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.SortTableComparator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.ListDataModel;

public class SortFeature implements DataTableFeature {

    private boolean isSortRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_sorting");
    }

    @Override
    public void decode(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String sortKey = params.get(clientId + "_sortKey");
        String sortDir = params.get(clientId + "_sortDir");

        String[] sortKeys = sortKey.split(",");
        String[] sortOrders = sortDir.split(",");

        if (sortKeys.length != sortOrders.length) {
            throw new FacesException("sortKeys != sortDirs");
        }

        Map<String, SortMeta> sortByMap = table.getSortByAsMap();
        Map<String, Integer> sortKeysIndexes = IntStream.range(0, sortKeys.length).boxed()
                .collect(Collectors.toMap(i -> sortKeys[i], i -> i));

        for (Map.Entry<String, SortMeta> entry : sortByMap.entrySet()) {
            SortMeta sortMeta = entry.getValue();
            if (sortMeta.isHeaderRow()) {
                continue;
            }

            Integer index = sortKeysIndexes.get(entry.getKey());
            if (index != null) {
                sortMeta.setOrder(SortOrder.of(sortOrders[index]));
                sortMeta.setPriority(index);
            }
            else {
                sortMeta.setOrder(SortOrder.UNSORTED);
                sortMeta.setPriority(SortMeta.MIN_PRIORITY);
            }
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        table.setFirst(0);

        if (!table.loadLazyDataIfEnabled()) {
            //reset the value given in the filter feature property before sorting
            if (table.isFullUpdateRequest(context)) {
                table.setValue(null);
            }

            sort(context, table);

            if (table.isPaginator() && ComponentUtils.isRequestSource(table, context)) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", table.getRowCount());
            }

            //update filtered value accordingly to take account sorting
            if (table.isFilteringCurrentlyActive()) {
                if (table.isFullUpdateRequest(context)) {
                    DataTableFeatures.filterFeature().filter(context, table);
                }
                else {
                    table.setFilteredValue(resolveList(table.getValue()));
                }
            }
        }

        context.getApplication().publishEvent(context, PostSortEvent.class, table);

        if (!table.isFullUpdateRequest(context)) {
            renderer.encodeTbody(context, table, true);
        }

        if (table.isMultiViewState()) {
            Map<String, SortMeta> sortMeta = table.getSortByAsMap();
            if (!sortMeta.isEmpty()) {
                DataTableState ts = table.getMultiViewState(true);
                ts.setSortBy(sortMeta);
                if (table.isPaginator()) {
                    ts.setFirst(table.getFirst());
                    ts.setRows(table.getRows());
                }
            }
        }
    }

    public void sort(FacesContext context, DataTable table) {
        Object value = table.getValue();
        if (value == null) {
            return;
        }

        List<?> list = resolveList(value);
        String var = table.getVar();
        Object varBackup = context.getExternalContext().getRequestMap().get(var);

        list.sort(SortTableComparator.comparingSortByVE(context, table));

        if (varBackup == null) {
            context.getExternalContext().getRequestMap().remove(var);
        }
        else {
            context.getExternalContext().getRequestMap().put(var, varBackup);
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return isSortRequest(context, table);
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isSortRequest(context, table);
    }

    protected <T> List<T> resolveList(Object value) {
        if (value instanceof List) {
            return (List<T>) value;
        }
        else if (value instanceof ListDataModel) {
            return (List<T>) ((ListDataModel<T>) value).getWrappedData();
        }
        else {
            throw new FacesException(String.format("Data type should be java.util.List or %s instance to be sortable."
                    , jakarta.faces.model.ListDataModel.class.getName()));
        }
    }
}
