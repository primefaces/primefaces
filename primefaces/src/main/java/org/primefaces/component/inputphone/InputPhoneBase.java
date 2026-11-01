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

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;

import java.util.Map;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "countrySelect", event = SelectEvent.class,
            description = "Fires when the user selects a country, the event carries the ISO 3166-1 alpha-2 code.")
})
public abstract class InputPhoneBase extends AbstractPrimeHtmlInputText implements Widget, InputHolder, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputPhoneRenderer";

    public InputPhoneBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Country (ISO 3166-1 alpha-2 code, e.g. DE) selected when the value is empty, \"auto\" looks it up with"
            + " geoIpLookup. Defaults to the country of the view locale, US if the locale has none.")
    public abstract String getInitialCountry();

    @Property(description = "Client side function(success, failure) that looks up the country of the user for initialCountry=\"auto\","
            + " e.g. with a geo IP service, and calls success(countryCode).")
    public abstract String getGeoIpLookup();

    @Property(description = "Names of the countries (a Map of ISO 3166-1 alpha-2 code to name), replaces the names of the browser"
            + " in the language of the page.")
    public abstract Map<String, String> getLocalizedCountries();

    @Property(description = "Countries the user can select (ISO 3166-1 alpha-2 codes), a comma separated String or a Collection.")
    public abstract Object getOnlyCountries();

    @Property(description = "Countries the user can not select (ISO 3166-1 alpha-2 codes), a comma separated String or a Collection.")
    public abstract Object getExcludeCountries();

    @Property(description = "Countries listed at the top (ISO 3166-1 alpha-2 codes), a comma separated String or a Collection.")
    public abstract Object getPreferredCountries();

    @Property(defaultValue = "true",
            description = "Shows the number in the national format (e.g. 030 123456), otherwise in the international format (+49 30 123456).")
    public abstract boolean isNationalMode();

    @Property(defaultValue = "true", description = "Formats the number while the user types.")
    public abstract boolean isFormatAsYouType();

    @Property(defaultValue = "true", description = "Formats the value when it is displayed, otherwise it is shown as it is (E.164).")
    public abstract boolean isFormatOnDisplay();

    @Property(defaultValue = "polite", description = "Example mobile number of the selected country as placeholder: \"polite\" if no"
            + " placeholder is set, \"aggressive\" also replaces the placeholder, \"off\" never.")
    public abstract String getAutoPlaceholder();

    @Property(defaultValue = "true", description = "Lets the user select the country, otherwise only the initial country (or the one of the value) is shown.")
    public abstract boolean isAllowDropdown();

    @Property(defaultValue = "true", description = "Shows the calling code of the selected country next to its code in the country selector.")
    public abstract boolean isSeparateDialCode();

    @Property(defaultValue = "true", description = "In the international format (nationalMode=false): the calling code of the selected"
            + " country is filled in when the empty input gets the focus and removed again if nothing else is entered.")
    public abstract boolean isAutoHideDialCode();

    @Property(defaultValue = "true", description = "The country panel has the width of the component, otherwise it grows with the longest name.")
    public abstract boolean isFixDropdownWidth();

    @Property(description = "Inline style of the input.")
    public abstract String getInputStyle();

    @Property(description = "Style class of the input.")
    public abstract String getInputStyleClass();

    @Property(defaultValue = "@(body)", description = "Appends the country panel to the element defined by search expression.")
    public abstract String getAppendTo();
}
