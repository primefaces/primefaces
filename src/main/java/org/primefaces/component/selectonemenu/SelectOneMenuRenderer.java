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
        String value = context.getExternalContext().getRequestParameterMap().get(clientId + "_menu");

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
        String menuId = clientId + "_menu";

        writer.startElement("span", menu);
        writer.writeAttribute("id", clientId, "id");

        writer.startElement("select", menu);
        writer.writeAttribute("id", menuId, "id");
        writer.writeAttribute("name", menuId, null);

        renderPassThruAttributes(context, menu, HTML.SELECT_ONE_MENU_ATTRS);

        encodeSelectItems(context, menu);

        writer.endElement("select");

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(menu.resolveWidgetVar() + " = new PrimeFaces.widget.SelectOneMenu('" + clientId + "',{");

        writer.write("effect:'" + menu.getEffect() + "'");
        
        if(menu.getEffectDuration() != 400) writer.write(",effectDuration:" + menu.getEffectDuration());

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
}
