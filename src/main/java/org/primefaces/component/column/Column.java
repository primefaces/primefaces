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
package org.primefaces.component.column;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.celleditor.CellEditor;


public class Column extends ColumnBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Column";

    @Override
    public CellEditor getCellEditor() {

        CellEditor cellEditor = null;

        for (UIComponent child : getChildren()) {
            if (child instanceof CellEditor && ((CellEditor) child).isRendered()) {
                cellEditor = (CellEditor) child;
                break;
            }
        }

        return cellEditor;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public String getColumnKey() {
        return getClientId();
    }

    @Override
    public List getElements() {
        return getChildren();
    }

    @Override
    public int getElementsCount() {
        return getChildCount();
    }

    @Override
    public void renderChildren(FacesContext context) throws IOException {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                UIComponent child = getChildren().get(i);
                child.encodeAll(context);
            }
        }
    }
}