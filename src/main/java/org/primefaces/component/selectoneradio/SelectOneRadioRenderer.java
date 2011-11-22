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
package org.primefaces.component.selectoneradio;

import java.io.IOException;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;

public class SelectOneRadioRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectOneRadio radio = (SelectOneRadio) component;

        if(radio.isDisabled()) {
            return;
        }

        decodeBehaviors(context, radio);

        String clientId = radio.getClientId(context);
        String value = context.getExternalContext().getRequestParameterMap().get(clientId);
        
        radio.setSubmittedValue(value);
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

        writer.startElement("table", radio);
        writer.writeAttribute("id", clientId, "id");
        if(style != null) writer.writeAttribute("style", style, "style");
        if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");

        encodeSelectItems(context, radio);

        writer.endElement("table");
    }

    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectOneRadio','" + radio.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(radio.isDisabled()) writer.write(",disabled: true");
        if(radio.isUnselectable()) writer.write(",unselectable: true");

        encodeClientBehaviors(context, radio);

        writer.write("});");

        endScript(writer);
    }

    protected void encodeOptionInput(FacesContext context, SelectOneRadio radio, String clientId, String containerClientId, boolean checked, boolean disabled, String label, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.RADIOBUTTON_INPUT_WRAPPER_CLASS, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", containerClientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", value, null);

        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(radio.getOnchange() != null) writer.writeAttribute("onchange", radio.getOnchange(), null);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectOneRadio radio, String containerClientId, String label) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        writer.write(label);
        writer.endElement("label");
    }

    protected void encodeOptionOutput(FacesContext context, SelectOneRadio radio, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = HTML.RADIOBUTTON_BOX_CLASS;
        styleClass = checked ? styleClass + " ui-state-active" : styleClass;

        String iconClass = HTML.RADIOBUTTON_ICON_CLASS;
        iconClass = checked ? iconClass + " " + HTML.RADIOBUTTON_CHECKED_ICON_CLASS : iconClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeSelectItems(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, radio);
        String layout = radio.getLayout();
        boolean pageDirection = layout != null && layout.equals("pageDirection");

        for(SelectItem selectItem : selectItems) {
            Object itemValue = selectItem.getValue();
            String itemLabel = selectItem.getLabel();

            if(pageDirection) {
                writer.startElement("tr", null);
            }

            encodeOption(context, radio, itemLabel, itemValue);

            if(pageDirection) {
                writer.endElement("tr");
            }
        }
    }

    protected void encodeOption(FacesContext context, SelectOneRadio radio, String itemLabel, Object itemValue) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = radio.getValue();
        Converter converter = getConverter(context, radio);
        
        String convertedValue = getOptionAsString(context, radio, converter, itemValue);
        String clientId = radio.getClientId(context);
        String containerClientId = radio.getContainerClientId(context);
        boolean disabled = radio.isDisabled();
        Class type = getValueType(context, radio);
        
        if(itemValue != null && !itemValue.equals("")) {
            itemValue = context.getApplication().getExpressionFactory().coerceToType(itemValue, type);
        }
        
        boolean checked = value != null && value.equals(itemValue);

        writer.startElement("td", null);

        String styleClass = HTML.RADIOBUTTON_CLASS;
        if(disabled) {
            styleClass += " ui-state-disabled";
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        encodeOptionInput(context, radio, clientId, containerClientId, checked, disabled, itemLabel, convertedValue);
        encodeOptionOutput(context, radio, checked);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, radio, containerClientId, itemLabel);
        writer.endElement("td");
    }
    
    protected Class getValueType(FacesContext context, UIInput input) {
        ValueExpression ve = input.getValueExpression("value");
        Class type = ve == null ? String.class : ve.getType(context.getELContext());
        
        return type == null ? String.class : type;
    }
}
