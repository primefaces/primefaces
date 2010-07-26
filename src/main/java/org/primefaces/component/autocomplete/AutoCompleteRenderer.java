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
package org.primefaces.component.autocomplete;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletResponse;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class AutoCompleteRenderer extends CoreRenderer implements PartialRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		AutoComplete autoComplete = (AutoComplete) component;
		String clientId = autoComplete.getClientId(facesContext);
		String paramKey = autoComplete.getVar()  == null ? clientId + "_input" : clientId + "_hinput";
		
		String submittedValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(paramKey);
		autoComplete.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		AutoComplete autoComplete = (AutoComplete) component;
		
		encodeScript(facesContext, autoComplete);
		encodeMarkup(facesContext, autoComplete);
	}

	@SuppressWarnings("unchecked")
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		AutoComplete autoComplete = (AutoComplete) component;
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("application/json");
		
		MethodExpression me = autoComplete.getCompleteMethod();
		String query = facesContext.getExternalContext().getRequestParameterMap().get("autoCompleteQuery");
		
		List results = (List) me.invoke(facesContext.getELContext(), new Object[]{query});
		writer.write("{");
		writer.write("\"ResultSet\" : {");
		writer.write("\"Result\" : [");
		
		for(Iterator iterator = results.iterator(); iterator.hasNext();) {
			Object result = iterator.next();
			
			if(autoComplete.getVar() == null) {
				writer.write("{\"value\":\"" + result + "\"}");
			} else {
				facesContext.getExternalContext().getRequestMap().put(autoComplete.getVar(), result);
			
				writer.write("{");
				writer.write("\"label\":\"" + autoComplete.getItemLabel() + "\"");
				writer.write(",\"value\":\"" + ComponentUtils.getStringValueToRender(facesContext, autoComplete, autoComplete.getItemValue()) + "\"");
				writer.write("}");
			}
			
			if(iterator.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
		writer.write("}");
		writer.write("}");
	}
	
	protected void encodeMarkup(FacesContext facesContext, AutoComplete autoComplete) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = autoComplete.getClientId(facesContext);
		Object value = autoComplete.getValue();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_input", null);
		writer.writeAttribute("name", clientId + "_input", null);
		writer.writeAttribute("type", "text", null);
		if(value != null) {
			if(autoComplete.getVar() == null) {
				writer.writeAttribute("value", ComponentUtils.getStringValueToRender(facesContext, autoComplete) , null);
			} else {
				facesContext.getExternalContext().getRequestMap().put(autoComplete.getVar(), value);
				writer.writeAttribute("value", autoComplete.getItemLabel() , null);
			}
		}
		writer.endElement("input");
		
		if(autoComplete.getVar() != null) {
			writer.startElement("input", null);
			writer.writeAttribute("id", clientId + "_hinput", null);
			writer.writeAttribute("name", clientId + "_hinput", null);
			writer.writeAttribute("type", "hidden", null);
			if(value != null) {
				writer.writeAttribute("value", ComponentUtils.getStringValueToRender(facesContext, autoComplete, autoComplete.getItemValue()), null);
			}
			writer.endElement("input");
		}
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeScript(FacesContext facesContext, AutoComplete autoComplete) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = autoComplete.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, autoComplete);
		String actionURL = getActionURL(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		
		writer.write(var + " = new PrimeFaces.widget.AutoComplete('" + clientId + "',");
		writer.write("{url:'" + actionURL + "'");
		
		if(autoComplete.getVar() == null)
			writer.write(",fields:[\"value\"]});\n");
		else
			writer.write(",fields:[\"label\", \"value\"]});\n");

		if(autoComplete.isAnimHoriz()) writer.write(var + ".animHoriz = true;\n");
		if(!autoComplete.isAnimVert()) writer.write(var + ".animVert = false;\n");
		if(autoComplete.getAnimSpeed() != 0.3) writer.write(var + ".animSpeed = " + autoComplete.getAnimSpeed() + ";\n");
		if(autoComplete.getMaxResults() != 10) writer.write(var + ".maxResultsDisplayed = " + autoComplete.getMaxResults() + ";\n");
		if(autoComplete.getMinQueryLength() != 1) writer.write(var + ".minQueryLength = " + autoComplete.getMinQueryLength() + ";\n");
		if(autoComplete.getQueryDelay() != 0.2) writer.write(var + ".queryDelay = " + autoComplete.getQueryDelay() + ";\n");
		if(!autoComplete.isAutoHighlight()) writer.write(var + ".autoHighlight = false;\n");
		if(autoComplete.isUseShadow()) writer.write(var + ".useShadow = true;\n");
		if(autoComplete.isTypeAhead()) writer.write(var + ".typeAhead = true;\n");
		if(autoComplete.getTypeAheadDelay() != 0.5) writer.write(var + ".typeAheadDelay = " + autoComplete.getTypeAheadDelay() + ";\n");
		
		writer.write("});");

		writer.endElement("script");
	}
	
	@Override
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		AutoComplete autoComplete = (AutoComplete) component;
		String value = (String) submittedValue;
		Converter converter = autoComplete.getConverter();
		
		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(facesContext, autoComplete, value);
		}
		//Try to guess
		else {
			ValueExpression expr = autoComplete.getValueExpression("value");
			if(expr != null) {
				Class<?> valueType = expr.getType(facesContext.getELContext());
				Converter converterForType = facesContext.getApplication().createConverter(valueType);
				
				if(converterForType != null) {
					return converterForType.getAsObject(facesContext, autoComplete, value);
				}
			}
		}
		
		return value;
	}
}