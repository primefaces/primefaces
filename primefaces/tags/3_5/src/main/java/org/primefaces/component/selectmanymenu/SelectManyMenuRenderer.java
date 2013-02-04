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
package org.primefaces.component.selectmanymenu;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.component.column.Column;
import org.primefaces.component.selectonelistbox.SelectOneListbox;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.WidgetBuilder;

public class SelectManyMenuRenderer extends SelectManyRenderer {
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Menu").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyMenu menu = (SelectManyMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected void encodeMarkup(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, menu);
        
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? SelectManyMenu.CONTAINER_CLASS : SelectManyMenu.CONTAINER_CLASS + " " + styleClass;
        styleClass = menu.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !menu.isValid() ? styleClass + " ui-state-error" : styleClass;
        

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) writer.writeAttribute("style", style, "style");

        encodeInput(context, menu, clientId, selectItems);
        encodeList(context, menu, selectItems);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("SelectManyMenu", menu.resolveWidgetVar(), clientId, false)
            .attr("disabled", menu.isDisabled(), false)
            .attr("showCheckbox", menu.isShowCheckbox(), false);
        
        encodeClientBehaviors(context, menu, wb);

        startScript(writer, clientId);
        writer.write(wb.build());
        endScript(writer);
    }

    protected void encodeInput(FacesContext context, SelectManyMenu menu, String clientId, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        
        writer.startElement("select", null);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected
        
        if(menu.getTabindex() != null) writer.writeAttribute("tabindex", menu.getTabindex(), null);
        if(menu.getOnchange() != null) writer.writeAttribute("onchange", menu.getOnchange(), null);

        encodeSelectItems(context, menu, selectItems);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectManyMenu menu, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        boolean customContent = menu.getVar() != null;
        boolean showCheckbox = menu.isShowCheckbox();

        if(customContent) {
            writer.startElement("table", null);
            writer.writeAttribute("class", SelectOneListbox.LIST_CLASS, null);
            writer.startElement("tbody", null);
            for(SelectItem selectItem : selectItems) {
                encodeItem(context, menu, selectItem, values, submittedValues, converter, customContent, showCheckbox);
            }
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            writer.startElement("ul", null);
            writer.writeAttribute("class", SelectOneListbox.LIST_CLASS, null);
            for(SelectItem selectItem : selectItems) {
                encodeItem(context, menu, selectItem, values, submittedValues, converter, customContent, showCheckbox);
            }
            writer.endElement("ul");
        }
    }
    
    protected void encodeItem(FacesContext context, SelectManyMenu menu, SelectItem option, Object values, Object submittedValues, 
                        Converter converter, boolean customContent, boolean showCheckbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
        boolean disabled = option.isDisabled() || menu.isDisabled();
        String itemClass = disabled ? SelectManyMenu.ITEM_CLASS + " ui-state-disabled" : SelectManyMenu.ITEM_CLASS;

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
        
        if(selected) {
            itemClass = itemClass + " ui-state-highlight";
        }

        if(customContent) {
            String var = menu.getVar();
            context.getExternalContext().getRequestMap().put(var, option.getValue());
            
            writer.startElement("tr", null);
            writer.writeAttribute("class", itemClass, null);
            if(option.getDescription() != null) {
                writer.writeAttribute("title", option.getDescription(), null);
            }
            
            if(showCheckbox) {
                writer.startElement("td", null);
                RendererUtils.encodeCheckbox(context, selected);
                writer.endElement("td");
            }

            for(UIComponent child : menu.getChildren()) {
                if(child instanceof Column && child.isRendered()) {
                    writer.startElement("td", null);
                    child.encodeAll(context);
                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        } 
        else {
            writer.startElement("li", null);
            writer.writeAttribute("class", itemClass, null);
            
            if(showCheckbox) {
                RendererUtils.encodeCheckbox(context, selected);
            }
            
            if(option.isEscape()) {
                writer.writeText(option.getLabel(), null);
            } else {
                writer.write(option.getLabel());
            }

            writer.endElement("li");
        }
        
    }

    protected void encodeSelectItems(FacesContext context, SelectManyMenu menu, List<SelectItem> selectItems) throws IOException {
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, menu, selectItem, values, submittedValues, converter);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectManyMenu menu, SelectItem option, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
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

        boolean selected = isSelected(context, menu, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }

        writer.startElement("option", null);
        writer.writeAttribute("value", itemValueAsString, null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(selected) writer.writeAttribute("selected", "selected", null);

        writer.write(option.getLabel());

        writer.endElement("option");
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        return selectMany.getClientId(context) + "_input";
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