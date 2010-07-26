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
package org.primefaces.component.editor;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class EditorRenderer extends CoreRenderer{

	public void decode(FacesContext facesContext, UIComponent component) {
		Editor editor = (Editor) component;
		String paramKey = getInputId(editor.getClientId(facesContext));
		
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(paramKey);
		editor.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Editor editor = (Editor) component;

		encodeMarkup(facesContext, editor);
		encodeScript(facesContext, editor);
	}

	private void encodeMarkup(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		String clientId = editor.getClientId(facesContext);
		String inputId = getInputId(clientId);
		String value = ComponentUtils.getStringValueToRender(facesContext, editor);
		
		writer.startElement("div", editor);
		writer.writeAttribute("id", clientId , null);
		
		writer.startElement("textarea", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		if(value != null)
			writer.write(value);

		writer.endElement("textarea");

		if(editor.isResizable())
			encodeSizeStateHolder(facesContext, editor, clientId);

		writer.endElement("div");
	}
	
	private void encodeSizeStateHolder(FacesContext facesContext, Editor editor, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String size = editor.getWidth() + "," + editor.getHeight();

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", getSizeStateHolderId(clientId), null);
		writer.writeAttribute("name", getSizeStateHolderId(clientId), null);
		writer.writeAttribute("value", size, null);
		writer.endElement("input");
	}
	
	private void restoreSize(FacesContext facesContext, Editor editor, String clientId) {
		String newSize = facesContext.getExternalContext().getRequestParameterMap().get(getSizeStateHolderId(clientId));
		
		if(newSize != null) {
			String boundaries[] = newSize.split(",");
			
			editor.setWidth(boundaries[0].trim());
			editor.setHeight(boundaries[1].trim());
		}
	}

	private void encodeScript(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
		String editorVariable = createUniqueWidgetVar(facesContext, editor);
		restoreSize(facesContext, editor, clientId);
		
		writer.startElement("script", editor);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(editorVariable + " = new PrimeFaces.widget.Editor('" + getInputId(clientId) + "',{");
		writer.write("width:'" + editor.getWidth() + "'");
		writer.write(",height:'" + editor.getHeight() + "'");
		writer.write(",handleSubmit: true");
		
		if(editor.isResizable()) {
			writer.write(",resizable: true");
			writer.write(",widthHeightController: '" + getSizeStateHolderId(clientId) + "'");
			writer.write(",dompath: true");
		}
		
		if(editor.isDisabled()) writer.write(",disabled:true");
		if(editor.getLanguage() != null) writer.write(",language:'" + editor.getLanguage() + "'");
		if(editor.getTitle() != null) writer.write(",title:'" + editor.getTitle() + "'");
		
		writer.write("});\n");
		
		writer.write(editorVariable + ".render();\n");
		
		writer.endElement("script");
	}
	
	private String getSizeStateHolderId(String clientId) {
		return clientId.replaceAll(":", "_") + "_size";
	}
	
	private String getInputId(String clientId) {
		return clientId + ":input";
	}
}