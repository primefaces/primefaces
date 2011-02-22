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
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.InputRenderer;

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
        styleClass = styleClass == null ? SelectOneRadio.STYLE_CLASS : SelectOneRadio.STYLE_CLASS + " " + styleClass;

        writer.startElement("table", radio);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null)
            writer.writeAttribute("style", style, "style");

        encodeSelectItems(context, radio);

        writer.endElement("table");
    }

    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(radio.resolveWidgetVar() + " = new PrimeFaces.widget.SelectOneRadio({id:'" + clientId + "'");
        
        if(radio.isDisabled()) writer.write(",disabled: true");
        if(radio.isUnselectable()) writer.write(",unselectable: true");

        encodeClientBehaviors(context, radio);

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeOptionInput(FacesContext context, SelectOneRadio radio, String clientId, String containerClientId, boolean checked, boolean disabled, String label, String formattedValue) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-radiobutton-inputwrapper", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", containerClientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", formattedValue, null);

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
        String styleClass = "ui-radiobutton-box ui-widget ui-corner-all ui-radiobutton-relative ui-state-default";
        styleClass = checked ? styleClass + " ui-state-active" : styleClass;

        String iconClass = "ui-radiobutton-icon";
        iconClass = checked ? iconClass + " ui-icon ui-icon-bullet" : iconClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		SelectOneRadio radio = (SelectOneRadio) component;
		String value = (String) submittedValue;
		Converter converter = radio.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, radio, value);
		}
		//Try to guess
		else {
            ValueExpression ve = radio.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, radio, value);
                }
            }
		}

		return value;
	}

    protected void encodeSelectItems(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, radio);
        Converter converter = getConverter(context, radio);
        Object value = radio.getValue();
        String layout = radio.getLayout();
        boolean pageDirection = layout != null && layout.equals("pageDirection");

        for(SelectItem selectItem : selectItems) {
            Object itemValue = selectItem.getValue();
            String itemLabel = selectItem.getLabel();

            if(pageDirection) {
                writer.startElement("tr", null);
            }

            encodeOption(context, radio, value, converter, itemLabel, itemValue);

            if(pageDirection) {
                writer.endElement("tr");
            }
        }
    }

    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, Converter converter, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectOneRadio radio = (SelectOneRadio) component;
        String formattedValue = getOptionAsString(context, component, converter, value);
        String clientId = component.getClientId(context);
        String containerClientId = component.getContainerClientId(context);
        boolean checked = componentValue != null && componentValue.equals(value);
        boolean disabled = radio.isDisabled();

        writer.startElement("td", null);

        String styleClass = "ui-radiobutton ui-widget";
        if(disabled) {
            styleClass += " ui-state-disabled";
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        encodeOptionInput(context, radio, clientId, containerClientId, checked, disabled, label, formattedValue);
        encodeOptionOutput(context, radio, checked);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, radio, containerClientId, label);
        writer.endElement("td");
    }
}
