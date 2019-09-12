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
package org.primefaces.component.imageswitch;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;

public abstract class ImageSwitchBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ImageSwitchRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        effect,
        speed,
        slideshowSpeed,
        slideshowAuto,
        activeIndex
    }

    public ImageSwitchBase() {
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

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public int getSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.speed, 500);
    }

    public void setSpeed(int speed) {
        getStateHelper().put(PropertyKeys.speed, speed);
    }

    public int getSlideshowSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.slideshowSpeed, 3000);
    }

    public void setSlideshowSpeed(int slideshowSpeed) {
        getStateHelper().put(PropertyKeys.slideshowSpeed, slideshowSpeed);
    }

    public boolean isSlideshowAuto() {
        return (Boolean) getStateHelper().eval(PropertyKeys.slideshowAuto, true);
    }

    public void setSlideshowAuto(boolean slideshowAuto) {
        getStateHelper().put(PropertyKeys.slideshowAuto, slideshowAuto);
    }

    public int getActiveIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.activeIndex, 0);
    }

    public void setActiveIndex(int activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, activeIndex);
    }
}