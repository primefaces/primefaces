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

import javax.faces.component.UIPanel;

public abstract class SplitterPanelBase extends UIPanel {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {
        size,
        minSize,
        style,
        styleClass
    }

    public SplitterPanelBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public Integer getSize() {
        return (Integer) getStateHelper().eval(SplitterPanelBase.PropertyKeys.size, null);
    }

    public void setSize(Integer size) {
        getStateHelper().put(SplitterPanelBase.PropertyKeys.size, size);
    }

    public Integer getMinSize() {
        return (Integer) getStateHelper().eval(SplitterPanelBase.PropertyKeys.minSize, null);
    }

    public void setMinSize(Integer minSize) {
        getStateHelper().put(SplitterPanelBase.PropertyKeys.minSize, minSize);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(SplitterPanelBase.PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(SplitterPanelBase.PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(SplitterPanelBase.PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(SplitterPanelBase.PropertyKeys.styleClass, styleClass);
    }
}
