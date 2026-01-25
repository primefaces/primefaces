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
package org.primefaces.component.keyboard;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Keyboard.DEFAULT_RENDERER, componentFamily = Keyboard.COMPONENT_FAMILY)
public class KeyboardRenderer extends InputRenderer<Keyboard> {

    @Override
    public void decode(FacesContext context, Keyboard component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            component.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, Keyboard component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, Keyboard component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Keyboard", component)
                .attr("useThemeRoller", true)
                .attr("showOn", component.getShowMode())
                .attr("showAnim", component.getEffect())
                .attr("buttonImageOnly", component.isButtonImageOnly(), false)
                .attr("duration", component.getEffectDuration(), null);

        if (component.getButtonImage() != null) {
            wb.attr("buttonImage", getResourceURL(context, component.getButtonImage()));
        }

        if (!component.isKeypadOnly()) {
            wb.attr("keypadOnly", false)
                    .attr("layoutName", component.getLayout())
                    .attr("layoutTemplate", component.getLayoutTemplate(), null);
        }

        if (ComponentUtils.isRTL(context, component)) {
            wb.attr("isRTL", true);
        }

        wb.attr("keypadClass", component.getStyleClass(), null)
                .attr("prompt", component.getPromptLabel(), null)
                .attr("backText", component.getBackspaceLabel(), null)
                .attr("clearText", component.getClearLabel(), null)
                .attr("closeText", component.getCloseLabel(), null);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Keyboard component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String type = component.isPassword() ? "password" : "text";
        String styleClass = createStyleClass(component, Keyboard.STYLE_CLASS) ;
        String valueToRender = ComponentUtils.getValueToRender(context, component);

        writer.startElement("input", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", type, null);

        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, "value");
        }

        writer.writeAttribute("class", styleClass, "styleClass");

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, component);

        writer.endElement("input");
    }
}
