/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class ScrollFeature implements DataTableFeature {

    @Override
    public void decode(FacesContext context, DataTable table) {
        throw new RuntimeException("RowScrollFeature should not decode.");
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        int scrollRows = table.getScrollRows();
        String clientId = table.getClientId(context);
        boolean isVirtualScroll = table.isVirtualScroll();
        boolean isLazy = table.isLazy();
        int scrollOffset = 0;

        if (isVirtualScroll) {
            scrollOffset = Integer.parseInt(params.get(table.getClientId(context) + "_first"));
            int rowCount = table.getRowCount();
            int virtualScrollRows = (scrollRows * 2);
            scrollRows = (scrollOffset + virtualScrollRows) > rowCount ? (rowCount - scrollOffset) : virtualScrollRows;
        }
        else {
            scrollOffset = Integer.parseInt(params.get(table.getClientId(context) + "_scrollOffset"));
            table.setScrollOffset(scrollOffset);
        }

        if (isLazy) {
            table.loadLazyScrollData(scrollOffset, scrollRows);
        }

        if (table.isSelectionEnabled()) {
            table.findSelectedRowKeys();
        }

        int firstIndex = (isLazy && isVirtualScroll) ? 0 : scrollOffset;
        int lastIndex = (firstIndex + scrollRows);

        for (int i = firstIndex; i < lastIndex; i++) {
            table.setRowIndex(i);

            if (table.isRowAvailable()) {
                int rowIndex = (isLazy && isVirtualScroll) ? scrollOffset + i : i;
                renderer.encodeRow(context, table, clientId, rowIndex);
            }
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return table.isScrollingRequest(context);
    }

}
