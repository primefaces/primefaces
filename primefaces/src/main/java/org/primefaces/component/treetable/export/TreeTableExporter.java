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
        int first = table.getFirst();
        int rows = table.getRows();

        TreeNode root = table.getValue();
        root.setExpanded(true);
        int totalRows = getTreeRowCount(root) - 1;
        if (rows == 0) {
            rows = totalRows;
        }

        int rowsToExport = first + rows;
        if (rowsToExport > totalRows) {
            rowsToExport = totalRows;
        }

        for (int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {
            exportRow(context, table, rowIndex);
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
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        Object origVar = requestMap.get(table.getVar());

        // rowIndex +1 because we are not interested in rootNode
        Object data = traverseTree(table.getValue(), rowIndex + 1);

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
        String var = table.getVar();

        if (selection != null) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (selection.getClass().isArray()) {
                int size = Array.getLength(selection);

                for (int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    exportRow(context, table, -1);
                }
            }
            else if (Collection.class.isAssignableFrom(selection.getClass())) {
                for (Object obj : (Collection) selection) {
                    if (obj instanceof TreeNode) {
                        TreeNode node = (TreeNode) obj;
                        requestMap.put(var, node.getData());
                    }
                    else {
                        requestMap.put(var, obj);
                    }
                    exportRow(context, table, -1);
                }
            }
            else {
                requestMap.put(var, selection);
                exportRow(context, table, -1);
            }
        }
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
