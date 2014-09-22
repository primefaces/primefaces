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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu;
import org.primefaces.util.WidgetBuilder;

public class SelectCheckboxMenuRenderer extends org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenuRenderer {
        
    @Override
    protected void encodeScript(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        String clientId = menu.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("SelectCheckboxMenu", menu.resolveWidgetVar(), clientId).finish();        
    }
    
    @Override
    public void encodeMarkup(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, menu);
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        String clientId = menu.getClientId(context);

        writer.startElement("select", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("data-role", "none", null);
        
        if(menu.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(menu.getOnkeydown() != null) writer.writeAttribute("onkeydown", menu.getOnkeydown(), null);
        if(menu.getOnkeyup() != null) writer.writeAttribute("onkeyup", menu.getOnkeyup(), null);
        
        renderOnchange(context, menu);
        renderDynamicPassThruAttributes(context, menu);
        
        encodeSelectItems(context, menu, selectItems, values, submittedValues, converter);

        writer.endElement("select");
    }
    
    protected void encodeSelectItems(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems, Object values, Object submittedValues, Converter converter) throws IOException {
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, menu, selectItem, values, submittedValues, converter);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectCheckboxMenu menu, SelectItem option, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        if(option instanceof SelectItemGroup) {
            SelectItemGroup group = (SelectItemGroup) option;            
            for(SelectItem groupItem : group.getSelectItems()) {
                encodeOption(context, menu, groupItem, values, submittedValues, converter);
            }
        }
        else {
            String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
            boolean disabled = option.isDisabled();

            Object valuesArray;
            Object itemValue;
            if(submittedValues != null) {
                valuesArray = submittedValues;
                itemValue = itemValueAsString;
            } else {
                valuesArray = values;
                itemValue = option.getValue();
            }

            boolean selected = isSelected(context, menu, itemValue, valuesArray, converter);

            writer.startElement("option", null);
            writer.writeAttribute("value", itemValueAsString, null);
            if(disabled) writer.writeAttribute("disabled", "disabled", null);
            if(selected) writer.writeAttribute("selected", "selected", null);

            if(!isValueBlank(option.getLabel())) {
	            if(option.isEscape())
	                writer.writeText(option.getLabel(), "value");
	            else
	                writer.write(option.getLabel());
            }

            writer.endElement("option");
        }
    }
}
