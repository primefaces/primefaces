/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class AutoCompleteRenderer extends org.primefaces.component.autocomplete.AutoCompleteRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, AutoComplete ac) throws IOException {
        String clientId = ac.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AutoComplete", ac.resolveWidgetVar(), clientId);
        
        wb.attr("minLength", ac.getMinQueryLength(), 1)
            .attr("delay", ac.getQueryDelay(), 300);
                
        String emptyMessage = ac.getEmptyMessage();
        if(emptyMessage != null) {
            wb.attr("emptyMessage", emptyMessage, null);
        }
        
        encodeClientBehaviors(context, ac);

        wb.finish();
    }
    
    @Override
    protected void encodeMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        String style = ac.getStyle();
        String styleClass = ac.getStyleClass();
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);
        
        encodeInput(context, ac);
        encodePanel(context, ac);
        
        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        String inputId = clientId + "_input";
        String valueToRender = ComponentUtils.getValueToRender(context, ac);
            
        writer.startElement("div", null);
        writer.writeAttribute("class", AutoComplete.MOBILE_INPUT_CONTAINER_CLASS, null);
        
        writer.startElement("input", ac);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("data-enhanced", "true", null);
        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, ac, HTML.INPUT_TEXT_EVENTS);
        
        if(valueToRender != null) writer.writeAttribute("value", valueToRender , null);
        if(ac.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(ac.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        
        writer.endElement("input");
        
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", AutoComplete.MOBILE_CLEAR_ICON_CLASS, null);
        writer.endElement("a");
        
        if(ac.getVar() != null) {
            encodeHiddenInput(context, ac, clientId);
        }
        
        writer.endElement("div");
    }
    
    @Override
    protected void encodePanel(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelStyle = ac.getPanelStyle();
        String panelStyleClass = ac.getPanelStyleClass();
        panelStyleClass = (panelStyleClass == null)? AutoComplete.MOBILE_PANEL_CLASS: AutoComplete.MOBILE_PANEL_CLASS + " " + panelStyleClass;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", panelStyleClass, null);
        if(panelStyle != null) {
            writer.writeAttribute("style", panelStyle, null);
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", AutoComplete.MOBILE_ITEM_CONTAINER_CLASS, null);
        writer.endElement("div");
        writer.endElement("div");
    }
    
    @Override
    protected void encodeSuggestions(FacesContext context, AutoComplete ac, List items) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        boolean pojo = (var != null);
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        Converter converter = ComponentUtils.getConverter(context, ac);
        boolean hasContent = (ac.getChildCount() > 0);
        
        for(Object item : items) {
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", AutoComplete.MOBILE_ITEM_CLASS, null);
            
            if(pojo) {
                requestMap.put(var, item);
                String value = (converter == null) ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                writer.writeAttribute("data-item-value", value, null);
                writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
                
                if(hasContent)
                    renderChildren(context, ac);
                else
                    writer.writeText(ac.getItemLabel(), null);
            }
            else {
                writer.writeAttribute("data-item-label", item, null);
                writer.writeAttribute("data-item-value", item, null);
                
                writer.writeText(item, null);
            }

            writer.endElement("a");
        }
        
        if(pojo) {
            requestMap.remove(var);
        }
    }
}
