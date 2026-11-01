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
package org.primefaces.component.inputphone;

import org.primefaces.cdk.api.FacesRendererInfo;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.ClientValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import org.json.JSONObject;

@FacesRenderer(rendererType = InputPhone.DEFAULT_RENDERER, componentFamily = InputPhone.COMPONENT_FAMILY)
@FacesRendererInfo
public class InputPhoneRenderer extends InputRenderer<InputPhone> {

    @Override
    public void decode(FacesContext context, InputPhone component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        // selecting a country does not submit the number: no validation error for an empty or incomplete number
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        if (ComponentUtils.isRequestSource(component, context)
                && InputPhone.ClientBehaviorEventKeys.countrySelect.getName().equals(params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM))) {
            return;
        }

        String submittedValue = params.get(component.getValidatableInputClientId());
        if (submittedValue != null) {
            component.setSubmittedValue(submittedValue.trim());
        }
    }

    @Override
    public void encodeEnd(FacesContext context, InputPhone component) throws IOException {
        String value = ComponentUtils.getValueToRender(context, component);
        if (value == null) {
            value = "";
        }

        // the country of a number without one (empty or invalid), the widget selects the country of a valid number
        String country = component.resolveCountry(context);
        List<String> onlyCountries = InputPhone.toCountryCodes(component.getOnlyCountries());
        List<String> excludeCountries = InputPhone.toCountryCodes(component.getExcludeCountries());

        encodeMarkup(context, component, value, country, onlyCountries, excludeCountries);
        encodeScript(context, component, country, onlyCountries, excludeCountries);
    }

    protected void encodeMarkup(FacesContext context, InputPhone component, String value, String country,
                                List<String> onlyCountries, List<String> excludeCountries) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = getStyleClassBuilder(context)
                .add(InputPhone.STYLE_CLASS)
                .add(component.getStyleClass())
                .add(component.isDisabled(), "p-state-disabled")
                .add(!component.isValid(), "p-state-error")
                .build();

        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        encodeCountry(context, component, clientId, country);
        encodeInput(context, component, clientId, value);
        encodeValueInput(context, component, value, onlyCountries, excludeCountries);
        // keeps the selected country when the value has none, e.g. after a validation error
        renderHiddenInput(context, component.getCountryInputClientId(), country, component.isDisabled());

        writer.endElement("span");
    }

    /**
     * Encodes the country selector, a button showing the code and the calling code of the country (filled in by the
     * widget), or a plain element if the user can not select the country.
     */
    protected void encodeCountry(FacesContext context, InputPhone component, String clientId, String country) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        // readonly: the country is shown like with allowDropdown="false"
        boolean dropdown = component.isAllowDropdown() && !component.isReadonly();
        boolean disabled = component.isDisabled();

        writer.startElement(dropdown ? "button" : "span", null);
        writer.writeAttribute("id", clientId + "_country", null);
        writer.writeAttribute("class", InputPhone.COUNTRY_CLASS, null);
        if (dropdown) {
            writer.writeAttribute("type", "button", null);
            writer.writeAttribute(HTML.ARIA_HASPOPUP, HTML.ARIA_ROLE_LISTBOX, null);
            writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);
            writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_panel", null);
            if (disabled) {
                writer.writeAttribute("disabled", "disabled", null);
            }
            renderPassThruAttributes(context, component, HTML.TAB_INDEX);
        }
        else {
            // the widget labels it with the name and the calling code of the country, a span without role has no label
            writer.writeAttribute("role", "img", null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", InputPhone.COUNTRY_CODE_CLASS, null);
        writer.writeText(country, null);
        writer.endElement("span");

        if (component.isSeparateDialCode()) {
            writer.startElement("span", null);
            writer.writeAttribute("class", InputPhone.DIAL_CODE_CLASS, null);
            writer.endElement("span");
        }

        if (dropdown) {
            writer.startElement("span", null);
            writer.writeAttribute("class", InputPhone.DROPDOWN_ICON_CLASS, null);
            writer.endElement("span");
        }

        writer.endElement(dropdown ? "button" : "span");
    }

    protected void encodeInput(FacesContext context, InputPhone component, String clientId, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = component.getInputClientId();

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "tel", null);
        writer.writeAttribute("class", createStyleClass(component, "inputStyleClass", InputPhone.INPUT_CLASS), null);
        if (LangUtils.isBlank(component.getAutocomplete())) {
            // an autocomplete attribute is rendered with the pass through attributes
            writer.writeAttribute("autocomplete", "tel", null);
        }
        // the widget formats the number, without JavaScript the E.164 number is shown
        writer.writeAttribute("value", value, null);
        if (component.getInputStyle() != null) {
            writer.writeAttribute("style", component.getInputStyle(), null);
        }

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);

        writer.endElement("input");
    }

    /**
     * Encodes the hidden input with the submitted value (E.164), it carries the metadata of the client side validation.
     */
    protected void encodeValueInput(FacesContext context, InputPhone component, String value,
                                    List<String> onlyCountries, List<String> excludeCountries) throws IOException {
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
        renderCompositeValidationMetadata(context, component, new PhoneClientValidator(onlyCountries, excludeCountries));
        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, InputPhone component, String country,
                                List<String> onlyCountries, List<String> excludeCountries) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputPhone", component)
                .attr("initialCountry", country)
                .attr("nationalMode", component.isNationalMode(), true)
                .attr("formatAsYouType", component.isFormatAsYouType(), true)
                .attr("formatOnDisplay", component.isFormatOnDisplay(), true)
                .attr("autoPlaceholder", component.getAutoPlaceholder(), "polite")
                .attr("allowDropdown", component.isAllowDropdown(), true)
                .attr("autoHideDialCode", component.isAutoHideDialCode(), true)
                .attr("fixDropdownWidth", component.isFixDropdownWidth(), true)
                .attr("disabled", component.isDisabled(), false)
                .attr("readonly", component.isReadonly(), false)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()), null);

        encodeCountries(wb, "onlyCountries", onlyCountries);
        encodeCountries(wb, "excludeCountries", excludeCountries);
        encodeCountries(wb, "preferredCountries", InputPhone.toCountryCodes(component.getPreferredCountries()));

        Map<String, String> localizedCountries = component.getLocalizedCountries();
        if (localizedCountries != null && !localizedCountries.isEmpty()) {
            wb.nativeAttr("localizedCountries", new JSONObject(localizedCountries).toString());
        }

        if (component.isGeoIpLookupRequired(context)) {
            if (LangUtils.isBlank(component.getGeoIpLookup())) {
                throw new FacesException("InputPhone " + component.getClientId(context) + ": initialCountry=\"auto\" requires geoIpLookup");
            }
            wb.callback("geoIpLookup", "function(success, failure)", component.getGeoIpLookup());
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    protected String getHighlighter() {
        return "inputphone";
    }

    /**
     * Encodes a list of country codes as JSON array, the codes are validated by {@link InputPhone#toCountryCodes(Object)}.
     */
    protected void encodeCountries(WidgetBuilder wb, String name, List<String> countries) throws IOException {
        if (!countries.isEmpty()) {
            wb.nativeAttr(name, "[\"" + String.join("\",\"", countries) + "\"]");
        }
    }

    /**
     * The client side validation of the number (libphonenumber-js): valid for its country, which must be one of the
     * onlyCountries and none of the excludeCountries. The widget registers it.
     */
    protected static class PhoneClientValidator implements ClientValidator {

        private final List<String> onlyCountries;
        private final List<String> excludeCountries;

        public PhoneClientValidator(List<String> onlyCountries, List<String> excludeCountries) {
            this.onlyCountries = onlyCountries;
            this.excludeCountries = excludeCountries;
        }

        @Override
        public Map<String, Object> getMetadata() {
            Map<String, Object> metadata = new HashMap<>(2);
            if (!onlyCountries.isEmpty()) {
                metadata.put("data-p-phone-only", String.join(",", onlyCountries));
            }
            if (!excludeCountries.isEmpty()) {
                metadata.put("data-p-phone-exclude", String.join(",", excludeCountries));
            }
            return metadata;
        }

        @Override
        public String getValidatorId() {
            return "primefaces.InputPhone";
        }
    }
}
