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
package org.primefaces.component.inputswitch;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class InputSwitchRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        InputSwitch inputSwitch = (InputSwitch) component;

        if (!shouldDecode(inputSwitch)) {
            return;
        }

        decodeBehaviors(context, inputSwitch);

        String clientId = inputSwitch.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (submittedValue != null && isChecked(submittedValue)) {
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
        boolean checked = Boolean.parseBoolean(ComponentUtils.getValueToRender(context, inputSwitch));
        boolean showLabels = inputSwitch.isShowLabels();
        String clientId = inputSwitch.getClientId(context);
        String style = inputSwitch.getStyle();
        String styleClass = inputSwitch.getStyleClass();
        styleClass = (styleClass == null) ? InputSwitch.CONTAINER_CLASS : InputSwitch.CONTAINER_CLASS + " " + styleClass;
        styleClass = (checked) ? styleClass + " " + InputSwitch.CHECKED_CLASS : styleClass;
        if (inputSwitch.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }

        writer.startElement("div", inputSwitch);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeOption(context, inputSwitch.getOffLabel(), InputSwitch.OFF_LABEL_CLASS, showLabels);
        encodeOption(context, inputSwitch.getOnLabel(), InputSwitch.ON_LABEL_CLASS, showLabels);
        encodeHandle(context);
        encodeInput(context, inputSwitch, clientId, checked);

        writer.endElement("div");
    }

    protected void encodeOption(FacesContext context, String label, String styleClass, boolean showLabels) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        writer.startElement("span", null);

        if (showLabels) {
            writer.writeText(label, null);
        }
        else {
            writer.write("&nbsp;");
        }

        writer.endElement("span");
        writer.endElement("div");
    }

    protected void encodeHandle(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", InputSwitch.HANDLE_CLASS, null);
        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, InputSwitch inputSwitch, String clientId, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("div", inputSwitch);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderValidationMetadata(context, inputSwitch);
        renderAccessibilityAttributes(context, inputSwitch);
        renderPassThruAttributes(context, inputSwitch, HTML.TAB_INDEX);
        renderOnchange(context, inputSwitch);
        renderDomEvents(context, inputSwitch, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, InputSwitch inputSwitch) throws IOException {
        String clientId = inputSwitch.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputSwitch", inputSwitch.resolveWidgetVar(context), clientId).finish();
    }

    protected boolean isChecked(String value) {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }
}
