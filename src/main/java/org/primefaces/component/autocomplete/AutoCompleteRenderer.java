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
package org.primefaces.component.autocomplete;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import org.primefaces.component.column.Column;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class AutoCompleteRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        AutoComplete autoComplete = (AutoComplete) component;

        if(autoComplete.isDisabled() || autoComplete.isReadonly()) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = autoComplete.getClientId(context);
        String valueParam = autoComplete.getVar() == null ? clientId + "_input" : clientId + "_hinput";
        String submittedValue = params.get(valueParam);

        if(submittedValue != null) {
            autoComplete.setSubmittedValue(submittedValue);
        }
        
        decodeBehaviors(context, autoComplete);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        AutoComplete autoComplete = (AutoComplete) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(autoComplete.getClientId(context) + "_query");

        if(query != null) {
            encodeResults(context, component, query);
        }
        else {
            encodeMarkup(context, autoComplete);
            encodeScript(context, autoComplete);
        }
    }

    @SuppressWarnings("unchecked")
    public void encodeResults(FacesContext context, UIComponent component, String query) throws IOException {
        AutoComplete ac = (AutoComplete) component;
        List results = (List) ac.getCompleteMethod().invoke(context.getELContext(), new Object[]{query});
        int maxResults = ac.getMaxResults();
        
        if(maxResults != Integer.MAX_VALUE && results.size() > maxResults) {
            results = results.subList(0, ac.getMaxResults());
        }

        encodeSuggestions(context, ac, results);
    }

    protected void encodeMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        Object value = ac.getValue();
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.STYLE_CLASS : AutoComplete.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if(ac.getStyle() != null) {
            writer.writeAttribute("style", ac.getStyle(), null);
        }

        encodeInput(context, ac, clientId, value);
        
        if(ac.getVar() != null) {
            encodeHiddenInput(context, ac, clientId, value);
        }
        
        if(ac.isDropdown()) {
            encodeDropDown(context, ac);
        }
        
        encodePanel(context, ac);

        writer.endElement("span");
    }
    
    protected void encodeInput(FacesContext context, AutoComplete ac, String clientId, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean disabled = ac.isDisabled();
        String defaultStyleClass = ac.isDropdown() ? AutoComplete.INPUT_WITH_DROPDOWN_CLASS : AutoComplete.INPUT_CLASS;
        
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        if(value != null) {
            if(ac.getVar() == null) {
                writer.writeAttribute("value", ComponentUtils.getStringValueToRender(context, ac), null);
            } else {
                context.getExternalContext().getRequestMap().put(ac.getVar(), value);
                writer.writeAttribute("value", ac.getItemLabel(), null);
            }
        }

        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(ac.isReadonly()) writer.writeAttribute("readonly", "readonly", null);

        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS);

        if(themeForms()) {
            String styleClass = disabled ? defaultStyleClass + " ui-state-disabled" : defaultStyleClass;
            writer.writeAttribute("class", styleClass, null);
        }

        writer.endElement("input");
    }
    
    protected void encodeHiddenInput(FacesContext context, AutoComplete ac, String clientId, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_hinput", null);
        writer.writeAttribute("name", clientId + "_hinput", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (value != null) {
            writer.writeAttribute("value", ComponentUtils.getStringValueToRender(context, ac, ac.getItemValue()), null);
        }
        writer.endElement("input");

        context.getExternalContext().getRequestMap().remove(ac.getVar());	//clean
    }
    
    protected void encodeDropDown(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("button", ac);
        writer.writeAttribute("class", "ui-button ui-widget ui-state-default ui-corner-right ui-button-icon-only", null);
        writer.writeAttribute("type", "button", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-icon-primary ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");
        
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-text", null);
        writer.write("&nbsp;");
        writer.endElement("span");
        
        
        writer.endElement("button");
    }
    
    protected void encodePanel(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = ac.getPanelStyleClass();
        styleClass = styleClass == null ? AutoComplete.PANEL_CLASS : AutoComplete.PANEL_CLASS + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", ac.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(ac.getPanelStyle() != null) {
            writer.writeAttribute("style", ac.getPanelStyle(), null);
        }
        
        writer.endElement("div");
    }
    
    protected void encodeSuggestions(FacesContext context, AutoComplete ac, List items) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean customContent = ac.getColums().size() > 0;
        Converter converter = getConverter(context, ac);
        
        if(customContent) {
            encodeSuggestionsAsTable(context, ac, items, converter);
        } else {
            encodeSuggestionsAsList(context, ac, items, converter);
        }
    }
    
    protected void encodeSuggestionsAsTable(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;
        
        writer.startElement("table", ac);
        writer.writeAttribute("class", AutoComplete.TABLE_CLASS, null);
        writer.startElement("tbody", ac);
        
        for(Object item : items) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", AutoComplete.ROW_CLASS, null);
            
            if(pojo) {
                requestMap.put(var, item);
                String value = converter == null ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                writer.writeAttribute("data-item-value", value, null);
                writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
            }
            
            for(Column column : ac.getColums()) {
                if(column.isRendered()) {
                    writer.startElement("td", null);
                    if(column.getStyle() != null) writer.writeAttribute("style", item, null);
                    if(column.getStyleClass() != null) writer.writeAttribute("class", column.getStyleClass(), null);
                    
                    column.encodeAll(context);
                    
                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }
        
        writer.startElement("tbody", ac);
        writer.endElement("table");
    }

    protected void encodeSuggestionsAsList(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;
        
        writer.startElement("ul", ac);
        writer.writeAttribute("class", AutoComplete.LIST_CLASS, null);

        for(Object item : items) {
            writer.startElement("li", null);
            writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);
            
            if(pojo) {
                requestMap.put(var, item);
                String value = converter == null ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                writer.writeAttribute("data-item-value", value, null);
                writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
                
                writer.writeText(ac.getItemLabel(), null);
            }
            else {
                writer.writeAttribute("data-item-label", item, null);
                
                writer.writeText(item, null);
            }

            writer.endElement("li");
        }
        
        writer.endElement("ul");
        
        if(pojo) {
            requestMap.remove(var);
        }
    }

    protected void encodeScript(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function(){");

        writer.write(ac.resolveWidgetVar() + " = new PrimeFaces.widget.AutoComplete('" + clientId + "', {");
        writer.write("pojo:" + (ac.getVar() != null));

        //Configuration
        if(ac.getMinQueryLength() != 1) writer.write(",minLength:" + ac.getMinQueryLength());
        if(ac.getQueryDelay() != 300) writer.write(",delay:" + ac.getQueryDelay());
        if(ac.isForceSelection()) writer.write(",forceSelection:true");
        if(!ac.isGlobal()) writer.write(",global:false");
        if(ac.getScrollHeight() != Integer.MAX_VALUE) writer.write(",scrollHeight:" + ac.getScrollHeight());

        //Client side callbacks
        if(ac.getOnstart() != null) writer.write(",onstart:function(request) {" + ac.getOnstart() + ";}");
        if(ac.getOncomplete() != null) writer.write(",oncomplete:function(response) {" + ac.getOncomplete() + ";}");
        
        //Effects
        String effect = ac.getEffect();
        if(effect != null) {
            writer.write(",effect:'" + effect + "'");
            writer.write(",effectDuration:" + ac.getEffectDuration());
        }
  
        //Behaviors
        encodeClientBehaviors(context, ac);

        //Themeing
        if(!themeForms()) {
            writer.write(",theme:false");
        }

        writer.write("});});");

        writer.endElement("script");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
