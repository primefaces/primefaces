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
package org.primefaces.component.selectmanycheckbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;

public class SelectManyCheckboxRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;

        if(checkbox.isDisabled() || checkbox.isReadonly()) {
            return;
        }

        decodeBehaviors(context, checkbox);

        String clientId = checkbox.getClientId(context);
        String[] values = context.getExternalContext().getRequestParameterValuesMap().get(clientId);

        if(values != null) {
            checkbox.setSubmittedValue(values);
        }
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
        String layout = checkbox.getLayout();
        if(layout == null) {
            layout = "lineDirection";
        }

        writer.startElement("span", checkbox);
        writer.writeAttribute("id", clientId, "id");
        if(layout.equals("lineDirection")) {
            writer.writeAttribute("class", "wijmo-checkbox-horizontal", null);
        }

        encodeSelectItems(context, checkbox);

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(checkbox.resolveWidgetVar() + " = new PrimeFaces.widget.SelectManyCheckbox({id:'" + clientId + "'");

        encodeClientBehaviors(context, checkbox);

        writer.write("});");

        writer.endElement("script");
    }

    @Override
    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, Converter converter, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String formattedValue = formatOptionValue(context, component, converter, value);
        String clientId = component.getClientId(context);
        String containerClientId = component.getContainerClientId(context);

        writer.startElement("input", null);
        writer.writeAttribute("id", containerClientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", formattedValue, null);
        
        if(componentValue != null && ((List) componentValue).contains(value)) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderPassThruAttributes(context, component, HTML.SELECT_ATTRS);

        writer.endElement("input");

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        writer.write(label);
        writer.endElement("label");
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
		String[] values = (String[]) submittedValue;
		Converter converter = getConverter(context, checkbox);
        List list = null;

        if(converter != null) {
            list = new ArrayList();

            for(String value : values) {
                list.add(converter.getAsObject(context, checkbox, value));
            }
        }
        else {
            list = Arrays.asList(values);
        }

        return list;
	}
}
