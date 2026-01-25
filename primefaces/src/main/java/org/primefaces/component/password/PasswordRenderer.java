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
package org.primefaces.component.password;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Password.DEFAULT_RENDERER, componentFamily = Password.COMPONENT_FAMILY)
public class PasswordRenderer extends InputRenderer<Password> {

    @Override
    public void decode(FacesContext context, Password component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String submittedValue = context.getExternalContext().getRequestParameterMap().get(component.getClientId(context));

        if (submittedValue != null) {
            component.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, Password component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, Password component) throws IOException {
        boolean feedback = component.isFeedback();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Password", component);
        wb.attr("unmaskable", component.isToggleMask(), false);

        if (feedback) {
            wb.attr("feedback", true)
                    .attr("inline", component.isInline())
                    .attr("showEvent", component.getShowEvent(), null)
                    .attr("hideEvent", component.getHideEvent(), null)
                    .attr("promptLabel", component.getPromptLabel(), null)
                    .attr("weakLabel", component.getWeakLabel(), null)
                    .attr("goodLabel", component.getGoodLabel(), null)
                    .attr("strongLabel", component.getStrongLabel(), null);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Password component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean toggleMask = component.isToggleMask();

        if (toggleMask) {
            writer.startElement("span", null);
            boolean isRTL = ComponentUtils.isRTL(context, component);
            String positionClass = getStyleClassBuilder(context)
                        .add(Password.STYLE_CLASS)
                        .add(Password.MASKED_CLASS)
                        .add(Password.WRAPPER_CLASS)
                        .add(isRTL, "ui-input-icon-left", "ui-input-icon-right")
                        .build();
            writer.writeAttribute("class", positionClass, null);
        }

        String inputClass = getStyleClassBuilder(context)
                        .add(!toggleMask, Password.STYLE_CLASS)
                        .add(createStyleClass(component, Password.INPUT_CLASS))
                        .build();

        writer.startElement("input", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "password", null);
        writer.writeAttribute("class", inputClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }
        if (component.isIgnoreLastPass()) {
            writer.writeAttribute("data-lpignore", "true", null);
        }

        String valueToRender = ComponentUtils.getValueToRender(context, component);
        if (LangUtils.isNotBlank(valueToRender) && component.isRedisplay()) {
            writer.writeAttribute("value", valueToRender, null);
        }

        renderAccessibilityAttributes(context, component);
        renderRTLDirection(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, component);

        writer.endElement("input");

        if (toggleMask) {
            writer.startElement("i", null);
            writer.writeAttribute("id", clientId + "_mask", "id");
            writer.writeAttribute("class", Password.ICON_CLASS, null);
            writer.endElement("i");

            writer.endElement("span");
        }
    }
}
