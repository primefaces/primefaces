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
package org.primefaces.mobile.component.uiswitch;

import java.io.IOException;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class UISwitchRenderer extends CoreRenderer {
    
    private final static Logger logger = Logger.getLogger(UISwitchRenderer.class.getName());
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
		UISwitch uiswitch = (UISwitch) component;
        if(uiswitch.isDisabled()) {
            return;
        }
             
        String clientId = uiswitch.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

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
        UISwitch uiswitch = (UISwitch) component;
        
        encodeMarkup(context, uiswitch);
        encodeScript(context, uiswitch);
        
        logger.info("Mobile only switch component is deprecated, use p:inputSwitch instead.");
    }
    
    public void encodeMarkup(FacesContext context, UISwitch uiswitch) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = uiswitch.getClientId(context);
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, uiswitch));
        String inputId = clientId + "_input";
        String onLabel = uiswitch.getOnLabel();
        String offLabel = uiswitch.getOffLabel();
        String style = uiswitch.getStyle();
        String styleClass = uiswitch.getStyleClass();
        styleClass = (styleClass == null) ? UISwitch.CONTAINER_CLASS: UISwitch.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", uiswitch);
        writer.writeAttribute("id", clientId, "id");
        if(style != null) writer.writeAttribute("style", style, "style");
        if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");
        
        writer.startElement("span", uiswitch);
        writer.writeAttribute("class", UISwitch.ON_CLASS, null);
        writer.writeText(onLabel, null);
        writer.endElement("span");
        
        writer.startElement("span", uiswitch);
        writer.writeAttribute("class", UISwitch.OFF_CLASS, null);
        writer.writeText(offLabel, null);
        writer.endElement("span");
        
        writer.startElement("input", uiswitch);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("data-role", "none", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("class", UISwitch.INPUT_CLASS, null);
        
        if (checked) writer.writeAttribute("checked", "checked", null);
        if (uiswitch.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        
        renderOnchange(context, uiswitch);
        
        writer.endElement("input");
        
        writer.endElement("div");
    }
    
    public void encodeScript(FacesContext context, UISwitch uiswitch) throws IOException {
        String clientId = uiswitch.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("UISwitch", uiswitch.resolveWidgetVar(), clientId).finish();
    }
}