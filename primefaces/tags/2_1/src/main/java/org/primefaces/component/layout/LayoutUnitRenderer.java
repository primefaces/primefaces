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
package org.primefaces.component.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class LayoutUnitRenderer extends CoreRenderer {

	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		LayoutUnit unit = (LayoutUnit) component;
		
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(facesContext), "id");
		if(unit.getStyle() != null) writer.writeAttribute("style", unit.getStyle(), "style");
		if(unit.getStyleClass() != null) writer.writeAttribute("class", unit.getStyleClass(), "styleClass");
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.endElement("div");
	}
}