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
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectonebutton.SelectOneButton;

public class SelectOneButtonRenderer extends org.primefaces.component.selectoneradio.SelectOneRadioRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectOneButton button = (SelectOneButton) component;
        String clientId = button.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, button);
        String style = button.getStyle();
        String styleClass = button.getStyleClass();
        Converter converter = button.getConverter();
        
        writer.startElement("div", button);
        writer.writeAttribute("data-role", "controlgroup", null);
        writer.writeAttribute("data-type", "horizontal", null);
        if (shouldWriteId(component)) writer.writeAttribute("id", clientId, "id");
        if (style != null) writer.writeAttribute("style", style, "style");
        if (styleClass != null) writer.writeAttribute("class", style, "styleClass");
        
        if (selectItems != null && !selectItems.isEmpty()) {
            Object value = button.getSubmittedValue();
            if(value == null) {
                value = button.getValue();
            }
            Class type = value == null ? String.class : value.getClass();
        
            int idx = 0;
            for (SelectItem selectItem : selectItems) {
                boolean disabled = selectItem.isDisabled() || button.isDisabled();
                String id = clientId + UINamingContainer.getSeparatorChar(context) + idx;
                Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
                boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
 
                encodeOption(context, button, selectItem, id, clientId, converter, selected, disabled); 
                idx++;
            }
        }
        
        writer.endElement("div");
    }
    
    protected void encodeOption(FacesContext context, SelectOneButton button, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, button, converter, option.getValue());

        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", itemValueAsString, null);

        renderOnchange(context, button);

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
