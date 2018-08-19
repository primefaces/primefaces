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
package org.primefaces.component.celleditor;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;


public class CellEditor extends CellEditorBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.CellEditor";
    private UIComponent parentTable = null;

    @Override
    public void processDecodes(FacesContext context) {
        if (isEditRequest(context)) {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (isEditRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (isEditRequest(context)) {
            super.processUpdates(context);
        }
    }

    public boolean isEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context));
    }

    public UIComponent getParentTable(FacesContext context) {
        if (parentTable == null) {
            UIComponent parent = getParent();

            while (parent != null) {
                if (parent instanceof DataTable || parent instanceof TreeTable) {
                    parentTable = parent;
                    break;
                }

                parent = parent.getParent();
            }
        }

        return parentTable;
    }

}