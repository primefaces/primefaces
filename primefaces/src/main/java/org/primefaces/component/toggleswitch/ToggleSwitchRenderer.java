/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

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
        boolean checked = isChecked(submittedValue);
        toggleSwitch.setSubmittedValue(checked);
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
        String styleClass = getStyleClassBuilder(context)
                .add(ToggleSwitch.CONTAINER_CLASS)
                .add(toggleSwitch.getStyleClass())
                .add(checked, ToggleSwitch.CHECKED_CLASS)
                .add(disabled, "ui-state-disabled")
                .add(toggleSwitch.getOffIcon() != null, "ui-toggleswitch-dual-icon")
                .build();

        writer.startElement("div", toggleSwitch);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, toggleSwitch, clientId, checked);
        encodeSlider(context, toggleSwitch, checked);

        writer.endElement("div");
    }

    protected void encodeSlider(FacesContext context, ToggleSwitch toggleSwitch, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String styleClass = getStyleClassBuilder(context)
                    .add(ToggleSwitch.SLIDER_CLASS)
                    .add(!toggleSwitch.isValid(), "ui-state-error")
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", ToggleSwitch.HANDLER_CLASS, null);

        // on icon
        if (LangUtils.isNotEmpty(toggleSwitch.getOnIcon())) {
            writer.startElement("span", null);
            writer.writeAttribute("class", toggleSwitch.getOnIcon(), null);
            writer.endElement("span");
        }

        // off icon
        if (LangUtils.isNotEmpty(toggleSwitch.getOffIcon())) {
            writer.startElement("span", null);
            writer.writeAttribute("class", toggleSwitch.getOffIcon(), null);
            writer.endElement("span");
        }

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, ToggleSwitch toggleSwitch, String clientId, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        String ariaLabel = toggleSwitch.getAriaLabel() != null ? toggleSwitch.getAriaLabel() : toggleSwitch.getLabel();

        writer.startElement("div", toggleSwitch);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_CHECKED, checked, null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);

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
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ToggleSwitch", toggleSwitch).finish();
    }

    protected boolean isChecked(String value) {
        return value != null
                && ("on".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value));
    }

    @Override
    public String getHighlighter() {
        return "toggleswitch";
    }
}

