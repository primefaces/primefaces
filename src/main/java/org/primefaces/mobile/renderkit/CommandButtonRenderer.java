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
import org.primefaces.util.SharedStringBuilder;

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
        StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);

        if (button.getOnclick() != null) {
            onclick.append(button.getOnclick()).append(";");
        }

        String onclickBehaviors = getEventBehaviors(context, button, "click");
        if (onclickBehaviors != null) {
            onclick.append(onclickBehaviors);
        }
        
		writer.startElement("input", button);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", type, null);
        
        if (icon != null) {
            writer.writeAttribute("data-icon", icon, null);
            writer.writeAttribute("data-iconpos", iconPos, null);
        }
        
        if (icon != null) {
            writer.writeAttribute("data-icon", icon, null);
        }
        
        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        
        if (!type.equals("reset") && !type.equals("button")) {
            String request;
            boolean ajax = button.isAjax();
			
            if(ajax) {
                 request = buildAjaxRequest(context, button, null);
            }
            else {
                UIComponent form = ComponentUtils.findParentForm(context, button);
                if(form == null) {
                    throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
                }
                
                request = buildNonAjaxRequest(context, button, form, null, false);
            }
            			
            onclick.append(request);
		}

		if (onclick.length() > 0) {
            writer.writeAttribute("onclick", onclick.toString(), "onclick");
		}
		
		//renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
        renderDynamicPassThruAttributes(context, component);
        
        if(button.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if(button.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        
        writer.endElement("input");
	}
}
