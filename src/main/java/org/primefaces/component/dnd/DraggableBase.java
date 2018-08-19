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
package org.primefaces.component.dnd;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class DraggableBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DraggableRenderer";

    public enum PropertyKeys {

        widgetVar,
        proxy,
        dragOnly,
        forValue("for"),
        disabled,
        axis,
        containment,
        helper,
        revert,
        snap,
        snapMode,
        snapTolerance,
        zindex,
        handle,
        opacity,
        stack,
        grid,
        scope,
        cursor,
        dashboard,
        appendTo,
        onStart,
        onStop,
        cancel;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
    }

    public DraggableBase() {
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

    public boolean isProxy() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.proxy, false);
    }

    public void setProxy(boolean _proxy) {
        getStateHelper().put(PropertyKeys.proxy, _proxy);
    }

    public boolean isDragOnly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dragOnly, false);
    }

    public void setDragOnly(boolean _dragOnly) {
        getStateHelper().put(PropertyKeys.dragOnly, _dragOnly);
    }

    public java.lang.String getFor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(java.lang.String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public java.lang.String getAxis() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.axis, null);
    }

    public void setAxis(java.lang.String _axis) {
        getStateHelper().put(PropertyKeys.axis, _axis);
    }

    public java.lang.String getContainment() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.containment, null);
    }

    public void setContainment(java.lang.String _containment) {
        getStateHelper().put(PropertyKeys.containment, _containment);
    }

    public java.lang.String getHelper() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.helper, null);
    }

    public void setHelper(java.lang.String _helper) {
        getStateHelper().put(PropertyKeys.helper, _helper);
    }

    public boolean isRevert() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.revert, false);
    }

    public void setRevert(boolean _revert) {
        getStateHelper().put(PropertyKeys.revert, _revert);
    }

    public boolean isSnap() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.snap, false);
    }

    public void setSnap(boolean _snap) {
        getStateHelper().put(PropertyKeys.snap, _snap);
    }

    public java.lang.String getSnapMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.snapMode, null);
    }

    public void setSnapMode(java.lang.String _snapMode) {
        getStateHelper().put(PropertyKeys.snapMode, _snapMode);
    }

    public int getSnapTolerance() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.snapTolerance, 20);
    }

    public void setSnapTolerance(int _snapTolerance) {
        getStateHelper().put(PropertyKeys.snapTolerance, _snapTolerance);
    }

    public int getZindex() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.zindex, -1);
    }

    public void setZindex(int _zindex) {
        getStateHelper().put(PropertyKeys.zindex, _zindex);
    }

    public java.lang.String getHandle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.handle, null);
    }

    public void setHandle(java.lang.String _handle) {
        getStateHelper().put(PropertyKeys.handle, _handle);
    }

    public double getOpacity() {
        return (java.lang.Double) getStateHelper().eval(PropertyKeys.opacity, 1.0);
    }

    public void setOpacity(double _opacity) {
        getStateHelper().put(PropertyKeys.opacity, _opacity);
    }

    public java.lang.String getStack() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.stack, null);
    }

    public void setStack(java.lang.String _stack) {
        getStateHelper().put(PropertyKeys.stack, _stack);
    }

    public java.lang.String getGrid() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.grid, null);
    }

    public void setGrid(java.lang.String _grid) {
        getStateHelper().put(PropertyKeys.grid, _grid);
    }

    public java.lang.String getScope() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.scope, null);
    }

    public void setScope(java.lang.String _scope) {
        getStateHelper().put(PropertyKeys.scope, _scope);
    }

    public java.lang.String getCursor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cursor, "crosshair");
    }

    public void setCursor(java.lang.String _cursor) {
        getStateHelper().put(PropertyKeys.cursor, _cursor);
    }

    public java.lang.String getDashboard() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dashboard, null);
    }

    public void setDashboard(java.lang.String _dashboard) {
        getStateHelper().put(PropertyKeys.dashboard, _dashboard);
    }

    public java.lang.String getAppendTo() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(java.lang.String _appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, _appendTo);
    }

    public java.lang.String getOnStart() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onStart, null);
    }

    public void setOnStart(java.lang.String _onStart) {
        getStateHelper().put(PropertyKeys.onStart, _onStart);
    }

    public java.lang.String getOnStop() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onStop, null);
    }

    public void setOnStop(java.lang.String _onStop) {
        getStateHelper().put(PropertyKeys.onStop, _onStop);
    }

    public java.lang.String getCancel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cancel, null);
    }

    public void setCancel(java.lang.String _cancel) {
        getStateHelper().put(PropertyKeys.cancel, _cancel);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}