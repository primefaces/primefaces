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
package org.primefaces.component.calendar;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class CalendarBase extends UICalendar implements Widget, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CalendarRenderer";

    public CalendarBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Number of months to display.", defaultValue = "1")
    public abstract int getPages();

    @Property(description = "Display mode. Options: 'popup', 'inline'.", defaultValue = "popup")
    public abstract String getMode();

    @Property(description = "When enabled, shows month/year navigator.", defaultValue = "false")
    public abstract boolean isNavigator();

    @Property(description = "When enabled, shows button panel.", defaultValue = "false")
    public abstract boolean isShowButtonPanel();

    @Property(description = "Animation effect for showing/hiding calendar.")
    public abstract String getEffect();

    @Property(description = "Duration of animation effect.", defaultValue = "normal")
    public abstract String getEffectDuration();

    @Property(description = "Event that triggers calendar display. Options: 'focus', 'button', 'both'.", defaultValue = "focus")
    public abstract String getShowOn();

    @Property(description = "When enabled, shows week numbers.", defaultValue = "false")
    public abstract boolean isShowWeek();

    @Property(description = "When enabled, disables weekends.", defaultValue = "false")
    public abstract boolean isDisabledWeekends();

    @Property(description = "When enabled, shows dates from other months.", defaultValue = "false")
    public abstract boolean isShowOtherMonths();

    @Property(description = "When enabled, allows selecting dates from other months.", defaultValue = "false")
    public abstract boolean isSelectOtherMonths();

    @Property(description = "Year range for navigation. Format: 'start:end'.")
    public abstract String getYearRange();

    @Property(description = "Step value for hour spinner.", defaultValue = "1")
    public abstract int getStepHour();

    @Property(description = "Step value for minute spinner.", defaultValue = "1")
    public abstract int getStepMinute();

    @Property(description = "Step value for second spinner.", defaultValue = "1")
    public abstract int getStepSecond();

    @Property(description = "Minimum hour value.", defaultValue = "0")
    public abstract int getMinHour();

    @Property(description = "Maximum hour value.", defaultValue = "23")
    public abstract int getMaxHour();

    @Property(description = "Minimum minute value.", defaultValue = "0")
    public abstract int getMinMinute();

    @Property(description = "Maximum minute value.", defaultValue = "59")
    public abstract int getMaxMinute();

    @Property(description = "Minimum second value.", defaultValue = "0")
    public abstract int getMinSecond();

    @Property(description = "Maximum second value.", defaultValue = "59")
    public abstract int getMaxSecond();

    @Property(description = "Initial page date for the calendar.")
    public abstract Object getPagedate();

    @Property(description = "JavaScript callback to execute before showing each day.")
    public abstract String getBeforeShowDay();

    @Property(description = "Time control type. Options: 'slider', 'select'.", defaultValue = "slider")
    public abstract String getTimeControlType();

    @Property(description = "JavaScript callback to execute before showing the calendar.")
    public abstract String getBeforeShow();

    @Property(description = "JavaScript object for custom time control.")
    public abstract String getTimeControlObject();

    @Property(description = "When enabled, uses time input field instead of spinner.", defaultValue = "false")
    public abstract boolean isTimeInput();

    @Property(description = "Show hour control. Options: 'true', 'false', 'auto'.")
    public abstract String getShowHour();

    @Property(description = "Show minute control. Options: 'true', 'false', 'auto'.")
    public abstract String getShowMinute();

    @Property(description = "Show second control. Options: 'true', 'false', 'auto'.")
    public abstract String getShowSecond();

    @Property(description = "Show millisecond control. Options: 'true', 'false', 'auto'.")
    public abstract String getShowMillisec();

    @Property(description = "When enabled, shows today button.", defaultValue = "true")
    public abstract boolean isShowTodayButton();

    @Property(description = "Tabindex for the trigger button.")
    public abstract String getButtonTabindex();

    @Property(description = "When enabled, focuses input after selection.", defaultValue = "false")
    public abstract boolean isFocusOnSelect();

    @Property(description = "When enabled, displays calendar in one line.", defaultValue = "false")
    public abstract boolean isOneLine();

    @Property(description = "Cutoff year for two-digit year interpretation.")
    public abstract String getShortYearCutoff();

    @Override
    public String calculateWidgetPattern() {
        return isTimeOnly() ? calculateTimeOnlyPattern() : calculatePattern();
    }

}