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
package org.primefaces.component.column.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.util.ComponentUtils;

public class ColumnGroupHelperRenderer implements HelperColumnRenderer {

    @Override
    public void encode(FacesContext context, Column column) throws IOException {
        ColumnGroup group = findGroup(column);
        DataTable table = (DataTable) group.getParent();
        String type = group.getType();
        DataTableRenderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                DataTable.COMPONENT_FAMILY,
                DataTable.DEFAULT_RENDERER);

        if (type.equals("header") || type.equals("frozenHeader") || type.equals("scrollableHeader")) {
            renderer.encodeColumnHeader(context, table, column);
        }
        else if (type.equals("footer") || type.equals("frozenFooter") || type.equals("scrollableFooter")) {
            renderer.encodeColumnFooter(context, table, column);
        }
    }

    private ColumnGroup findGroup(Column column) {
        UIComponent parent = column.getParent();

        while (!(parent instanceof ColumnGroup)) {
            parent = parent.getParent();
        }

        return (ColumnGroup) parent;
    }

}
