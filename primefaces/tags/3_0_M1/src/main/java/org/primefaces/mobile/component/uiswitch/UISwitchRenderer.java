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
package org.primefaces.mobile.component.uiswitch;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.InputRenderer;

public class UISwitchRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		UISwitch uiswitch = (UISwitch) component;

		String clientId = uiswitch.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);

        if(submittedValue != null && submittedValue.equalsIgnoreCase("on")) {
            uiswitch.setSubmittedValue(true);
        }
        else {
            uiswitch.setSubmittedValue(false);
        }
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UISwitch uiswitch = (UISwitch) component;
        String clientId = uiswitch.getClientId(context);
        Object value = uiswitch.getValue();
        boolean on = value == null ? false : (Boolean) value;

        writer.startElement("select", uiswitch);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("data-role", "slider", null);

        encodeOption(context, uiswitch.getOffLabel(), "off", !on);
        encodeOption(context, uiswitch.getOnLabel(), "on", on);

        writer.endElement("select");
    }

    public void encodeOption(FacesContext context, String itemLabel, String itemValue, boolean selected) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("option", null);
        writer.writeAttribute("value", itemValue, null);
        
        if(selected) {
            writer.writeAttribute("selected", "selected", null);
        }
        writer.writeText(itemLabel, null);
        writer.endElement("option");
    }
    
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		UISwitch uiswitch = (UISwitch) component;
		String value = (String) submittedValue;
		Converter converter = uiswitch.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(facesContext, uiswitch, value);
		}
		//Try to guess
		else {
            ValueExpression ve = uiswitch.getValueExpression("value");
            if(ve != null) {
                Class<?> valueType = ve.getType(facesContext.getELContext());
                Converter converterForType = facesContext.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(facesContext, uiswitch, value);
                }
            }
		}

		return value;
	}
}
