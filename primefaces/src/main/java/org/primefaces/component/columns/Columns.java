/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.columns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.LangUtils;

public class Columns extends ColumnsBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Columns";

    private CellEditor cellEditor;
    private List<DynamicColumn> dynamicColumns;

    @Override
    public CellEditor getCellEditor() {
        if (cellEditor == null) {
            cellEditor = ComponentTraversalUtils.firstChildRendered(CellEditor.class, this);
        }

        return cellEditor;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public java.lang.String getColumnIndexVar() {
        return super.getRowIndexVar();
    }

    public void setColumnIndexVar(String _columnIndexVar) {
        super.setRowIndexVar(_columnIndexVar);
    }

    @Override
    public String getColumnKey() {
        return getClientId();
    }

    @Override
    public String getColumnKey(UIComponent parent, String rowIndex) {
        char separator = UINamingContainer.getSeparatorChar(getFacesContext());
        return getColumnKey().replace(parent.getId() + separator + rowIndex + separator, parent.getId() + separator);
    }

    @Override
    public void renderChildren(FacesContext context) throws IOException {
        encodeChildren(context);
    }

    @Override
    public String getHeaderText() {
        String headerText = super.getHeaderText();
        if (headerText == null) {
            String field = getField();
            if (LangUtils.isNotBlank(field)) {
                headerText = LangUtils.toCapitalCase(field);
            }
        }
        return headerText;
    }

    public List<DynamicColumn> getDynamicColumns() {
        if (dynamicColumns == null) {
            FacesContext context = getFacesContext();
            setRowIndex(-1);
            dynamicColumns = new ArrayList<>(getRowCount());

            for (int i = 0; i < getRowCount(); i++) {
                DynamicColumn dynaColumn = new DynamicColumn(i, this, context);
                dynamicColumns.add(dynaColumn);
            }
        }

        return dynamicColumns;
    }

    public void setDynamicColumns(List<DynamicColumn> dynamicColumns) {
        this.dynamicColumns = dynamicColumns;
    }

    @Override
    public Object saveState(FacesContext context) {
        dynamicColumns = null;
        cellEditor = null;

        return super.saveState(context);
    }
}
