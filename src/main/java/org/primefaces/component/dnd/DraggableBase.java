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

        private String toString;

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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isProxy() {
        return (Boolean) getStateHelper().eval(PropertyKeys.proxy, false);
    }

    public void setProxy(boolean proxy) {
        getStateHelper().put(PropertyKeys.proxy, proxy);
    }

    public boolean isDragOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dragOnly, false);
    }

    public void setDragOnly(boolean dragOnly) {
        getStateHelper().put(PropertyKeys.dragOnly, dragOnly);
    }

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getAxis() {
        return (String) getStateHelper().eval(PropertyKeys.axis, null);
    }

    public void setAxis(String axis) {
        getStateHelper().put(PropertyKeys.axis, axis);
    }

    public String getContainment() {
        return (String) getStateHelper().eval(PropertyKeys.containment, null);
    }

    public void setContainment(String containment) {
        getStateHelper().put(PropertyKeys.containment, containment);
    }

    public String getHelper() {
        return (String) getStateHelper().eval(PropertyKeys.helper, null);
    }

    public void setHelper(String helper) {
        getStateHelper().put(PropertyKeys.helper, helper);
    }

    public boolean isRevert() {
        return (Boolean) getStateHelper().eval(PropertyKeys.revert, false);
    }

    public void setRevert(boolean revert) {
        getStateHelper().put(PropertyKeys.revert, revert);
    }

    public boolean isSnap() {
        return (Boolean) getStateHelper().eval(PropertyKeys.snap, false);
    }

    public void setSnap(boolean snap) {
        getStateHelper().put(PropertyKeys.snap, snap);
    }

    public String getSnapMode() {
        return (String) getStateHelper().eval(PropertyKeys.snapMode, null);
    }

    public void setSnapMode(String snapMode) {
        getStateHelper().put(PropertyKeys.snapMode, snapMode);
    }

    public int getSnapTolerance() {
        return (Integer) getStateHelper().eval(PropertyKeys.snapTolerance, 20);
    }

    public void setSnapTolerance(int snapTolerance) {
        getStateHelper().put(PropertyKeys.snapTolerance, snapTolerance);
    }

    public int getZindex() {
        return (Integer) getStateHelper().eval(PropertyKeys.zindex, -1);
    }

    public void setZindex(int zindex) {
        getStateHelper().put(PropertyKeys.zindex, zindex);
    }

    public String getHandle() {
        return (String) getStateHelper().eval(PropertyKeys.handle, null);
    }

    public void setHandle(String handle) {
        getStateHelper().put(PropertyKeys.handle, handle);
    }

    public double getOpacity() {
        return (Double) getStateHelper().eval(PropertyKeys.opacity, 1.0);
    }

    public void setOpacity(double opacity) {
        getStateHelper().put(PropertyKeys.opacity, opacity);
    }

    public String getStack() {
        return (String) getStateHelper().eval(PropertyKeys.stack, null);
    }

    public void setStack(String stack) {
        getStateHelper().put(PropertyKeys.stack, stack);
    }

    public String getGrid() {
        return (String) getStateHelper().eval(PropertyKeys.grid, null);
    }

    public void setGrid(String grid) {
        getStateHelper().put(PropertyKeys.grid, grid);
    }

    public String getScope() {
        return (String) getStateHelper().eval(PropertyKeys.scope, null);
    }

    public void setScope(String scope) {
        getStateHelper().put(PropertyKeys.scope, scope);
    }

    public String getCursor() {
        return (String) getStateHelper().eval(PropertyKeys.cursor, "crosshair");
    }

    public void setCursor(String cursor) {
        getStateHelper().put(PropertyKeys.cursor, cursor);
    }

    public String getDashboard() {
        return (String) getStateHelper().eval(PropertyKeys.dashboard, null);
    }

    public void setDashboard(String dashboard) {
        getStateHelper().put(PropertyKeys.dashboard, dashboard);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public String getOnStart() {
        return (String) getStateHelper().eval(PropertyKeys.onStart, null);
    }

    public void setOnStart(String onStart) {
        getStateHelper().put(PropertyKeys.onStart, onStart);
    }

    public String getOnStop() {
        return (String) getStateHelper().eval(PropertyKeys.onStop, null);
    }

    public void setOnStop(String onStop) {
        getStateHelper().put(PropertyKeys.onStop, onStop);
    }

    public String getCancel() {
        return (String) getStateHelper().eval(PropertyKeys.cancel, null);
    }

    public void setCancel(String cancel) {
        getStateHelper().put(PropertyKeys.cancel, cancel);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}