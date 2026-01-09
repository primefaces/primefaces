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
package org.primefaces.component.selectbooleancheckbox;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = SelectBooleanCheckbox.DEFAULT_RENDERER, componentFamily = SelectBooleanCheckbox.COMPONENT_FAMILY)
public class SelectBooleanCheckboxRenderer extends InputRenderer<SelectBooleanCheckbox> {

    @Override
    public void decode(FacesContext context, SelectBooleanCheckbox component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        boolean checked = isChecked(submittedValue);
        component.setSubmittedValue(checked);
    }

    protected boolean isChecked(String value) {
        return value != null
                && ("on".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value));
    }

    @Override
    public void encodeEnd(FacesContext context, SelectBooleanCheckbox component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, SelectBooleanCheckbox component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean checked = Boolean.parseBoolean(ComponentUtils.getValueToRender(context, component));
        boolean disabled = component.isDisabled();
        String title = component.getTitle();

        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add("ui-selectbooleancheckbox")
                .add(HTML.CHECKBOX_CLASS)
                .add(component.getStyleClass())
                .add(component.isReadonly(), "ui-state-readonly")
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (title != null) {
            writer.writeAttribute("title", title, "title");
        }

        encodeInput(context, component, clientId, checked);
        encodeOutput(context, component, checked, disabled);
        encodeItemLabel(context, component, clientId, disabled);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, SelectBooleanCheckbox component, String clientId, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";
        String ariaLabel = component.getAriaLabel() != null ? component.getAriaLabel() : component.getItemLabel();

        writer.startElement("div", component);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);

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
    }

    protected void encodeOutput(FacesContext context, SelectBooleanCheckbox component, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = createStyleClass(component, null, HTML.CHECKBOX_BOX_CLASS);
        styleClass = getStyleClassBuilder(context)
                .add(styleClass)
                .add(checked, "ui-state-active")
                .build();

        String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeItemLabel(FacesContext context, SelectBooleanCheckbox component, String clientId, boolean disabled) throws IOException {
        String label = component.getItemLabel();

        if (label != null) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("span", null);
            String styleClass = HTML.CHECKBOX_LABEL_CLASS;
            styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;
            writer.writeAttribute("class", styleClass, null);

            if (component.isEscape()) {
                writer.writeText(label, "itemLabel");
            }
            else {
                writer.write(label);
            }

            writer.endElement("span");
        }
    }

    protected void encodeScript(FacesContext context, SelectBooleanCheckbox checkbox) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectBooleanCheckbox", checkbox).finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return ((submittedValue instanceof Boolean) ? submittedValue : Boolean.valueOf(submittedValue.toString()));
    }

    @Override
    public String getHighlighter() {
        return "booleanchkbox";
    }
}
