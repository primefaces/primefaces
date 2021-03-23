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
package org.primefaces.component.treetable.export;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UISelectMany;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableBase;
import org.primefaces.model.TreeNode;
import org.primefaces.util.Constants;

public abstract class TreeTableExporter implements Exporter<TreeTable> {

    private OutputStream outputStream;

    protected enum ColumnType {
        HEADER("header"),
        FOOTER("footer");

        private final String facet;

        ColumnType(String facet) {
            this.facet = facet;
        }

        public String facet() {
            return facet;
        }

        @Override
        public String toString() {
            return facet;
        }
    }

    protected List<UIColumn> getColumnsToExport(UIData table) {
        return table.getChildren().stream()
                .filter(UIColumn.class::isInstance)
                .map(UIColumn.class::cast)
                .collect(Collectors.toList());
    }

    protected boolean hasColumnFooter(List<UIColumn> columns) {
        return columns.stream().anyMatch(c -> c.getFooter() != null);
    }

    protected String exportColumnByFunction(FacesContext context, org.primefaces.component.api.UIColumn column) {
        MethodExpression exportFunction = column.getExportFunction();

        if (exportFunction != null) {
            return (String) exportFunction.invoke(context.getELContext(), new Object[]{column});
        }

        return Constants.EMPTY_STRING;
    }

    public String exportValue(FacesContext context, UIComponent component) {

        if (component instanceof HtmlCommandLink) {  //support for PrimeFaces and standard HtmlCommandLink
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) {
                return String.valueOf(value);
            }
            else {
                //export first value holder
                for (UIComponent child : link.getChildren()) {
                    if (child instanceof ValueHolder) {
                        return exportValue(context, child);
                    }
                }

                return Constants.EMPTY_STRING;
            }
        }
        else if (component instanceof ValueHolder) {
            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();
            if (value == null) {
                return Constants.EMPTY_STRING;
            }

            Converter converter = valueHolder.getConverter();
            if (converter == null) {
                Class valueType = value.getClass();
                converter = context.getApplication().createConverter(valueType);
            }

            if (converter != null) {
                if (component instanceof UISelectMany) {
                    StringBuilder builder = new StringBuilder();
                    List collection = null;

                    if (value instanceof List) {
                        collection = (List) value;
                    }
                    else if (value.getClass().isArray()) {
                        collection = Arrays.asList(value);
                    }
                    else {
                        throw new FacesException("Value of " + component.getClientId(context) + " must be a List or an Array.");
                    }

                    int collectionSize = collection.size();
                    for (int i = 0; i < collectionSize; i++) {
                        Object object = collection.get(i);
                        builder.append(converter.getAsString(context, component, object));

                        if (i < (collectionSize - 1)) {
                            builder.append(",");
                        }
                    }

                    String valuesAsString = builder.toString();
                    builder.setLength(0);

                    return valuesAsString;
                }
                else {
                    return converter.getAsString(context, component, value);
                }
            }
            else {
                return value.toString();
            }
        }
        else if (component instanceof CellEditor) {
            return exportValue(context, component.getFacet("output"));
        }
        else if (component instanceof HtmlGraphicImage) {
            return (String) component.getAttributes().get("alt");
        }
        else if (component instanceof OverlayPanel) {
            return Constants.EMPTY_STRING;
        }
        else {
            //This would get the plain texts on UIInstructions when using Facelets
            String value = component.toString();

            if (value != null) {
                return value.trim();
            }
            else {
                return Constants.EMPTY_STRING;
            }
        }
    }

    protected void exportPageOnly(FacesContext context, TreeTable table, Object document) {
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
            exportRow(context, table, document, rowIndex);
        }
    }

    protected void exportAll(FacesContext context, TreeTable table, Object document) {
        int first = table.getFirst();
        TreeNode root = table.getValue();
        root.setExpanded(true);
        int rowCount = getTreeRowCount(root) - 1;

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            exportRow(context, table, document, rowIndex);
        }

        // restore
        table.setFirst(first);
    }

    protected void exportRow(FacesContext context, TreeTable table, Object document, int rowIndex) {

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        Object origVar = requestMap.get(table.getVar());

        // rowIndex +1 because we are not interested in rootNode
        Object data = traverseTree(table.getValue(), rowIndex + 1);

        requestMap.put(table.getVar(), data);

        preRowExport(table, document);
        exportCells(table, document);
        postRowExport(table, document);

        if (origVar != null) {
            requestMap.put(table.getVar(), origVar);
        }
        else {
            requestMap.remove(table.getVar());
        }
    }

    protected void exportRow(TreeTable table, Object document) {
        preRowExport(table, document);
        exportCells(table, document);
        postRowExport(table, document);
    }

    protected void exportSelectionOnly(FacesContext context, TreeTable table, Object document) {
        Object selection = table.getSelection();
        String var = table.getVar();

        if (selection != null) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (selection.getClass().isArray()) {
                int size = Array.getLength(selection);

                for (int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    exportRow(table, document);
                }
            }
            else if (Collection.class.isAssignableFrom(selection.getClass())) {
                for (Object obj : (Collection) selection) {
                    requestMap.put(var, obj);
                    exportRow(table, document);
                }
            }
            else {
                requestMap.put(var, selection);
                exportCells(table, document);
            }
        }
    }

    protected void preExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        // NOOP
    }

    protected void postExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        // NOOP
    }

    protected void preRowExport(TreeTable table, Object document) {
        // NOOP
    }

    protected void postRowExport(TreeTable table, Object document) {
        // NOOP
    }

    protected abstract void exportCells(TreeTable table, Object document);

    @Override
    public void export(FacesContext context, List<TreeTable> tables, OutputStream outputStream, ExportConfiguration exportConfiguration) throws IOException {
        this.outputStream = outputStream;

        preExport(context, exportConfiguration);

        int index = 0;
        for (TreeTable table : tables) {
            TreeTableVisitCallBack visitCallback = new TreeTableVisitCallBack(table, exportConfiguration, index);
            int nbTables = visitCallback.invoke(context);
            index += nbTables;
        }

        postExport(context, exportConfiguration);

        this.outputStream = null;
    }

    /**
     * Export TreeTable
     * @param facesContext faces context
     * @param table TreeTable to export
     * @param exportConfiguration export configuration
     * @param index TreeTable current index during export process
     * @throws IOException
     */
    protected abstract void doExport(FacesContext facesContext, TreeTable table, ExportConfiguration exportConfiguration, int index) throws IOException;

    private class TreeTableVisitCallBack implements VisitCallback {

        private ExportConfiguration config;

        private TreeTable target;

        private int index;

        private int counter;

        public TreeTableVisitCallBack(TreeTable target, ExportConfiguration config, int index) {
            this.target = target;
            this.config = config;
            this.index = index;
        }

        @Override
        public VisitResult visit(VisitContext context, UIComponent component) {
            if (target == component) {
                try {
                    doExport(context.getFacesContext(), target, config, index);
                    index++;
                    counter++;
                }
                catch (IOException e) {
                    throw new FacesException(e);
                }
            }
            return VisitResult.ACCEPT;
        }

        /**
         * Returns number of tables exported
         * @param context faces context
         * @return number of tables exported
         */
        public int invoke(FacesContext context) {
            invokeOnClosestTreeTableParent(target, p -> {
                VisitContext visitContext = VisitContext.createVisitContext(context);
                p.visitTree(visitContext, this);
            }, true);

            return counter;
        }

        public boolean invokeOnClosestTreeTableParent(UIComponent component, Consumer<UIComponent> function, boolean includeSelf) {
            Predicate<UIComponent> isIteratorComponent = p -> p instanceof TreeTableBase;

            UIComponent parent = component;
            while (null != (parent = parent.getParent())) {
                if (isIteratorComponent.test(parent)) {
                    function.accept(parent);
                    return true;
                }
            }

            if (includeSelf && isIteratorComponent.test(component)) {
                function.accept(component);
                return true;
            }

            return false;
        }
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }

    protected static int getTreeRowCount(TreeNode node) {
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
        return traverseTree(node, new MutableInt(rowIndex));
    }

    /**
     * Traverses a tree and visitis all children until it finds the one with row index i
     *
     * @param node
     * @param rowIndex
     * @return data of found treenode
     */
    protected static Object traverseTree(TreeNode node, MutableInt rowIndex) {

        int index = rowIndex.getValue();
        rowIndex.decrement();
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

    private static class MutableInt {

        private int value;

        public MutableInt(int value) {
            super();
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public void decrement() {
            value--;
        }
    }

}
