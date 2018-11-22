/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.event;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.ValueHolder;
import javax.faces.component.behavior.Behavior;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UIData;
import org.primefaces.component.api.UITree;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;

public class CellEditEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private Object oldValue;

    private Object newValue;

    private int rowIndex;

    private UIColumn column;

    private String rowKey;

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

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getNewValue() {
        if (newValue == null) {
            newValue = resolveValue();
        }
        return newValue;
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

    private Object resolveValue() {
        if (source instanceof UIData) {
            DataTable data = (DataTable) source;
            data.setRowModel(rowIndex);
        }
        else if (source instanceof UITree) {
            TreeTable data = (TreeTable) source;
            data.setRowKey(rowKey);
        }

        Object value = null;

        for (UIComponent child : column.getChildren()) {
            if (child instanceof CellEditor) {
                UIComponent inputFacet = child.getFacet("input");

                //multiple
                if (inputFacet instanceof UIPanel) {
                    List<Object> values = new ArrayList<>();
                    for (UIComponent kid : inputFacet.getChildren()) {
                        if (kid instanceof ValueHolder) {
                            values.add(((ValueHolder) kid).getValue());
                        }
                    }

                    value = values;
                }
//single
                else {
                    value = ((ValueHolder) inputFacet).getValue();
                }

            }
        }

        return value;
    }
}
