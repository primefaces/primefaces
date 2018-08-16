/*
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

import org.primefaces.component.api.UIData;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIData;
import org.primefaces.component.celleditor.CellEditor;

@ResourceDependencies({

})
public class Columns extends ColumnsBase implements org.primefaces.component.api.UIColumn {



    public String getSelectionMode() {
        return null;
    }

    private CellEditor cellEditor = null;

    public CellEditor getCellEditor() {
        if(cellEditor == null) {
            for(UIComponent child : getChildren()) {
                if(child instanceof CellEditor)
                    cellEditor = (CellEditor) child;
            }
        }

        return cellEditor;
    }

    public boolean isDynamic() {
        return true;
    }

    public java.lang.String getColumnIndexVar() {
		return super.getRowIndexVar();
	}
	public void setColumnIndexVar(String _columnIndexVar) {
		super.setRowIndexVar(_columnIndexVar);
	}

    public String getColumnKey() {
        return this.getClientId();
    }

    public void renderChildren(FacesContext context) throws IOException {
        this.encodeChildren(context);
    }

    private List<DynamicColumn> dynamicColumns;

    public List<DynamicColumn> getDynamicColumns() {
        if(dynamicColumns == null) {
            FacesContext context = this.getFacesContext();
            this.setRowIndex(-1);
            char separator = UINamingContainer.getSeparatorChar(context);
            dynamicColumns = new ArrayList<DynamicColumn>();
            String clientId = this.getClientId(context);

            for(int i=0; i < this.getRowCount(); i++) {
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