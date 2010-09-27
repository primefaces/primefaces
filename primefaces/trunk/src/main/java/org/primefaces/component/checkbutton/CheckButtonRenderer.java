/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.checkbutton;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class CheckButtonRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        CheckButton button = (CheckButton) component;
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String paramName = button.getClientId(context) + "_input";

        if(params.containsKey(paramName)) {
            button.setSubmittedValue(Boolean.TRUE);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        CheckButton button = (CheckButton) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, CheckButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        String inputId = clientId + "_input";
        Boolean checked = (Boolean) button.getValue();
        String label = button.getLabel();
        
        writer.startElement("span", button);
        writer.writeAttribute("id", clientId, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        if(checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        writer.endElement("input");
        
        writer.startElement("label", null);
        writer.writeAttribute("for", inputId, null);
        if(label != null) {
            writer.write(label);
        }
        writer.endElement("label");
       

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, CheckButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(button.resolveWidgetVar() + " = new PrimeFaces.widget.CheckButton('" + clientId + "', {");

        writer.write("});");

        writer.endElement("script");
    }
}
