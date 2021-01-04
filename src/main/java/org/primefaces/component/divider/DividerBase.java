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
package org.primefaces.component.divider;

import javax.faces.component.UIComponentBase;

public abstract class DividerBase extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DividerRenderer";

    public enum PropertyKeys {
        align,
        layout,
        type,
        style,
        styleClass
    }

    public DividerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getAlign() {
        return (String) getStateHelper().eval(DividerBase.PropertyKeys.align, null);
    }

    public void setAlign(String align) {
        getStateHelper().put(DividerBase.PropertyKeys.align, align);
    }

    public String getLayout() {
        return (String) getStateHelper().eval(DividerBase.PropertyKeys.layout, "horizontal");
    }

    public void setLayout(String layout) {
        getStateHelper().put(DividerBase.PropertyKeys.layout, layout);
    }

    public String getType() {
        return (String) getStateHelper().eval(DividerBase.PropertyKeys.type, "solid");
    }

    public void setType(String type) {
        getStateHelper().put(DividerBase.PropertyKeys.type, type);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(DividerBase.PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(DividerBase.PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(DividerBase.PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(DividerBase.PropertyKeys.styleClass, styleClass);
    }
}
