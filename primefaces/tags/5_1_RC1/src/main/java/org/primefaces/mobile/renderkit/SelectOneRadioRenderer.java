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
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.util.WidgetBuilder;

public class SelectOneRadioRenderer extends org.primefaces.component.selectoneradio.SelectOneRadioRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneRadio", radio.resolveWidgetVar(), radio.getClientId(context)).finish();
    }
    
    @Override
    protected void encodeMarkup(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, radio);
        String style = radio.getStyle();
        String styleClass = radio.getStyleClass();
        Converter converter = radio.getConverter();
        
        writer.startElement("div", radio);
        writer.writeAttribute("id", clientId, "id");
        
        if (style != null) writer.writeAttribute("style", style, "style");
        if (styleClass != null) writer.writeAttribute("class", style, "styleClass");
        
        if (selectItems != null && !selectItems.isEmpty()) {
            Object value = radio.getSubmittedValue();
            if(value == null) {
                value = radio.getValue();
            }
            Class type = value == null ? String.class : value.getClass();
        
            int idx = 0;
            for (SelectItem selectItem : selectItems) {
                boolean disabled = selectItem.isDisabled() || radio.isDisabled();
                String id = clientId + UINamingContainer.getSeparatorChar(context) + idx;
                Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
                boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
 
                encodeOption(context, radio, selectItem, id, clientId, converter, selected, disabled); 
                idx++;
            }
        }
        
        writer.endElement("div");
    }
    
    @Override
    protected void encodeOption(FacesContext context, SelectOneRadio radio, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, radio, converter, option.getValue());

        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", itemValueAsString, null);

        renderOnchange(context, radio);
        renderDynamicPassThruAttributes(context, radio);
        
        if (radio.getTabindex() != null) writer.writeAttribute("tabindex", radio.getTabindex(), null);
        if (selected) writer.writeAttribute("checked", "checked", null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        
        writer.endElement("input");

        //label
        writer.startElement("label", null);
        writer.writeAttribute("for", id, null);
        
        if (option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        
        writer.endElement("label");
    }
}
