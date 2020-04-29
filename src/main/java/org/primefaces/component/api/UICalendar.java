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
package org.primefaces.component.api;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.ResolverStyle;
import java.util.Locale;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import org.primefaces.util.CalendarUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MessageFactory;

public abstract class UICalendar extends HtmlInputText implements InputHolder {

    public static final String CONTAINER_CLASS = "ui-calendar";
    public static final String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";
    public static final String DATE_OUT_OF_RANGE_MESSAGE_ID = "primefaces.calendar.OUT_OF_RANGE";
    public static final String DATE_MIN_DATE_ID = "primefaces.calendar.MIN_DATE";
    public static final String DATE_MAX_DATE_ID = "primefaces.calendar.MAX_DATE";
    public static final String DATE_INVALID_MESSAGE_ID = "primefaces.calendar.INVALID";
    public static final String DATE_INVALID_RANGE_MESSAGE_ID = "primefaces.calendar.DATE_INVALID_RANGE_MESSAGE_ID";

    protected String timeOnlyPattern = null;

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
        type,
        rangeSeparator,
        resolverStyle
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

    public Boolean isTimeOnlyWithoutDefault() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeOnly);
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

    public String getSelectionMode() {
        return null;
    }

    public Locale calculateLocale(FacesContext facesContext) {
        return LocaleUtils.resolveLocale(facesContext, getLocale(), getClientId(facesContext));
    }

    public boolean hasTime() {
        String pattern = getPattern();

        return (pattern != null && (pattern.contains("HH") || pattern.contains("mm") || pattern.contains("ss")));
    }

    public String calculatePattern() {
        String pattern = getPattern();

        if (pattern == null) {
            Locale locale = calculateLocale(getFacesContext());
            return DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);
        }
        else return pattern;
    }

    public String calculateTimeOnlyPattern() {
        if (timeOnlyPattern == null) {
            Locale locale = calculateLocale(getFacesContext());
            String localePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);
            String userTimePattern = getPattern();

            timeOnlyPattern = localePattern + " " + userTimePattern;
        }

        return timeOnlyPattern;
    }

    public abstract String calculateWidgetPattern();

    public String convertPattern(String patternTemplate) {
        String pattern = patternTemplate.replace("MMM", "###");
        int patternLen = pattern.length();
        int countM = patternLen - pattern.replace("M", "").length();
        int countD = patternLen - pattern.replace("d", "").length();
        if (countM == 1) {
            pattern = pattern.replace("M", "mm");
        }

        if (countD == 1) {
            pattern = pattern.replace("d", "dd");
        }

        pattern = pattern.replaceAll("[a-zA-Z]", "9");
        pattern = pattern.replace("###", "aaa");
        return pattern;
    }

    public boolean isConversionFailed() {
        return conversionFailed;
    }

    public void setConversionFailed(boolean value) {
        conversionFailed = value;
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    public String getRangeSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.rangeSeparator, "-");
    }

    public void setRangeSeparator(java.lang.String _rangeSeparator) {
        getStateHelper().put(PropertyKeys.rangeSeparator, _rangeSeparator);
    }

    public String getResolverStyle() {
        return (String) getStateHelper().eval(PropertyKeys.resolverStyle, ResolverStyle.SMART.name());
    }

    public void setResolverStyle(String resolverStyle) {
        getStateHelper().put(PropertyKeys.resolverStyle, resolverStyle);
    }

    public enum ValidationResult {
        OK, INVALID_DISABLED_DATE, INVALID_RANGE_DATES_SEQUENTIAL, INVALID_MIN_DATE, INVALID_MAX_DATE, INVALID_OUT_OF_RANGE
    }

    protected void createFacesMessageFromValidationResult(FacesContext context, ValidationResult validationResult) {
        FacesMessage msg = null;
        String validatorMessage = getValidatorMessage();
        Object[] params = new Object[] {MessageFactory.getLabel(context, this),
                CalendarUtils.getValueAsString(context, this, getMindate()),
                CalendarUtils.getValueAsString(context, this, getMaxdate())};
        if (validatorMessage != null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
        }
        else {
            switch (validationResult) {
                case OK:
                    break;
                case INVALID_DISABLED_DATE:
                    msg = MessageFactory.getMessage(DATE_INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_RANGE_DATES_SEQUENTIAL:
                    msg = MessageFactory.getMessage(DATE_INVALID_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_MIN_DATE:
                    msg = MessageFactory.getMessage(DATE_MIN_DATE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_MAX_DATE:
                    msg = MessageFactory.getMessage(DATE_MAX_DATE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_OUT_OF_RANGE:
                    msg = MessageFactory.getMessage(DATE_OUT_OF_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
            }
        }
        context.addMessage(getClientId(context), msg);
    }

    /**
     * Only for internal usage within PrimeFaces.
     * @return Type of the value bound via value expression. May return null when no value is bound.
     */
    public Class<?> getTypeFromValueByValueExpression(FacesContext context) {
        ValueExpression ve = getValueExpression("value");
        if (ve != null) {
            return ve.getType(context.getELContext());
        }
        else {
            return null;
        }
    }

    /*
    @Override
    public Converter getConverter() {
        Converter converter = super.getConverter();

        //TODO: Why does it matter whether Client-Side-Validation is enabled?
        if (converter == null && PrimeApplicationContext.getCurrentInstance(getFacesContext()).getConfig().isClientSideValidationEnabled()) {
            DateTimeConverter con = new DateTimeConverter();
            con.setPattern(calculatePattern());
            con.setTimeZone(TimeZone.getTimeZone(CalendarUtils.calculateZoneId(this.getTimeZone())));
            con.setLocale(calculateLocale(getFacesContext()));

            return con;
        }

        return converter;
    }
    */
}
