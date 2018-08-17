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
package org.primefaces.component.timeline;

import javax.faces.component.UIComponentBase;

import org.primefaces.util.ComponentUtils;


abstract class TimelineBase extends UIComponentBase implements org.primefaces.component.api.Widget, javax.faces.component.behavior.ClientBehaviorHolder, org.primefaces.component.api.PrimeClientBehaviorHolder {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(java.lang.String _var) {
        getStateHelper().put(PropertyKeys.var, _var);
    }

    public org.primefaces.model.timeline.TimelineModel getValue() {
        return (org.primefaces.model.timeline.TimelineModel) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(org.primefaces.model.timeline.TimelineModel _value) {
        getStateHelper().put(PropertyKeys.value, _value);
    }

    public java.lang.String getVarGroup() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.varGroup, null);
    }

    public void setVarGroup(java.lang.String _varGroup) {
        getStateHelper().put(PropertyKeys.varGroup, _varGroup);
    }

    public java.lang.Object getLocale() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.locale, null);
    }

    public void setLocale(java.lang.Object _locale) {
        getStateHelper().put(PropertyKeys.locale, _locale);
    }

    public java.lang.Object getTimeZone() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.timeZone, null);
    }

    public void setTimeZone(java.lang.Object _timeZone) {
        getStateHelper().put(PropertyKeys.timeZone, _timeZone);
    }

    public java.lang.Object getBrowserTimeZone() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.browserTimeZone, null);
    }

    public void setBrowserTimeZone(java.lang.Object _browserTimeZone) {
        getStateHelper().put(PropertyKeys.browserTimeZone, _browserTimeZone);
    }

    public java.lang.String getHeight() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.height, "auto");
    }

    public void setHeight(java.lang.String _height) {
        getStateHelper().put(PropertyKeys.height, _height);
    }

    public int getMinHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minHeight, 0);
    }

    public void setMinHeight(int _minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, _minHeight);
    }

    public java.lang.String getWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.width, "100%");
    }

    public void setWidth(java.lang.String _width) {
        getStateHelper().put(PropertyKeys.width, _width);
    }

    public boolean isResponsive() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.responsive, true);
    }

    public void setResponsive(boolean _responsive) {
        getStateHelper().put(PropertyKeys.responsive, _responsive);
    }

    public boolean isAxisOnTop() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.axisOnTop, false);
    }

    public void setAxisOnTop(boolean _axisOnTop) {
        getStateHelper().put(PropertyKeys.axisOnTop, _axisOnTop);
    }

    public int getDragAreaWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.dragAreaWidth, 10);
    }

    public void setDragAreaWidth(int _dragAreaWidth) {
        getStateHelper().put(PropertyKeys.dragAreaWidth, _dragAreaWidth);
    }

    public boolean isEditable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean _editable) {
        getStateHelper().put(PropertyKeys.editable, _editable);
    }

    public boolean isSelectable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.selectable, true);
    }

    public void setSelectable(boolean _selectable) {
        getStateHelper().put(PropertyKeys.selectable, _selectable);
    }

    public boolean isUnselectable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.unselectable, true);
    }

    public void setUnselectable(boolean _unselectable) {
        getStateHelper().put(PropertyKeys.unselectable, _unselectable);
    }

    public boolean isZoomable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.zoomable, true);
    }

    public void setZoomable(boolean _zoomable) {
        getStateHelper().put(PropertyKeys.zoomable, _zoomable);
    }

    public boolean isMoveable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.moveable, true);
    }

    public void setMoveable(boolean _moveable) {
        getStateHelper().put(PropertyKeys.moveable, _moveable);
    }

    public java.util.Date getStart() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.start, null);
    }

    public void setStart(java.util.Date _start) {
        getStateHelper().put(PropertyKeys.start, _start);
    }

    public java.util.Date getEnd() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.end, null);
    }

    public void setEnd(java.util.Date _end) {
        getStateHelper().put(PropertyKeys.end, _end);
    }

    public java.util.Date getMin() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.min, null);
    }

    public void setMin(java.util.Date _min) {
        getStateHelper().put(PropertyKeys.min, _min);
    }

    public java.util.Date getMax() {
        return (java.util.Date) getStateHelper().eval(PropertyKeys.max, null);
    }

    public void setMax(java.util.Date _max) {
        getStateHelper().put(PropertyKeys.max, _max);
    }

    public java.lang.Long getZoomMin() {
        return (java.lang.Long) getStateHelper().eval(PropertyKeys.zoomMin, 10L);
    }

    public void setZoomMin(java.lang.Long _zoomMin) {
        getStateHelper().put(PropertyKeys.zoomMin, _zoomMin);
    }

    public java.lang.Long getZoomMax() {
        return (java.lang.Long) getStateHelper().eval(PropertyKeys.zoomMax, 315360000000000L);
    }

    public void setZoomMax(java.lang.Long _zoomMax) {
        getStateHelper().put(PropertyKeys.zoomMax, _zoomMax);
    }

    public java.lang.Float getPreloadFactor() {
        return (java.lang.Float) getStateHelper().eval(PropertyKeys.preloadFactor, 0.0f);
    }

    public void setPreloadFactor(java.lang.Float _preloadFactor) {
        getStateHelper().put(PropertyKeys.preloadFactor, _preloadFactor);
    }

    public int getEventMargin() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.eventMargin, 10);
    }

    public void setEventMargin(int _eventMargin) {
        getStateHelper().put(PropertyKeys.eventMargin, _eventMargin);
    }

    public int getEventMarginAxis() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.eventMarginAxis, 10);
    }

    public void setEventMarginAxis(int _eventMarginAxis) {
        getStateHelper().put(PropertyKeys.eventMarginAxis, _eventMarginAxis);
    }

    public java.lang.String getEventStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.eventStyle, "box");
    }

    public void setEventStyle(java.lang.String _eventStyle) {
        getStateHelper().put(PropertyKeys.eventStyle, _eventStyle);
    }

    public boolean isGroupsChangeable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.groupsChangeable, true);
    }

    public void setGroupsChangeable(boolean _groupsChangeable) {
        getStateHelper().put(PropertyKeys.groupsChangeable, _groupsChangeable);
    }

    public boolean isGroupsOnRight() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.groupsOnRight, false);
    }

    public void setGroupsOnRight(boolean _groupsOnRight) {
        getStateHelper().put(PropertyKeys.groupsOnRight, _groupsOnRight);
    }

    public boolean isGroupsOrder() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.groupsOrder, true);
    }

    public void setGroupsOrder(boolean _groupsOrder) {
        getStateHelper().put(PropertyKeys.groupsOrder, _groupsOrder);
    }

    public java.lang.String getGroupsWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.groupsWidth, null);
    }

    public void setGroupsWidth(java.lang.String _groupsWidth) {
        getStateHelper().put(PropertyKeys.groupsWidth, _groupsWidth);
    }

    public int getGroupMinHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.groupMinHeight, 0);
    }

    public void setGroupMinHeight(int _groupMinHeight) {
        getStateHelper().put(PropertyKeys.groupMinHeight, _groupMinHeight);
    }

    public boolean isSnapEvents() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.snapEvents, true);
    }

    public void setSnapEvents(boolean _snapEvents) {
        getStateHelper().put(PropertyKeys.snapEvents, _snapEvents);
    }

    public boolean isStackEvents() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stackEvents, true);
    }

    public void setStackEvents(boolean _stackEvents) {
        getStateHelper().put(PropertyKeys.stackEvents, _stackEvents);
    }

    public boolean isShowCurrentTime() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showCurrentTime, true);
    }

    public void setShowCurrentTime(boolean _showCurrentTime) {
        getStateHelper().put(PropertyKeys.showCurrentTime, _showCurrentTime);
    }

    public boolean isShowMajorLabels() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showMajorLabels, true);
    }

    public void setShowMajorLabels(boolean _showMajorLabels) {
        getStateHelper().put(PropertyKeys.showMajorLabels, _showMajorLabels);
    }

    public boolean isShowMinorLabels() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showMinorLabels, true);
    }

    public void setShowMinorLabels(boolean _showMinorLabels) {
        getStateHelper().put(PropertyKeys.showMinorLabels, _showMinorLabels);
    }

    public boolean isShowButtonNew() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showButtonNew, false);
    }

    public void setShowButtonNew(boolean _showButtonNew) {
        getStateHelper().put(PropertyKeys.showButtonNew, _showButtonNew);
    }

    public boolean isShowNavigation() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showNavigation, false);
    }

    public void setShowNavigation(boolean _showNavigation) {
        getStateHelper().put(PropertyKeys.showNavigation, _showNavigation);
    }

    public boolean isTimeChangeable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.timeChangeable, true);
    }

    public void setTimeChangeable(boolean _timeChangeable) {
        getStateHelper().put(PropertyKeys.timeChangeable, _timeChangeable);
    }

    public java.lang.String getDropHoverStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dropHoverStyleClass, null);
    }

    public void setDropHoverStyleClass(java.lang.String _dropHoverStyleClass) {
        getStateHelper().put(PropertyKeys.dropHoverStyleClass, _dropHoverStyleClass);
    }

    public java.lang.String getDropActiveStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dropActiveStyleClass, null);
    }

    public void setDropActiveStyleClass(java.lang.String _dropActiveStyleClass) {
        getStateHelper().put(PropertyKeys.dropActiveStyleClass, _dropActiveStyleClass);
    }

    public java.lang.String getDropAccept() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dropAccept, null);
    }

    public void setDropAccept(java.lang.String _dropAccept) {
        getStateHelper().put(PropertyKeys.dropAccept, _dropAccept);
    }

    public java.lang.String getDropScope() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dropScope, null);
    }

    public void setDropScope(java.lang.String _dropScope) {
        getStateHelper().put(PropertyKeys.dropScope, _dropScope);
    }

    public boolean isAnimate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, true);
    }

    public void setAnimate(boolean _animate) {
        getStateHelper().put(PropertyKeys.animate, _animate);
    }

    public boolean isAnimateZoom() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animateZoom, true);
    }

    public void setAnimateZoom(boolean _animateZoom) {
        getStateHelper().put(PropertyKeys.animateZoom, _animateZoom);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}