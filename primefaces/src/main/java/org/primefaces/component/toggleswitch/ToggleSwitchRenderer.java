/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ToggleSwitch.DEFAULT_RENDERER, componentFamily = ToggleSwitch.COMPONENT_FAMILY)
public class ToggleSwitchRenderer extends InputRenderer<ToggleSwitch> {

    @Override
    public void decode(FacesContext context, ToggleSwitch component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        boolean checked = isChecked(submittedValue);
        component.setSubmittedValue(checked);
    }

    @Override
    public void encodeEnd(FacesContext context, ToggleSwitch component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, ToggleSwitch component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean checked = Boolean.parseBoolean(ComponentUtils.getValueToRender(context, component));
        boolean disabled = component.isDisabled();
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(ToggleSwitch.CONTAINER_CLASS)
                .add(component.getStyleClass())
                .add(checked, ToggleSwitch.CHECKED_CLASS)
                .add(disabled, "ui-state-disabled")
                .add(component.isReadonly(), "ui-state-readonly")
                .add(component.getOffIcon() != null, "ui-toggleswitch-dual-icon")
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, component, clientId, checked);
        encodeSlider(context, component, checked);

        writer.endElement("div");
    }

    protected void encodeSlider(FacesContext context, ToggleSwitch component, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String styleClass = getStyleClassBuilder(context)
                    .add(ToggleSwitch.SLIDER_CLASS)
                    .add(!component.isValid(), "ui-state-error")
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", ToggleSwitch.HANDLER_CLASS, null);

        // on icon
        if (LangUtils.isNotEmpty(component.getOnIcon())) {
            writer.startElement("span", null);
            writer.writeAttribute("class", component.getOnIcon(), null);
            writer.endElement("span");
        }

        // off icon
        if (LangUtils.isNotEmpty(component.getOffIcon())) {
            writer.startElement("span", null);
            writer.writeAttribute("class", component.getOffIcon(), null);
            writer.endElement("span");
        }

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, ToggleSwitch component, String clientId, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        String ariaLabel = component.getAriaLabel() != null ? component.getAriaLabel() : component.getLabel();

        writer.startElement("div", component);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);

        if (checked && !component.isDisabled()) {
            writer.writeAttribute(HTML.ARIA_CHECKED, "true", null);
            writer.writeAttribute("checked", "checked", null);
        }

        renderValidationMetadata(context, component);
        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.TAB_INDEX);
        renderOnchange(context, component);
        renderDomEvents(context, component, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ToggleSwitch component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ToggleSwitch", component).finish();
    }

    protected boolean isChecked(String value) {
        if (value == null) {
            return false;
        }
        return "on".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }

    @Override
    public String getHighlighter() {
        return "toggleswitch";
    }
}

