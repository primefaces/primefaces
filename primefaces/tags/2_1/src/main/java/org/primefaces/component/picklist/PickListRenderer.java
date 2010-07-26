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
package org.primefaces.component.picklist;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.model.DualListModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class PickListRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		PickList pickList = (PickList) component;
		String clientId = pickList.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		String sourceKey = clientId + "_sourceList";
		String targetKey = clientId + "_targetList";
		if(params.containsKey(sourceKey) && params.containsKey(targetKey)) {
			pickList.setSubmittedValue(new String[]{params.get(sourceKey), params.get(targetKey)});
		}
	}
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		PickList pickList = (PickList) component;
		
		encodeMarkup(facesContext, pickList);
		encodeScript(facesContext, pickList);
	}
	
	private void encodeScript(FacesContext facesContext, PickList pickList) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = pickList.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, pickList);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.PickList('" + clientId + "', {});");		
		
		writer.endElement("script");
	}
	
	@SuppressWarnings("unchecked")
	private void encodeMarkup(FacesContext facesContext, PickList pickList) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = pickList.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, pickList);
		DualListModel model = (DualListModel) pickList.getValue();
		
		writer.startElement("table", pickList);
		writer.writeAttribute("id", clientId, "id");
		if(pickList.getStyle() != null) writer.writeAttribute("style", pickList.getStyle(), null);
		if(pickList.getStyleClass() != null) writer.writeAttribute("class", pickList.getStyleClass(), null);
		
		writer.startElement("tbody", null);
		writer.startElement("tr", null);
		
		//Source
		writer.startElement("td", null);
		encodeList(facesContext, pickList, clientId + "_source", widgetVar, "ui-picklist-source", model.getSource());
		writer.endElement("td");
		
		//Controls
		writer.startElement("td", null);
		if(pickList.getFacetCount() > 0) {
			encodeFacet(facesContext, pickList, widgetVar, "add");
			encodeFacet(facesContext, pickList, widgetVar, "addAll");
			encodeFacet(facesContext, pickList, widgetVar, "remove");
			encodeFacet(facesContext, pickList, widgetVar, "removeAll");
		} else {
			encodeDefaultControl(facesContext, pickList, widgetVar, "&gt;", "add");
			encodeDefaultControl(facesContext, pickList, widgetVar, "&gt;&gt;", "addAll");
			encodeDefaultControl(facesContext, pickList, widgetVar, "&lt;", "remove");
			encodeDefaultControl(facesContext, pickList, widgetVar, "&lt;&lt;", "removeAll");
		}
		writer.endElement("td");
		
		//Target
		writer.startElement("td", null);
		encodeList(facesContext, pickList, clientId + "_target", widgetVar, "ui-picklist-target", model.getTarget());
		writer.endElement("td");
		
		writer.endElement("tr");
		writer.endElement("tbody");
		
		writer.endElement("table");
	}
	
	private void encodeDefaultControl(FacesContext facesContext, PickList pickList, String widgetVar, String label, String fn) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		writer.startElement("div", null);
		writer.writeAttribute("class", "ui-picklist-control", null);
		
		writer.startElement("button", null);
		writer.writeAttribute("type", "push", null);
		writer.writeAttribute("style", "width:35px;", null);
		writer.writeAttribute("onclick", widgetVar + "." + fn + "();return false;", null);
		writer.write(label);
		writer.endElement("button");
		
		writer.endElement("div");
	}
	
	private void encodeFacet(FacesContext facesContext, PickList pickList, String widgetVar, String facet) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent control = pickList.getFacet(facet);
		
		if(control != null) {
			writer.startElement("div", null);
			writer.writeAttribute("class", "ui-picklist-control", null);
			
			ComponentUtils.decorateAttribute(control, "onclick", widgetVar + "." + facet + "();return false;");
			renderChild(facesContext, control);
			
			writer.endElement("div");
		}	
	}
	
	@SuppressWarnings("unchecked")
	private void encodeList(FacesContext facesContext, PickList  pickList, String listId, String widgetVar, String styleClass, List model) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String method = styleClass.equals("ui-picklist-source") ? ".add();" : ".remove();";
		
		writer.startElement("select", null);
		writer.writeAttribute("id", listId, "id");
		writer.writeAttribute("name", listId, "id");
		writer.writeAttribute("class", styleClass, null);
		writer.writeAttribute("multiple", "multiple", null);
		writer.writeAttribute("ondblclick", widgetVar + method, null);

		String state = encodeOptions(facesContext, pickList, model);
		
		writer.endElement("select");
		
		encodeListStateHolder(facesContext, listId + "List", state);
	}
	
	@SuppressWarnings("unchecked")
	private String encodeOptions(FacesContext facesContext, PickList pickList, List model) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String var = pickList.getVar();
		Converter converter = pickList.getConverter();
		
		StringBuffer state = new StringBuffer();
		for(Object item : model) {
			facesContext.getExternalContext().getRequestMap().put(var, item);
			String value = converter != null ? converter.getAsString(facesContext, pickList, pickList.getItemValue()) : (String) pickList.getItemValue();
			
			writer.startElement("option", null);
			writer.writeAttribute("value", value, null);
			writer.write(pickList.getItemLabel());
			writer.endElement("option");
			
			state.append(value);
			state.append(";");
		}
		
		facesContext.getExternalContext().getRequestMap().remove(var);
		
		return state.toString();
	}
	
	private void encodeListStateHolder(FacesContext facesContext, String clientId, String value) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("value", value, null);
		writer.endElement("input");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		PickList pickList = (PickList) component;
		String[] value = (String[]) submittedValue;
		String[] sourceList = value[0].split(";");
		String[] targetList = value[1].split(";");
		DualListModel model = new DualListModel();
		
		doConvertValue(facesContext, pickList, sourceList, model.getSource());
		doConvertValue(facesContext, pickList, targetList, model.getTarget());
				
		return model;
	}
	
	@SuppressWarnings("unchecked")
	private void doConvertValue(FacesContext facesContext, PickList pickList, String[] values, List model) {
		Converter converter = pickList.getConverter();
		
		for(String value : values) {
			if(isValueBlank(value))
				continue;
			
			String val = value.trim();
			Object convertedValue = converter != null ? converter.getAsObject(facesContext, pickList, val) : val;
			
			if(convertedValue != null)
				model.add(convertedValue);
		}
	}
}