/*
 * Copyright 2009-2013 PrimeTek.
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
import javax.faces.application.ProjectStage;
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
        
        writer.startElement("div", ac);
        writer.writeAttribute("id", clientId, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);
        
        encodeInput(context, ac);
        encodePanel(context, ac);
        
        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = ac.getClientId(context) + "_input";
        String valueToRender = ComponentUtils.getValueToRender(context, ac);
            
        writer.startElement("div", ac);
        writer.writeAttribute("class", "ui-input-search ui-body-inherit ui-corner-all ui-shadow-inset ui-input-has-clear", null);
        
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
        writer.endElement("div");
    }
    
    @Override
    protected void encodePanel(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-controlgroup ui-controlgroup-vertical ui-corner-all ui-screen-hidden", null);
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-controlgroup-controls", null);
        writer.endElement("div");
        writer.endElement("div");
    }
    
    @Override
    protected void encodeSuggestions(FacesContext context, AutoComplete ac, List items) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        for(Object item : items) {
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", "ui-autocomplete-item ui-btn ui-corner-all ui-shadow", null);
            writer.writeAttribute("data-item-label", item, null);
            writer.writeAttribute("data-item-value", item, null);
            writer.writeText(item, null);  
            writer.endElement("a");
        }
    }
}
