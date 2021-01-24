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
package org.primefaces.component.timeline;

import java.time.LocalDateTime;
import java.util.Locale;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.util.LocaleUtils;


public abstract class TimelineBase extends UIComponentBase implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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
        clientTimeZone,
        height,
        minHeight,
        maxHeight,
        horizontalScroll,
        verticalScroll,
        width,
        responsive,
        orientationAxis,
        orientationItem,
        editable,
        editableAdd,
        editableRemove,
        editableGroup,
        editableTime,
        editableOverrideItems,
        selectable,
        zoomable,
        moveable,
        start,
        end,
        min,
        max,
        zoomKey,
        zoomMin,
        zoomMax,
        preloadFactor,
        eventMargin,
        eventHorizontalMargin,
        eventVerticalMargin,
        eventMarginAxis,
        eventStyle,
        groupsOrder,
        groupStyle,
        snap,
        stackEvents,
        showCurrentTime,
        showMajorLabels,
        showMinorLabels,
        clickToUse,
        showTooltips,
        tooltipFollowMouse,
        tooltipOverflowMethod,
        tooltipDelay,
        dropHoverStyleClass,
        dropActiveStyleClass,
        dropAccept,
        dropScope,
        dir,
        extender
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

    @SuppressWarnings("unchecked")
    public TimelineModel<Object, Object> getValue() {
        return (TimelineModel<Object, Object>) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(TimelineModel<Object, Object> value) {
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

    public Locale calculateLocale(FacesContext facesContext) {
        return LocaleUtils.resolveLocale(facesContext, getLocale(), getClientId(facesContext));
    }

    public Object getTimeZone() {
        return getStateHelper().eval(PropertyKeys.timeZone, null);
    }

    public void setTimeZone(Object timeZone) {
        getStateHelper().put(PropertyKeys.timeZone, timeZone);
    }

    public Object getClientTimeZone() {
        return getStateHelper().eval(PropertyKeys.clientTimeZone, null);
    }

    public void setClientTimeZone(Object clientTimeZone) {
        getStateHelper().put(PropertyKeys.clientTimeZone, clientTimeZone);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public Integer getMinHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.minHeight, null);
    }

    public void setMinHeight(Integer minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, minHeight);
    }

    public Integer getMaxHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxHeight, null);
    }

    public void setMaxHeight(Integer maxHeight) {
        getStateHelper().put(PropertyKeys.maxHeight, maxHeight);
    }

    public boolean isHorizontalScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.horizontalScroll, false);
    }

    public void setHorizontalScroll(boolean horizontalScroll) {
        getStateHelper().put(PropertyKeys.horizontalScroll, horizontalScroll);
    }

    public boolean isVerticalScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.verticalScroll, false);
    }

    public void setVerticalScroll(boolean verticalScroll) {
        getStateHelper().put(PropertyKeys.verticalScroll, verticalScroll);
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

    public String getOrientationAxis() {
        return (String) getStateHelper().eval(PropertyKeys.orientationAxis, "bottom");
    }

    public void setOrientationAxis(String orientationAxis) {
        getStateHelper().put(PropertyKeys.orientationAxis, orientationAxis);
    }

    public String getOrientationItem() {
        return (String) getStateHelper().eval(PropertyKeys.orientationItem, "bottom");
    }

    public void setOrientationItem(String orientationItem) {
        getStateHelper().put(PropertyKeys.orientationItem, orientationItem);
    }

    public boolean isEditable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
    }

    public boolean isEditableAdd() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editableAdd, isEditable());
    }

    public void setEditableAdd(boolean editableAdd) {
        getStateHelper().put(PropertyKeys.editable, editableAdd);
    }

    public boolean isEditableRemove() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editableRemove, isEditable());
    }

    public void setEditableRemove(boolean editableRemove) {
        getStateHelper().put(PropertyKeys.editableRemove, editableRemove);
    }

    public boolean isEditableGroup() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editableGroup, isEditable());
    }

    public void setEditableGroup(boolean editableUpdateGroup) {
        getStateHelper().put(PropertyKeys.editableGroup, editableUpdateGroup);
    }

    public boolean isEditableTime() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editableTime, isEditable());
    }

    public void setEditableTime(boolean editableTime) {
        getStateHelper().put(PropertyKeys.editableTime, editableTime);
    }

    public boolean isEditableOverrideItems() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editableOverrideItems, false);
    }

    public void setEditableOverrideItems(boolean editableOverrideItems) {
        getStateHelper().put(PropertyKeys.editableOverrideItems, editableOverrideItems);
    }

    public boolean isSelectable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectable, true);
    }

    public void setSelectable(boolean selectable) {
        getStateHelper().put(PropertyKeys.selectable, selectable);
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

    public LocalDateTime getStart() {
        return (LocalDateTime) getStateHelper().eval(PropertyKeys.start, null);
    }

    public void setStart(LocalDateTime  start) {
        getStateHelper().put(PropertyKeys.start, start);
    }

    public LocalDateTime getEnd() {
        return (LocalDateTime) getStateHelper().eval(PropertyKeys.end, null);
    }

    public void setEnd(LocalDateTime end) {
        getStateHelper().put(PropertyKeys.end, end);
    }

    public LocalDateTime getMin() {
        return (LocalDateTime) getStateHelper().eval(PropertyKeys.min, null);
    }

    public void setMin(LocalDateTime min) {
        getStateHelper().put(PropertyKeys.min, min);
    }

    public LocalDateTime  getMax() {
        return (LocalDateTime) getStateHelper().eval(PropertyKeys.max, null);
    }

    public void setMax(LocalDateTime max) {
        getStateHelper().put(PropertyKeys.max, max);
    }

    public String getZoomKey() {
        return (String) getStateHelper().eval(PropertyKeys.zoomKey, null);
    }

    public void setZoomKey(String zoomKey) {
        getStateHelper().put(PropertyKeys.zoomKey, zoomKey);
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

    public int getEventHorizontalMargin() {
        return (Integer) getStateHelper().eval(PropertyKeys.eventHorizontalMargin, getEventMargin());
    }

    public void setEventHorizontalMargin(int eventHorizontalMargin) {
        getStateHelper().put(PropertyKeys.eventHorizontalMargin, eventHorizontalMargin);
    }

    public int getEventVerticalMargin() {
        return (Integer) getStateHelper().eval(PropertyKeys.eventVerticalMargin, getEventMargin());
    }

    public void setEventVerticalMargin(int eventVerticalMargin) {
        getStateHelper().put(PropertyKeys.eventVerticalMargin, eventVerticalMargin);
    }

    public int getEventMarginAxis() {
        return (Integer) getStateHelper().eval(PropertyKeys.eventMarginAxis, 10);
    }

    public void setEventMarginAxis(int eventMarginAxis) {
        getStateHelper().put(PropertyKeys.eventMarginAxis, eventMarginAxis);
    }

    public String getEventStyle() {
        return (String) getStateHelper().eval(PropertyKeys.eventStyle, null);
    }

    public void setEventStyle(String eventStyle) {
        getStateHelper().put(PropertyKeys.eventStyle, eventStyle);
    }

    public boolean isGroupsOrder() {
        return (Boolean) getStateHelper().eval(PropertyKeys.groupsOrder, true);
    }

    public void setGroupsOrder(boolean groupsOrder) {
        getStateHelper().put(PropertyKeys.groupsOrder, groupsOrder);
    }

    public String getGroupStyle() {
        return (String) getStateHelper().eval(PropertyKeys.groupStyle, null);
    }

    public void setGroupStyle(String groupStyle) {
        getStateHelper().put(PropertyKeys.groupStyle, groupStyle);
    }

    public String getSnap() {
        return (String) getStateHelper().eval(PropertyKeys.snap, null);
    }

    public String setSnap(String snap) {
        return (String) getStateHelper().put(PropertyKeys.snap, snap);
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

    public boolean isClickToUse() {
        return (Boolean) getStateHelper().eval(PropertyKeys.clickToUse, Boolean.FALSE);
    }

    public void setClickToUse(boolean clickToUse) {
        getStateHelper().put(PropertyKeys.clickToUse, clickToUse);
    }

    public boolean isShowTooltips() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showTooltips, Boolean.TRUE);
    }

    public void setShowTooltips(boolean showTooltips) {
        getStateHelper().put(PropertyKeys.showTooltips, showTooltips);
    }

    public boolean isTooltipFollowMouse() {
        return (Boolean) getStateHelper().eval(PropertyKeys.tooltipFollowMouse, Boolean.FALSE);
    }

    public void setTooltipFollowMouse(boolean tooltipFollowMouse) {
        getStateHelper().put(PropertyKeys.tooltipFollowMouse, tooltipFollowMouse);
    }

    public String getTooltipOverflowMethod() {
        return (String) getStateHelper().eval(PropertyKeys.tooltipOverflowMethod, "flip");
    }

    public void setTooltipOverflowMethod(String tooltipOverflowMethod) {
        getStateHelper().put(PropertyKeys.tooltipOverflowMethod, tooltipOverflowMethod);
    }

    public int getTooltipDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.tooltipDelay, 500);
    }

    public void setTooltipDelay(int tooltipDelay) {
        getStateHelper().put(PropertyKeys.tooltipDelay, tooltipDelay);
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

    @Override
    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public String getExtender() {
        return (String) getStateHelper().eval(PropertyKeys.extender, null);
    }

    public void setExtender(String extender) {
        getStateHelper().put(PropertyKeys.extender, extender);
    }

}
