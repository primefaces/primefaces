/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.selectcheckboxmenu;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.SelectManyRenderer;

public class SelectCheckboxMenuRenderer extends SelectManyRenderer {
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Checkbox").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectCheckboxMenu menu = (SelectCheckboxMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }
    
    protected void encodeMarkup(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, menu);
        boolean valid = menu.isValid();
        
        String style = menu.getStyle();
        String styleclass = menu.getStyleClass();
        styleclass = styleclass == null ? SelectCheckboxMenu.STYLE_CLASS : SelectCheckboxMenu.STYLE_CLASS + " " + styleclass;
        styleclass = menu.isDisabled() ? styleclass + " ui-state-disabled" : styleclass;
        styleclass = !valid ? styleclass + " ui-state-error" : styleclass;
        
        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleclass, "styleclass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeInputs(context, menu, selectItems);
        encodeLabel(context, menu, selectItems, valid);
        encodeMenuIcon(context, menu, valid);

        writer.endElement("div");
    }
    
    protected void encodeInputs(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;

            encodeOption(context, menu, values, submittedValues, converter, selectItem, idx);
        }
        
        writer.endElement("div");
    }
    
    protected void encodeOption(FacesContext context, SelectCheckboxMenu menu, Object values, Object submittedValues, Converter converter, SelectItem option, int idx) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
        String name = menu.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || menu.isDisabled();

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }
        
        boolean checked = isSelected(context, menu, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !checked) {
            return;
        }
        
        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", itemValueAsString, null);

        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(menu.getOnchange() != null) writer.writeAttribute("onchange", menu.getOnchange(), null);

        writer.endElement("input");
        
        //label
        writer.startElement("label", null);
        writer.writeAttribute("for", id, null);
        if(disabled)
            writer.writeAttribute("class", "ui-state-disabled", null);
        
        if(option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        
        writer.endElement("label");
    }
    
    protected void encodeLabel(FacesContext context, SelectCheckboxMenu menu, List<SelectItem> selectItems, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = menu.getLabel();
        String labelClass = !valid ? SelectCheckboxMenu.LABEL_CLASS + " ui-state-error" : SelectCheckboxMenu.LABEL_CLASS;
        if(label == null) {
            label = "&nbsp;";
        }
        
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", SelectCheckboxMenu.LABEL_CONTAINER_CLASS, null);
        if(menu.getTabindex() != null) {
            writer.writeAttribute("tabindex", menu.getTabindex(), null);
        }
        
        writer.startElement("label", null);
        writer.writeAttribute("class", labelClass, null);
        writer.write(label);
        writer.endElement("label");
        writer.endElement("a");
    }
    
    protected void encodeMenuIcon(FacesContext context, SelectCheckboxMenu menu, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconClass = valid ? SelectCheckboxMenu.TRIGGER_CLASS : SelectCheckboxMenu.TRIGGER_CLASS + " ui-state-error";
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", iconClass, null);

        writer.startElement("span", menu);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, SelectCheckboxMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("$(function(){");
        writer.write("PrimeFaces.cw('SelectCheckboxMenu','" + menu.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(menu.getOnShow() != null) writer.write(",onShow:function(){" + menu.getOnShow() + "}");
        if(menu.getOnHide() != null) writer.write(",onHide:function(){" + menu.getOnHide() + "}");
        if(menu.getScrollHeight() != Integer.MAX_VALUE) writer.write(",scrollHeight:" + menu.getScrollHeight());
        if(menu.isFilter()) {
            writer.write(",filter:true");
            
            if(menu.getFilterMatchMode() != null) writer.write(",filterMatchMode:'" + menu.getFilterMatchMode() + "'");     
            if(menu.getFilterFunction() != null) writer.write(",filterFunction:" + menu.getFilterFunction());
            if(menu.isCaseSensitive()) writer.write(",caseSensitive:true");
        }
        
        if(menu.getPanelStyle() != null) writer.write(",panelStyle:'" + menu.getPanelStyle() + "'");
        if(menu.getPanelStyleClass() != null) writer.write(",panelStyleClass:'" + menu.getPanelStyleClass() + "'");
        
        encodeClientBehaviors(context, menu);

        writer.write("});});");

        endScript(writer);
    }
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        return selectMany.getClientId(context);
    }
}
