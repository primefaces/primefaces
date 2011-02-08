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
package org.primefaces.component.selectoneradio;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;

public class SelectOneRadioRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectOneRadio radio = (SelectOneRadio) component;

        if(radio.isDisabled() || radio.isReadonly()) {
            return;
        }

        decodeBehaviors(context, radio);

        String clientId = radio.getClientId(context);
        String value = context.getExternalContext().getRequestParameterMap().get(clientId);

        if(value != null) {
            radio.setSubmittedValue(value);
        }
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
        String layout = radio.getLayout();
        if(layout == null) {
            layout = "lineDirection";
        }

        writer.startElement("span", radio);
        writer.writeAttribute("id", clientId, "id");
        if(layout.equals("lineDirection")) {
            writer.writeAttribute("class", "wijmo-wijradio-horizontal", null);
        }

        encodeSelectItems(context, radio);

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(radio.resolveWidgetVar() + " = new PrimeFaces.widget.SelectOneRadio({id:'" + clientId + "'");

        encodeClientBehaviors(context, radio);

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
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", formattedValue, null);
        if(componentValue != null && componentValue.equals(value)) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderPassThruAttributes(context, component, HTML.SELECT_ATTRS);

        writer.endElement("input");

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        writer.write(label);
        writer.endElement("label");
    }
}
