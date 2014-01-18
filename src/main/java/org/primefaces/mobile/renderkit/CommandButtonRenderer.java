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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class CommandButtonRenderer extends org.primefaces.component.commandbutton.CommandButtonRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		CommandButton button = (CommandButton) component;
        String clientId = button.getClientId(context);
        Object value = button.getValue(); 
        String type = button.getType();
        String icon = button.getIcon();
        String iconPos = (value == null && icon != null) ? "notext" : button.getIconPos();
        String request = null;
        
		writer.startElement("input", button);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", type, null);
        
        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        
        if (icon != null) {
            writer.writeAttribute("data-icon", icon, null);
            writer.writeAttribute("data-iconpos", iconPos, null);
        }
                
        if (!type.equals("reset") && !type.equals("button")) {
            if(button.isAjax()) {
                 request = buildAjaxRequest(context, button, null);
            }
            else {
                UIComponent form = ComponentUtils.findParentForm(context, button);
                if(form == null) {
                    throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
                }
                
                request = buildNonAjaxRequest(context, button, form, null, false);
            };
		}
        
        String onclick = buildDomEvent(context, button, "onclick", "click", "action", request);
		if (onclick.length() > 0) {
            writer.writeAttribute("onclick", onclick, "onclick");
		}
		
        renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
        renderDynamicPassThruAttributes(context, component);
        
        if (button.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if (button.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        
        writer.endElement("input");
	}
}
