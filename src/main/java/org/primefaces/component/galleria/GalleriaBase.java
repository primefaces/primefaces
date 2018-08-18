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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    @Override
    public java.lang.Object getValue() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.value, null);
    }

    @Override
    public void setValue(java.lang.Object _value) {
        getStateHelper().put(PropertyKeys.value, _value);
    }

    public java.lang.String getVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(java.lang.String _var) {
        getStateHelper().put(PropertyKeys.var, _var);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fade");
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public int getEffectSpeed() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectSpeed, 500);
    }

    public void setEffectSpeed(int _effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
    }

    public int getFrameWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frameWidth, 60);
    }

    public void setFrameWidth(int _frameWidth) {
        getStateHelper().put(PropertyKeys.frameWidth, _frameWidth);
    }

    public int getFrameHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frameHeight, 40);
    }

    public void setFrameHeight(int _frameHeight) {
        getStateHelper().put(PropertyKeys.frameHeight, _frameHeight);
    }

    public boolean isShowFilmstrip() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showFilmstrip, true);
    }

    public void setShowFilmstrip(boolean _showFilmstrip) {
        getStateHelper().put(PropertyKeys.showFilmstrip, _showFilmstrip);
    }

    public boolean isAutoPlay() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoPlay, true);
    }

    public void setAutoPlay(boolean _autoPlay) {
        getStateHelper().put(PropertyKeys.autoPlay, _autoPlay);
    }

    public int getTransitionInterval() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.transitionInterval, 4000);
    }

    public void setTransitionInterval(int _transitionInterval) {
        getStateHelper().put(PropertyKeys.transitionInterval, _transitionInterval);
    }

    public int getPanelWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.panelWidth, java.lang.Integer.MIN_VALUE);
    }

    public void setPanelWidth(int _panelWidth) {
        getStateHelper().put(PropertyKeys.panelWidth, _panelWidth);
    }

    public int getPanelHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.panelHeight, java.lang.Integer.MIN_VALUE);
    }

    public void setPanelHeight(int _panelHeight) {
        getStateHelper().put(PropertyKeys.panelHeight, _panelHeight);
    }

    public boolean isShowCaption() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showCaption, false);
    }

    public void setShowCaption(boolean _showCaption) {
        getStateHelper().put(PropertyKeys.showCaption, _showCaption);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}