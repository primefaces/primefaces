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
package org.primefaces.component.splitter;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

public abstract class SplitterBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SplitterRenderer";

    public enum PropertyKeys {
        widgetVar,
        layout,
        gutterSize,
        stateKey,
        stateStorage,
        onResizeEnd,
        style,
        styleClass
    }

    public SplitterBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getLayout() {
        return (String) getStateHelper().eval(PropertyKeys.layout, "horizontal");
    }

    public void setLayout(String layout) {
        getStateHelper().put(PropertyKeys.layout, layout);
    }

    public Integer getGutterSize() {
        return (Integer) getStateHelper().eval(PropertyKeys.gutterSize, 4);
    }

    public void setGutterSize(Integer gutterSize) {
        getStateHelper().put(PropertyKeys.gutterSize, gutterSize);
    }

    public String getStateKey() {
        return (String) getStateHelper().eval(PropertyKeys.stateKey, null);
    }

    public void setStateKey(String stateKey) {
        getStateHelper().put(PropertyKeys.stateKey, stateKey);
    }

    public String getStateStorage() {
        return (String) getStateHelper().eval(PropertyKeys.stateStorage, "session");
    }

    public void setStateStorage(String stateStorage) {
        getStateHelper().put(PropertyKeys.stateStorage, stateStorage);
    }

    public String getOnResizeEnd() {
        return (String) getStateHelper().eval(PropertyKeys.onResizeEnd, null);
    }

    public void setOnResizeEnd(String onResizeEnd) {
        getStateHelper().put(PropertyKeys.onResizeEnd, onResizeEnd);
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
}
