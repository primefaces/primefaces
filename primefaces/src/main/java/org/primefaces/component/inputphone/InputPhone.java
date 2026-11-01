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

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MessageFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = InputPhone.COMPONENT_TYPE, namespace = InputPhone.COMPONENT_FAMILY)
@FacesComponentInfo(description = "InputPhone is an input for international phone numbers with a country selector,"
        + " the value is the number in the E.164 format (e.g. +4930123456).")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "inputphone/inputphone.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "inputphone/inputphone.js")
public class InputPhone extends InputPhoneBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.InputPhone";

    public static final String STYLE_CLASS = "p-inputphone p-widget";
    public static final String COUNTRY_CLASS = "p-inputphone-country";
    public static final String COUNTRY_CODE_CLASS = "p-inputphone-country-code";
    public static final String DIAL_CODE_CLASS = "p-inputphone-dial-code";
    public static final String DROPDOWN_ICON_CLASS = "p-icon p-dropdown-icon p-inputphone-dropdown-icon";
    public static final String INPUT_CLASS = "p-inputphone-input p-inputfield p-inputtext p-widget p-state-default";

    public static final String DEFAULT_COUNTRY = "US";
    public static final String COUNTRY_AUTO = "auto";

    public static final String INVALID_MESSAGE_ID = "primefaces.inputphone.INVALID";

    protected static final List<PrimeClientBehaviorEventKeys> UNOBTRUSIVE_EVENT_KEYS = LangUtils.unmodifiableList(ClientBehaviorEventKeys.countrySelect);

    /** An E.164 number: a plus sign and up to 15 digits, the country calling code does not start with 0. */
    private static final Pattern E164 = Pattern.compile("\\+[1-9]\\d{1,14}");
    private static final Pattern COUNTRY_CODE = Pattern.compile("[A-Z]{2}");

    @Override
    public Collection<PrimeClientBehaviorEventKeys> getUnobtrusiveClientBehaviorEventKeys() {
        return UNOBTRUSIVE_EVENT_KEYS;
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_hidden";
    }

    /**
     * @return the client id of the hidden input with the selected country
     */
    public String getCountryInputClientId() {
        return getClientId(getFacesContext()) + "_countryCode";
    }

    /**
     * @param context the {@link FacesContext}
     * @return the country selected for a value without a country (e.g. an invalid number): the submitted country, else
     *         the initial country
     */
    public String resolveCountry(FacesContext context) {
        String country = context.getExternalContext().getRequestParameterMap().get(getCountryInputClientId());
        return country != null && COUNTRY_CODE.matcher(country).matches() ? country : resolveInitialCountry();
    }

    /**
     * @param context the {@link FacesContext}
     * @return whether the widget looks up the country with geoIpLookup: initialCountry is "auto" and no country was
     *         submitted
     */
    public boolean isGeoIpLookupRequired(FacesContext context) {
        return COUNTRY_AUTO.equalsIgnoreCase(getInitialCountry())
                && !context.getExternalContext().getRequestParameterMap().containsKey(getCountryInputClientId());
    }

    /**
     * @return the country selected for an empty value: the initialCountry, else (also while the country of
     *         initialCountry="auto" is looked up) the country of the view locale, else {@link #DEFAULT_COUNTRY}
     */
    public String resolveInitialCountry() {
        String country = getInitialCountry();
        if (LangUtils.isBlank(country) || COUNTRY_AUTO.equalsIgnoreCase(country)) {
            Locale locale = LocaleUtils.getCurrentLocale(getFacesContext());
            country = locale == null ? null : locale.getCountry();
        }
        country = country == null ? "" : country.trim().toUpperCase(Locale.ROOT);
        return COUNTRY_CODE.matcher(country).matches() ? country : DEFAULT_COUNTRY;
    }

    /**
     * Converts a list of countries to upper case ISO 3166-1 alpha-2 codes, invalid codes are dropped.
     *
     * @param countries a comma separated String or a Collection of codes, may be {@code null}
     * @return the codes, never {@code null}
     */
    public static List<String> toCountryCodes(Object countries) {
        if (countries == null) {
            return Collections.emptyList();
        }

        Collection<?> values = countries instanceof Collection<?> collection
                ? collection
                : List.of(countries.toString().split(","));
        List<String> codes = new ArrayList<>(values.size());
        for (Object value : values) {
            String code = Objects.toString(value, "").trim().toUpperCase(Locale.ROOT);
            if (COUNTRY_CODE.matcher(code).matches()) {
                codes.add(code);
            }
        }
        return codes;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (isAjaxBehaviorEventSource(event) && isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.countrySelect)) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;
            String country = context.getExternalContext().getRequestParameterMap().get(getCountryInputClientId());
            if (country == null || !COUNTRY_CODE.matcher(country).matches()) {
                country = null;
            }
            SelectEvent<String> selectEvent = new SelectEvent<>(this, ajaxBehaviorEvent.getBehavior(), country);
            selectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
            super.queueEvent(selectEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    protected void validateValue(FacesContext context, Object newValue) {
        // an invalid number is invalid before the validators run, they only get valid numbers
        String submittedValue = Objects.toString(getSubmittedValue(), "");
        if (submittedValue.isEmpty() || isValidNumber(context, submittedValue)) {
            super.validateValue(context, newValue);
            return;
        }

        String validatorMessage = getValidatorMessage();
        FacesMessage message = validatorMessage != null
                ? new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage)
                : MessageFactory.getFacesMessage(context, INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, ComponentUtils.getLabel(context, this));
        context.addMessage(getClientId(context), message);
        setValid(false);
    }

    /**
     * Validates a number: it must be in the E.164 format and, if Google's libphonenumber is available, valid for its
     * country, which must be one of the onlyCountries and none of the excludeCountries.
     *
     * @param context the {@link FacesContext}
     * @param number the submitted number
     * @return whether the number is valid
     */
    protected boolean isValidNumber(FacesContext context, String number) {
        if (!E164.matcher(number).matches()) {
            return false;
        }
        return !PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isPhoneNumberUtilAvailable()
                || PhoneNumberUtilValidation.isValid(number, toCountryCodes(getOnlyCountries()), toCountryCodes(getExcludeCountries()));
    }
}
