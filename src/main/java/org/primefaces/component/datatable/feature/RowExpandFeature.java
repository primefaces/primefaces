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
import javax.faces.context.ResponseWriter;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.model.LazyDataModel;

public class RowExpandFeature implements DataTableFeature {

    @Override
    public void decode(FacesContext context, DataTable table) {
        throw new RuntimeException("RowExpandFeature should not encode.");
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        int expandedRowIndex = Integer.parseInt(params.get(table.getClientId(context) + "_expandedRowIndex"));

        if (table.isLazy() && ((LazyDataModel) table.getValue()).getWrappedData() == null) {
            table.loadLazyData();
        }

        encodeExpansion(context, renderer, table, expandedRowIndex);
        table.setRowIndex(-1);
    }

    public void encodeExpansion(FacesContext context, DataTableRenderer renderer, DataTable table, int rowIndex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        RowExpansion rowExpansion = table.getRowExpansion();

        String styleClass = DataTable.EXPANDED_ROW_CONTENT_CLASS + " ui-widget-content";
        if (rowExpansion.getStyleClass() != null) {
            styleClass = styleClass + " " + rowExpansion.getStyleClass();
        }

        table.setRowIndex(rowIndex);

        if (rowExpansion.isRendered()) {
            if (rowIndexVar != null) {
                context.getExternalContext().getRequestMap().put(rowIndexVar, rowIndex);
            }

            writer.startElement("tr", null);
            writer.writeAttribute("class", styleClass, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", table.getColumnsCount(), null);

            table.getRowExpansion().encodeAll(context);

            writer.endElement("td");

            writer.endElement("tr");
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_rowExpansion");
    }

}
