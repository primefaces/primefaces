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
package org.primefaces.component.multiselectlistbox;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class MultiSelectListboxRenderer extends SelectOneRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Listbox").getConvertedValue(context, component, submittedValue);
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        MultiSelectListbox listbox = (MultiSelectListbox) component;

        encodeMarkup(context, listbox);
        encodeScript(context, listbox);
    }
    
    protected void encodeMarkup(FacesContext context, MultiSelectListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, listbox);     
        String style = listbox.getStyle();
        String styleClass = listbox.getStyleClass();
        styleClass = styleClass == null ? MultiSelectListbox.CONTAINER_CLASS : MultiSelectListbox.CONTAINER_CLASS + " " + styleClass;
        styleClass = listbox.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !listbox.isValid() ? styleClass + " ui-state-error" : styleClass;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeInput(context, listbox);
        encodeLists(context, listbox, selectItems);

        writer.endElement("div");
    }
    
    protected void encodeLists(FacesContext context, MultiSelectListbox listbox, List<SelectItem> itemList) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectItem[] items = (itemList == null) ? null : itemList.toArray(new SelectItem[itemList.size()]);
        String header = listbox.getHeader();
        String listStyleClass = MultiSelectListbox.LIST_CLASS;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("class", MultiSelectListbox.LIST_CONTAINER_CLASS, null);
                
        if(header != null) {
            listStyleClass = listStyleClass + " ui-corner-bottom";
            
            writer.startElement("div", listbox);
            writer.writeAttribute("class", MultiSelectListbox.LIST_HEADER_CLASS, null);
            writer.writeText(header, null);
            writer.endElement("div");
        } 
        else {
            listStyleClass = listStyleClass + " ui-corner-all";
        }
        
        writer.startElement("ul", listbox);
        writer.writeAttribute("class", listStyleClass, null);
        
        if(items != null) {
            encodeListItems(context, listbox, items);
        }
        
        writer.endElement("ul");
        
        writer.endElement("div");
    }
    
    protected void encodeListItems(FacesContext context, MultiSelectListbox listbox, SelectItem[] items) throws IOException {
        if(items != null && items.length > 0) {
            ResponseWriter writer = context.getResponseWriter();
            Converter converter = ComponentUtils.getConverter(context, listbox);
            String itemValue = null;
            
            for (int i = 0; i < items.length; i++) {
                SelectItem item = items[i];
                itemValue = converter != null ? converter.getAsString(context, listbox, item.getValue()) : String.valueOf(item.getValue());
                writer.startElement("li", null);
                writer.writeAttribute("class", MultiSelectListbox.ITEM_CLASS, null);
                writer.writeAttribute("data-value", itemValue, null);
                
                writer.startElement("span", listbox);
                writer.writeText(item.getLabel(), null);
                writer.endElement("span");
                
                if(item instanceof SelectItemGroup) {
                    SelectItemGroup group = (SelectItemGroup) item;
                    SelectItem[] groupItems = group.getSelectItems();
                            
                    if(groupItems != null && groupItems.length > 0)
                        encodeGroupItems(context, listbox, group.getSelectItems());
                }
                
                writer.endElement("li");
            }
        }
    }
    
    protected void encodeGroupItems(FacesContext context, MultiSelectListbox listbox, SelectItem[] selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("ul", listbox);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        encodeListItems(context, listbox, selectItems);
        writer.endElement("ul");
    }

    protected void encodeScript(FacesContext context, MultiSelectListbox listbox) throws IOException {
        String clientId = listbox.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        
        wb.init("MultiSelectListbox", listbox.resolveWidgetVar(), clientId)
            .attr("effect", listbox.getEffect(), null)
            .attr("showHeaders", listbox.isShowHeaders(), false)
            .finish();
    } 
    
    protected void encodeInput(FacesContext context, MultiSelectListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = listbox.getClientId(context) + "_input";
        String valueToRender = ComponentUtils.getValueToRender(context, listbox);

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("autocomplete", "off", null);
        if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
		writer.endElement("input");
    }
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }
}
