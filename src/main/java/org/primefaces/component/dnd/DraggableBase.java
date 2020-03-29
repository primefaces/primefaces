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
package org.primefaces.component.dnd;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;

public abstract class DraggableBase extends UIComponentBase implements Widget {

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
        onDrag,
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

    public String getOnDrag() {
        return (String) getStateHelper().eval(PropertyKeys.onDrag, null);
    }

    public void setOnDrag(String onDrag) {
        getStateHelper().put(PropertyKeys.onDrag, onDrag);
    }

    public String getCancel() {
        return (String) getStateHelper().eval(PropertyKeys.cancel, null);
    }

    public void setCancel(String cancel) {
        getStateHelper().put(PropertyKeys.cancel, cancel);
    }
}