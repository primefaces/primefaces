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

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.event.data.PostPageEvent;

import java.io.IOException;

import jakarta.faces.context.FacesContext;

public class PageFeature implements DataTableFeature {

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        table.updatePaginationData(context);

        boolean isPageState = table.isPageStateRequest(context);

        if (!isPageState) {
            table.loadLazyDataIfEnabled();
        }

        if (!isPageState && !table.isFullUpdateRequest(context)) {
            renderer.encodeTbody(context, table, true);
        }

        context.getApplication().publishEvent(context, PostPageEvent.class, table);

        if (table.isMultiViewState()) {
            DataTableState ts = table.getMultiViewState(true);

            ts.setFirst(table.getFirst());
            ts.setRows(table.getRows());
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return table.isPaginationRequest(context);
    }

}
