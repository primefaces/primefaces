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
package org.primefaces.component.selectmanybutton;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.HTML;

public class SelectManyButtonRenderer extends SelectManyRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Checkbox").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyButton button = (SelectManyButton) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, SelectManyButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        String style = button.getStyle();
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? SelectManyButton.STYLE_CLASS : SelectManyButton.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("div", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeSelectItems(context, button);

        writer.endElement("div");
    }
    
    protected void encodeSelectItems(FacesContext context, SelectManyButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, button);
        Converter converter = button.getConverter();
        Object values = getValues(button);
        Object submittedValues = getSubmittedValues(button);
        int selectItemsSize = selectItems.size();

        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;

            encodeOption(context, button, values, submittedValues, converter, selectItem, idx, selectItemsSize);
        }
    }
    
    protected void encodeOption(FacesContext context, UIInput component, Object values, Object submittedValues, Converter converter, SelectItem option, int idx, int size) throws IOException {
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
        writer.writeAttribute("type", "checkbox", null);
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

    protected void encodeScript(FacesContext context, SelectManyButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectManyButton','" + button.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        encodeClientBehaviors(context, button);

        writer.write("});");

        endScript(writer);
    }
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        return selectMany.getClientId(context);
    }
    
}
