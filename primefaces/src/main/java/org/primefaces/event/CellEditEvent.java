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
package org.primefaces.event;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.PrimeUIData;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITree;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.util.CompositeUtils;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.Behavior;

public class CellEditEvent<T> extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Old value of the cell
     */
    private T oldValue;

    /**
     * New value of the cell
     */
    private T newValue;

    /**
     * (Optional) Row Index used only for DataTable to identify row
     */
    private int rowIndex;

    /**
     * (Optional) Row Key used only for TreeTable to identify TreeNode
     */
    private String rowKey;

    /**
     * Column of the cell being edited
     */
    private UIColumn column;

    /**
     * Data contained in the DataTable row or TreeTable node.
     */
    private Object rowData;

    public CellEditEvent(UIComponent component, Behavior behavior, int rowIndex, UIColumn column) {
        super(component, behavior);
        this.rowIndex = rowIndex;
        this.column = column;
        this.oldValue = resolveValue();
    }

    public CellEditEvent(UIComponent component, Behavior behavior, int rowIndex, UIColumn column, String rowKey) {
        this(component, behavior, rowIndex, column);
        this.rowKey = rowKey;
    }

    public CellEditEvent(UIComponent component, Behavior behavior, UIColumn column, String rowKey) {
        super(component, behavior);
        this.rowKey = rowKey;
        this.column = column;
        this.oldValue = resolveValue();
    }

    public T getOldValue() {
        return this.oldValue;
    }

    public T getNewValue() {
        if (newValue == null) {
            newValue = resolveValue();
        }
        return newValue;
    }

    public Object getRowData() {
        if (rowData == null) {
            rowData = resolveRowData();
        }
        return rowData;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public UIColumn getColumn() {
        return column;
    }

    public String getRowKey() {
        return rowKey;
    }

    private Object resolveRowData() {
        if (source instanceof PrimeUIData) {
            DataTable data = (DataTable) source;
            data.setRowModel(rowIndex);
            return data.getRowData();
        }
        else if (source instanceof UITree) {
            TreeTable data = (TreeTable) source;
            data.setRowKey(data.getValue(), rowKey);
            return data.getRowNode();
        }
        return null;
    }

    private T resolveValue() {
        if (source instanceof PrimeUIData) {
            DataTable data = (DataTable) source;
            data.setRowModel(rowIndex);
        }
        else if (source instanceof UITree) {
            TreeTable data = (TreeTable) source;
            data.setRowKey(data.getValue(), rowKey);
        }

        if (column.isDynamic()) {
            DynamicColumn dynamicColumn = (DynamicColumn) column;
            dynamicColumn.applyStatelessModel();
        }

        T value = null;

        for (UIComponent child : column.getChildren()) {
            if (child instanceof CellEditor) {
                UIComponent inputFacet = child.getFacet("input");

                List<Object> values = new ArrayList<>(1);

                CompositeUtils.invokeOnDeepestEditableValueHolder(getFacesContext(), inputFacet, (ctx, component) -> {
                    values.add(((EditableValueHolder) component).getValue());
                });

                if (values.isEmpty()) {
                    throw new FacesException("No ValueHolder found inside the 'input' facet of the CellEditor!");
                }

                value = values.size() > 1 ? (T) values : (T) values.get(0);
            }
        }

        return value;
    }
}
