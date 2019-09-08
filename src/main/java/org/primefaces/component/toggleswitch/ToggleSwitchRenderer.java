/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
        wb.init("ToggleSwitch", toggleSwitch.resolveWidgetVar(context), clientId).finish();
    }

    protected boolean isChecked(String value) {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }
}

