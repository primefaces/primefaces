/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.touch.component.rowgroup;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.touch.component.rowgroup.RowGroup;

public class RowGroupRenderer extends CoreRenderer {
	
	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		RowGroup rowGroup = (RowGroup) component;
		String display = rowGroup.getDisplay();
		
		writer.startElement("span", null);
		writer.writeAttribute("id", rowGroup.getClientId(facesContext), "id");
		
		if(!display.equalsIgnoreCase("edgetoedge") && rowGroup.getTitle() != null) {
			writer.startElement("h2", null);
			writer.write(rowGroup.getTitle());
			writer.endElement("h2");
		}
		
		writer.startElement("ul", rowGroup);
		writer.writeAttribute("class", display, null);
		
		if(display.equalsIgnoreCase("edgetoedge") && rowGroup.getTitle() != null) {
			writer.startElement("li", component);
			writer.writeAttribute("class", "sep", null);
			writer.write(rowGroup.getTitle());
			writer.endElement("li");
		}
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.endElement("ul");
		
		writer.endElement("span");
	}
}
