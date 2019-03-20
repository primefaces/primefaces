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
package org.primefaces.component.celleditor;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.renderkit.CoreRenderer;

public class CellEditorRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        CellEditor editor = (CellEditor) component;
        UIComponent parentTable = editor.getParentTable(context);
        boolean isLazyEdit = false;

        if (editor.isDisabled()) {
            editor.getFacet("output").encodeAll(context);
            return;
        }

        if (parentTable != null) {
            String editMode = null;
            String cellEditMode = null;
            boolean isLazyRowEdit = false;

            if (parentTable instanceof DataTable) {
                DataTable dt = (DataTable) parentTable;
                editMode = dt.getEditMode();
                cellEditMode = dt.getCellEditMode();

                String rowEditMode = dt.getRowEditMode();
                isLazyRowEdit = rowEditMode != null && editMode.equals("row") && rowEditMode.equals("lazy")
                        && !dt.isRowEditInitRequest(context) && !context.isValidationFailed();
            }
            else if (parentTable instanceof TreeTable) {
                TreeTable tt = (TreeTable) parentTable;
                editMode = tt.getEditMode();
                cellEditMode = tt.getCellEditMode();
            }

            isLazyEdit = editMode != null && (cellEditMode != null && editMode.equals("cell") && cellEditMode.equals("lazy") || isLazyRowEdit);
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", DataTable.CELL_EDITOR_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.CELL_EDITOR_OUTPUT_CLASS, null);
        editor.getFacet("output").encodeAll(context);
        writer.endElement("div");

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.CELL_EDITOR_INPUT_CLASS, null);

        if (!isLazyEdit) {
            editor.getFacet("input").encodeAll(context);
        }
        writer.endElement("div");

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}
