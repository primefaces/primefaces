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
package org.primefaces.component.selectbooleancheckbox;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;

public class SelectBooleanCheckboxRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		SelectBooleanCheckbox checkbox = (SelectBooleanCheckbox) component;

        if(checkbox.isDisabled() || checkbox.isReadonly()) {
            return;
        }

        decodeBehaviors(context, checkbox);

		String clientId = checkbox.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId + "_checkbox");

        if(submittedValue != null && submittedValue.equalsIgnoreCase("on")) {
            checkbox.setSubmittedValue(true);
        }
        else {
            checkbox.setSubmittedValue(false);
        }
	}


    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectBooleanCheckbox checkbox = (SelectBooleanCheckbox) component;

        encodeMarkup(context, checkbox);
        encodeScript(context, checkbox);
    }

    protected void encodeMarkup(FacesContext context, SelectBooleanCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String checkboxId = clientId + "_checkbox";
        Boolean checked = (Boolean) checkbox.getValue();

        writer.startElement("span", checkbox);
        writer.writeAttribute("id", clientId, "id");

        writer.startElement("input", null);
        writer.writeAttribute("id", checkboxId, "id");
        writer.writeAttribute("name", checkboxId, null);
        writer.writeAttribute("type", "checkbox", null);

        if(checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderPassThruAttributes(context, checkbox, HTML.SELECT_ATTRS);

        writer.endElement("input");

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, SelectBooleanCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(checkbox.resolveWidgetVar() + " = new PrimeFaces.widget.SelectBooleanCheckbox({id:'" + clientId + "'");
        
        encodeClientBehaviors(context, checkbox);

        writer.write("});");

        writer.endElement("script");
    }
}
