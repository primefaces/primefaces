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
package org.primefaces.component.column.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.util.ComponentUtils;

public class ColumnGroupHelperRenderer implements HelperColumnRenderer {

    @Override
    public void encode(FacesContext context, Column column) throws IOException {
        ColumnGroup group = findGroup(column);

        if (group.getParent() instanceof DataTable) {
            encodeDataTable(context, (DataTable) group.getParent(), column, group);
        }
        else if (group.getParent() instanceof TreeTable) {
            encodeTreeTable(context, (TreeTable) group.getParent(), column, group);
        }
        else {
            throw new IllegalArgumentException("ColumnGroupHelperRenderer cannot be used for " + group.getParent().getClass());
        }
    }

    private ColumnGroup findGroup(Column column) {
        UIComponent parent = column.getParent();

        while (!(parent instanceof ColumnGroup)) {
            parent = parent.getParent();
        }

        return (ColumnGroup) parent;
    }

    private void encodeDataTable(FacesContext context, DataTable dataTable, Column column, ColumnGroup group) throws IOException {
        DataTableRenderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                DataTable.COMPONENT_FAMILY,
                DataTable.DEFAULT_RENDERER);

        String type = group.getType();
        if ("header".equals(type) || "frozenHeader".equals(type) || "scrollableHeader".equals(type)) {
            renderer.encodeColumnHeader(context, dataTable, column);
        }
        else if ("footer".equals(type) || "frozenFooter".equals(type) || "scrollableFooter".equals(type)) {
            renderer.encodeColumnFooter(context, dataTable, column);
        }
    }

    private void encodeTreeTable(FacesContext context, TreeTable treeTable, Column column, ColumnGroup group) throws IOException {
        TreeTableRenderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                TreeTable.COMPONENT_FAMILY,
                TreeTable.DEFAULT_RENDERER);

        String type = group.getType();
        if ("header".equals(type)) {
            renderer.encodeColumnHeader(context, treeTable, column);
        }
        else if ("footer".equals(type)) {
            renderer.encodeColumnFooter(context, treeTable, column);
        }
    }
}
