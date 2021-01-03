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

import org.primefaces.component.api.Widget;
import javax.faces.component.UIComponentBase;

public abstract class SplitterBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SplitterRenderer";

    public enum PropertyKeys {
        widgetVar,
        layout,
        gutterSize,
        stateKey,
        stateStorage,
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
        return (String) getStateHelper().eval(SplitterBase.PropertyKeys.layout, "horizontal");
    }

    public void setLayout(String layout) {
        getStateHelper().put(SplitterBase.PropertyKeys.layout, layout);
    }

    public Integer getGutterSize() {
        return (Integer) getStateHelper().eval(SplitterBase.PropertyKeys.gutterSize, 4);
    }

    public void setGutterSize(Integer gutterSize) {
        getStateHelper().put(SplitterBase.PropertyKeys.gutterSize, gutterSize);
    }

    public String getStateKey() {
        return (String) getStateHelper().eval(SplitterBase.PropertyKeys.stateKey, null);
    }

    public void setStateKey(String stateKey) {
        getStateHelper().put(SplitterBase.PropertyKeys.stateKey, stateKey);
    }

    public String getStateStorage() {
        return (String) getStateHelper().eval(SplitterBase.PropertyKeys.stateStorage, "session");
    }

    public void setStateStorage(String stateStorage) {
        getStateHelper().put(SplitterBase.PropertyKeys.stateStorage, stateStorage);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(SplitterBase.PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(SplitterBase.PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(SplitterBase.PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(SplitterBase.PropertyKeys.styleClass, styleClass);
    }
}
