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
import org.primefaces.component.api.table.SortableColumnsFeature;

public class SortFeature extends DataTableFeature {

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return SortableColumnsFeature.INSTANCE.shouldDecode(context, table);
    }

    @Override
    public void decode(FacesContext context, DataTable table) {
        SortableColumnsFeature.INSTANCE.decode(context, table);

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

        if (table.isPaginator() && table.isMultiViewState()) {
            DataTableState ts = table.getMultiViewState(true);
            ts.setFirst(table.getFirst());
            ts.setRows(table.getRows());
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

        ChainedBeanPropertyComparator chainedComparator = new ChainedBeanPropertyComparator();

        for (SortMeta meta : table.getActiveSortMeta().values()) {
            BeanPropertyComparator comparator;
            Object source = meta.isForHeaderRow() ? meta.getHeaderRow() : meta.getColumn();

            if (source instanceof DynamicColumn) {
                comparator = new DynamicBeanPropertyComparator(table.getVar(), meta, locale);
            }
            else {
                comparator = new BeanPropertyComparator(table.getVar(), meta, locale);
            }

            chainedComparator.addComparator(comparator);
        }

        list.sort(chainedComparator);
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return shouldDecode(context, table);
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
