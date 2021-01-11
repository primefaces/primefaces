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
import java.text.Collator;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.el.ValueExpression;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.headerrow.HeaderRow;

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
            SortMeta sortBy = entry.getValue();
            if (!(sortBy.getComponent() instanceof UIColumn)) {
                continue;
            }

            Integer index = sortKeysIndexes.get(entry.getKey());
            if (index != null) {
                sortBy.setOrder(SortOrder.of(sortOrders[index]));
                sortBy.setPriority(index);
            }
            else {
                sortBy.setOrder(SortOrder.UNSORTED);
                sortBy.setPriority(SortMeta.MIN_PRIORITY);
            }
        }

        table.setFirst(0);

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
            sort(context, table);

            if (table.isPaginator()) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", table.getRowCount());
            }

            //update filtered value accordingly to take account sorting
            if (table.isFilteringEnabled()) {
                table.updateFilteredValue(context, resolveList(table.getValue()));
            }
        }

        context.getApplication().publishEvent(context, PostSortEvent.class, table);

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

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        renderer.encodeTbody(context, table, true);
    }

    public void sort(FacesContext context, DataTable table) {
        Object value = table.getValue();
        if (value == null) {
            return;
        }

        List<?> list = resolveList(value);
        Locale locale = table.resolveDataLocale();
        String var = table.getVar();
        Collator collator = Collator.getInstance(locale);
        AtomicInteger comparisonResult = new AtomicInteger();
        Map<String, SortMeta> sortBy = table.getActiveSortMeta();

        Object varBackup = context.getExternalContext().getRequestMap().get(var);

        list.sort((o1, o2) -> {
            for (SortMeta sortMeta : sortBy.values()) {
                comparisonResult.set(0);

                if (sortMeta.getComponent() instanceof HeaderRow) {
                    int result = compare(context, var, sortMeta, o1, o2, collator, locale);
                    comparisonResult.set(result);
                }
                else {
                    // Currently ColumnGrouping supports ui:repeat, therefore we have to use a callback
                    // and can't use sortMeta.getComponent()
                    // Later when we refactored ColumnGrouping, we may remove #invokeOnColumn as we dont support ui:repeat in other cases
                    table.invokeOnColumn(sortMeta.getColumnKey(), column -> {
                        if (column instanceof DynamicColumn) {
                            ((DynamicColumn) column).applyStatelessModel();
                        }

                        int result = compare(context, var, sortMeta, o1, o2, collator, locale);
                        comparisonResult.set(result);
                    });
                }

                if (comparisonResult.get() != 0) {
                    return comparisonResult.get();
                }
            }

            return 0;
        });

        if (varBackup == null) {
            context.getExternalContext().getRequestMap().remove(var);
        }
        else {
            context.getExternalContext().getRequestMap().put(var, varBackup);
        }
    }

    protected int compare(FacesContext context, String var, SortMeta sortMeta, Object o1, Object o2,
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
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        }
    }
}
