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
package org.primefaces.component.resizable;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class ResizableBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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

    public ResizableBase() {
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

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public boolean isAspectRatio() {
        return (Boolean) getStateHelper().eval(PropertyKeys.aspectRatio, false);
    }

    public void setAspectRatio(boolean aspectRatio) {
        getStateHelper().put(PropertyKeys.aspectRatio, aspectRatio);
    }

    public boolean isProxy() {
        return (Boolean) getStateHelper().eval(PropertyKeys.proxy, false);
    }

    public void setProxy(boolean proxy) {
        getStateHelper().put(PropertyKeys.proxy, proxy);
    }

    public String getHandles() {
        return (String) getStateHelper().eval(PropertyKeys.handles, null);
    }

    public void setHandles(String handles) {
        getStateHelper().put(PropertyKeys.handles, handles);
    }

    public boolean isGhost() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ghost, false);
    }

    public void setGhost(boolean ghost) {
        getStateHelper().put(PropertyKeys.ghost, ghost);
    }

    public boolean isAnimate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animate, false);
    }

    public void setAnimate(boolean animate) {
        getStateHelper().put(PropertyKeys.animate, animate);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, "swing");
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

    public int getMaxWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxWidth, Integer.MAX_VALUE);
    }

    public void setMaxWidth(int maxWidth) {
        getStateHelper().put(PropertyKeys.maxWidth, maxWidth);
    }

    public int getMaxHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxHeight, Integer.MAX_VALUE);
    }

    public void setMaxHeight(int maxHeight) {
        getStateHelper().put(PropertyKeys.maxHeight, maxHeight);
    }

    public int getMinWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.minWidth, Integer.MIN_VALUE);
    }

    public void setMinWidth(int minWidth) {
        getStateHelper().put(PropertyKeys.minWidth, minWidth);
    }

    public int getMinHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.minHeight, Integer.MIN_VALUE);
    }

    public void setMinHeight(int minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, minHeight);
    }

    public boolean isContainment() {
        return (Boolean) getStateHelper().eval(PropertyKeys.containment, false);
    }

    public void setContainment(boolean containment) {
        getStateHelper().put(PropertyKeys.containment, containment);
    }

    public int getGrid() {
        return (Integer) getStateHelper().eval(PropertyKeys.grid, 1);
    }

    public void setGrid(int grid) {
        getStateHelper().put(PropertyKeys.grid, grid);
    }

    public String getOnStart() {
        return (String) getStateHelper().eval(PropertyKeys.onStart, null);
    }

    public void setOnStart(String onStart) {
        getStateHelper().put(PropertyKeys.onStart, onStart);
    }

    public String getOnResize() {
        return (String) getStateHelper().eval(PropertyKeys.onResize, null);
    }

    public void setOnResize(String onResize) {
        getStateHelper().put(PropertyKeys.onResize, onResize);
    }

    public String getOnStop() {
        return (String) getStateHelper().eval(PropertyKeys.onStop, null);
    }

    public void setOnStop(String onStop) {
        getStateHelper().put(PropertyKeys.onStop, onStop);
    }
}