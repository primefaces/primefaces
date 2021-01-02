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
package org.primefaces.component.scrolltop;

import org.primefaces.component.api.Widget;

import javax.faces.component.UIComponentBase;

public abstract class ScrollTopBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ScrollTopRenderer";

    public enum PropertyKeys {
        widgetVar,
        target,
        threshold,
        icon,
        behavior,
        style,
        styleClass
    }

    public ScrollTopBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getTarget() {
        return (String) getStateHelper().eval(ScrollTopBase.PropertyKeys.target, "window");
    }

    public void setTarget(String target) {
        getStateHelper().put(ScrollTopBase.PropertyKeys.target, target);
    }

    public String getThreshold() {
        return (String) getStateHelper().eval(ScrollTopBase.PropertyKeys.threshold, "400");
    }

    public void setThreshold(String threshold) {
        getStateHelper().put(ScrollTopBase.PropertyKeys.threshold, threshold);
    }

    public String getIcon() {
        return (String) getStateHelper().eval(ScrollTopBase.PropertyKeys.icon, "pi pi-chevron-up");
    }

    public void setIcon(String icon) {
        getStateHelper().put(ScrollTopBase.PropertyKeys.icon, icon);
    }

    public String getBehavior() {
        return (String) getStateHelper().eval(ScrollTopBase.PropertyKeys.behavior, "smooth");
    }

    public void setBehavior(String behavior) {
        getStateHelper().put(ScrollTopBase.PropertyKeys.behavior, behavior);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(ScrollTopBase.PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(ScrollTopBase.PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(ScrollTopBase.PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(ScrollTopBase.PropertyKeys.styleClass, styleClass);
    }
}
