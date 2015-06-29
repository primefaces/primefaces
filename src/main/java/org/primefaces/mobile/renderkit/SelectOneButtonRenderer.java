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
import org.primefaces.component.selectonebutton.SelectOneButton;

public class SelectOneButtonRenderer extends org.primefaces.component.selectonebutton.SelectOneButtonRenderer {
    
    @Override
    public void encodeMarkup(FacesContext context, SelectOneButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, button);
        String style = button.getStyle();
        String styleClass = button.getStyleClass();
        styleClass = (styleClass == null) ? SelectOneButton.MOBILE_STYLE_CLASS: SelectOneButton.MOBILE_STYLE_CLASS + " " + styleClass;
        Converter converter = button.getConverter();
        
        writer.startElement("div", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneButton.MOBILE_ITEMS_CLASS, "id");
        
        if (selectItems != null && !selectItems.isEmpty()) {
            int itemCount = selectItems.size();
            Object value = button.getSubmittedValue();
            if(value == null) {
                value = button.getValue();
            }
            Class type = value == null ? String.class : value.getClass();
        
            for (int idx = 0; idx < selectItems.size(); idx++) {
                SelectItem selectItem = selectItems.get(idx);
                boolean disabled = selectItem.isDisabled() || button.isDisabled();
                String id = clientId + UINamingContainer.getSeparatorChar(context) + idx;
                Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
                boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
                String labelClass = (idx == 0) ? "ui-first-child" : (idx == (itemCount - 1)) ? "ui-last-child" : null;
 
                encodeOption(context, button, selectItem, id, clientId, converter, selected, disabled, labelClass); 
            }
        }
        
        writer.endElement("div");
        writer.endElement("div");
    }
    
    protected void encodeOption(FacesContext context, SelectOneButton button, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled, String labelClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, button, converter, option.getValue());
        String labelStyleClass = (labelClass == null) ? SelectOneButton.MOBILE_LABEL_CLASS: SelectOneButton.MOBILE_LABEL_CLASS + " " + labelClass;
        
        if(selected) {
            labelStyleClass = labelStyleClass + " ui-btn-active";
        }
        
        if(disabled) {
            labelStyleClass = labelStyleClass + " ui-state-disabled";
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-radio", null);
        
        //label
        writer.startElement("label", null);
        writer.writeAttribute("class", labelStyleClass, null);
        writer.writeAttribute("for", id, null);
        
        if (option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        
        writer.endElement("label");
        
        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", itemValueAsString, null);
        writer.writeAttribute("data-role", "none", null);

        renderOnchange(context, button);
        renderDynamicPassThruAttributes(context, button);

        if (selected) writer.writeAttribute("checked", "checked", null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        
        writer.endElement("input");

        writer.endElement("div");
    }
}
