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
package org.primefaces.component.datepicker;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.api.Widget;
import org.primefaces.model.datepicker.DateMetadataModel;
import org.primefaces.util.CalendarUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@FacesComponentBase
public abstract class DatePickerBase extends UICalendar implements Widget, MixedClientBehaviorHolder, FlexAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DatePickerRenderer";

    /**
     * Standard 576px considered a small screen so we can auto switch the picker to touch mode
     */
    public static final int RESPONSIVE_BREAKPOINT_SMALL = 576;

    protected String timeSeparator;
    protected String fractionSeparator;

    public DatePickerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "When enabled, displays calendar inline.", defaultValue = "false")
    public abstract boolean isInline();

    @Property(description = "Tabindex for the trigger button.")
    public abstract String getButtonTabindex();

    @Property(description = "When enabled, shows calendar icon.", defaultValue = "false")
    public abstract boolean isShowIcon();

    @Property(description = "JavaScript callback to execute before showing the calendar.")
    public abstract String getBeforeShow();

    @Property(description = "When enabled, focuses input after selection.", defaultValue = "false")
    public abstract boolean isFocusOnSelect();

    @Property(description = "Year range for navigation. Format: 'start:end'.")
    public abstract String getYearRange();

    @Property(description = "Selection mode. Options: 'single', 'multiple', 'range'. Defaults to 'range' when view is 'week', otherwise 'single'.")
    public abstract String getSelectionMode();

    @Property(description = "When enabled, shows dates from other months.", defaultValue = "false")
    public abstract boolean isShowOtherMonths();

    @Property(description = "When enabled, allows selecting dates from other months.", defaultValue = "false")
    public abstract boolean isSelectOtherMonths();

    @Property(description = "When enabled, shows calendar on input focus.", defaultValue = "true")
    public abstract boolean isShowOnFocus();

    @Property(description = "Cutoff year for two-digit year interpretation.")
    public abstract String getShortYearCutoff();

    @Property(description = "When enabled, shows month navigator.", defaultValue = "false")
    public abstract boolean isMonthNavigator();

    @Property(description = "Year navigator mode. Options: 'false', 'true', 'dropdown'.", defaultValue = "false")
    public abstract String getYearNavigator();

    @Property(description = "When enabled, shows time picker.", defaultValue = "false")
    public abstract boolean isShowTime();

    @Property(description = "Hour format. Options: '12' for 12-hour, '24' for 24-hour.")
    public abstract String getHourFormat();

    @Property(description = "When enabled, shows seconds in time picker.", defaultValue = "false")
    public abstract boolean isShowSeconds();

    @Property(description = "When enabled, shows milliseconds in time picker.", defaultValue = "false")
    public abstract boolean isShowMilliseconds();

    @Property(description = "Step value for hour spinner.", defaultValue = "1")
    public abstract int getStepHour();

    @Property(description = "Step value for minute spinner.", defaultValue = "1")
    public abstract int getStepMinute();

    @Property(description = "Step value for second spinner.", defaultValue = "1")
    public abstract int getStepSecond();

    @Property(description = "Step value for millisecond spinner.", defaultValue = "1")
    public abstract int getStepMillisecond();

    @Property(description = "When enabled, shows button bar with today/clear buttons.", defaultValue = "false")
    public abstract boolean isShowButtonBar();

    @Property(description = "CSS class for the calendar panel.")
    public abstract String getPanelStyleClass();

    @Property(description = "Inline style for the calendar panel.")
    public abstract String getPanelStyle();

    @Property(description = "When enabled, keeps invalid input value.", defaultValue = "false")
    public abstract boolean isKeepInvalid();

    @Property(description = "When enabled, hides calendar after date/time selection.", defaultValue = "false")
    public abstract boolean isHideOnDateTimeSelect();

    @Property(description = "When enabled, hides calendar after range selection.", defaultValue = "false")
    public abstract boolean isHideOnRangeSelection();

    @Property(description = "Maximum number of dates that can be selected.", defaultValue = "2147483647")
    public abstract int getMaxDateCount();

    @Property(description = "Number of months to display.", defaultValue = "1")
    public abstract int getNumberOfMonths();

    @Property(description = "View mode. Options: 'date', 'month', 'year', 'week'.")
    public abstract String getView();

    @Property(description = "When enabled, automatically detects display mode.", defaultValue = "true")
    public abstract boolean isAutoDetectDisplay();

    @Property(description = "When enabled, uses touch-optimized UI.", defaultValue = "false")
    public abstract boolean isTouchUI();

    @Property(description = "Template for custom date rendering.")
    public abstract String getDateTemplate();

    @Property(description = "Search expression to append the calendar panel to.", defaultValue = "@(body)")
    public abstract String getAppendTo();

    @Property(description = "Icon for the trigger button.")
    public abstract String getTriggerButtonIcon();

    @Property(description = "List of disabled dates.")
    public abstract List<Object> getDisabledDates();

    @Property(description = "List of enabled dates.")
    public abstract List<Object> getEnabledDates();

    @Property(description = "List of disabled days of week (0=Sunday, 6=Saturday).")
    public abstract List<Integer> getDisabledDays();

    @Property(description = "JavaScript callback for month change event.")
    public abstract String getOnMonthChange();

    @Property(description = "JavaScript callback for year change event.")
    public abstract String getOnYearChange();

    @Property(description = "When enabled, uses time input field instead of spinner.", defaultValue = "false")
    public abstract boolean isTimeInput();

    @Property(description = "When enabled, shows week numbers. Defaults to true when view is 'week'.")
    public abstract boolean isShowWeek();

    @Property(description = "Week calculator algorithm. Options: 'ISO', 'US'.")
    public abstract String getWeekCalculator();

    @Property(description = "DateMetadataModel for advanced date metadata.")
    public abstract DateMetadataModel getModel();

    @Property(description = "Responsive breakpoint in pixels for auto-switching to touch mode.", defaultValue = "576")
    public abstract int getResponsiveBreakpoint();

    @Property(description = "When enabled, shows min/max range indicators.", defaultValue = "true")
    public abstract boolean isShowMinMaxRange();

    @Property(description = "When enabled, automatically formats month display.", defaultValue = "true")
    public abstract boolean isAutoMonthFormat();

    @Property(description = "When enabled, shows long month names.", defaultValue = "false")
    public abstract boolean isShowLongMonthNames();

    @Property(description = "Flex layout configuration.")
    public abstract Boolean getFlex();

    @Override
    public boolean hasTime() {
        return this.isShowTime() || this.isTimeOnly();
    }

    @Override
    public String calculatePattern() {
        if (isTimeOnly()) {
            return calculateTimeOnlyPattern();
        }
        else if (isShowTime()) {
            return calculateWidgetPattern() + " " + calculateTimeOnlyPattern();
        }
        return calculateWidgetPattern();
    }

    @Override
    public String calculateWidgetPattern() {
        return CalendarUtils.removeTime(super.calculatePattern());
    }

    @Override
    public String calculateTimeOnlyPattern() {
        if (timeOnlyPattern == null) {
            String separator = getTimeSeparator();
            boolean ampm = "12".equals(getHourFormat());
            timeOnlyPattern = ampm ? "hh" : "HH";
            timeOnlyPattern += (separator + "mm");
            if (isShowSeconds() || isShowMilliseconds()) {
                timeOnlyPattern += (separator + "ss");
            }
            if (isShowMilliseconds()) {
                String fractSeparator = getFractionSeparator();
                timeOnlyPattern += (fractSeparator + "SSS");
            }
            if (ampm) {
                timeOnlyPattern += " a";
            }
        }
        return timeOnlyPattern;
    }

    public String getTimeSeparator() {
        if (timeSeparator == null) {
            // #5528 Determine separator for locale
            Locale locale = calculateLocale(getFacesContext());
            String localePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT, IsoChronology.INSTANCE, locale);
            timeSeparator = localePattern.contains(":") ? ":" : ".";
        }
        return timeSeparator;
    }

    public String getFractionSeparator() {
        if (fractionSeparator == null) {
            // Determine separator for locale
            Locale locale = calculateLocale(getFacesContext());
            char ds = ((DecimalFormat) NumberFormat.getInstance(locale)).getDecimalFormatSymbols().getDecimalSeparator();
            fractionSeparator = Character.toString(ds);
        }
        return fractionSeparator;
    }

}
