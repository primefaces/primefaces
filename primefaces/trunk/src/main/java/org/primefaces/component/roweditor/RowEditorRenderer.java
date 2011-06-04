/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.roweditor;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.RowEditEvent;
import org.primefaces.renderkit.CoreRenderer;

public class RowEditorRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        RowEditor editor = (RowEditor) component;
        String clientId = editor.getClientId(context);

        //Decode row edit request triggered by this editor
        if(params.containsKey(clientId)) {
            DataTable table = findParentTable(context, editor);

            table.setRowIndex(-1);
            int editedRowId = Integer.parseInt(params.get(table.getClientId(context) + "_editedRowId"));
            table.setRowIndex(editedRowId);
            table.queueEvent(new RowEditEvent(table, table.getRowData()));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", DataTable.ROW_EDITOR_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-pencil", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-check", null);
        writer.writeAttribute("style", "display:none", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-close", null);
        writer.writeAttribute("style", "display:none", null);
        writer.endElement("span");

        writer.endElement("span");
    }

    protected DataTable findParentTable(FacesContext context, RowEditor editor) {
		UIComponent parent = editor.getParent();

		while(parent != null) {
			if(parent instanceof DataTable) {
				return (DataTable) parent;
            }

			parent = parent.getParent();
		}

		return null;
	}
}
