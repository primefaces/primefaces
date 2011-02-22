/*
 * Copyright 2009-2010 Prime Technology.
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
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;

public class SelectOneMenuRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectOneMenu menu = (SelectOneMenu) component;

        if(menu.isDisabled() || menu.isReadonly()) {
            return;
        }

        decodeBehaviors(context, menu);

        String clientId = menu.getClientId(context);
        String value = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if(value != null) {
            menu.setSubmittedValue(value);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneMenu menu = (SelectOneMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected void encodeMarkup(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleclass = menu.getStyleClass();
        styleclass = styleclass == null ? SelectOneMenu.STYLE_CLASS : SelectOneMenu.STYLE_CLASS + " " + styleclass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleclass, "styleclass");
        if(style != null)
            writer.writeAttribute("style", style, "style");

        encodeInput(context, menu, clientId);
        encodeLabel(context, menu);
        encodeMenuIcon(context, menu);
        encodePanel(context, menu);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, SelectOneMenu menu, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        writer.startElement("select", menu);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);

        encodeSelectItems(context, menu);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeLabel(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("label", null);
        writer.writeAttribute("class", SelectOneMenu.LABEL_CLASS, null);

        writer.writeText(menu.getSelectedLabel(), null);

        writer.endElement("label");
    }

    protected void encodeMenuIcon(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", SelectOneMenu.TRIGGER_CLASS, null);

        writer.startElement("span", menu);
        writer.writeAttribute("class", "ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneMenu.PANEL_CLASS, null);

        writer.startElement("ul", menu);
        writer.writeAttribute("class", SelectOneMenu.LIST_CLASS, null);
        writer.endElement("ul");
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(menu.resolveWidgetVar() + " = new PrimeFaces.widget.SelectOneMenu('" + clientId + "',{");

        writer.write("effect:'" + menu.getEffect() + "'");
        
        if(menu.getEffectDuration() != 400) {
            writer.write(",effectDuration:" + menu.getEffectDuration());
        }

        encodeClientBehaviors(context, menu);

        writer.write("});");

        writer.endElement("script");
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		SelectOneMenu menu = (SelectOneMenu) component;
		String value = (String) submittedValue;
		Converter converter = menu.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, menu, value);
		}
		//Try to guess
		else {
            ValueExpression ve = menu.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, menu, value);
                }
            }
		}

		return value;
	}

    @Override
    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, Converter converter, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectOneMenu menu = (SelectOneMenu) component;
        String formattedValue = formatOptionValue(context, component, converter, value);

        writer.startElement("option", null);
        writer.writeAttribute("value", formattedValue, null);
        
        if((componentValue == null && value.equals("")) || (componentValue != null && componentValue.equals(value))) {
            writer.writeAttribute("selected", "selected", null);
            menu.setSelectedLabel(label);
        }
        
        writer.write(label);
        writer.endElement("option");
    }
}
