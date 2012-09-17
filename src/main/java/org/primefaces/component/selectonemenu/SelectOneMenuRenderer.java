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
package org.primefaces.component.selectonemenu;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.component.column.Column;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;

public class SelectOneMenuRenderer extends SelectOneRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if(!shouldDecode(component)) {
            return;
        }
        
        SelectOneMenu menu = (SelectOneMenu) component;
        if(menu.isEditable()) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            
            decodeBehaviors(context, menu);
            
            menu.setSubmittedValue(params.get(menu.getClientId(context) + "_editableInput"));
        }
        else {
            super.decode(context, component);
        }
    }
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Menu").getConvertedValue(context, component, submittedValue);
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneMenu menu = (SelectOneMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected void encodeMarkup(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, menu);
        String clientId = menu.getClientId(context);
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        boolean valid = menu.isValid();
                
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? SelectOneMenu.STYLE_CLASS : SelectOneMenu.STYLE_CLASS + " " + styleClass;
        styleClass = !valid ? styleClass + " ui-state-error" : styleClass;
        styleClass = menu.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleclass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, menu, clientId, selectItems, values, submittedValues, converter);
        encodeLabel(context, menu, selectItems);
        encodeMenuIcon(context, menu, valid);
        encodePanel(context, menu, selectItems);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, SelectOneMenu menu, String clientId, List<SelectItem> selectItems, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("select", menu);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        if(menu.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(menu.getTabindex() != null) writer.writeAttribute("tabindex", menu.getTabindex(), null);
        if(menu.getOnkeydown() != null) writer.writeAttribute("onkeydown", menu.getOnkeydown(), null);
        if(menu.getOnkeyup() != null) writer.writeAttribute("onkeyup", menu.getOnkeyup(), null);
        
        encodeSelectItems(context, menu, selectItems, values, submittedValues, converter);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeLabel(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String valueToRender = ComponentUtils.getValueToRender(context, menu);
        
        if(menu.isEditable()) {
            writer.startElement("input", null);
            writer.writeAttribute("type", "text", null);
            writer.writeAttribute("name", menu.getClientId() + "_editableInput", null);
            writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);
            
            if(menu.getTabindex() != null) {
                writer.writeAttribute("tabindex", menu.getTabindex(), null);
            }
            
            if(menu.isDisabled()) {
                writer.writeAttribute("disabled", "disabled", null);
            }

            if(valueToRender != null) {
                writer.writeAttribute("value", valueToRender , null);
            }

            writer.endElement("input");
        }
        else {
            writer.startElement("label", null);
            writer.writeAttribute("id", menu.getClientId() + "_label", null);
            writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);
            writer.write("&nbsp;");
            writer.endElement("label");
        }
    }

    protected void encodeMenuIcon(FacesContext context, SelectOneMenu menu, boolean valid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconClass = valid ? SelectOneMenu.TRIGGER_CLASS : SelectOneMenu.TRIGGER_CLASS + " ui-state-error";
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", iconClass, null);

        writer.startElement("span", menu);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean customContent = menu.getVar() != null;
        String panelStyle = menu.getPanelStyle();
        String panelStyleClass = menu.getPanelStyleClass();
        panelStyleClass = panelStyleClass == null ? SelectOneMenu.PANEL_CLASS : SelectOneMenu.PANEL_CLASS + " " + panelStyleClass;
        
        writer.startElement("div", null);
        writer.writeAttribute("id", menu.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", panelStyleClass, null);
        if(panelStyle != null) {
            writer.writeAttribute("style", panelStyle, null);
        }
        
        if(menu.isFilter()) {
            encodeFilter(context, menu);
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneMenu.ITEMS_WRAPPER_CLASS, null);
        writer.writeAttribute("style", "height:" + calculateWrapperHeight(menu, selectItems.size()), null);

        if(customContent) {
            writer.startElement("table", menu);
            writer.writeAttribute("class", SelectOneMenu.TABLE_CLASS, null);
            writer.startElement("tbody", menu);
            encodeOptionsAsTable(context, menu, selectItems);
            writer.endElement("tbody");
            writer.endElement("table");
        } 
        else {
            writer.startElement("ul", menu);
            writer.writeAttribute("class", SelectOneMenu.LIST_CLASS, null);
            encodeOptionsAsList(context, menu, selectItems);
            writer.endElement("ul");
        }
        
        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeOptionsAsTable(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = menu.getVar();
        List<Column> columns = menu.getColums();
        Object value = menu.getValue();

        for(SelectItem selectItem : selectItems) {
            Object itemValue = selectItem.getValue();
            
            context.getExternalContext().getRequestMap().put(var, selectItem.getValue());
            
            writer.startElement("tr", null);
            writer.writeAttribute("class", SelectOneMenu.ROW_CLASS, null);
            if(selectItem.getDescription() != null) {
                writer.writeAttribute("title", selectItem.getDescription(), null);
            }

            if(itemValue instanceof String) {
                writer.startElement("td", null);
                writer.writeAttribute("colspan", columns.size(), null);
                writer.writeText(selectItem.getLabel(), null);
                writer.endElement("td");
            } 
            else {
                for(Column column : columns) {
                    writer.startElement("td", null);
                    column.encodeAll(context);
                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }

        context.getExternalContext().getRequestMap().put(var, null);
    }

    protected void encodeOptionsAsList(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = menu.getValue();

        for(int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            String itemLabel = selectItem.getLabel();
            itemLabel = isValueBlank(itemLabel) ? "&nbsp;" : itemLabel;
            
            writer.startElement("li", null);
            writer.writeAttribute("class", SelectOneMenu.ITEM_CLASS, null);
            if(selectItem.getDescription() != null) {
                writer.writeAttribute("title", selectItem.getDescription(), null);
            }
            
            if(itemLabel.equals("&nbsp;"))
                writer.write(itemLabel);
            else {
                if(selectItem.isEscape())
                    writer.writeText(itemLabel, "value");
                else
                    writer.write(itemLabel);
            }
                

            writer.endElement("li");
        }
    }

    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("$(function(){");
        writer.write("PrimeFaces.cw('SelectOneMenu','" + menu.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(menu.getEffect() != null) writer.write(",effect:'" + menu.getEffect() + "'");
        if(menu.getEffectSpeed() != null) writer.write(",effectSpeed:'" + menu.getEffectSpeed() + "'");
        if(menu.isEditable())  writer.write(",editable:true");
        if(menu.getOnchange() != null) writer.write(",onchange:function() {" + menu.getOnchange() + "}");

        if(menu.isFilter()) {
            writer.write(",filter:true");
            
            if(menu.getFilterMatchMode() != null) writer.write(",filterMatchMode:'" + menu.getFilterMatchMode() + "'");     
            if(menu.getFilterFunction() != null) writer.write(",filterFunction:" + menu.getFilterFunction());
            if(menu.isCaseSensitive()) writer.write(",caseSensitive:true");
        }
        
        encodeClientBehaviors(context, menu);

        writer.write("});});");

        endScript(writer);
    }

    protected void encodeSelectItems(FacesContext context, SelectOneMenu menu, List<SelectItem> selectItems, Object values, Object submittedValues, Converter converter) throws IOException {
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, menu, selectItem, values, submittedValues, converter);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectOneMenu menu, SelectItem option, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
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
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }

        writer.startElement("option", null);
        writer.writeAttribute("value", itemValueAsString, null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(selected) writer.writeAttribute("selected", "selected", null);

        if(option.isEscape())
            writer.writeText(option.getLabel(), "value");
        else
            writer.write(option.getLabel());

        writer.endElement("option");
    }

    protected String calculateWrapperHeight(SelectOneMenu menu, int itemSize) {
        int height = menu.getHeight();
        
        if(height != Integer.MAX_VALUE) {
            return height + "px";
        } else if(itemSize > 10) {
            return 200 + "px";
        }
        
        return "auto";
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }

    protected void encodeFilter(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = menu.getClientId(context) + "_filter";
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-selectonemenu-filter-container", null);
        
        writer.startElement("input", null);
        writer.writeAttribute("class", "ui-selectonemenu-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", id);
        writer.endElement("span");
        
        writer.endElement("input");
        
        writer.endElement("div");
    }
}