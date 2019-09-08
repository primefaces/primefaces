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
package org.primefaces.component.inputtext;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.expression.SearchExpressionFacade;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class InputTextRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        InputText inputText = (InputText) component;

        if (!shouldDecode(inputText)) {
            return;
        }

        decodeBehaviors(context, inputText);

        String clientId = inputText.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            inputText.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputText inputText = (InputText) component;

        encodeMarkup(context, inputText);
        encodeScript(context, inputText);
    }

    protected void encodeScript(FacesContext context, InputText inputText) throws IOException {
        String clientId = inputText.getClientId(context);
        String counter = inputText.getCounter();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputText", inputText.resolveWidgetVar(context), clientId)
                .attr("maxlength", inputText.getMaxlength(), Integer.MAX_VALUE);

        if (counter != null) {
            UIComponent counterComponent = SearchExpressionFacade.resolveComponent(context, inputText, counter);

            wb.attr("counter", counterComponent.getClientId(context))
                    .attr("counterTemplate", inputText.getCounterTemplate(), null);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputText inputText) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputText.getClientId(context);

        writer.startElement("input", inputText);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", inputText.getType(), null);

        String valueToRender = ComponentUtils.getValueToRender(context, inputText);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }

        if (inputText.getStyle() != null) {
            writer.writeAttribute("style", inputText.getStyle(), null);
        }

        writer.writeAttribute("class", createStyleClass(inputText), "styleClass");

        renderAccessibilityAttributes(context, inputText);
        renderRTLDirection(context, inputText);
        renderPassThruAttributes(context, inputText, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputText, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, inputText);

        writer.endElement("input");
    }

    protected String createStyleClass(InputText inputText) {
        String defaultClass = InputText.STYLE_CLASS;
        defaultClass = inputText.isValid() ? defaultClass : defaultClass + " ui-state-error";
        defaultClass = !inputText.isDisabled() ? defaultClass : defaultClass + " ui-state-disabled";

        String styleClass = inputText.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;

        return styleClass;
    }
}
