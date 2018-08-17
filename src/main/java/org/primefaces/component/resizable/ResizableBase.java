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
package org.primefaces.component.resizable;

import javax.faces.component.UIComponentBase;

import org.primefaces.util.ComponentUtils;


abstract class ResizableBase extends UIComponentBase implements org.primefaces.component.api.Widget, javax.faces.component.behavior.ClientBehaviorHolder, org.primefaces.component.api.PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ResizableRenderer";

    public enum PropertyKeys {

        widgetVar,
        forValue("for"),
        aspectRatio,
        proxy,
        handles,
        ghost,
        animate,
        effect,
        effectDuration,
        maxWidth,
        maxHeight,
        minWidth,
        minHeight,
        containment,
        grid,
        onStart,
        onResize,
        onStop;

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

    public ResizableBase() {
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

    public java.lang.String getFor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(java.lang.String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public boolean isAspectRatio() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.aspectRatio, false);
    }

    public void setAspectRatio(boolean _aspectRatio) {
        getStateHelper().put(PropertyKeys.aspectRatio, _aspectRatio);
    }

    public boolean isProxy() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.proxy, false);
    }

    public void setProxy(boolean _proxy) {
        getStateHelper().put(PropertyKeys.proxy, _proxy);
    }

    public java.lang.String getHandles() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.handles, null);
    }

    public void setHandles(java.lang.String _handles) {
        getStateHelper().put(PropertyKeys.handles, _handles);
    }

    public boolean isGhost() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ghost, false);
    }

    public void setGhost(boolean _ghost) {
        getStateHelper().put(PropertyKeys.ghost, _ghost);
    }

    public boolean isAnimate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, false);
    }

    public void setAnimate(boolean _animate) {
        getStateHelper().put(PropertyKeys.animate, _animate);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "swing");
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public java.lang.String getEffectDuration() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effectDuration, "normal");
    }

    public void setEffectDuration(java.lang.String _effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
    }

    public int getMaxWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxWidth, Integer.MAX_VALUE);
    }

    public void setMaxWidth(int _maxWidth) {
        getStateHelper().put(PropertyKeys.maxWidth, _maxWidth);
    }

    public int getMaxHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxHeight, Integer.MAX_VALUE);
    }

    public void setMaxHeight(int _maxHeight) {
        getStateHelper().put(PropertyKeys.maxHeight, _maxHeight);
    }

    public int getMinWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minWidth, Integer.MIN_VALUE);
    }

    public void setMinWidth(int _minWidth) {
        getStateHelper().put(PropertyKeys.minWidth, _minWidth);
    }

    public int getMinHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minHeight, Integer.MIN_VALUE);
    }

    public void setMinHeight(int _minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, _minHeight);
    }

    public boolean isContainment() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.containment, false);
    }

    public void setContainment(boolean _containment) {
        getStateHelper().put(PropertyKeys.containment, _containment);
    }

    public int getGrid() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.grid, 1);
    }

    public void setGrid(int _grid) {
        getStateHelper().put(PropertyKeys.grid, _grid);
    }

    public java.lang.String getOnStart() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onStart, null);
    }

    public void setOnStart(java.lang.String _onStart) {
        getStateHelper().put(PropertyKeys.onStart, _onStart);
    }

    public java.lang.String getOnResize() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onResize, null);
    }

    public void setOnResize(java.lang.String _onResize) {
        getStateHelper().put(PropertyKeys.onResize, _onResize);
    }

    public java.lang.String getOnStop() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onStop, null);
    }

    public void setOnStop(java.lang.String _onStop) {
        getStateHelper().put(PropertyKeys.onStop, _onStop);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}