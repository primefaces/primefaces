/*
 * Copyright 2009-2013 PrimeTek.
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class UISwitchRenderer extends CoreRenderer {
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
		UISwitch uiswitch = (UISwitch) component;
        if(uiswitch.isDisabled()) {
            return;
        }
             
        String clientId = uiswitch.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);

        if(submittedValue != null && isChecked(submittedValue)) {
            uiswitch.setSubmittedValue(true);
        }
        else {
            uiswitch.setSubmittedValue(false);
        }
	}
    
    protected boolean isChecked(String value) {
        return value.equalsIgnoreCase("on")||value.equalsIgnoreCase("yes")||value.equalsIgnoreCase("true");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UISwitch uiswitch = (UISwitch) component;
        String clientId = uiswitch.getClientId(context);
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, uiswitch));
        String onLabel = uiswitch.getOnLabel();
        String offLabel = uiswitch.getOffLabel();        

        writer.startElement("input", uiswitch);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("data-role", "flipswitch", null);
        writer.writeAttribute("type", "checkbox", null);
        if (checked) writer.writeAttribute("checked", "checked", null);
        if (onLabel != null) writer.writeAttribute("data-on-text", onLabel, null);
        if (offLabel != null) writer.writeAttribute("data-off-text", offLabel, null);
        if (uiswitch.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        
        renderOnchange(context, component);
        
        writer.endElement("input");
    }
}