/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.treetable.export;

import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.export.TableExporter;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.model.TreeNode;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.faces.context.FacesContext;

public abstract class TreeTableExporter<P, O extends ExporterOptions> extends TableExporter<TreeTable, P, O> {

    protected TreeTableExporter(O defaultOptions) {
        super(defaultOptions);
    }

    protected TreeTableExporter(O defaultOptions, Set<FacetType> supportedFacetTypes, boolean joinCellComponents) {
        super(defaultOptions, supportedFacetTypes, joinCellComponents);
    }

    @Override
    protected void exportPageOnly(FacesContext context, TreeTable table) {
        TreeNode<?> root = table.getValue();
        if (root == null) {
            return;
        }

        // a page of a TreeTable is a slice of the direct children of the root, each one rendered together with the
        // descendants which are currently displayed - see TreeTableRenderer#encodeNodeChildren
        int first = table.getFirst();
        int last = Math.min(first + table.getRowsToRender(), root.getChildCount());

        for (int i = first; i < last; i++) {
            exportNode(context, table, root.getChildren().get(i), true);
        }
    }

    /**
     * Exports the given node followed by its descendants, in the same order the renderer displays them.
     *
     * @param context the {@link FacesContext}
     * @param table the {@link TreeTable} being exported
     * @param node the node to export
     * @param displayedOnly whether to descend into collapsed nodes, which are not displayed
     */
    protected void exportNode(FacesContext context, TreeTable table, TreeNode<?> node, boolean displayedOnly) {
        exportRow(context, table, node.getData());

        if (displayedOnly && !node.isExpanded()) {
            return;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            exportNode(context, table, node.getChildren().get(i), displayedOnly);
        }
    }

    @Override
    protected void exportAll(FacesContext context, TreeTable table) {
        int first = table.getFirst();
        TreeNode root = table.getValue();
        root.setExpanded(true);
        int rowCount = getTreeRowCount(root) - 1;

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            exportRow(context, table, rowIndex);
        }

        // restore
        table.setFirst(first);
    }

    protected void exportRow(FacesContext context, TreeTable table, int rowIndex) {
        // rowIndex +1 because we are not interested in rootNode
        exportRow(context, table, traverseTree(table.getValue(), rowIndex + 1));
    }

    /**
     * Exports a single row for the given row data.
     *
     * @param context the {@link FacesContext}
     * @param table the {@link TreeTable} being exported
     * @param data the data of the node to export, which the columns are resolved against
     */
    protected void exportRow(FacesContext context, TreeTable table, Object data) {
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        Object origVar = requestMap.get(table.getVar());

        requestMap.put(table.getVar(), data);

        super.addCells(context, table);

        if (origVar != null) {
            requestMap.put(table.getVar(), origVar);
        }
        else {
            requestMap.remove(table.getVar());
        }
    }

    @Override
    protected void exportSelectionOnly(FacesContext context, TreeTable table) {
        Object selection = table.getSelection();
        if (selection == null) {
            return;
        }

        if (selection.getClass().isArray()) {
            int size = Array.getLength(selection);

            for (int i = 0; i < size; i++) {
                exportSelectedRow(context, table, Array.get(selection, i));
            }
        }
        else if (selection instanceof Collection<?> collection) {
            for (Object selected : collection) {
                exportSelectedRow(context, table, selected);
            }
        }
        else {
            exportSelectedRow(context, table, selection);
        }
    }

    protected void exportSelectedRow(FacesContext context, TreeTable table, Object selected) {
        exportRow(context, table, selected instanceof TreeNode<?> node ? node.getData() : selected);
    }

    protected static int getTreeRowCount(TreeNode<?> node) {
        int count = 1;
        if (node.getChildren() != null) {
            for (TreeNode childNode : node.getChildren()) {
                count += getTreeRowCount(childNode);
            }
            return count;
        }
        return count;
    }

    protected static Object traverseTree(TreeNode node, int rowIndex) {
        return traverseTree(node, new AtomicInteger(rowIndex));
    }

    /**
     * Traverses a tree and visits all children until it finds the one with row index i
     *
     * @param node
     * @param rowIndex
     * @return data of found treenode
     */
    protected static Object traverseTree(TreeNode<?> node, AtomicInteger rowIndex) {

        int index = rowIndex.get();
        rowIndex.decrementAndGet();
        if (index <= 0) {
            return node.getData();
        }

        if (node.getChildren() != null) {
            Object data = null;
            for (TreeNode childNode : node.getChildren()) {
                data = traverseTree(childNode, rowIndex);
                if (data != null) {
                    break;
                }
            }
            return data;
        }
        else {
            return null;
        }

    }
}
