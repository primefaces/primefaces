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

import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.InputHolder;
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

public abstract class DatePickerBase extends UICalendar implements Widget, InputHolder, MixedClientBehaviorHolder, FlexAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DatePickerRenderer";

    /**
     * Standard 576px considered a small screen so we can auto switch the picker to touch mode
     */
    public static final int RESPONSIVE_BREAKPOINT_SMALL = 576;

    protected String timeSeparator;
    protected String fractionSeparator;

    public enum PropertyKeys {

        appendTo,
        autoDetectDisplay,
        autoMonthFormat,
        beforeShow,
        buttonTabindex,
        dateTemplate,
        disabledDates,
        disabledDays,
        enabledDates,
        flex,
        focusOnSelect,
        hideOnDateTimeSelect,
        hideOnRangeSelection,
        hourFormat,
        inline,
        keepInvalid,
        maxDateCount,
        model,
        monthNavigator,
        numberOfMonths,
        onMonthChange,
        onYearChange,
        panelStyle,
        panelStyleClass,
        placeholder,
        responsiveBreakpoint,
        selectOtherMonths,
        selectionMode,
        shortYearCutoff,
        showButtonBar,
        showIcon,
        showMilliseconds,
        showMinMaxRange,
        showOnFocus,
        showOtherMonths,
        showLongMonthNames,
        showSeconds,
        showTime,
        showWeek,
        stepHour,
        stepMillisecond,
        stepMinute,
        stepSecond,
        timeInput,
        timeOnly,
        touchUI,
        triggerButtonIcon,
        view,
        weekCalculator,
        widgetVar,
        yearNavigator,
        yearRange
    }

    public DatePickerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isInline() {
        return (Boolean) getStateHelper().eval(PropertyKeys.inline, false);
    }

    public void setInline(boolean inline) {
        getStateHelper().put(PropertyKeys.inline, inline);
    }

    public String getButtonTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.buttonTabindex, null);
    }

    public void setButtonTabindex(String buttonTabindex) {
        getStateHelper().put(PropertyKeys.buttonTabindex, buttonTabindex);
    }

    public boolean isShowIcon() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showIcon, false);
    }

    public void setShowIcon(boolean showIcon) {
        getStateHelper().put(PropertyKeys.showIcon, showIcon);
    }

    public String getBeforeShow() {
        return (String) getStateHelper().eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        getStateHelper().put(PropertyKeys.beforeShow, beforeShow);
    }

    public boolean isFocusOnSelect() {
        return (Boolean) getStateHelper().eval(PropertyKeys.focusOnSelect, false);
    }

    public void setFocusOnSelect(boolean focusOnSelect) {
        getStateHelper().put(PropertyKeys.focusOnSelect, focusOnSelect);
    }

    public String getYearRange() {
        return (String) getStateHelper().eval(PropertyKeys.yearRange, null);
    }

    public void setYearRange(String yearRange) {
        getStateHelper().put(PropertyKeys.yearRange, yearRange);
    }

    @Override
    public String getSelectionMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionMode,
                () -> "week".equals(getView()) ? "range" : "single");
    }

    public void setSelectionMode(String selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, selectionMode);
    }

    public boolean isShowOtherMonths() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showOtherMonths, false);
    }

    public void setShowOtherMonths(boolean showOtherMonths) {
        getStateHelper().put(PropertyKeys.showOtherMonths, showOtherMonths);
    }

    public boolean isSelectOtherMonths() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectOtherMonths, false);
    }

    public void setSelectOtherMonths(boolean selectOtherMonths) {
        getStateHelper().put(PropertyKeys.selectOtherMonths, selectOtherMonths);
    }

    public boolean isShowOnFocus() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showOnFocus, true);
    }

    public void setShowOnFocus(boolean showOnFocus) {
        getStateHelper().put(PropertyKeys.showOnFocus, showOnFocus);
    }

    public String getShortYearCutoff() {
        return (String) getStateHelper().eval(PropertyKeys.shortYearCutoff, null);
    }

    public void setShortYearCutoff(String shortYearCutoff) {
        getStateHelper().put(PropertyKeys.shortYearCutoff, shortYearCutoff);
    }

    public boolean isMonthNavigator() {
        return (Boolean) getStateHelper().eval(PropertyKeys.monthNavigator, false);
    }

    public void setMonthNavigator(boolean monthNavigator) {
        getStateHelper().put(PropertyKeys.monthNavigator, monthNavigator);
    }

    public String getYearNavigator() {
        return (String) getStateHelper().eval(PropertyKeys.yearNavigator, "false");
    }

    public void setYearNavigator(String yearNavigator) {
        getStateHelper().put(PropertyKeys.yearNavigator, yearNavigator);
    }

    public boolean isShowTime() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showTime, false);
    }

    public Boolean isShowTimeWithoutDefault() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showTime);
    }

    public void setShowTime(boolean showTime) {
        getStateHelper().put(PropertyKeys.showTime, showTime);
    }

    public String getHourFormat() {
        return (String) getStateHelper().eval(PropertyKeys.hourFormat, null);
    }

    public void setHourFormat(String hourFormat) {
        getStateHelper().put(PropertyKeys.hourFormat, hourFormat);
    }

    public boolean isShowSeconds() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showSeconds, false);
    }

    public Boolean isShowSecondsWithoutDefault() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showSeconds);
    }

    public void setShowSeconds(boolean showSeconds) {
        getStateHelper().put(PropertyKeys.showSeconds, showSeconds);
    }

    public boolean isShowMilliseconds() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showMilliseconds, false);
    }

    public Boolean isShowMillisecondsWithoutDefault() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showMilliseconds);
    }

    public void setShowMilliseconds(boolean showMilliseconds) {
        getStateHelper().put(PropertyKeys.showMilliseconds, showMilliseconds);
    }

    public int getStepHour() {
        return (Integer) getStateHelper().eval(PropertyKeys.stepHour, 1);
    }

    public void setStepHour(int stepHour) {
        getStateHelper().put(PropertyKeys.stepHour, stepHour);
    }

    public int getStepMinute() {
        return (Integer) getStateHelper().eval(PropertyKeys.stepMinute, 1);
    }

    public void setStepMinute(int stepMinute) {
        getStateHelper().put(PropertyKeys.stepMinute, stepMinute);
    }

    public int getStepSecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.stepSecond, 1);
    }

    public void setStepSecond(int stepSecond) {
        getStateHelper().put(PropertyKeys.stepSecond, stepSecond);
    }

    public int getStepMillisecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.stepMillisecond, 1);
    }

    public void setStepMillisecond(int stepMillisecond) {
        getStateHelper().put(PropertyKeys.stepMillisecond, stepMillisecond);
    }

    public boolean isShowButtonBar() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showButtonBar, false);
    }

    public void setShowButtonBar(boolean showButtonBar) {
        getStateHelper().put(PropertyKeys.showButtonBar, showButtonBar);
    }

    public String getPanelStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
    }

    public void setPanelStyleClass(String panelStyleClass) {
        getStateHelper().put(PropertyKeys.panelStyleClass, panelStyleClass);
    }

    public String getPanelStyle() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyle, null);
    }

    public void setPanelStyle(String panelStyle) {
        getStateHelper().put(PropertyKeys.panelStyle, panelStyle);
    }

    public boolean isKeepInvalid() {
        return (Boolean) getStateHelper().eval(PropertyKeys.keepInvalid, false);
    }

    public void setKeepInvalid(boolean keepInvalid) {
        getStateHelper().put(PropertyKeys.keepInvalid, keepInvalid);
    }

    public boolean isHideOnDateTimeSelect() {
        return (Boolean) getStateHelper().eval(PropertyKeys.hideOnDateTimeSelect, false);
    }

    public void setHideOnDateTimeSelect(boolean hideOnDateTimeSelect) {
        getStateHelper().put(PropertyKeys.hideOnDateTimeSelect, hideOnDateTimeSelect);
    }

    public boolean isHideOnRangeSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.hideOnRangeSelection, false);
    }

    public void setHideOnRangeSelection(boolean hideOnRangeSelection) {
        getStateHelper().put(PropertyKeys.hideOnRangeSelection, hideOnRangeSelection);
    }

    public int getMaxDateCount() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxDateCount, Integer.MAX_VALUE);
    }

    public void setMaxDateCount(int maxDateCount) {
        getStateHelper().put(PropertyKeys.maxDateCount, maxDateCount);
    }

    public int getNumberOfMonths() {
        return (Integer) getStateHelper().eval(PropertyKeys.numberOfMonths, 1);
    }

    public void setNumberOfMonths(int numberOfMonths) {
        getStateHelper().put(PropertyKeys.numberOfMonths, numberOfMonths);
    }

    public String getView() {
        return (String) getStateHelper().eval(PropertyKeys.view, null);
    }

    public void setView(String view) {
        getStateHelper().put(PropertyKeys.view, view);
    }

    public boolean isAutoDetectDisplay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoDetectDisplay, true);
    }

    public void setAutoDetectDisplay(boolean autoDetectDisplay) {
        getStateHelper().put(PropertyKeys.autoDetectDisplay, autoDetectDisplay);
    }

    public boolean isTouchUI() {
        return (Boolean) getStateHelper().eval(PropertyKeys.touchUI, false);
    }

    public void setTouchUI(boolean touchUI) {
        getStateHelper().put(PropertyKeys.touchUI, touchUI);
    }

    public String getDateTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.dateTemplate, null);
    }

    public void setDateTemplate(String dateTemplate) {
        getStateHelper().put(PropertyKeys.dateTemplate, dateTemplate);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, "@(body)");
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public String getTriggerButtonIcon() {
        return (String) getStateHelper().eval(PropertyKeys.triggerButtonIcon, null);
    }

    public void setTriggerButtonIcon(String triggerButtonIcon) {
        getStateHelper().put(PropertyKeys.triggerButtonIcon, triggerButtonIcon);
    }

    public List getDisabledDates() {
        return (List) getStateHelper().eval(PropertyKeys.disabledDates, null);
    }

    public void setDisabledDates(List disabledDates) {
        getStateHelper().put(PropertyKeys.disabledDates, disabledDates);
    }

    public List getEnabledDates() {
        return (List) getStateHelper().eval(PropertyKeys.enabledDates, null);
    }

    public void setEnabledDates(List enabledDates) {
        getStateHelper().put(PropertyKeys.enabledDates, enabledDates);
    }

    public List<Integer> getDisabledDays() {
        return (List<Integer>) getStateHelper().eval(PropertyKeys.disabledDays, null);
    }

    public void setDisabledDays(List<Integer> disabledDays) {
        getStateHelper().put(PropertyKeys.disabledDays, disabledDays);
    }

    public String getOnMonthChange() {
        return (String) getStateHelper().eval(PropertyKeys.onMonthChange, null);
    }

    public void setOnMonthChange(String onMonthChange) {
        getStateHelper().put(PropertyKeys.onMonthChange, onMonthChange);
    }

    public String getOnYearChange() {
        return (String) getStateHelper().eval(PropertyKeys.onYearChange, null);
    }

    public void setOnYearChange(String onYearChange) {
        getStateHelper().put(PropertyKeys.onYearChange, onYearChange);
    }

    public boolean isTimeInput() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeInput, false);
    }

    public void setTimeInput(boolean timeInput) {
        getStateHelper().put(PropertyKeys.timeInput, timeInput);
    }

    public boolean isShowWeek() {
        return (boolean) getStateHelper().eval(PropertyKeys.showWeek,
                () -> "week".equals(getView()));
    }

    public void setShowWeek(boolean showWeek) {
        getStateHelper().put(PropertyKeys.showWeek, showWeek);
    }

    public String getWeekCalculator() {
        return (String) getStateHelper().eval(PropertyKeys.weekCalculator, null);
    }

    public void setWeekCalculator(String weekCalculator) {
        getStateHelper().put(PropertyKeys.weekCalculator, weekCalculator);
    }

    public DateMetadataModel getModel() {
        return (DateMetadataModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(DateMetadataModel model) {
        getStateHelper().put(PropertyKeys.model, model);
    }

    public void setResponsiveBreakpoint(int responsiveBreakpoint) {
        getStateHelper().put(PropertyKeys.responsiveBreakpoint, responsiveBreakpoint);
    }

    public int getResponsiveBreakpoint() {
        return (Integer) getStateHelper().eval(PropertyKeys.responsiveBreakpoint, RESPONSIVE_BREAKPOINT_SMALL);
    }

    public boolean isShowMinMaxRange() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showMinMaxRange, true);
    }

    public void setShowMinMaxRange(boolean showMinMaxRange) {
        getStateHelper().put(PropertyKeys.showMinMaxRange, showMinMaxRange);
    }

    public boolean isAutoMonthFormat() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoMonthFormat, true);
    }

    public void setAutoMonthFormat(boolean autoMonthFormat) {
        getStateHelper().put(PropertyKeys.autoMonthFormat, autoMonthFormat);
    }

    public boolean isShowLongMonthNames() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showLongMonthNames, false);
    }

    public void setShowLongMonthNames(boolean showLongMonthNames) {
        getStateHelper().put(PropertyKeys.showLongMonthNames, showLongMonthNames);
    }

    @Override
    public Boolean getFlex() {
        return (Boolean) getStateHelper().eval(PropertyKeys.flex, null);
    }

    public void setFlex(Boolean flex) {
        getStateHelper().put(PropertyKeys.flex, flex);
    }

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

    @Override
    public boolean isReadonlyInput() {
        return (boolean) getStateHelper().eval(UICalendar.PropertyKeys.readonlyInput,
                () -> "week".equals(getView()) ? true : false);
    }

}
