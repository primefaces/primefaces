/*
 * Copyright 2009-2014 PrimeTek.
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.button.Button;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.util.HTML;

public class ButtonRenderer extends org.primefaces.component.button.ButtonRenderer {
    
    @Override
    public void encodeMarkup(FacesContext context, Button button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
        Object value = (String) button.getValue();
        String icon = button.resolveIcon();

		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", resolveStyleClass(button), "styleClass");

		renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

        if(button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        else {
            writer.writeAttribute("onclick", buildOnclick(context, button), null);
        }

        if(value == null) {
            writer.write("ui-button");
        }
        else {
            if(button.isEscape())
                writer.writeText(value, "value");
            else
                writer.write(value.toString());
        }
			
		writer.endElement("button");
    }

    @Override
    public void encodeScript(FacesContext context, Button button) throws IOException {
        //no widget
    }
    
    @Override
    protected String buildOnclick(FacesContext context, Button button) {
        String command = null;
        String outcome = button.getOutcome();
        
        if(outcome != null && outcome.startsWith("pm:")) {
            command = MobileUtils.buildNavigation(outcome);
        }
        else {
            String targetURL = getTargetURL(context, button);
            if(targetURL != null) {
                command = "window.open('" + targetURL + "','" + button.getTarget() + "')";
            }
        }
        
        return buildDomEvent(context, button, "onclick", "click", "action", command);
    }
    
    public String resolveStyleClass(Button button) {
        String icon = button.getIcon();
        String iconPos = button.getIconPos();
        Object value = button.getValue();
        String styleClass = "ui-btn ui-shadow ui-corner-all";
            
        if(value != null && icon != null) {
            styleClass = styleClass + " " + icon + " ui-btn-icon-" + iconPos;
        } else if(value == null && icon != null) {
            styleClass = styleClass + " " + icon + " ui-btn-icon-notext";
        }
    
        if(button.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        } 
    
        String userStyleClass = button.getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
    
        return styleClass;
    }
}
