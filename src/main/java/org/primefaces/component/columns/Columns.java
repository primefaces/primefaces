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
package org.primefaces.component.columns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.celleditor.CellEditor;


public class Columns extends ColumnsBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Columns";
    private CellEditor cellEditor = null;
    private List<DynamicColumn> dynamicColumns;

    @Override
    public String getSelectionMode() {
        return null;
    }

    @Override
    public CellEditor getCellEditor() {
        if (cellEditor == null) {
            for (UIComponent child : getChildren()) {
                if (child instanceof CellEditor) {
                    cellEditor = (CellEditor) child;
                }
            }
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
    public void renderChildren(FacesContext context) throws IOException {
        encodeChildren(context);
    }

    public List<DynamicColumn> getDynamicColumns() {
        if (dynamicColumns == null) {
            FacesContext context = getFacesContext();
            setRowIndex(-1);
            char separator = UINamingContainer.getSeparatorChar(context);
            dynamicColumns = new ArrayList<>();
            String clientId = getClientId(context);

            for (int i = 0; i < getRowCount(); i++) {
                DynamicColumn dynaColumn = new DynamicColumn(i, this);
                dynaColumn.setColumnKey(clientId + separator + i);

                dynamicColumns.add(dynaColumn);
            }
        }

        return dynamicColumns;
    }

    public void setDynamicColumns(List<DynamicColumn> dynamicColumns) {
        this.dynamicColumns = dynamicColumns;
    }

}