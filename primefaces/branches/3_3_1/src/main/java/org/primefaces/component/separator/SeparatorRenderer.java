/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.separator;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class SeparatorRenderer extends CoreRenderer{

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		Separator separator = (Separator) component;
		ResponseWriter writer = context.getResponseWriter();
		String styleClass = separator.getStyleClass();
        styleClass = styleClass == null ? Separator.DEFAULT_STYLE_CLASS : Separator.DEFAULT_STYLE_CLASS + " " + styleClass;
		
		writer.startElement("hr", separator);
		writer.writeAttribute("id", separator.getClientId(context), "id");
		writer.writeAttribute("class", styleClass, "styleClass");
		
		if(separator.getTitle() != null) writer.writeAttribute("title", separator.getTitle(), "title");
		if(separator.getStyle() != null) writer.writeAttribute("style", separator.getStyle(), "style");
        
        writer.endElement("hr");
	}
}
