/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.schedule;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ScheduleBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ScheduleRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        locale,
        aspectRatio,
        view,
        initialDate,
        showWeekends,
        style,
        styleClass,
        draggable,
        resizable,
        showHeader,
        leftHeaderTemplate,
        centerHeaderTemplate,
        rightHeaderTemplate,
        allDaySlot,
        slotDuration,
        slotMinutes,
        scrollTime,
        firstHour,
        minTime,
        maxTime,
        slotLabelFormat,
        axisFormat,
        timeFormat,
        columnFormat,
        timeZone,
        clientTimeZone,
        ignoreTimezone,
        tooltip,
        showWeekNumbers,
        extender,
        displayEventEnd,
        weekNumberCalculation,
        weekNumberCalculator,
        nextDayThreshold,
        slotEventOverlap,
        urlTarget
    }

    public ScheduleBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public org.primefaces.model.ScheduleModel getValue() {
        return (org.primefaces.model.ScheduleModel) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(org.primefaces.model.ScheduleModel value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public Object getLocale() {
        return getStateHelper().eval(PropertyKeys.locale, null);
    }

    public void setLocale(Object locale) {
        getStateHelper().put(PropertyKeys.locale, locale);
    }

    public double getAspectRatio() {
        return (Double) getStateHelper().eval(PropertyKeys.aspectRatio, Double.MIN_VALUE);
    }

    public void setAspectRatio(double aspectRatio) {
        getStateHelper().put(PropertyKeys.aspectRatio, aspectRatio);
    }

    public String getView() {
        return (String) getStateHelper().eval(PropertyKeys.view, "month");
    }

    public void setView(String view) {
        getStateHelper().put(PropertyKeys.view, view);
    }

    public Object getInitialDate() {
        return getStateHelper().eval(PropertyKeys.initialDate, null);
    }

    public void setInitialDate(Object initialDate) {
        getStateHelper().put(PropertyKeys.initialDate, initialDate);
    }

    public boolean isShowWeekends() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showWeekends, true);
    }

    public void setShowWeekends(boolean showWeekends) {
        getStateHelper().put(PropertyKeys.showWeekends, showWeekends);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isDraggable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
    }

    public void setDraggable(boolean draggable) {
        getStateHelper().put(PropertyKeys.draggable, draggable);
    }

    public boolean isResizable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
    }

    public void setResizable(boolean resizable) {
        getStateHelper().put(PropertyKeys.resizable, resizable);
    }

    public boolean isShowHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
    }

    public void setShowHeader(boolean showHeader) {
        getStateHelper().put(PropertyKeys.showHeader, showHeader);
    }

    public String getLeftHeaderTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.leftHeaderTemplate, "prev,next today");
    }

    public void setLeftHeaderTemplate(String leftHeaderTemplate) {
        getStateHelper().put(PropertyKeys.leftHeaderTemplate, leftHeaderTemplate);
    }

    public String getCenterHeaderTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.centerHeaderTemplate, "title");
    }

    public void setCenterHeaderTemplate(String centerHeaderTemplate) {
        getStateHelper().put(PropertyKeys.centerHeaderTemplate, centerHeaderTemplate);
    }

    public String getRightHeaderTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.rightHeaderTemplate, "month,agendaWeek,agendaDay");
    }

    public void setRightHeaderTemplate(String rightHeaderTemplate) {
        getStateHelper().put(PropertyKeys.rightHeaderTemplate, rightHeaderTemplate);
    }

    public boolean isAllDaySlot() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allDaySlot, true);
    }

    public void setAllDaySlot(boolean allDaySlot) {
        getStateHelper().put(PropertyKeys.allDaySlot, allDaySlot);
    }

    public String getSlotDuration() {
        return (String) getStateHelper().eval(PropertyKeys.slotDuration, "00:30:00");
    }

    public void setSlotDuration(String slotDuration) {
        getStateHelper().put(PropertyKeys.slotDuration, slotDuration);
    }

    public int getSlotMinutes() {
        return (Integer) getStateHelper().eval(PropertyKeys.slotMinutes, 30);
    }

    public void setSlotMinutes(int slotMinutes) {
        getStateHelper().put(PropertyKeys.slotMinutes, slotMinutes);
    }

    public String getScrollTime() {
        return (String) getStateHelper().eval(PropertyKeys.scrollTime, "06:00:00");
    }

    public void setScrollTime(String scrollTime) {
        getStateHelper().put(PropertyKeys.scrollTime, scrollTime);
    }

    public int getFirstHour() {
        return (Integer) getStateHelper().eval(PropertyKeys.firstHour, 6);
    }

    public void setFirstHour(int firstHour) {
        getStateHelper().put(PropertyKeys.firstHour, firstHour);
    }

    public String getMinTime() {
        return (String) getStateHelper().eval(PropertyKeys.minTime, null);
    }

    public void setMinTime(String minTime) {
        getStateHelper().put(PropertyKeys.minTime, minTime);
    }

    public String getMaxTime() {
        return (String) getStateHelper().eval(PropertyKeys.maxTime, null);
    }

    public void setMaxTime(String maxTime) {
        getStateHelper().put(PropertyKeys.maxTime, maxTime);
    }

    public String getSlotLabelFormat() {
        return (String) getStateHelper().eval(PropertyKeys.slotLabelFormat, null);
    }

    public void setSlotLabelFormat(String slotLabelFormat) {
        getStateHelper().put(PropertyKeys.slotLabelFormat, slotLabelFormat);
    }

    public String getAxisFormat() {
        return (String) getStateHelper().eval(PropertyKeys.axisFormat, null);
    }

    public void setAxisFormat(String axisFormat) {
        getStateHelper().put(PropertyKeys.axisFormat, axisFormat);
    }

    public String getTimeFormat() {
        return (String) getStateHelper().eval(PropertyKeys.timeFormat, null);
    }

    public void setTimeFormat(String timeFormat) {
        getStateHelper().put(PropertyKeys.timeFormat, timeFormat);
    }

    public String getColumnFormat() {
        return (String) getStateHelper().eval(PropertyKeys.columnFormat, null);
    }

    public void setColumnFormat(String columnFormat) {
        getStateHelper().put(PropertyKeys.columnFormat, columnFormat);
    }

    public Object getTimeZone() {
        return getStateHelper().eval(PropertyKeys.timeZone, null);
    }

    public void setTimeZone(Object timeZone) {
        getStateHelper().put(PropertyKeys.timeZone, timeZone);
    }

    public String getClientTimeZone() {
        return (String) getStateHelper().eval(PropertyKeys.clientTimeZone, null);
    }

    public void setClientTimeZone(String clientTimeZone) {
        getStateHelper().put(PropertyKeys.clientTimeZone, clientTimeZone);
    }

    public boolean isIgnoreTimezone() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ignoreTimezone, true);
    }

    public void setIgnoreTimezone(boolean ignoreTimezone) {
        getStateHelper().put(PropertyKeys.ignoreTimezone, ignoreTimezone);
    }

    public boolean isTooltip() {
        return (Boolean) getStateHelper().eval(PropertyKeys.tooltip, false);
    }

    public void setTooltip(boolean tooltip) {
        getStateHelper().put(PropertyKeys.tooltip, tooltip);
    }

    public boolean isShowWeekNumbers() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showWeekNumbers, false);
    }

    public void setShowWeekNumbers(boolean showWeekNumbers) {
        getStateHelper().put(PropertyKeys.showWeekNumbers, showWeekNumbers);
    }

    public String getExtender() {
        return (String) getStateHelper().eval(PropertyKeys.extender, null);
    }

    public void setExtender(String extender) {
        getStateHelper().put(PropertyKeys.extender, extender);
    }

    public String getDisplayEventEnd() {
        return (String) getStateHelper().eval(PropertyKeys.displayEventEnd, null);
    }

    public void setDisplayEventEnd(String displayEventEnd) {
        getStateHelper().put(PropertyKeys.displayEventEnd, displayEventEnd);
    }

    public String getWeekNumberCalculation() {
        return (String) getStateHelper().eval(PropertyKeys.weekNumberCalculation, "local");
    }

    public void setWeekNumberCalculation(String weekNumberCalculation) {
        getStateHelper().put(PropertyKeys.weekNumberCalculation, weekNumberCalculation);
    }

    public String getWeekNumberCalculator() {
        return (String) getStateHelper().eval(PropertyKeys.weekNumberCalculator, null);
    }

    public void setWeekNumberCalculator(String weekNumberCalculator) {
        getStateHelper().put(PropertyKeys.weekNumberCalculator, weekNumberCalculator);
    }

    public String getNextDayThreshold() {
        return (String) getStateHelper().eval(PropertyKeys.nextDayThreshold, "09:00:00");
    }

    public void setNextDayThreshold(String nextDayThreshold) {
        getStateHelper().put(PropertyKeys.nextDayThreshold, nextDayThreshold);
    }

    public boolean isSlotEventOverlap() {
        return (Boolean) getStateHelper().eval(PropertyKeys.slotEventOverlap, true);
    }

    public void setSlotEventOverlap(boolean slotEventOverlap) {
        getStateHelper().put(PropertyKeys.slotEventOverlap, slotEventOverlap);
    }

    public String getUrlTarget() {
        return (String) getStateHelper().eval(PropertyKeys.urlTarget, "_blank");
    }

    public void setUrlTarget(String urlTarget) {
        getStateHelper().put(PropertyKeys.urlTarget, urlTarget);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}