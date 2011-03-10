/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.mobile.component.listview;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

/**
 *
 * @author jagatai
 */
public class ListViewRenderer extends CoreRenderer{

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ListView listView = (ListView) component;
        int rowCount = listView.getRowCount();

        writer.startElement("ul", listView);
        writer.writeAttribute("id", listView.getClientId(context), "id");
        writer.writeAttribute("data-role", "listview", null);

        if(listView.isInset()) writer.writeAttribute("data-inset", true, null);
        if(listView.getSwatch() != null) writer.writeAttribute("data-theme", listView.getSwatch(), null);
        if(listView.getStyle() != null) writer.writeAttribute("style", listView.getStyle(), null);
        if(listView.getStyleClass() != null) writer.writeAttribute("class", listView.getStyleClass(), null);

        if(listView.getVar() != null) {
            for(int i = 0; i < rowCount; i++) {
                listView.setRowIndex(i);

                writer.startElement("li", listView);
                renderChildren(context, listView);
                writer.endElement("li");
            }
        }
        else {
            for(UIComponent child : listView.getChildren()) {
                if(child.isRendered()) {
                    writer.startElement("li", listView);
                    child.encodeAll(context);
                    writer.endElement("li");
                }
            }
        }
        writer.endElement("ul");

        listView.setRowIndex(-1);
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
