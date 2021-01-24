/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.api.Widget;
import org.primefaces.util.CalendarUtils;
import org.primefaces.model.datepicker.DateMetadataModel;

public abstract class DatePickerBase extends UICalendar implements Widget, InputHolder, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DatePickerRenderer";

    protected String timeSeparator;

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        timeOnly,
        inline,
        buttonTabindex,
        showIcon,
        beforeShow,
        focusOnSelect,
        yearRange,
        selectionMode,
        showOtherMonths,
        selectOtherMonths,
        showOnFocus,
        shortYearCutoff,
        monthNavigator,
        yearNavigator,
        showTime,
        hourFormat,
        showSeconds,
        stepHour,
        stepMinute,
        stepSecond,
        showButtonBar,
        panelStyleClass,
        panelStyle,
        keepInvalid,
        hideOnDateTimeSelect,
        maxDateCount,
        numberOfMonths,
        view,
        touchUI,
        dateTemplate,
        appendTo,
        triggerButtonIcon,
        disabledDates,
        disabledDays,
        onMonthChange,
        onYearChange,
        timeInput,
        showWeek,
        weekCalculator,
        model
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
        return (String) getStateHelper().eval(PropertyKeys.selectionMode, "single");
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

    public boolean isYearNavigator() {
        return (Boolean) getStateHelper().eval(PropertyKeys.yearNavigator, false);
    }

    public void setYearNavigator(boolean yearNavigator) {
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
        return (Boolean) getStateHelper().eval(PropertyKeys.showWeek, false);
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
            if (isShowSeconds()) {
                timeOnlyPattern += (separator + "ss");
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



}
