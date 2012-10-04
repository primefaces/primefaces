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
package org.primefaces.component.selectonebutton;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.HTML;

public class SelectOneButtonRenderer extends SelectOneRenderer {
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Radio").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneButton button = (SelectOneButton) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, SelectOneButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        String style = button.getStyle();
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? SelectOneButton.STYLE_CLASS : SelectOneButton.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("div", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeSelectItems(context, button);

        writer.endElement("div");
    }
    
    protected void encodeSelectItems(FacesContext context, SelectOneButton button) throws IOException {
        List<SelectItem> selectItems = getSelectItems(context, button);
        int selectItemsSize = selectItems.size();
        Converter converter = button.getConverter();
        String name = button.getClientId(context);
        Object value = button.getSubmittedValue();
        if(value == null) {
            value = button.getValue();
        }
        Class type = value == null ? String.class : value.getClass();
        
        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;
            boolean disabled = selectItem.isDisabled() || button.isDisabled();
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;
            Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
            boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
            
            encodeOption(context, button, selectItem, id, name, converter, selected, disabled, idx, selectItemsSize);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectOneButton button, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled, int idx, int size) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, button, converter, option.getValue());
       
        String buttonStyle = HTML.BUTTON_TEXT_ONLY_BUTTON_FLAT_CLASS;
        if(idx == 0)
            buttonStyle = buttonStyle + " ui-corner-left";
        else if(idx == (size - 1))
            buttonStyle = buttonStyle + " ui-corner-right";
        
        buttonStyle = selected ? buttonStyle + " ui-state-active" : buttonStyle;
        buttonStyle = disabled ? buttonStyle + " ui-state-disabled" : buttonStyle;
        buttonStyle = !button.isValid() ? buttonStyle + " ui-state-error" : buttonStyle;
        
        //button
        writer.startElement("div", null);
		writer.writeAttribute("class", buttonStyle, null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(option.getDescription() != null) writer.writeAttribute("title", option.getDescription(), null);
              
        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", itemValueAsString, null);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        if(selected) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(button.getOnchange() != null) writer.writeAttribute("onchange", button.getOnchange(), null);

        writer.endElement("input");
        
        //item label
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(option.getLabel(), "itemLabel");
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectOneButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectOneButton','" + button.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        encodeClientBehaviors(context, button);

        writer.write("});");

        endScript(writer);
    }
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context);
    }
}
