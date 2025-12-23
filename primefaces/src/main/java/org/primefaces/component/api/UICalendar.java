/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.ELUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MessageFactory;

import java.time.Instant;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "dateSelect", event = AjaxBehaviorEvent.class, description = "Fires when a date is selected."),
    @FacesBehaviorEvent(name = "viewChange", event = DateViewChangeEvent.class, description = "Fires when the view changes."),
    @FacesBehaviorEvent(name = "close", event = AjaxBehaviorEvent.class, description = "Fires when the calendar is closed.")
})
@FacesComponentBase
public abstract class UICalendar extends AbstractPrimeHtmlInputText implements InputHolder, TouchAware, MixedClientBehaviorHolder {

    public static final String CONTAINER_CLASS = "ui-calendar";
    public static final String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default";
    public static final String DATE_OUT_OF_RANGE_MESSAGE_ID = "primefaces.calendar.OUT_OF_RANGE";
    public static final String DATE_MIN_DATE_ID = "primefaces.calendar.MIN_DATE";
    public static final String DATE_MAX_DATE_ID = "primefaces.calendar.MAX_DATE";
    public static final String DATE_INVALID_MESSAGE_ID = "primefaces.calendar.INVALID";
    public static final String DATE_INVALID_RANGE_MESSAGE_ID = "primefaces.calendar.DATE_INVALID_RANGE_MESSAGE_ID";

    protected static final List<String> UNOBSTRUSIVE_EVENT_NAMES = LangUtils.unmodifiableList("dateSelect", "viewChange", "close");

    protected String timeOnlyPattern;

    private boolean conversionFailed;

    @Override
    public Collection<String> getUnobstrusiveEventNames() {
        return UNOBSTRUSIVE_EVENT_NAMES;
    }

    @Property(description = "Locale for the calendar.")
    public abstract Object getLocale();

    @Property(description = "Time zone for the calendar.")
    public abstract Object getTimeZone();

    @Property(description = "Date/time pattern for formatting.")
    public abstract String getPattern();

    @Property(description = "Minimum selectable date.")
    public abstract Object getMindate();

    public abstract void setMindate(Object mindate);

    @Property(description = "Maximum selectable date.")
    public abstract Object getMaxdate();

    public abstract void setMaxdate(Object maxdate);

    @Property(description = "When enabled, shows only time picker.", defaultValue = "false")
    public abstract boolean isTimeOnly();

    public abstract Boolean getTimeOnlyWithoutDefault();

    @Property(description = "When enabled, makes the input field readonly.", defaultValue = "false")
    public abstract boolean isReadonlyInput();

    @Property(description = "Inline style for the input element.")
    public abstract String getInputStyle();

    @Property(description = "CSS class for the input element.")
    public abstract String getInputStyleClass();

    @Property(description = "Default hour value.", defaultValue = "0")
    public abstract int getDefaultHour();

    @Property(description = "Default minute value.", defaultValue = "0")
    public abstract int getDefaultMinute();

    @Property(description = "Default second value.", defaultValue = "0")
    public abstract int getDefaultSecond();

    @Property(description = "Default millisecond value.", defaultValue = "0")
    public abstract int getDefaultMillisecond();

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
            return calculateLocalizedPattern();
        }
        else return pattern;
    }

    public String calculateTimeOnlyPattern() {
        if (timeOnlyPattern == null) {
            String localePattern = calculateLocalizedPattern();
            String userTimePattern = getPattern();
            timeOnlyPattern = localePattern + " " + userTimePattern;
        }

        return timeOnlyPattern;
    }

    public String calculateLocalizedPattern() {
        Locale locale = calculateLocale(getFacesContext());
        String localePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);

        // #6170 default to 4 digit year
        if (LangUtils.countMatches(localePattern, 'y') == 2) {
            localePattern = localePattern.replace("yy", "yyyy");
        }
        return localePattern;
    }

    public abstract String calculateWidgetPattern();

    /**
     * @see <a href="https://github.com/RobinHerbots/Inputmask/blob/5.x/README_date.md">Inputmask README_date</a>
     * @param patternTemplate the date pattern
     * @return the value converted for InputMask plugin
     */
    public String convertPattern(String patternTemplate) {
        // switch capital and lower M's for InputMask
        char[] chars = patternTemplate.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == 'm' || c == 'M') {
                if (Character.isUpperCase(c)) {
                    chars[i] = Character.toLowerCase(c);
                }
                else if (Character.isLowerCase(c)) {
                    chars[i] = Character.toUpperCase(c);
                }
            }
        }
        String pattern = new String(chars);
        int countY = LangUtils.countMatches(pattern, 'y');
        int countM = LangUtils.countMatches(pattern, 'm');
        int countD = LangUtils.countMatches(pattern, 'd');
        if (countD == 1) {
            pattern = pattern.replace("d", "dd");
        }
        if (countM == 1) {
            pattern = pattern.replace("m", "mm");
        }
        if (countY == 1) {
            pattern = pattern.replace("y", "yy");
        }
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

    @Override
    public String getAriaDescribedBy() {
        return (String) getStateHelper().get("ariaDescribedBy");
    }

    @Override
    public void setAriaDescribedBy(String ariaDescribedBy) {
        getStateHelper().put("ariaDescribedBy", ariaDescribedBy);
    }

    @Property(description = "Separator character for date ranges.", defaultValue = "-")
    public abstract String getRangeSeparator();

    @Property(description = "Resolver style for date parsing. Options: 'STRICT', 'SMART', 'LENIENT'.", defaultValue = "SMART")
    public abstract String getResolverStyle();

    @Property(description = "Input mask pattern. Set to 'false' to disable.", defaultValue = "false")
    public abstract String getMask();

    @Property(description = "Character to display in empty mask slots.", defaultValue = "_")
    public abstract String getMaskSlotChar();

    @Property(description = "When enabled, clears the input when it doesn't match the mask.", defaultValue = "true")
    public abstract boolean isMaskAutoClear();

    public enum ValidationResult {
        OK, INVALID_DISABLED_DATE, INVALID_RANGE_DATES_SEQUENTIAL, INVALID_MIN_DATE, INVALID_MAX_DATE, INVALID_OUT_OF_RANGE
    }

    protected void createFacesMessageFromValidationResult(FacesContext context, ValidationResult validationResult) {
        FacesMessage msg = null;
        String validatorMessage = getValidatorMessage();
        Object[] params = new Object[] {ComponentUtils.getLabel(context, this),
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
                    msg = MessageFactory.getFacesMessage(context, DATE_INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_RANGE_DATES_SEQUENTIAL:
                    msg = MessageFactory.getFacesMessage(context, DATE_INVALID_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_MIN_DATE:
                    msg = MessageFactory.getFacesMessage(context, DATE_MIN_DATE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_MAX_DATE:
                    msg = MessageFactory.getFacesMessage(context, DATE_MAX_DATE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_OUT_OF_RANGE:
                    msg = MessageFactory.getFacesMessage(context, DATE_OUT_OF_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
            }
        }
        context.addMessage(getClientId(context), msg);
    }

    public Class<?> getValueType() {
        return ELUtils.getType(getFacesContext(), getValueExpression("value"), this::getValue);
    }

    public void validateMinMax(FacesContext context) {
        Instant minDate = CalendarUtils.getObjectAsInstant(context, this, getMindate(), "mindate");
        Instant maxDate = CalendarUtils.getObjectAsInstant(context, this, getMaxdate(), "maxdate");
        if (minDate != null && maxDate != null && maxDate.compareTo(minDate) < 0) {
            String id = getClientId(context);
            String component = this.getClass().getSimpleName();
            throw new FacesException(component + " : \"" + id + "\" minimum date must be less than maximum date.");
        }
    }
}
