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
package org.primefaces.component.dnd;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DraggableRenderer extends CoreRenderer {
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Draggable draggable = (Draggable) component;
		String draggableVar = createUniqueWidgetVar(facesContext, draggable);
		String parentClientId = draggable.getParent().getClientId(facesContext);
		String clientId = draggable.getClientId(facesContext);

		writer.startElement("script", draggable);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(draggableVar + " = new PrimeFaces.widget.Draggable('"+ parentClientId + "',");
		writer.write("{");
		writer.write("proxy:" + draggable.isProxy());
		
		if(draggable.isDragOnly()) {
			writer.write(",dragOnly:true");
		} else {
			UIComponent form = ComponentUtils.findParentForm(facesContext, draggable);
			String formClientId = null;
			
			if(form != null) {
				formClientId = form.getClientId(facesContext);
			}
			else {
				throw new FacesException("Draggable: '" + clientId + "' needs to be enclosed in a form when dropping is enabled");
			}
			
			writer.write(",formId:'" + formClientId + "'");
			
			if(draggable.getUpdate() != null) {
				writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, draggable, draggable.getUpdate()) + "'");
			}
		}
		
		writer.write("});\n");
		
		writer.endElement("script");
	}
}
