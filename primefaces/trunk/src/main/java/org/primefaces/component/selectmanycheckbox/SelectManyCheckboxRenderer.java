/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.selectmanycheckbox;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;

public class SelectManyCheckboxRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;

        if(checkbox.isDisabled()) {
            return;
        }

        decodeBehaviors(context, checkbox);

        String clientId = checkbox.getClientId(context);
        Map<String,String[]> params = context.getExternalContext().getRequestParameterValuesMap();
        
        if(params.containsKey(clientId)) {
            checkbox.setSubmittedValue(params.get(clientId));
        } else {
            checkbox.setSubmittedValue(new String[0]);
        }
    }
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Checkbox").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;

        encodeMarkup(context, checkbox);
        encodeScript(context, checkbox);
    }

    protected void encodeMarkup(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? SelectManyCheckbox.STYLE_CLASS : SelectManyCheckbox.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("table", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null)
            writer.writeAttribute("style", style, "style");

        encodeSelectItems(context, checkbox);

        writer.endElement("table");
    }

    protected void encodeScript(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectManyCheckbox','" + checkbox.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        if(checkbox.isDisabled()) 
            writer.write(",disabled: true");

        encodeClientBehaviors(context, checkbox);

        writer.write("});");

        endScript(writer);
    }

    protected void encodeOptionInput(FacesContext context, SelectManyCheckbox checkbox, String id, String name, boolean checked, boolean disabled, String label, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_INPUT_WRAPPER_CLASS, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", value, null);

        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(checkbox.getOnchange() != null) writer.writeAttribute("onchange", checkbox.getOnchange(), null);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectManyCheckbox checkbox, String containerClientId, SelectItem option, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        if(disabled)
            writer.writeAttribute("class", "ui-state-disabled", null);
        
        if(option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        
        writer.endElement("label");
    }

    protected void encodeOptionOutput(FacesContext context, SelectManyCheckbox checkbox, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.CHECKBOX_BOX_CLASS;
        boxClass = checked ? boxClass + " ui-state-active" : boxClass;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        
        String iconClass = HTML.CHECKBOX_ICON_CLASS;
        iconClass = checked ? iconClass + " " + HTML.CHECKBOX_CHECKED_ICON_CLASS : iconClass;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeSelectItems(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = getConverter(context, checkbox);
        Object values = getValues(checkbox);
        Object submittedValues = (Object[]) checkbox.getSubmittedValue();
        String layout = checkbox.getLayout();
        boolean pageDirection = layout != null && layout.equals("pageDirection");

        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;
            if(pageDirection) {
                writer.startElement("tr", null);
            }

            encodeOption(context, checkbox, values, submittedValues, converter, selectItem, idx);

            if(pageDirection) {
                writer.endElement("tr");
            }
        }
    }

    protected void encodeOption(FacesContext context, UIInput component, Object values, Object submittedValues, Converter converter, SelectItem option, int idx) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = checkbox.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
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
        
        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }
        
        writer.startElement("td", null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);

        encodeOptionInput(context, checkbox, id, name, selected, disabled, option.getLabel(), itemValueAsString);
        encodeOptionOutput(context, checkbox, selected, disabled);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, checkbox, id, option, disabled);
        writer.endElement("td");
    }
    
    protected Object getValues(UIComponent component) {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
        Object value = checkbox.getValue();

        if(value == null) {
            return null;
        } else if (value instanceof Collection) {
            return ((Collection) value).toArray();
        } else if(value.getClass().isArray()) {
            if(Array.getLength(value) == 0) {
                return null;
            }
        } else if (!value.getClass().isArray()) {
            throw new FacesException("Value of selectManyCheckbox must be an array or a collection");
        }

        return value;
    }
    
    protected boolean isSelected(FacesContext context, UIComponent component, Object itemValue, Object valueArray, Converter converter) {
        if(itemValue == null && valueArray == null) {
            return true;
        }
        
        if(valueArray != null) {
            if(!valueArray.getClass().isArray()) {
                return valueArray.equals(itemValue);
            }
            
            int length = Array.getLength(valueArray);
            for(int i = 0; i < length; i++) {
                Object value = Array.get(valueArray, i);
                
                if(value == null && itemValue == null) {
                    return true;
                } 
                else {
                    if((value == null) ^ (itemValue == null)) {
                        continue;
                    }
                    
                    Object compareValue;
                    if (converter == null) {
                        compareValue = coerceToModelType(context, itemValue, value.getClass());
                    } 
                    else {
                        compareValue = itemValue;
                        
                        if (compareValue instanceof String && !(value instanceof String)) {
                            compareValue = converter.getAsObject(context, component, (String) compareValue);
                        }
                    }

                    if (value.equals(compareValue)) {
                        return (true);
                    }
                }
            }
        }
        return false;

    }
    
    protected Object coerceToModelType(FacesContext ctx, Object value, Class itemValueType) {
        Object newValue;
        try {
            ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
            newValue = ef.coerceToType(value, itemValueType);
        } 
        catch (ELException ele) {
            newValue = value;
        } 
        catch (IllegalArgumentException iae) {
            newValue = value;
        }

        return newValue;
    }
}
