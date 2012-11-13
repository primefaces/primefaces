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
package org.primefaces.component.selectoneradio;

import java.io.IOException;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.component.radiobutton.RadioButton;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.HTML;

public class SelectOneRadioRenderer extends SelectOneRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Radio").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneRadio radio = (SelectOneRadio) component;
        
        encodeMarkup(context, radio);
        encodeScript(context, radio);
    }

    protected void encodeMarkup(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        String style = radio.getStyle();
        String styleClass = radio.getStyleClass();
        styleClass = styleClass == null ? SelectOneRadio.STYLE_CLASS : SelectOneRadio.STYLE_CLASS + " " + styleClass;
        String layout = radio.getLayout();
        boolean custom = layout != null && layout.equals("custom");
        
        List<SelectItem> selectItems = getSelectItems(context, radio);
        
        if(custom) {
            //populate selectitems for radiobutton access
            radio.setSelectItems(getSelectItems(context, radio));
            
            //render dummy markup to enable processing of ajax behaviors (finding form on client side)
            writer.startElement("span", radio);
            writer.writeAttribute("id", radio.getClientId(context), "id");
            writer.endElement("span");
        }
        else {
            writer.startElement("table", radio);
            writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("class", styleClass, "styleClass");
            if(style != null) {
                writer.writeAttribute("style", style, "style");
            }

            encodeSelectItems(context, radio, selectItems, layout);

            writer.endElement("table");
        }
    }

    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        String layout = radio.getLayout();
        boolean custom = layout != null && layout.equals("custom");

        startScript(writer, clientId);
        
        writer.write("$(function(){");
        writer.write("PrimeFaces.cw('SelectOneRadio','" + radio.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        if(custom) {
            writer.write(",custom:true");
        }

        encodeClientBehaviors(context, radio);

        writer.write("});});");

        endScript(writer);
    }
    
    protected void encodeSelectItems(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems, String layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        boolean pageDirection = layout != null && layout.equals("pageDirection");
        Object value = radio.getSubmittedValue();
        if(value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();
        
        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;
            boolean disabled = selectItem.isDisabled() || radio.isDisabled();
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;
            Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
            boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
            
            if(pageDirection) {
                writer.startElement("tr", null);
            }

            encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);

            if(pageDirection) {
                writer.endElement("tr");
            }
        }
    }
    
    protected void encodeOption(FacesContext context, SelectOneRadio radio, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, radio, converter, option.getValue());
       
        writer.startElement("td", null);

        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.RADIOBUTTON_CLASS, null);

        encodeOptionInput(context, radio, id, name, selected, disabled, itemValueAsString);
        encodeOptionOutput(context, radio, selected, disabled);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, radio, id, option, disabled);
        writer.endElement("td");
    }

    protected void encodeOptionInput(FacesContext context, SelectOneRadio radio, String id, String name, boolean checked, boolean disabled, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", value, null);

        if(radio.getTabindex() != null) writer.writeAttribute("tabindex", radio.getTabindex(), null);
        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(radio.getOnchange() != null) writer.writeAttribute("onchange", radio.getOnchange(), null);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectOneRadio radio, String containerClientId, SelectItem option, boolean disabled) throws IOException {
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

    protected void encodeOptionOutput(FacesContext context, SelectOneRadio radio, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
        boxClass = selected ? boxClass + " ui-state-active" : boxClass;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = !radio.isValid() ? boxClass + " ui-state-error" : boxClass;

        String iconClass = HTML.RADIOBUTTON_ICON_CLASS;
        iconClass = selected ? iconClass + " " + HTML.RADIOBUTTON_CHECKED_ICON_CLASS : iconClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }
        
    protected void encodeRadioButton(FacesContext context, SelectOneRadio radio, RadioButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
    }

    protected Class getValueType(FacesContext context, UIInput input) {
        ValueExpression ve = input.getValueExpression("value");
        Class type = ve == null ? String.class : ve.getType(context.getELContext());
        
        return type == null ? String.class : type;
    }
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context);
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
