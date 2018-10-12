/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.toggleswitch;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class ToggleSwitchRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ToggleSwitch toggleSwitch = (ToggleSwitch) component;

        if (!shouldDecode(toggleSwitch)) {
            return;
        }

        decodeBehaviors(context, toggleSwitch);

        String clientId = toggleSwitch.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (submittedValue != null && isChecked(submittedValue)) {
            toggleSwitch.setSubmittedValue(true);
        }
        else {
            toggleSwitch.setSubmittedValue(false);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ToggleSwitch toggleSwitch = (ToggleSwitch) component;

        encodeMarkup(context, toggleSwitch);
        encodeScript(context, toggleSwitch);
    }

    protected void encodeMarkup(FacesContext context, ToggleSwitch toggleSwitch) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = toggleSwitch.getClientId(context);
        boolean checked = Boolean.parseBoolean(ComponentUtils.getValueToRender(context, toggleSwitch));
        boolean disabled = toggleSwitch.isDisabled();
        String style = toggleSwitch.getStyle();
        String styleClass = toggleSwitch.getStyleClass();
        styleClass = (styleClass == null) ? ToggleSwitch.CONTAINER_CLASS : ToggleSwitch.CONTAINER_CLASS + " " + styleClass;
        styleClass = (checked) ? styleClass + " " + ToggleSwitch.CHECKED_CLASS : styleClass;
        if (disabled) {
            styleClass = styleClass + " ui-state-disabled";
        }

        writer.startElement("div", toggleSwitch);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute("role", "checkbox", null);
        writer.writeAttribute(HTML.ARIA_CHECKED, checked, null);
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, toggleSwitch, clientId, checked);
        encodeSlider(context);

        writer.endElement("div");
    }

    protected void encodeSlider(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", ToggleSwitch.SLIDER_CLASS, null);
        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, ToggleSwitch toggleSwitch, String clientId, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("div", toggleSwitch);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderValidationMetadata(context, toggleSwitch);
        renderAccessibilityAttributes(context, toggleSwitch);
        renderPassThruAttributes(context, toggleSwitch, HTML.TAB_INDEX);
        renderOnchange(context, toggleSwitch);
        renderDomEvents(context, toggleSwitch, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ToggleSwitch toggleSwitch) throws IOException {
        String clientId = toggleSwitch.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ToggleSwitch", toggleSwitch.resolveWidgetVar(), clientId).finish();
    }

    protected boolean isChecked(String value) {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }
}

