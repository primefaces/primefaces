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
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.event.data.PostSortEvent;
import org.primefaces.model.*;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

        List<String> sortKeys = Arrays.asList(sortKey.split(","));
        List<String> sortOrders = Arrays.asList(sortDir.split(","));

        if (sortKeys.size() != sortOrders.size()) {
            throw new FacesException("sortKeys != sortDirs");
        }

        int i = 0;
        for(Map.Entry<String, SortMeta> entry : table.getSortByAsMap().entrySet()) {
            SortMeta m = entry.getValue();
            if (!sortKeys.contains(entry.getKey())) {
                m.setSortOrder(SortOrder.UNSORTED);
                m.setPriority(Integer.MAX_VALUE);
            }
            else {
                m = table.getSortByAsMap().get(sortKeys.get(i));
                m.setSortOrder(SortOrder.of(sortOrders.get(i)));
                m.setPriority(i);
                i++;
            }
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        table.setFirst(0);

        if (table.isLazy()) {
            if (table.isLiveScroll()) {
                table.loadLazyScrollData(0, table.getScrollRows());
            }
            else if (table.isVirtualScroll()) {
                int rows = table.getRows();
                int scrollRows = table.getScrollRows();
                int virtualScrollRows = (scrollRows * 2);
                scrollRows = (rows == 0) ? virtualScrollRows : ((virtualScrollRows > rows) ? rows : virtualScrollRows);

                table.loadLazyScrollData(0, scrollRows);
            }
            else {
                table.loadLazyData();
            }
        }
        else {
            sort(context, table);

            if (table.isPaginator()) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", table.getRowCount());
            }

            //save state
            Object filteredValue = table.getFilteredValue();
            if (!table.isLazy() && table.isFilteringEnabled() && filteredValue != null) {
                table.updateFilteredValue(context, (List) filteredValue);
            }
        }

        renderer.encodeTbody(context, table, true);

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

        List list = resolveList(value);
        boolean caseSensitiveSort = table.isCaseSensitiveSort();
        Locale locale = table.resolveDataLocale();
        int nullSortOrder = table.getNullSortOrder();

        if (table.isMultiSort()) {
            Map<String, SortMeta> sortedMeta = table.getSortByAsMap().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));
            table.setSortByAsMap(sortedMeta);
        }

        ChainedBeanPropertyComparator chainedComparator = new ChainedBeanPropertyComparator();

        for (SortMeta meta : table.getSortByAsMap().values()) {
            if (!meta.isActive()) {
                continue;
            }

            BeanPropertyComparator comparator;
            Object source = meta.getComponent();

            if (source instanceof UIColumn && ((UIColumn) source).isDynamic()) {
                comparator = new DynamicBeanPropertyComparator(table.getVar(),
                        meta, caseSensitiveSort, locale, nullSortOrder);
            }
            else {
                comparator = new BeanPropertyComparator(table.getVar(), meta, caseSensitiveSort, locale, nullSortOrder);
            }

            chainedComparator.addComparator(comparator);
        }

        list.sort(chainedComparator);

        context.getApplication().publishEvent(context, PostSortEvent.class, table);
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return isSortRequest(context, table);
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isSortRequest(context, table);
    }

    protected List resolveList(Object value) {
        if (value instanceof List) {
            return (List) value;
        }
        else if (value instanceof ListDataModel) {
            return (List) ((ListDataModel) value).getWrappedData();
        }
        else {
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        }
    }
}
