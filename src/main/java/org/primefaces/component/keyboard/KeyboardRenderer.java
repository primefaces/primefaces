/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.keyboard;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class KeyboardRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Keyboard keyboard = (Keyboard) component;

        if (!shouldDecode(keyboard)) {
            return;
        }

        decodeBehaviors(context, keyboard);

        String clientId = keyboard.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            keyboard.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Keyboard keyboard = (Keyboard) component;

        encodeMarkup(context, keyboard);
        encodeScript(context, keyboard);
    }

    protected void encodeScript(FacesContext context, Keyboard keyboard) throws IOException {
        String clientId = keyboard.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Keyboard", keyboard.resolveWidgetVar(), clientId)
                .attr("useThemeRoller", true)
                .attr("showOn", keyboard.getShowMode())
                .attr("showAnim", keyboard.getEffect())
                .attr("buttonImageOnly", keyboard.isButtonImageOnly(), false)
                .attr("duration", keyboard.getEffectDuration(), null);

        if (keyboard.getButtonImage() != null) {
            wb.attr("buttonImage", getResourceURL(context, keyboard.getButtonImage()));
        }

        if (!keyboard.isKeypadOnly()) {
            wb.attr("keypadOnly", false)
                    .attr("layoutName", keyboard.getLayout())
                    .attr("layoutTemplate", keyboard.getLayoutTemplate(), null);
        }

        wb.attr("keypadClass", keyboard.getStyleClass(), null)
                .attr("prompt", keyboard.getPromptLabel(), null)
                .attr("backText", keyboard.getBackspaceLabel(), null)
                .attr("clearText", keyboard.getClearLabel(), null)
                .attr("closeText", keyboard.getCloseLabel(), null);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Keyboard keyboard) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = keyboard.getClientId(context);
        String type = keyboard.isPassword() ? "password" : "text";
        String defaultClass = Keyboard.STYLE_CLASS;
        defaultClass = !keyboard.isValid() ? defaultClass + " ui-state-error" : defaultClass;
        defaultClass = keyboard.isDisabled() ? defaultClass + " ui-state-disabled" : defaultClass;
        String styleClass = keyboard.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        String valueToRender = ComponentUtils.getValueToRender(context, keyboard);

        writer.startElement("input", keyboard);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", type, null);

        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, "value");
        }

        writer.writeAttribute("class", styleClass, "styleClass");

        if (keyboard.getStyle() != null) {
            writer.writeAttribute("style", keyboard.getStyle(), "style");
        }

        renderAccessibilityAttributes(context, keyboard);
        renderPassThruAttributes(context, keyboard, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, keyboard, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, keyboard);

        writer.endElement("input");
    }
}
