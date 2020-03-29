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
package org.primefaces.component.inputmask;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.*;

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
            String mask = inputMask.getMask();

            if (inputMask.isValidateMask() && !submittedValue.isEmpty() && !LangUtils.isValueBlank(mask)) {
                Pattern pattern = translateMaskIntoRegex(context, mask);
                if (!pattern.matcher(submittedValue).matches()) {
                    submittedValue = "";
                }
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
     * @param mask The mask value of component
     * @return The generated {@link Pattern}
     */
    protected Pattern translateMaskIntoRegex(FacesContext context, String mask) {
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
        wb.init("InputMask", inputMask.resolveWidgetVar(context), clientId);
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
