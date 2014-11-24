/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.visit.ResetInputVisitCallback;

public class RowEditFeature implements DataTableFeature {

    public void decode(FacesContext context, DataTable table) {
        throw new RuntimeException("RowEditFeature should not encode.");
    }

    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        if (table.isSelectionEnabled()) {
            table.findSelectedRowKeys();
        }
                
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        int editedRowId = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
        String action = params.get(clientId + "_rowEditAction");
        table.setRowIndex(editedRowId);

        if (action.equals("cancel")) {
            VisitContext visitContext = null;

            for (UIColumn column : table.getColumns()) {
                for (UIComponent grandkid : column.getChildren()) {
                    if (grandkid instanceof CellEditor) {
                        UIComponent inputFacet = grandkid.getFacet("input");

                        if (inputFacet instanceof EditableValueHolder) {
                            ((EditableValueHolder) inputFacet).resetValue();
                        }
                        else {
                            if (visitContext == null) {
                                visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                            }
                            inputFacet.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
                        }
                    }
                }
            }
        }

        if (table.isRowAvailable()) {                    
            renderer.encodeRow(context, table, clientId, editedRowId);
        }
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_rowEditAction");
    }
}
