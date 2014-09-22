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
package org.primefaces.component.inputswitch;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class InputSwitchRenderer extends InputRenderer {
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputSwitch inputSwitch = (InputSwitch) component;

        if(inputSwitch.isDisabled()) {
            return;
        }

        decodeBehaviors(context, inputSwitch);

		String clientId = inputSwitch.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if(submittedValue != null && isChecked(submittedValue)) {
            inputSwitch.setSubmittedValue(true);
        }
        else {
            inputSwitch.setSubmittedValue(false);
        }
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputSwitch inputSwitch = (InputSwitch) component;

        encodeMarkup(context, inputSwitch);
        encodeScript(context, inputSwitch);
    }
    
    protected void encodeMarkup(FacesContext context, InputSwitch inputSwitch) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, inputSwitch));
        boolean disabled = inputSwitch.isDisabled();
        boolean showLabels = inputSwitch.isShowLabels();
        String clientId = inputSwitch.getClientId(context);
        String style = inputSwitch.getStyle();
        String styleClass = inputSwitch.getStyleClass();
        styleClass = (styleClass == null) ? InputSwitch.CONTAINER_CLASS : InputSwitch.CONTAINER_CLASS + " " + styleClass; 
        if(inputSwitch.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }
        
        writer.startElement("div", inputSwitch);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeOption(context, inputSwitch.getOffLabel(), InputSwitch.OFF_LABEL_CLASS, showLabels);
        encodeOption(context, inputSwitch.getOnLabel(), InputSwitch.ON_LABEL_CLASS, showLabels);
        encodeHandle(context);
        encodeInput(context, inputSwitch, clientId, checked, disabled);
                
        writer.endElement("div");
    }
    
    protected void encodeOption(FacesContext context, String label, String styleClass, boolean showLabels) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        writer.startElement("span", null);
        
        if(showLabels)
            writer.writeText(label, null);
        else
            writer.write("&nbsp;");
        
        writer.endElement("span");
        writer.endElement("div");
    }
    
    protected void encodeHandle(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", InputSwitch.HANDLE_CLASS, null);
        writer.endElement("div");
    }
        
    protected void encodeInput(FacesContext context, InputSwitch inputSwitch, String clientId, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        
        writer.startElement("div", inputSwitch);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);

        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(inputSwitch.getTabindex() != null) writer.writeAttribute("tabindex", inputSwitch.getTabindex(), null);
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, inputSwitch);
        }
        
        renderOnchange(context, inputSwitch);
        
        writer.endElement("input");

        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, InputSwitch inputSwitch) throws IOException {
        String clientId = inputSwitch.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputSwitch", inputSwitch.resolveWidgetVar(), clientId, "inputswitch").finish();
    }
    
    protected boolean isChecked(String value) {
        return value.equalsIgnoreCase("on")||value.equalsIgnoreCase("yes")||value.equalsIgnoreCase("true");
    }
}
