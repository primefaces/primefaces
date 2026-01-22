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
package org.primefaces.component.selectbooleanbutton;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Objects;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = SelectBooleanButton.DEFAULT_RENDERER, componentFamily = SelectBooleanButton.COMPONENT_FAMILY)
public class SelectBooleanButtonRenderer extends InputRenderer<SelectBooleanButton> {

    @Override
    public void decode(FacesContext context, SelectBooleanButton component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        boolean checked = "on".equalsIgnoreCase(submittedValue);
        component.setSubmittedValue(checked);
    }

    @Override
    public void encodeEnd(FacesContext context, SelectBooleanButton component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }


    protected void encodeMarkup(FacesContext context, SelectBooleanButton component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean checked = Boolean.parseBoolean(ComponentUtils.getValueToRender(context, component));
        boolean disabled = component.isDisabled();
        String inputId = clientId + "_input";
        String label = checked ? component.getOnLabel() : component.getOffLabel();
        String icon = checked ? component.getOnIcon() : component.getOffIcon();
        boolean hasIcon = icon != null;
        String title = component.getTitle();
        String style = component.getStyle();
        String styleClass = "ui-selectbooleanbutton " + component.resolveStyleClass(checked, disabled);

        // button
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        // input
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute(HTML.ARIA_LABEL, label, null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderValidationMetadata(context, component);
        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.TAB_INDEX);
        renderOnchange(context, component);
        renderDomEvents(context, component, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");

        // icon
        if (hasIcon) {
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }

        // label
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        renderButtonValue(writer, true, label, component.getTitle(), component.getAriaLabel());

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectBooleanButton component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectBooleanButton", component)
                    .attr("onLabel", component.getOnLabel(), null)
                    .attr("offLabel", component.getOffLabel())
                    .attr("onIcon", component.getOnIcon(), null)
                    .attr("offIcon", component.getOffIcon(), null);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        if (submittedValue instanceof Boolean) {
            return submittedValue;
        }
        Object convertedValue = ComponentUtils.getConvertedValue(context, component, submittedValue);
        return ((convertedValue instanceof Boolean) ? convertedValue : Boolean.valueOf(Objects.toString(convertedValue)));
    }

    @Override
    protected String getHighlighter() {
        return "booleanbutton";
    }
}
