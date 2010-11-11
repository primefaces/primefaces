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
package org.primefaces.component.editor;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class EditorRenderer extends CoreRenderer{

    @Override
	public void decode(FacesContext context, UIComponent component) {
		Editor editor = (Editor) component;
        String clientId = editor.getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        if(params.containsKey(clientId)) {
            editor.setSubmittedValue(params.get(clientId));
        }
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Editor editor = (Editor) component;

		encodeMarkup(facesContext, editor);
		encodeScript(facesContext, editor);
	}

	protected void encodeMarkup(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
        String value = (String) editor.getValue();
        
		writer.startElement("textarea", editor);
		writer.writeAttribute("id", clientId , null);
        writer.writeAttribute("name", clientId , null);

        if(value != null) {
            writer.write(value);
        }

		writer.endElement("textarea");
	}
	
	private void encodeScript(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
		String widgetVar = editor.resolveWidgetVar();
		
		writer.startElement("script", editor);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function() {");
		
		writer.write(widgetVar + " = new PrimeFaces.widget.Editor('" + clientId + "',{");

		writer.write("});});");
		
		writer.endElement("script");
	}
}