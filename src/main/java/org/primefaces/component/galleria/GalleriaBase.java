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
package org.primefaces.component.galleria;

import javax.faces.component.UIOutput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class GalleriaBase extends UIOutput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GalleriaRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        var,
        style,
        styleClass,
        effect,
        effectSpeed,
        frameWidth,
        frameHeight,
        showFilmstrip,
        autoPlay,
        transitionInterval,
        panelWidth,
        panelHeight,
        showCaption
    }

    public GalleriaBase() {
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

    @Override
    public Object getValue() {
        return getStateHelper().eval(PropertyKeys.value, null);
    }

    @Override
    public void setValue(Object value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
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
        return (String) getStateHelper().eval(PropertyKeys.effect, "fade");
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public int getEffectSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.effectSpeed, 500);
    }

    public void setEffectSpeed(int effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, effectSpeed);
    }

    public int getFrameWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.frameWidth, 60);
    }

    public void setFrameWidth(int frameWidth) {
        getStateHelper().put(PropertyKeys.frameWidth, frameWidth);
    }

    public int getFrameHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.frameHeight, 40);
    }

    public void setFrameHeight(int frameHeight) {
        getStateHelper().put(PropertyKeys.frameHeight, frameHeight);
    }

    public boolean isShowFilmstrip() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showFilmstrip, true);
    }

    public void setShowFilmstrip(boolean showFilmstrip) {
        getStateHelper().put(PropertyKeys.showFilmstrip, showFilmstrip);
    }

    public boolean isAutoPlay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoPlay, true);
    }

    public void setAutoPlay(boolean autoPlay) {
        getStateHelper().put(PropertyKeys.autoPlay, autoPlay);
    }

    public int getTransitionInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.transitionInterval, 4000);
    }

    public void setTransitionInterval(int transitionInterval) {
        getStateHelper().put(PropertyKeys.transitionInterval, transitionInterval);
    }

    public int getPanelWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.panelWidth, Integer.MIN_VALUE);
    }

    public void setPanelWidth(int panelWidth) {
        getStateHelper().put(PropertyKeys.panelWidth, panelWidth);
    }

    public int getPanelHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.panelHeight, Integer.MIN_VALUE);
    }

    public void setPanelHeight(int panelHeight) {
        getStateHelper().put(PropertyKeys.panelHeight, panelHeight);
    }

    public boolean isShowCaption() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showCaption, false);
    }

    public void setShowCaption(boolean showCaption) {
        getStateHelper().put(PropertyKeys.showCaption, showCaption);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}