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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.DragDropEvent;
import org.primefaces.renderkit.CoreRenderer;

public class DroppableRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		Droppable droppable = (Droppable) component;
		String dropId = droppable.getParent().getClientId(facesContext);
		
		if(params.containsKey(dropId)) {
			String dragId = params.get("dragId");
			
			droppable.queueEvent(new DragDropEvent(droppable, dragId, dropId));
		}	
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Droppable droppable = (Droppable) component;
		String draggableVar = createUniqueWidgetVar(facesContext, droppable);
		String parentClientId = droppable.getParent().getClientId(facesContext);

		writer.startElement("script", droppable);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(draggableVar + " = new PrimeFaces.widget.Droppable('"+ parentClientId + "');\n");
		
		writer.endElement("script");
	}
}