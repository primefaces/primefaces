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
package org.primefaces.component.selectonelistbox;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.InputRenderer;

public class SelectOneListboxRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectOneListbox listbox = (SelectOneListbox) component;

        if(listbox.isDisabled()) {
            return;
        }

        decodeBehaviors(context, listbox);

        String clientId = listbox.getClientId(context);
        String value = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        listbox.setSubmittedValue(value);
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
        String style = listbox.getStyle();
        String styleClass = listbox.getStyleClass();
        styleClass = styleClass == null ? SelectOneListbox.CONTAINER_CLASS : SelectOneListbox.CONTAINER_CLASS + " " + styleClass;
        styleClass = listbox.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) writer.writeAttribute("style", style, "style");

        encodeInput(context, listbox, clientId);
        encodeList(context, listbox);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(listbox.resolveWidgetVar() + " = new PrimeFaces.widget.SelectListbox('" + clientId + "',{");
        writer.write("selection:'single'");

        if(listbox.isDisabled()) writer.write(",disabled:true");

        encodeClientBehaviors(context, listbox);

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeInput(FacesContext context, SelectOneListbox listbox, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";

        writer.startElement("div", listbox);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        
        writer.startElement("select", listbox);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected
        if(listbox.getOnchange() != null) writer.writeAttribute("onchange", listbox.getOnchange(), null);

        encodeSelectItems(context, listbox);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", null);

        writer.endElement("ul");
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		SelectOneListbox listbox = (SelectOneListbox) component;
		String value = (String) submittedValue;
		Converter converter = listbox.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, listbox, value);
		}
		//Try to guess
		else {
            ValueExpression ve = listbox.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, listbox, value);
                }
            }
		}

		return value;
	}

    @Override
    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, Converter converter, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String formattedValue = formatOptionValue(context, component, converter, value);

        writer.startElement("option", null);
        writer.writeAttribute("value", formattedValue, null);
        if(componentValue != null && componentValue.equals(value)) {
            writer.writeAttribute("selected", "selected", null);
        }
        writer.write(label);
        writer.endElement("option");
    }
}
