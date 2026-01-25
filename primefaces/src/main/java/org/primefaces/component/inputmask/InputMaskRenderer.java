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
package org.primefaces.component.inputmask;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = InputMask.DEFAULT_RENDERER, componentFamily = InputMask.COMPONENT_FAMILY)
public class InputMaskRenderer extends InputRenderer<InputMask> {

    private static final String REGEX_METACHARS = "<([{\\^-=$!|]})?*+.>";
    private static final String SB_PATTERN = InputMaskRenderer.class.getName() + "#translateMaskIntoRegex";

    @Override
    public void decode(FacesContext context, InputMask component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            // #6469/#11958 strip mask characters in case of optional values
            String mask = component.getMask();
            if (isMaskOptional(mask)) {
                submittedValue = submittedValue.replace(component.getSlotChar(), Constants.EMPTY_STRING);
            }

            if (component.isValidateMask() && !LangUtils.isEmpty(submittedValue) && LangUtils.isNotBlank(mask)) {
                Pattern pattern = translateMaskIntoRegex(context, mask);
                if (!pattern.matcher(submittedValue).matches()) {
                    submittedValue = Constants.EMPTY_STRING;
                }
            }

            component.setSubmittedValue(submittedValue);
        }
    }


    /**
     * Translates the client side mask to to a {@link Pattern} base on:
     * https://github.com/RobinHerbots/Inputmask
     * a - Represents an alpha character (A-Z,a-z)
     * A - Represents an UPPERCASE alpha character (A-Z)
     * 9 - Represents a numeric character (0-9)
     * * - Represents an alphanumeric character (A-Z,a-z,0-9)
     * [] - Makes the input in between [ and ] optional
     *
     * @param context the FacesContext instance, used to retrieve a shared StringBuilder.
     * @param mask the mask pattern to translate into a regular expression.
     * @return the translated regular expression as a {@link Pattern}.
     */
    protected Pattern translateMaskIntoRegex(FacesContext context, String mask) {
        StringBuilder regex = SharedStringBuilder.get(context, SB_PATTERN);
        return translateMaskIntoRegex(regex, mask);
    }

    /**
     * Translates a mask pattern into a regular expression pattern.
     *
     * @param regex the StringBuilder to append the translated regular expression to.
     * @param mask the mask pattern to translate into a regular expression.
     * @return the translated regular expression as a {@link Pattern}.
     */
    protected Pattern translateMaskIntoRegex(StringBuilder regex, String mask) {
        boolean optionalFound = false;
        boolean escapeFound = false;

        for (char c : mask.toCharArray()) {
            if (c == '[' || c == ']') {
                optionalFound = true;
            }
            else if (c == '\\') {
                escapeFound = true;
            }
            else {
                regex.append(translateMaskCharIntoRegex(c, optionalFound, escapeFound));
                escapeFound = false;
            }
        }
        return Pattern.compile(regex.toString());
    }

    /**
     * Translates a single mask character into its corresponding regular expression snippet.
     *
     * @param c the character to translate.
     * @param optional whether the character is within optional brackets ('[' and ']').
     * @param escapeFound whether the escape character ('\\') was found before this character.
     * @return the translated character as a regular expression snippet.
     */
    protected String translateMaskCharIntoRegex(char c, boolean optional, boolean escapeFound) {
        String translated;

        if (escapeFound) {
            return String.valueOf(c);
        }
        else if (c == '[' || c == ']') {
            return Constants.EMPTY_STRING; //should be ignored
        }
        else if (c == '9') {
            translated = "[0-9]";
        }
        else if (c == 'a') {
            translated = "[A-Za-z]";
        }
        else if (c == 'A') {
            translated = "[A-Z]";
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

    /**
     * Checks if the given mask string contains any optional mask characters.
     * In this context, an optional mask character is defined as either '[' or ']'.
     *
     * @param mask the mask string to check
     * @return {@code true} if the mask contains either '[' or ']'; {@code false} otherwise
     */
    protected boolean isMaskOptional(String mask) {
        return mask.contains("[") || mask.contains("]");
    }

    @Override
    public void encodeEnd(FacesContext context, InputMask component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, InputMask component) throws IOException {
        String mask = component.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputMask", component);

        if (mask != null) {
            // autoclear must be false when using optional mask
            boolean autoClear = component.isAutoClear();
            if (isMaskOptional(mask)) {
                autoClear = false;
            }
            wb.attr("mask", mask)
                .attr("placeholder", component.getSlotChar(), "_")
                .attr("autoClear", autoClear, true)
                .attr("showMaskOnFocus", component.isShowMaskOnFocus(), true)
                .attr("showMaskOnHover", component.isShowMaskOnHover(), true);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputMask component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = createStyleClass(component, InputMask.STYLE_CLASS);

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", component.getType(), "text");

        String valueToRender = ComponentUtils.getValueToRender(context, component);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        writer.writeAttribute("class", styleClass, "styleClass");

        renderValidationMetadata(context, component);

        writer.endElement("input");
    }
}
