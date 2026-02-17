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
package org.primefaces.component.inputtext;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = InputText.DEFAULT_RENDERER, componentFamily = InputText.COMPONENT_FAMILY)
public class InputTextRenderer extends InputRenderer<InputText> {

    @Override
    public void decode(FacesContext context, InputText component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            int maxlength = component.getMaxlength();
            if (maxlength > 0 && submittedValue.length() > maxlength) {
                submittedValue = LangUtils.substring(submittedValue, 0, maxlength);
            }
            component.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, InputText component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, InputText inputText) throws IOException {
        String counter = inputText.getCounter();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputText", inputText)
                .attr("maxlength", inputText.getMaxlength(), Integer.MIN_VALUE);

        if (counter != null) {
            UIComponent counterComponent = SearchExpressionUtils.contextlessResolveComponent(context, inputText, counter);

            if (counterComponent != null) {
                wb.attr("counter", counterComponent.getClientId(context))
                        .attr("counterTemplate", inputText.getCounterTemplate(), null)
                        .attr("countBytesAsChars", inputText.isCountBytesAsChars());
            }
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputText component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("input", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", component.getType(), null);

        String valueToRender = ComponentUtils.getValueToRender(context, component);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        writer.writeAttribute("class", createStyleClass(component, InputText.STYLE_CLASS), "styleClass");

        renderAccessibilityAttributes(context, component);
        renderRTLDirection(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, component);

        writer.endElement("input");
    }

}
