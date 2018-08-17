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
package org.primefaces.component.media;

import javax.faces.component.UIComponentBase;


abstract class MediaBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MediaRenderer";

    public enum PropertyKeys {

        value,
        player,
        width,
        height,
        style,
        styleClass,
        cache,
        zoom,
        view
    }

    public MediaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.Object getValue() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(java.lang.Object _value) {
        getStateHelper().put(PropertyKeys.value, _value);
    }

    public java.lang.String getPlayer() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.player, null);
    }

    public void setPlayer(java.lang.String _player) {
        getStateHelper().put(PropertyKeys.player, _player);
    }

    public java.lang.String getWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(java.lang.String _width) {
        getStateHelper().put(PropertyKeys.width, _width);
    }

    public java.lang.String getHeight() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(java.lang.String _height) {
        getStateHelper().put(PropertyKeys.height, _height);
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

    public boolean isCache() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean _cache) {
        getStateHelper().put(PropertyKeys.cache, _cache);
    }

    public java.lang.String getZoom() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.zoom, null);
    }

    public void setZoom(java.lang.String _zoom) {
        getStateHelper().put(PropertyKeys.zoom, _zoom);
    }

    public java.lang.String getView() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.view, null);
    }

    public void setView(java.lang.String _view) {
        getStateHelper().put(PropertyKeys.view, _view);
    }

}