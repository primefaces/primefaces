/*
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.component.chips;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class ChipsRenderer extends InputRenderer {
    
    private static final Logger LOG = Logger.getLogger(ChipsRenderer.class.getName());
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        Chips chips = (Chips) component;
        String clientId = chips.getClientId(context);

        if(chips.isDisabled()) {
            return;
        }
        
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        Map<String, String[]> paramValues = context.getExternalContext().getRequestParameterValuesMap();
        String[] hInputValues = paramValues.get(clientId + "_hinput");
        String[] submittedValues = (hInputValues != null) ? hInputValues : new String[]{};
        String inputValue = params.get(clientId + "_input");
        
        if(inputValue != null && !inputValue.trim().equals("")) {
            submittedValues = ArrayUtils.concat(submittedValues, new String[]{inputValue});
        }

        if(submittedValues.length > 0) {
            chips.setSubmittedValue(submittedValues);
        }
        else {
            chips.setSubmittedValue("");
        }

        decodeBehaviors(context, chips);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Chips chips = (Chips) component;

        encodeMarkup(context, chips);
        encodeScript(context, chips);
    }
    
    protected void encodeMarkup(FacesContext context, Chips chips) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = chips.getClientId(context);
        String inputId = clientId + "_input";
        List values = (List) chips.getValue();
        List<String> stringValues = new ArrayList<String>();
        boolean disabled = chips.isDisabled();
        String title = chips.getTitle();
        
        String style = chips.getStyle();
        String styleClass = chips.getStyleClass();
        styleClass = styleClass == null ? Chips.STYLE_CLASS : Chips.STYLE_CLASS + " " + styleClass;
        String listClass = disabled ? Chips.CONTAINER_CLASS + " ui-state-disabled" : Chips.CONTAINER_CLASS;
        listClass = chips.isValid() ? listClass : listClass + " ui-state-error";
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        if(title != null) {
            writer.writeAttribute("title", title, null);
        }
        
        writer.startElement("ul", null);
        writer.writeAttribute("class", listClass, null);
        
        if(values != null && !values.isEmpty()) {
        	Converter converter = ComponentUtils.getConverter(context, chips);

            for(Iterator<Object> it = values.iterator(); it.hasNext();) {
                Object value = it.next();
                
                String tokenValue = converter != null ? converter.getAsString(context, chips, value) : String.valueOf(value);
                
                writer.startElement("li", null);
                writer.writeAttribute("data-token-value", tokenValue, null);
                writer.writeAttribute("class", Chips.TOKEN_DISPLAY_CLASS, null);
                
                writer.startElement("span", null);
                writer.writeAttribute("class", Chips.TOKEN_LABEL_CLASS, null);
                writer.writeText(String.valueOf(value), null);
                writer.endElement("span");
                
                writer.startElement("span", null);
                writer.writeAttribute("class", Chips.TOKEN_CLOSE_ICON_CLASS, null);
                writer.endElement("span");
                
                writer.endElement("li");
                
                stringValues.add(tokenValue);
            }
        }
       
        writer.startElement("li", null);
        writer.writeAttribute("class", Chips.TOKEN_INPUT_CLASS, null);
        writer.startElement("input", null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("class", "ui-widget", null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("autocomplete", "off", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", "disabled");

        renderPassThruAttributes(context, chips, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, chips, HTML.INPUT_TEXT_EVENTS);
        
        writer.endElement("input");
        writer.endElement("li");
        
        writer.endElement("ul");
        
        encodeHiddenSelect(context, chips, clientId, stringValues);

        writer.endElement("div");
    }
    
    protected void encodeHiddenSelect(FacesContext context, Chips chips, String clientId, List<String> values) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = clientId + "_hinput";
        
        writer.startElement("select", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        
        if(chips.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        
        for(String value : values) {
            writer.startElement("option", null);
            writer.writeAttribute("value", value, null);
            writer.writeAttribute("selected", "selected", null);
            writer.endElement("option");
        }
        
        writer.endElement("select");
    }
    
    protected void encodeScript(FacesContext context, Chips chips) throws IOException {
        String clientId = chips.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Chips", chips.resolveWidgetVar(), clientId)
           .attr("max", chips.getMax(), Integer.MAX_VALUE);
                
        encodeClientBehaviors(context, chips);

        wb.finish();
    }
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {        
        Chips chips = (Chips) component;
        
        if(submittedValue == null || submittedValue.equals("")) {
            return null;
        }
        
		Converter converter = ComponentUtils.getConverter(context, component);
        String[] values = (String[]) submittedValue;
        List list = new ArrayList();

        for(String value : values) {
            if(isValueBlank(value)) {
                continue;
            }

            Object convertedValue = converter != null ? converter.getAsObject(context, chips, value) : value;

            if(convertedValue != null) {
                list.add(convertedValue);
            }
        }

        return list;       
	}
}
