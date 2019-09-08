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
package org.primefaces.component.calendar;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.api.Widget;

public abstract class CalendarBase extends UICalendar implements Widget, InputHolder, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CalendarRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        pages,
        mode,
        navigator,
        showButtonPanel,
        effect,
        effectDuration,
        showOn,
        showWeek,
        disabledWeekends,
        showOtherMonths,
        selectOtherMonths,
        yearRange,
        stepHour,
        stepMinute,
        stepSecond,
        minHour,
        maxHour,
        minMinute,
        maxMinute,
        minSecond,
        maxSecond,
        pagedate,
        beforeShowDay,
        mask,
        timeControlType,
        beforeShow,
        maskSlotChar,
        maskAutoClear,
        timeControlObject,
        timeInput,
        showHour,
        showMinute,
        showSecond,
        showMillisec,
        showTodayButton,
        buttonTabindex,
        focusOnSelect,
        oneLine,
        defaultHour,
        defaultMinute,
        defaultSecond,
        defaultMillisec
    }

    public CalendarBase() {
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

    public int getPages() {
        return (Integer) getStateHelper().eval(PropertyKeys.pages, 1);
    }

    public void setPages(int pages) {
        getStateHelper().put(PropertyKeys.pages, pages);
    }

    public String getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "popup");
    }

    public void setMode(String mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public boolean isNavigator() {
        return (Boolean) getStateHelper().eval(PropertyKeys.navigator, false);
    }

    public void setNavigator(boolean navigator) {
        getStateHelper().put(PropertyKeys.navigator, navigator);
    }

    public boolean isShowButtonPanel() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showButtonPanel, false);
    }

    public void setShowButtonPanel(boolean showButtonPanel) {
        getStateHelper().put(PropertyKeys.showButtonPanel, showButtonPanel);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEffectDuration() {
        return (String) getStateHelper().eval(PropertyKeys.effectDuration, "normal");
    }

    public void setEffectDuration(String effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, effectDuration);
    }

    public String getShowOn() {
        return (String) getStateHelper().eval(PropertyKeys.showOn, "focus");
    }

    public void setShowOn(String showOn) {
        getStateHelper().put(PropertyKeys.showOn, showOn);
    }

    public boolean isShowWeek() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showWeek, false);
    }

    public void setShowWeek(boolean showWeek) {
        getStateHelper().put(PropertyKeys.showWeek, showWeek);
    }

    public boolean isDisabledWeekends() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabledWeekends, false);
    }

    public void setDisabledWeekends(boolean disabledWeekends) {
        getStateHelper().put(PropertyKeys.disabledWeekends, disabledWeekends);
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

    public String getYearRange() {
        return (String) getStateHelper().eval(PropertyKeys.yearRange, null);
    }

    public void setYearRange(String yearRange) {
        getStateHelper().put(PropertyKeys.yearRange, yearRange);
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

    public int getMinHour() {
        return (Integer) getStateHelper().eval(PropertyKeys.minHour, 0);
    }

    public void setMinHour(int minHour) {
        getStateHelper().put(PropertyKeys.minHour, minHour);
    }

    public int getMaxHour() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxHour, 23);
    }

    public void setMaxHour(int maxHour) {
        getStateHelper().put(PropertyKeys.maxHour, maxHour);
    }

    public int getMinMinute() {
        return (Integer) getStateHelper().eval(PropertyKeys.minMinute, 0);
    }

    public void setMinMinute(int minMinute) {
        getStateHelper().put(PropertyKeys.minMinute, minMinute);
    }

    public int getMaxMinute() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxMinute, 59);
    }

    public void setMaxMinute(int maxMinute) {
        getStateHelper().put(PropertyKeys.maxMinute, maxMinute);
    }

    public int getMinSecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.minSecond, 0);
    }

    public void setMinSecond(int minSecond) {
        getStateHelper().put(PropertyKeys.minSecond, minSecond);
    }

    public int getMaxSecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxSecond, 59);
    }

    public void setMaxSecond(int maxSecond) {
        getStateHelper().put(PropertyKeys.maxSecond, maxSecond);
    }

    public Object getPagedate() {
        return getStateHelper().eval(PropertyKeys.pagedate, null);
    }

    public void setPagedate(Object pagedate) {
        getStateHelper().put(PropertyKeys.pagedate, pagedate);
    }

    public String getBeforeShowDay() {
        return (String) getStateHelper().eval(PropertyKeys.beforeShowDay, null);
    }

    public void setBeforeShowDay(String beforeShowDay) {
        getStateHelper().put(PropertyKeys.beforeShowDay, beforeShowDay);
    }

    public String getMask() {
        return (String) getStateHelper().eval(PropertyKeys.mask, "false");
    }

    public void setMask(String mask) {
        getStateHelper().put(PropertyKeys.mask, mask);
    }

    public String getTimeControlType() {
        return (String) getStateHelper().eval(PropertyKeys.timeControlType, "slider");
    }

    public void setTimeControlType(String timeControlType) {
        getStateHelper().put(PropertyKeys.timeControlType, timeControlType);
    }

    public String getBeforeShow() {
        return (String) getStateHelper().eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        getStateHelper().put(PropertyKeys.beforeShow, beforeShow);
    }

    public String getMaskSlotChar() {
        return (String) getStateHelper().eval(PropertyKeys.maskSlotChar, null);
    }

    public void setMaskSlotChar(String maskSlotChar) {
        getStateHelper().put(PropertyKeys.maskSlotChar, maskSlotChar);
    }

    public boolean isMaskAutoClear() {
        return (Boolean) getStateHelper().eval(PropertyKeys.maskAutoClear, true);
    }

    public void setMaskAutoClear(boolean maskAutoClear) {
        getStateHelper().put(PropertyKeys.maskAutoClear, maskAutoClear);
    }

    public String getTimeControlObject() {
        return (String) getStateHelper().eval(PropertyKeys.timeControlObject, null);
    }

    public void setTimeControlObject(String timeControlObject) {
        getStateHelper().put(PropertyKeys.timeControlObject, timeControlObject);
    }

    public boolean isTimeInput() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeInput, false);
    }

    public void setTimeInput(boolean timeInput) {
        getStateHelper().put(PropertyKeys.timeInput, timeInput);
    }

    public String getShowHour() {
        return (String) getStateHelper().eval(PropertyKeys.showHour, null);
    }

    public void setShowHour(String showHour) {
        getStateHelper().put(PropertyKeys.showHour, showHour);
    }

    public String getShowMinute() {
        return (String) getStateHelper().eval(PropertyKeys.showMinute, null);
    }

    public void setShowMinute(String showMinute) {
        getStateHelper().put(PropertyKeys.showMinute, showMinute);
    }

    public String getShowSecond() {
        return (String) getStateHelper().eval(PropertyKeys.showSecond, null);
    }

    public void setShowSecond(String showSecond) {
        getStateHelper().put(PropertyKeys.showSecond, showSecond);
    }

    public String getShowMillisec() {
        return (String) getStateHelper().eval(PropertyKeys.showMillisec, null);
    }

    public void setShowMillisec(String showMillisec) {
        getStateHelper().put(PropertyKeys.showMillisec, showMillisec);
    }

    public boolean isShowTodayButton() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showTodayButton, true);
    }

    public void setShowTodayButton(boolean showTodayButton) {
        getStateHelper().put(PropertyKeys.showTodayButton, showTodayButton);
    }

    public String getButtonTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.buttonTabindex, null);
    }

    public void setButtonTabindex(String buttonTabindex) {
        getStateHelper().put(PropertyKeys.buttonTabindex, buttonTabindex);
    }

    public boolean isFocusOnSelect() {
        return (Boolean) getStateHelper().eval(PropertyKeys.focusOnSelect, false);
    }

    public void setFocusOnSelect(boolean focusOnSelect) {
        getStateHelper().put(PropertyKeys.focusOnSelect, focusOnSelect);
    }

    public boolean isOneLine() {
        return (Boolean) getStateHelper().eval(PropertyKeys.oneLine, false);
    }

    public void setOneLine(boolean oneLine) {
        getStateHelper().put(PropertyKeys.oneLine, oneLine);
    }

    public int getDefaultHour() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultHour, 0);
    }

    public void setDefaultHour(int defaultHour) {
        getStateHelper().put(PropertyKeys.defaultHour, defaultHour);
    }

    public int getDefaultMinute() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultMinute, 0);
    }

    public void setDefaultMinute(int defaultMinute) {
        getStateHelper().put(PropertyKeys.defaultMinute, defaultMinute);
    }

    public int getDefaultSecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultSecond, 0);
    }

    public void setDefaultSecond(int defaultSecond) {
        getStateHelper().put(PropertyKeys.defaultSecond, defaultSecond);
    }

    public int getDefaultMillisec() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultMillisec, 0);
    }

    public void setDefaultMillisec(int defaultMillisec) {
        getStateHelper().put(PropertyKeys.defaultMillisec, defaultMillisec);
    }
}