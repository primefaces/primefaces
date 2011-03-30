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
        String inputParam = editor.getClientId(context) + "_input";
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        if(params.containsKey(inputParam)) {
            editor.setSubmittedValue(params.get(inputParam));
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
        String inputId = clientId + "_input";

        writer.startElement("div", editor);
        writer.writeAttribute("id", clientId , null);
        if(editor.getStyle() != null) writer.writeAttribute("style", editor.getStyle(), null);
        if(editor.getStyleClass() != null) writer.writeAttribute("class", editor.getStyleClass(), null);
        
		writer.startElement("textarea", null);
		writer.writeAttribute("id", inputId , null);
        writer.writeAttribute("name", inputId , null);

        if(value != null) {
            writer.write(value);
        }

		writer.endElement("textarea");

        writer.endElement("div");
	}
	
	private void encodeScript(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
		String widgetVar = editor.resolveWidgetVar();
		
		writer.startElement("script", editor);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");
		
		writer.write(widgetVar + " = new PrimeFaces.widget.Editor('" + clientId + "',{");

        writer.write("lazy:" + editor.isLazy());

        if(editor.getControls() != null) writer.write(",controls:'" + editor.getControls() + "'");
        if(editor.getWidth() != Integer.MIN_VALUE) writer.write(",width:" + editor.getWidth());
        if(editor.getHeight() != Integer.MIN_VALUE) writer.write(",height:" + editor.getHeight());
        if(editor.isDisabled()) writer.write(",disabled:true");

		writer.write("});});");
		
		writer.endElement("script");
	}
}