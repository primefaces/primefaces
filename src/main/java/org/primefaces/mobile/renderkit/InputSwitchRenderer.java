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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.inputswitch.InputSwitch;
import org.primefaces.mobile.component.uiswitch.UISwitch;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class InputSwitchRenderer extends org.primefaces.component.inputswitch.InputSwitchRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputSwitch inputSwitch = (InputSwitch) component;

        encodeMarkup(context, inputSwitch);
        encodeScript(context, inputSwitch);
    }
    
    @Override
    public void encodeMarkup(FacesContext context, InputSwitch inputSwitch) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputSwitch.getClientId(context);
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, inputSwitch));
        String inputId = clientId + "_input";
        String onLabel = inputSwitch.getOnLabel();
        String offLabel = inputSwitch.getOffLabel();
        String style = inputSwitch.getStyle();
        String styleClass = inputSwitch.getStyleClass();
        styleClass = (styleClass == null) ? UISwitch.CONTAINER_CLASS: UISwitch.CONTAINER_CLASS + " " + styleClass;
        if(checked) {
            styleClass = styleClass + "  ui-flipswitch-active";
        }
        
        writer.startElement("div", inputSwitch);
        writer.writeAttribute("id", clientId, "id");
        if(style != null) writer.writeAttribute("style", style, "style");
        if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");
        
        renderDynamicPassThruAttributes(context, inputSwitch);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", UISwitch.ON_CLASS, null);
        writer.writeText(onLabel, null);
        writer.endElement("span");
        
        writer.startElement("span", null);
        writer.writeAttribute("class", UISwitch.OFF_CLASS, null);
        writer.writeText(offLabel, null);
        writer.endElement("span");
        
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("data-role", "none", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("class", UISwitch.INPUT_CLASS, null);
        
        if (checked) writer.writeAttribute("checked", "checked", null);
        if (inputSwitch.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        
        renderOnchange(context, inputSwitch);
        
        writer.endElement("input");
        
        writer.endElement("div");
    }
    
    @Override
    public void encodeScript(FacesContext context, InputSwitch inputSwitch) throws IOException {
        String clientId = inputSwitch.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("UISwitch", inputSwitch.resolveWidgetVar(), clientId).finish();
    }
}
