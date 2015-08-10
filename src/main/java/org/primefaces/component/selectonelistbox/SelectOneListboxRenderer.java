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
package org.primefaces.component.selectonelistbox;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.component.column.Column;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.WidgetBuilder;

public class SelectOneListboxRenderer extends SelectOneRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Listbox").getConvertedValue(context, component, submittedValue);
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneListbox listbox = (SelectOneListbox) component;

        encodeMarkup(context, listbox);
        encodeScript(context, listbox);
    }

    protected void encodeMarkup(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, listbox);
        
        String style = listbox.getStyle();
        String styleClass = listbox.getStyleClass();
        styleClass = styleClass == null ? SelectOneListbox.CONTAINER_CLASS : SelectOneListbox.CONTAINER_CLASS + " " + styleClass;
        styleClass = listbox.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !listbox.isValid() ? styleClass + " ui-state-error" : styleClass;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        if(listbox.isFilter()) {
            encodeFilter(context, listbox);
        }

        encodeInput(context, listbox, clientId, selectItems);
        encodeList(context, listbox, selectItems);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectOneListbox listbox) throws IOException {
        String clientId = listbox.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneListbox", listbox.resolveWidgetVar(), clientId)
            .attr("disabled", listbox.isDisabled(), false);
        
        if(listbox.isFilter()) {
            wb.attr("filter", true)
                .attr("filterMatchMode", listbox.getFilterMatchMode(), null)
                .nativeAttr("filterFunction", listbox.getFilterFunction(), null)
                .attr("caseSensitive", listbox.isCaseSensitive(), false);
        }
        
        wb.finish();
    }

    protected void encodeInput(FacesContext context, SelectOneListbox listbox, String clientId, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";
        String labelledBy = listbox.getLabelledBy();

        writer.startElement("div", listbox);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        
        writer.startElement("select", listbox);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected
        
        renderDomEvents(context, listbox, SelectOneListbox.DOM_EVENTS);
        
        if(listbox.getTabindex() != null) {
            writer.writeAttribute("tabindex", listbox.getTabindex(), null);
        }
        
        if(labelledBy != null) {
            writer.writeAttribute("aria-labelledby", labelledBy, null);
        }
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, listbox);
        }

        encodeSelectItems(context, listbox, selectItems);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectOneListbox listbox, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = listbox.getConverter();
        Object values = getValues(listbox);
        Object submittedValues = getSubmittedValues(listbox);
        boolean customContent = listbox.getVar() != null;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("class", SelectOneListbox.LIST_CONTAINER_CLASS, null);
        writer.writeAttribute("style", "height:" + calculateWrapperHeight(listbox, selectItems.size()), null);

        if(customContent) {
            writer.startElement("table", null);
            writer.writeAttribute("class", SelectOneListbox.LIST_CLASS, null);
            writer.startElement("tbody", null);
            for(SelectItem selectItem : selectItems) {
                encodeItem(context, listbox, selectItem, values, submittedValues, converter, customContent);
            }
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            writer.startElement("ul", null);
            writer.writeAttribute("class", SelectOneListbox.LIST_CLASS, null);
            for(SelectItem selectItem : selectItems) {
                encodeItem(context, listbox, selectItem, values, submittedValues, converter, customContent);
            }
            writer.endElement("ul");
        }
        
        writer.endElement("div");
    }
    
    protected void encodeItem(FacesContext context, SelectOneListbox listbox, SelectItem option, Object values, Object submittedValues, Converter converter, boolean customContent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, listbox, converter, option.getValue());
        boolean disabled = option.isDisabled() || listbox.isDisabled();
        String itemClass = disabled ? SelectOneListbox.ITEM_CLASS + " ui-state-disabled" : SelectOneListbox.ITEM_CLASS;

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, listbox, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }
        
        if(selected) {
            itemClass = itemClass + " ui-state-highlight";
        }

        if(customContent) {
            String var = listbox.getVar();
            context.getExternalContext().getRequestMap().put(var, option.getValue());
            
            writer.startElement("tr", null);
            writer.writeAttribute("class", itemClass, null);
            if(option.getDescription() != null) {
                writer.writeAttribute("title", option.getDescription(), null);
            }

            for(UIComponent child : listbox.getChildren()) {
                if(child instanceof Column && child.isRendered()) {
                    writer.startElement("td", null);
                    renderChildren(context, child);
                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        } 
        else {
            writer.startElement("li", null);
            writer.writeAttribute("class", itemClass, null);
            if(option.isEscape()) {
                writer.writeText(option.getLabel(), null);
            } else {
                writer.write(option.getLabel());
            }

            writer.endElement("li");
        }
        
    }

    protected void encodeSelectItems(FacesContext context, SelectOneListbox listbox, List<SelectItem> selectItems) throws IOException {
        Converter converter = listbox.getConverter();
        Object values = getValues(listbox);
        Object submittedValues = getSubmittedValues(listbox);
        
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, listbox, selectItem, values, submittedValues, converter);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectOneListbox listbox, SelectItem option, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, listbox, converter, option.getValue());
        boolean disabled = option.isDisabled() || listbox.isDisabled();

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, listbox, itemValue, valuesArray, converter);
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
    
    protected void encodeFilter(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = listbox.getClientId(context) + "_filter";
        boolean disabled = listbox.isDisabled();
        String filterClass = disabled ? SelectOneListbox.FILTER_CLASS + " ui-state-disabled" : SelectOneListbox.FILTER_CLASS;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneListbox.FILTER_CONTAINER_CLASS, null);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", SelectOneListbox.FILTER_ICON_CLASS, id);
        writer.endElement("span");
        
        writer.startElement("input", null);
        writer.writeAttribute("class", filterClass, null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);

        writer.endElement("input");
        
        writer.endElement("div");
    }
    
    protected String calculateWrapperHeight(SelectOneListbox listbox, int itemSize) {
        int height = listbox.getScrollHeight();
        
        if(height != Integer.MAX_VALUE) {
            return height + "px";
        } else if(itemSize > 10) {
            return 200 + "px";
        }
        
        return "auto";
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
    
    @Override
    public String getHighlighter() {
        return "listbox";
    }
}