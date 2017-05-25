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
package org.primefaces.component.celleditor;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;

public class CellEditorRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        CellEditor editor = (CellEditor) component;
        UIComponent parentTable = editor.getParentTable(context);
        boolean isDataTable = (parentTable != null && parentTable instanceof DataTable);
        boolean isLazyCellEdit = false;
        
        if(isDataTable) {
            DataTable dt = (DataTable)parentTable;
            String editMode = dt.getEditMode();
            String cellEditMode = dt.getCellEditMode();
            isLazyCellEdit = (editMode != null && editMode.equals("cell") && cellEditMode.equals("lazy"));
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", DataTable.CELL_EDITOR_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.CELL_EDITOR_OUTPUT_CLASS, null);
        editor.getFacet("output").encodeAll(context);
        writer.endElement("div");

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.CELL_EDITOR_INPUT_CLASS, null);  
        
        if(!isLazyCellEdit) {
            editor.getFacet("input").encodeAll(context);
        }
        writer.endElement("div");

        writer.endElement("div");
    }

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}

}
