/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.component.api;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;

public interface ColumnAware {

    int getChildCount();

    List<UIComponent> getChildren();

    static List<List<ColumnNode>> to2DArray(UIComponent root, int columnStart, int columnEnd) {
        List<List<ColumnNode>> matrix = new ArrayList<>();
        ColumnNode rootNode = ColumnNode.root(root);
        to2DArray(rootNode, matrix, columnStart, columnEnd);
        return matrix;
    }

    static void to2DArray(ColumnNode root, List<List<ColumnNode>> nodes, int columnStart, int columnEnd) {
        int idx = -1;
        for (int i = 0; i < root.getChildren().size(); i++) {
            UIComponent child = root.getChildren().get(i);
            if (ComponentUtils.isTargetableComponent(child)) {
                idx++;

                int level = root.getLevel() + 1;
                if (level == 1 && idx < columnStart) { // frozen col start
                    continue;
                }

                if (level == 1 && idx >= columnEnd) { // frozen col end
                    break;
                }

                if (nodes.size() < level) {
                    nodes.add(new ArrayList<>());
                }

                List<ColumnNode> row = nodes.get(root.getLevel());

                if (child instanceof Columns) {
                    Columns columns = (Columns) child;
                    columns.forEachColumn(false, UIColumn::isRendered, (col, pos) -> row.add(new ColumnNode(root, col)));
                }
                else if (child.isRendered() && (child instanceof Column || child instanceof ColumnGroup)) {
                    ColumnNode node = new ColumnNode(root, child);
                    row.add(node);
                    if (child instanceof ColumnGroup) {
                        to2DArray(node, nodes, columnStart, columnEnd);
                    }
                }
                else if (child.isRendered() && child instanceof SubTable) {
                    ColumnNode node = ColumnNode.root(child);
                    to2DArray(node, nodes, columnStart, columnEnd);
                }
            }
        }
    }

    default boolean hasFooterColumn() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered()) {
                if ((child instanceof UIColumn)) {
                    UIColumn column = (UIColumn) child;

                    if (column.getFooterText() != null || FacetUtils.shouldRenderFacet(column.getFacet("footer"))) {
                        return true;
                    }
                }
                else if (child instanceof ColumnGroup) {
                    if (((ColumnGroup) child).hasFooterColumn()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    default List<UIColumn> getColumns() {
        return ForEachRowColumn.collectColumns((UIComponent) this);
    }

    default int getColumnsCount() {
        return ForEachRowColumn.countColumns((UIComponent) this);
    }
}
