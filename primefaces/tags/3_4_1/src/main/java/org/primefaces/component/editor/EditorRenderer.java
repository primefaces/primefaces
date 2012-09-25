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
package org.primefaces.component.editor;

import java.io.IOException;
import java.util.Map;
import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class EditorRenderer extends CoreRenderer{

    @Override
	public void decode(FacesContext context, UIComponent component) {
		Editor editor = (Editor) component;
        String inputParam = editor.getClientId(context) + "_input";
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(inputParam);
        
        if(value != null && value.equals("<br/>")) {
            value = "";
        }
        
        editor.setSubmittedValue(value);
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Editor editor = (Editor) component;

		encodeMarkup(facesContext, editor);
		encodeScript(facesContext, editor);
	}

	protected void encodeMarkup(FacesContext context, Editor editor) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = editor.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, editor);
        String inputId = clientId + "_input";

        String style = editor.getStyle();
        style = style == null ? "visibility:hidden" : "visibility:hidden;" + style;

        writer.startElement("div", editor);
        writer.writeAttribute("id", clientId , null);
        writer.writeAttribute("style", style, null);
        if(editor.getStyleClass() != null) {
            writer.writeAttribute("class", editor.getStyleClass(), null);
        }
        
		writer.startElement("textarea", null);
		writer.writeAttribute("id", inputId , null);
        writer.writeAttribute("name", inputId , null);

        if(valueToRender != null) {
            writer.write(valueToRender);
        }

		writer.endElement("textarea");

        writer.endElement("div");
	}
	
	private void encodeScript(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
		String widgetVar = editor.resolveWidgetVar();
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        writer.write("PrimeFaces.cw('Editor','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");

        if(editor.isDisabled()) writer.write(",disabled:true");
        if(!editor.isValid()) writer.write(",invalid:true");
        if(editor.getControls() != null) writer.write(",controls:'" + editor.getControls() + "'");
        if(editor.getWidth() != Integer.MIN_VALUE) writer.write(",width:" + editor.getWidth());
        if(editor.getHeight() != Integer.MIN_VALUE) writer.write(",height:" + editor.getHeight());
        if(editor.getOnchange() != null) writer.write(",change:function(e){" + editor.getOnchange() + "}");

		writer.write("},'editor');});");
		
		endScript(writer);
	}
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		Editor editor = (Editor) component;
		String value = (String) submittedValue;
		Converter converter = editor.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, editor, value);
		}
		//Try to guess
		else {
            ValueExpression ve = editor.getValueExpression("value");
            
            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, editor, value);
                }
            }
		}

		return value;
	}
}