/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.inputotp;

import org.primefaces.cdk.api.FacesRendererInfo;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.ClientValidator;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = InputOtp.DEFAULT_RENDERER, componentFamily = InputOtp.COMPONENT_FAMILY)
@FacesRendererInfo
public class InputOtpRenderer extends InputRenderer<InputOtp> {

    @Override
    public void decode(FacesContext context, InputOtp component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String submittedValue = params.get(component.getValidatableInputClientId());
        if (submittedValue != null) {
            component.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, InputOtp component) throws IOException {
        if (component.getLength() < 1) {
            throw new FacesException("InputOtp length must be greater than 0: " + component.getClientId(context));
        }

        // a masked value is not rendered (like p:password), it would be readable in the page source
        String value = component.isMask() && !component.isRedisplay() ? null : ComponentUtils.getValueToRender(context, component);
        if (value == null) {
            value = "";
        }

        encodeMarkup(context, component, value);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, InputOtp component, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = getStyleClassBuilder(context)
                .add(InputOtp.STYLE_CLASS)
                .add(component.getStyleClass())
                .add(component.isDisabled(), "p-state-disabled")
                .add(!component.isValid(), "p-state-error")
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);
        if (LangUtils.isNotBlank(component.getAriaLabelledBy())) {
            writer.writeAttribute(HTML.ARIA_LABELLEDBY, component.getAriaLabelledBy(), null);
        }
        if (LangUtils.isNotBlank(component.getAriaDescribedBy())) {
            writer.writeAttribute(HTML.ARIA_DESCRIBEDBY, SearchExpressionUtils.resolveClientIds(component.getAriaDescribedBy(), component), null);
        }

        UIComponent separatorFacet = component.getSeparatorFacet();
        boolean hasSeparatorFacet = FacetUtils.shouldRenderFacet(separatorFacet);
        String separator = component.getSeparator();

        // one character per input, a character outside the BMP (e.g. an emoji) is one code point like in the widget
        int[] chars = value.codePoints().toArray();
        for (int i = 0; i < component.getLength(); i++) {
            if (i > 0 && (hasSeparatorFacet || LangUtils.isNotEmpty(separator))) {
                writer.startElement("span", null);
                writer.writeAttribute("class", InputOtp.SEPARATOR_CLASS, null);
                writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
                if (hasSeparatorFacet) {
                    separatorFacet.encodeAll(context);
                }
                else {
                    writer.writeText(separator, "separator");
                }
                writer.endElement("span");
            }

            encodeInput(context, component, i, i < chars.length ? Character.toString(chars[i]) : "");
        }

        encodeValueInput(context, component, value);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, InputOtp component, int index, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputClass = createStyleClass(component, "inputStyleClass", InputOtp.INPUT_CLASS);

        writer.startElement("input", null);
        writer.writeAttribute("id", component.getInputClientId(index), null);
        writer.writeAttribute("type", component.isMask() ? "password" : "text", null);
        writer.writeAttribute("class", inputClass, null);
        writer.writeAttribute("value", value, null);
        // no maxlength: a code filled in by the browser (one-time-code, e.g. "123-456") arrives in one input, the widget
        // distributes it; a maxlength would cut it before
        writer.writeAttribute("autocomplete", index == 0 ? component.getAutocomplete() : "off", null);
        if (component.isIntegerOnly()) {
            writer.writeAttribute("inputmode", "numeric", null);
        }
        if (component.getInputStyle() != null) {
            writer.writeAttribute("style", component.getInputStyle(), null);
        }

        String placeholder = component.getPlaceholder();
        if (LangUtils.isNotEmpty(placeholder)) {
            // one character per input like the value: a character outside the BMP is one code point
            int[] placeholderChars = placeholder.codePoints().toArray();
            int placeholderIndex = placeholderChars.length == 1 ? 0 : index;
            if (placeholderIndex < placeholderChars.length) {
                writer.writeAttribute("placeholder", Character.toString(placeholderChars[placeholderIndex]), null);
            }
        }

        String ariaLabel = component.getAriaLabel();
        if (ariaLabel != null) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel.replace("{0}", String.valueOf(index + 1)), null);
        }

        renderARIARequired(context, component);
        renderARIAInvalid(context, component);
        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (component.isReadonly()) {
            writer.writeAttribute("readonly", "readonly", null);
        }
        renderPassThruAttributes(context, component, HTML.TAB_INDEX);

        writer.endElement("input");
    }

    /**
     * Encodes the hidden input with the submitted value, it carries the metadata of the client side validation.
     */
    protected void encodeValueInput(FacesContext context, InputOtp component, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = component.getValidatableInputClientId();

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("value", value, null);
        writer.writeAttribute("autocomplete", "off", null);
        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        renderCompositeValidationMetadata(context, component, new OtpClientValidator(component.getLength()));
        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, InputOtp component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputOtp", component)
                .attr("integerOnly", component.isIntegerOnly(), false)
                .attr("disabled", component.isDisabled(), false)
                .attr("readonly", component.isReadonly(), false)
                .callback("onchange", "function(value)", component.getOnchange());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    protected String getHighlighter() {
        return "inputotp";
    }

    /**
     * The client side validation of the length (a value that is not empty must have all characters), the widget
     * registers it.
     */
    protected static class OtpClientValidator implements ClientValidator {

        private final int length;

        public OtpClientValidator(int length) {
            this.length = length;
        }

        @Override
        public Map<String, Object> getMetadata() {
            return Map.of("data-p-otp-length", length);
        }

        @Override
        public String getValidatorId() {
            return "primefaces.InputOtp";
        }
    }
}
