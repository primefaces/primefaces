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
package org.primefaces.component.imageswitch;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ImageSwitchBase extends UIComponentBase implements Widget {

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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}