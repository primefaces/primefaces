/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.ring;

import org.primefaces.component.api.UIData;
import org.primefaces.component.api.Widget;

public abstract class RingBase extends UIData implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RingRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        easing,
        autoplay,
        autoplayDuration,
        autoplayPauseOnHover,
        autoplayInitialDelay
    }

    public RingBase() {
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

    public String getEasing() {
        return (String) getStateHelper().eval(PropertyKeys.easing, null);
    }

    public void setEasing(String easing) {
        getStateHelper().put(PropertyKeys.easing, easing);
    }

    public boolean isAutoplay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoplay, false);
    }

    public void setAutoplay(boolean autoplay) {
        getStateHelper().put(PropertyKeys.autoplay, autoplay);
    }

    public int getAutoplayDuration() {
        return (Integer) getStateHelper().eval(PropertyKeys.autoplayDuration, 1000);
    }

    public void setAutoplayDuration(int autoplayDuration) {
        getStateHelper().put(PropertyKeys.autoplayDuration, autoplayDuration);
    }

    public boolean isAutoplayPauseOnHover() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoplayPauseOnHover, false);
    }

    public void setAutoplayPauseOnHover(boolean autoplayPauseOnHover) {
        getStateHelper().put(PropertyKeys.autoplayPauseOnHover, autoplayPauseOnHover);
    }

    public int getAutoplayInitialDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.autoplayInitialDelay, 0);
    }

    public void setAutoplayInitialDelay(int autoplayInitialDelay) {
        getStateHelper().put(PropertyKeys.autoplayInitialDelay, autoplayInitialDelay);
    }
}