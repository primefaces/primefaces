/**
 * Copyright 2009-2019 PrimeTek.
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
package org.primefaces.component.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.convert.DateTimeConverter;
import org.primefaces.util.LocaleUtils;

public abstract class UICalendar extends HtmlInputText {

    public static final String CONTAINER_CLASS = "ui-calendar";
    public static final String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";
    public static final String DATE_OUT_OF_RANGE_MESSAGE_ID = "primefaces.calendar.OUT_OF_RANGE";

    private java.util.Locale calculatedLocale;

    private java.util.TimeZone appropriateTimeZone;

    private String timeOnlyPattern = null;

    private boolean conversionFailed = false;

    public enum PropertyKeys {
        locale,
        timeZone,
        pattern,
        mindate,
        maxdate,
        timeOnly,
        readonlyInput,
        inputStyle,
        inputStyleClass,
        type;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public Object getLocale() {
        return getStateHelper().eval(PropertyKeys.locale, null);
    }

    public void setLocale(Object locale) {
        getStateHelper().put(PropertyKeys.locale, locale);
    }

    public Object getTimeZone() {
        return getStateHelper().eval(PropertyKeys.timeZone, null);
    }

    public void setTimeZone(Object timeZone) {
        getStateHelper().put(PropertyKeys.timeZone, timeZone);
    }

    public String getPattern() {
        return (String) getStateHelper().eval(PropertyKeys.pattern, null);
    }

    public void setPattern(String pattern) {
        getStateHelper().put(PropertyKeys.pattern, pattern);
    }

    public Object getMindate() {
        return getStateHelper().eval(PropertyKeys.mindate, null);
    }

    public void setMindate(Object mindate) {
        getStateHelper().put(PropertyKeys.mindate, mindate);
    }

    public Object getMaxdate() {
        return getStateHelper().eval(PropertyKeys.maxdate, null);
    }

    public void setMaxdate(Object maxdate) {
        getStateHelper().put(PropertyKeys.maxdate, maxdate);
    }

    public boolean isTimeOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeOnly, false);
    }

    public void setTimeOnly(boolean timeOnly) {
        getStateHelper().put(PropertyKeys.timeOnly, timeOnly);
    }

    public boolean isReadonlyInput() {
        return (Boolean) getStateHelper().eval(PropertyKeys.readonlyInput, false);
    }

    public void setReadonlyInput(boolean readonlyInput) {
        getStateHelper().put(PropertyKeys.readonlyInput, readonlyInput);
    }

    public String getInputStyle() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyle, null);
    }

    public void setInputStyle(String inputStyle) {
        getStateHelper().put(PropertyKeys.inputStyle, inputStyle);
    }

    public String getInputStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
    }

    public void setInputStyleClass(String inputStyleClass) {
        getStateHelper().put(PropertyKeys.inputStyleClass, inputStyleClass);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, "text");
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public java.util.Locale calculateLocale(FacesContext facesContext) {
        if (calculatedLocale == null) {
            calculatedLocale = LocaleUtils.resolveLocale(getLocale(), getClientId(facesContext));
        }

        return calculatedLocale;
    }

    public java.util.TimeZone calculateTimeZone() {
        if (appropriateTimeZone == null) {
            Object usertimeZone = getTimeZone();
            if (usertimeZone != null) {
                if (usertimeZone instanceof String) {
                    appropriateTimeZone = java.util.TimeZone.getTimeZone((String) usertimeZone);
                }
                else if (usertimeZone instanceof java.util.TimeZone) {
                    appropriateTimeZone = (java.util.TimeZone) usertimeZone;
                }
                else {
                    throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
                }
            }
            else {
                appropriateTimeZone = java.util.TimeZone.getDefault();
            }
        }

        return appropriateTimeZone;
    }

    public boolean hasTime() {
        String pattern = getPattern();

        return (pattern != null && (pattern.contains("HH") || pattern.contains("mm") || pattern.contains("ss")));
    }

    public String calculatePattern() {
        String pattern = getPattern();
        Locale locale = calculateLocale(getFacesContext());

        return pattern == null ? ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale)).toPattern() : pattern;
    }

    public String calculateTimeOnlyPattern() {
        if (timeOnlyPattern == null) {
            String localePattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, calculateLocale(getFacesContext()))).toPattern();
            String userTimePattern = getPattern();

            timeOnlyPattern = localePattern + " " + userTimePattern;
        }

        return timeOnlyPattern;
    }

    public String convertPattern(String patternTemplate) {
        String pattern = patternTemplate.replaceAll("MMM", "###");
        int patternLen = pattern.length();
        int countM = patternLen - pattern.replaceAll("M", "").length();
        int countD = patternLen - pattern.replaceAll("d", "").length();
        if (countM == 1) {
            pattern = pattern.replaceAll("M", "mm");
        }

        if (countD == 1) {
            pattern = pattern.replaceAll("d", "dd");
        }

        pattern = pattern.replaceAll("[a-zA-Z]", "9");
        pattern = pattern.replaceAll("###", "aaa");
        return pattern;
    }

    public boolean isConversionFailed() {
        return conversionFailed;
    }

    public void setConversionFailed(boolean value) {
        conversionFailed = value;
    }

    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public Converter getConverter() {
        Converter converter = super.getConverter();

        if (converter == null && PrimeApplicationContext.getCurrentInstance(getFacesContext()).getConfig().isClientSideValidationEnabled()) {
            DateTimeConverter con = new DateTimeConverter();
            con.setPattern(calculatePattern());
            con.setTimeZone(calculateTimeZone());
            con.setLocale(calculateLocale(getFacesContext()));

            return con;
        }

        return converter;
    }
}
