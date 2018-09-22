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
package org.primefaces.component.inputmask;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

public class InputMaskRenderer extends InputRenderer {

    private static final String REGEX_METACHARS = "<([{\\^-=$!|]})?*+.>";
    private static final String SB_PATTERN = InputMaskRenderer.class.getName() + "#translateMaskIntoRegex";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        InputMask inputMask = (InputMask) component;

        if (!shouldDecode(inputMask)) {
            return;
        }

        decodeBehaviors(context, inputMask);

        String clientId = inputMask.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            Pattern pattern = translateMaskIntoRegex(context, inputMask);
            if (!pattern.matcher(submittedValue).matches()) {
                submittedValue = null;
            }

            inputMask.setSubmittedValue(submittedValue);
        }
    }


    /**
     * Translates the client side mask to to a {@link Pattern} base on:
     * https://github.com/digitalBush/jquery.maskedinput
     * a - Represents an alpha character (A-Z,a-z)
     * 9 - Represents a numeric character (0-9)
     * * - Represents an alphanumeric character (A-Z,a-z,0-9)
     * ? - Makes the following input optional
     *
     * @param context   The {@link FacesContext}
     * @param inputMask The component
     * @return The generated {@link Pattern}
     */
    protected Pattern translateMaskIntoRegex(FacesContext context, InputMask inputMask) {
        String mask = inputMask.getMask();
        StringBuilder regex = SharedStringBuilder.get(context, SB_PATTERN);
        boolean optionalFound = false;

        for (char c : mask.toCharArray()) {
            if (c == '?') {
                optionalFound = true;
            }
            else {
                regex.append(translateMaskCharIntoRegex(c, optionalFound));
            }
        }
        return Pattern.compile(regex.toString());
    }

    protected String translateMaskCharIntoRegex(char c, boolean optional) {
        String translated;

        if (c == '?') {
            return ""; //should be ignored
        }
        else if (c == '9') {
            translated = "[0-9]";
        }
        else if (c == 'a') {
            translated = "[A-Za-z]";
        }
        else if (c == '*') {
            translated = "[A-Za-z0-9]";
        }
        else if (REGEX_METACHARS.indexOf(c) >= 0) {
            translated = "\\" + c;
        }
        else {
            translated = String.valueOf(c);
        }
        return optional ? (translated + "?") : translated;
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputMask inputMask = (InputMask) component;

        encodeMarkup(context, inputMask);
        encodeScript(context, inputMask);
    }

    protected void encodeScript(FacesContext context, InputMask inputMask) throws IOException {
        String clientId = inputMask.getClientId(context);
        String mask = inputMask.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputMask", inputMask.resolveWidgetVar(), clientId);
        String slotChar = inputMask.getSlotChar();

        if (mask != null) {
            wb.attr("mask", mask)
                    .attr("placeholder", slotChar, null)
                    .attr("autoclear", inputMask.isAutoClear(), true);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputMask inputMask) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputMask.getClientId(context);
        String styleClass = inputMask.getStyleClass();
        String defaultClass = InputMask.STYLE_CLASS;
        defaultClass = !inputMask.isValid() ? defaultClass + " ui-state-error" : defaultClass;
        defaultClass = inputMask.isDisabled() ? defaultClass + " ui-state-disabled" : defaultClass;
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", inputMask.getType(), "text");

        String valueToRender = ComponentUtils.getValueToRender(context, inputMask);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }

        renderAccessibilityAttributes(context, inputMask);
        renderPassThruAttributes(context, inputMask, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputMask, HTML.INPUT_TEXT_EVENTS);

        if (inputMask.getStyle() != null) {
            writer.writeAttribute("style", inputMask.getStyle(), "style");
        }

        writer.writeAttribute("class", styleClass, "styleClass");

        renderValidationMetadata(context, inputMask);

        writer.endElement("input");
    }
}
