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
package org.primefaces.component.timeline;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class TimelineBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TimelineRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        var,
        value,
        varGroup,
        locale,
        timeZone,
        browserTimeZone,
        height,
        minHeight,
        width,
        responsive,
        axisOnTop,
        dragAreaWidth,
        editable,
        selectable,
        unselectable,
        zoomable,
        moveable,
        start,
        end,
        min,
        max,
        zoomMin,
        zoomMax,
        preloadFactor,
        eventMargin,
        eventMarginAxis,
        eventStyle,
        groupsChangeable,
        groupsOnRight,
        groupsOrder,
        groupsWidth,
        groupMinHeight,
        snapEvents,
        stackEvents,
        showCurrentTime,
        showMajorLabels,
        showMinorLabels,
        showButtonNew,
        showNavigation,
        timeChangeable,
        dropHoverStyleClass,
        dropActiveStyleClass,
        dropAccept,
        dropScope,
        animate,
        animateZoom
    }

    public TimelineBase() {
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

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public org.primefaces.model.timeline.TimelineModel getValue() {
        return (org.primefaces.model.timeline.TimelineModel) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(org.primefaces.model.timeline.TimelineModel value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getVarGroup() {
        return (String) getStateHelper().eval(PropertyKeys.varGroup, null);
    }

    public void setVarGroup(String varGroup) {
        getStateHelper().put(PropertyKeys.varGroup, varGroup);
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

    public Object getBrowserTimeZone() {
        return getStateHelper().eval(PropertyKeys.browserTimeZone, null);
    }

    public void setBrowserTimeZone(Object browserTimeZone) {
        getStateHelper().put(PropertyKeys.browserTimeZone, browserTimeZone);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, "auto");
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public int getMinHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.minHeight, 0);
    }

    public void setMinHeight(int minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, minHeight);
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, "100%");
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public boolean isResponsive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.responsive, true);
    }

    public void setResponsive(boolean responsive) {
        getStateHelper().put(PropertyKeys.responsive, responsive);
    }

    public boolean isAxisOnTop() {
        return (Boolean) getStateHelper().eval(PropertyKeys.axisOnTop, false);
    }

    public void setAxisOnTop(boolean axisOnTop) {
        getStateHelper().put(PropertyKeys.axisOnTop, axisOnTop);
    }

    public int getDragAreaWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.dragAreaWidth, 10);
    }

    public void setDragAreaWidth(int dragAreaWidth) {
        getStateHelper().put(PropertyKeys.dragAreaWidth, dragAreaWidth);
    }

    public boolean isEditable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
    }

    public boolean isSelectable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectable, true);
    }

    public void setSelectable(boolean selectable) {
        getStateHelper().put(PropertyKeys.selectable, selectable);
    }

    public boolean isUnselectable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.unselectable, true);
    }

    public void setUnselectable(boolean unselectable) {
        getStateHelper().put(PropertyKeys.unselectable, unselectable);
    }

    public boolean isZoomable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.zoomable, true);
    }

    public void setZoomable(boolean zoomable) {
        getStateHelper().put(PropertyKeys.zoomable, zoomable);
    }

    public boolean isMoveable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.moveable, true);
    }

    public void setMoveable(boolean moveable) {
        getStateHelper().put(PropertyKeys.moveable, moveable);
    }

    public java.util.Date getStart() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.start, null);
    }

    public void setStart(java.util.Date start) {
        getStateHelper().put(PropertyKeys.start, start);
    }

    public java.util.Date getEnd() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.end, null);
    }

    public void setEnd(java.util.Date end) {
        getStateHelper().put(PropertyKeys.end, end);
    }

    public java.util.Date getMin() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.min, null);
    }

    public void setMin(java.util.Date min) {
        getStateHelper().put(PropertyKeys.min, min);
    }

    public java.util.Date getMax() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.max, null);
    }

    public void setMax(java.util.Date max) {
        getStateHelper().put(PropertyKeys.max, max);
    }

    public Long getZoomMin() {
        return (Long) getStateHelper().eval(PropertyKeys.zoomMin, 10L);
    }

    public void setZoomMin(Long zoomMin) {
        getStateHelper().put(PropertyKeys.zoomMin, zoomMin);
    }

    public Long getZoomMax() {
        return (Long) getStateHelper().eval(PropertyKeys.zoomMax, 315360000000000L);
    }

    public void setZoomMax(Long zoomMax) {
        getStateHelper().put(PropertyKeys.zoomMax, zoomMax);
    }

    public Float getPreloadFactor() {
        return (Float) getStateHelper().eval(PropertyKeys.preloadFactor, 0.0f);
    }

    public void setPreloadFactor(Float preloadFactor) {
        getStateHelper().put(PropertyKeys.preloadFactor, preloadFactor);
    }

    public int getEventMargin() {
        return (Integer) getStateHelper().eval(PropertyKeys.eventMargin, 10);
    }

    public void setEventMargin(int eventMargin) {
        getStateHelper().put(PropertyKeys.eventMargin, eventMargin);
    }

    public int getEventMarginAxis() {
        return (Integer) getStateHelper().eval(PropertyKeys.eventMarginAxis, 10);
    }

    public void setEventMarginAxis(int eventMarginAxis) {
        getStateHelper().put(PropertyKeys.eventMarginAxis, eventMarginAxis);
    }

    public String getEventStyle() {
        return (String) getStateHelper().eval(PropertyKeys.eventStyle, "box");
    }

    public void setEventStyle(String eventStyle) {
        getStateHelper().put(PropertyKeys.eventStyle, eventStyle);
    }

    public boolean isGroupsChangeable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.groupsChangeable, true);
    }

    public void setGroupsChangeable(boolean groupsChangeable) {
        getStateHelper().put(PropertyKeys.groupsChangeable, groupsChangeable);
    }

    public boolean isGroupsOnRight() {
        return (Boolean) getStateHelper().eval(PropertyKeys.groupsOnRight, false);
    }

    public void setGroupsOnRight(boolean groupsOnRight) {
        getStateHelper().put(PropertyKeys.groupsOnRight, groupsOnRight);
    }

    public boolean isGroupsOrder() {
        return (Boolean) getStateHelper().eval(PropertyKeys.groupsOrder, true);
    }

    public void setGroupsOrder(boolean groupsOrder) {
        getStateHelper().put(PropertyKeys.groupsOrder, groupsOrder);
    }

    public String getGroupsWidth() {
        return (String) getStateHelper().eval(PropertyKeys.groupsWidth, null);
    }

    public void setGroupsWidth(String groupsWidth) {
        getStateHelper().put(PropertyKeys.groupsWidth, groupsWidth);
    }

    public int getGroupMinHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.groupMinHeight, 0);
    }

    public void setGroupMinHeight(int groupMinHeight) {
        getStateHelper().put(PropertyKeys.groupMinHeight, groupMinHeight);
    }

    public boolean isSnapEvents() {
        return (Boolean) getStateHelper().eval(PropertyKeys.snapEvents, true);
    }

    public void setSnapEvents(boolean snapEvents) {
        getStateHelper().put(PropertyKeys.snapEvents, snapEvents);
    }

    public boolean isStackEvents() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stackEvents, true);
    }

    public void setStackEvents(boolean stackEvents) {
        getStateHelper().put(PropertyKeys.stackEvents, stackEvents);
    }

    public boolean isShowCurrentTime() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showCurrentTime, true);
    }

    public void setShowCurrentTime(boolean showCurrentTime) {
        getStateHelper().put(PropertyKeys.showCurrentTime, showCurrentTime);
    }

    public boolean isShowMajorLabels() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showMajorLabels, true);
    }

    public void setShowMajorLabels(boolean showMajorLabels) {
        getStateHelper().put(PropertyKeys.showMajorLabels, showMajorLabels);
    }

    public boolean isShowMinorLabels() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showMinorLabels, true);
    }

    public void setShowMinorLabels(boolean showMinorLabels) {
        getStateHelper().put(PropertyKeys.showMinorLabels, showMinorLabels);
    }

    public boolean isShowButtonNew() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showButtonNew, false);
    }

    public void setShowButtonNew(boolean showButtonNew) {
        getStateHelper().put(PropertyKeys.showButtonNew, showButtonNew);
    }

    public boolean isShowNavigation() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showNavigation, false);
    }

    public void setShowNavigation(boolean showNavigation) {
        getStateHelper().put(PropertyKeys.showNavigation, showNavigation);
    }

    public boolean isTimeChangeable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeChangeable, true);
    }

    public void setTimeChangeable(boolean timeChangeable) {
        getStateHelper().put(PropertyKeys.timeChangeable, timeChangeable);
    }

    public String getDropHoverStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.dropHoverStyleClass, null);
    }

    public void setDropHoverStyleClass(String dropHoverStyleClass) {
        getStateHelper().put(PropertyKeys.dropHoverStyleClass, dropHoverStyleClass);
    }

    public String getDropActiveStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.dropActiveStyleClass, null);
    }

    public void setDropActiveStyleClass(String dropActiveStyleClass) {
        getStateHelper().put(PropertyKeys.dropActiveStyleClass, dropActiveStyleClass);
    }

    public String getDropAccept() {
        return (String) getStateHelper().eval(PropertyKeys.dropAccept, null);
    }

    public void setDropAccept(String dropAccept) {
        getStateHelper().put(PropertyKeys.dropAccept, dropAccept);
    }

    public String getDropScope() {
        return (String) getStateHelper().eval(PropertyKeys.dropScope, null);
    }

    public void setDropScope(String dropScope) {
        getStateHelper().put(PropertyKeys.dropScope, dropScope);
    }

    public boolean isAnimate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animate, true);
    }

    public void setAnimate(boolean animate) {
        getStateHelper().put(PropertyKeys.animate, animate);
    }

    public boolean isAnimateZoom() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animateZoom, true);
    }

    public void setAnimateZoom(boolean animateZoom) {
        getStateHelper().put(PropertyKeys.animateZoom, animateZoom);
    }
}