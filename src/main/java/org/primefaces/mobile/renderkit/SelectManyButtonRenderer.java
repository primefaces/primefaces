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
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectmanybutton.SelectManyButton;
import org.primefaces.component.selectonebutton.SelectOneButton;

public class SelectManyButtonRenderer extends org.primefaces.component.selectmanybutton.SelectManyButtonRenderer {
    
    @Override
    public void encodeMarkup(FacesContext context, SelectManyButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, button);
        String style = button.getStyle();
        String styleClass = button.getStyleClass();
        styleClass = (styleClass == null) ? SelectManyButton.MOBILE_STYLE_CLASS: SelectManyButton.MOBILE_STYLE_CLASS + " " + styleClass;
     
        writer.startElement("div", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectManyButton.MOBILE_ITEMS_CLASS, "id");
        
        if (selectItems != null && !selectItems.isEmpty()) {
            int itemCount = selectItems.size();
            Converter converter = button.getConverter();
            Object values = getValues(button);
            Object submittedValues = getSubmittedValues(button);

            for (int idx = 0; idx < itemCount; idx++) {
                SelectItem selectItem = selectItems.get(idx);
                String labelClass = (idx == 0) ? "ui-first-child" : (idx == (itemCount - 1)) ? "ui-last-child" : null;
 
                encodeOption(context, button, values, submittedValues, converter, selectItem, idx, labelClass);
            }        
        }
        
        writer.endElement("div");
        writer.endElement("div");
    }
    
    protected void encodeOption(FacesContext context, UIInput component, Object values, Object submittedValues, Converter converter, SelectItem option, int idx, String labelClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectManyButton button = (SelectManyButton) component;
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = button.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || button.isDisabled();

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }
        
        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }
        
        String labelStyleClass = (labelClass == null) ? SelectManyButton.MOBILE_LABEL_CLASS: SelectManyButton.MOBILE_LABEL_CLASS + " " + labelClass;
        
        if(selected) {
            labelStyleClass = labelStyleClass + " ui-btn-active";
        }
        
        if(disabled) {
            labelStyleClass = labelStyleClass + " ui-state-disabled";
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-checkbox", null);
        
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
        writer.writeAttribute("type", "checkbox", null);
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
