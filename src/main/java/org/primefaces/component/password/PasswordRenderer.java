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
package org.primefaces.component.password;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class PasswordRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Password password = (Password) component;

        if (!shouldDecode(password)) {
            return;
        }

        decodeBehaviors(context, password);

        String submittedValue = context.getExternalContext().getRequestParameterMap().get(password.getClientId(context));

        if (submittedValue != null) {
            password.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Password password = (Password) component;

        encodeMarkup(context, password);
        encodeScript(context, password);
    }

    protected void encodeScript(FacesContext context, Password password) throws IOException {
        String clientId = password.getClientId(context);
        boolean feedback = password.isFeedback();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Password", password.resolveWidgetVar(context), clientId);

        if (feedback) {
            wb.attr("feedback", true)
                    .attr("inline", password.isInline())
                    .attr("promptLabel", password.getPromptLabel())
                    .attr("weakLabel", password.getWeakLabel())
                    .attr("goodLabel", password.getGoodLabel())
                    .attr("strongLabel", password.getStrongLabel());
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Password password) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = password.getClientId(context);
        boolean disabled = password.isDisabled();

        String inputClass = Password.STYLE_CLASS;
        inputClass = password.isValid() ? inputClass : inputClass + " ui-state-error";
        inputClass = !disabled ? inputClass : inputClass + " ui-state-disabled";
        String styleClass = password.getStyleClass() == null ? inputClass : inputClass + " " + password.getStyleClass();

        writer.startElement("input", password);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "password", null);
        writer.writeAttribute("class", styleClass, null);
        if (password.getStyle() != null) {
            writer.writeAttribute("style", password.getStyle(), null);
        }

        String valueToRender = ComponentUtils.getValueToRender(context, password);
        if (!LangUtils.isValueBlank(valueToRender) && password.isRedisplay()) {
            writer.writeAttribute("value", valueToRender, null);
        }

        renderAccessibilityAttributes(context, password);
        renderPassThruAttributes(context, password, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, password, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, password);

        writer.endElement("input");
    }
}
