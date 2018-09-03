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

    public Object getValue() {
        return getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(Object value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getPlayer() {
        return (String) getStateHelper().eval(PropertyKeys.player, null);
    }

    public void setPlayer(String player) {
        getStateHelper().put(PropertyKeys.player, player);
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
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

    public boolean isCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean cache) {
        getStateHelper().put(PropertyKeys.cache, cache);
    }

    public String getZoom() {
        return (String) getStateHelper().eval(PropertyKeys.zoom, null);
    }

    public void setZoom(String zoom) {
        getStateHelper().put(PropertyKeys.zoom, zoom);
    }

    public String getView() {
        return (String) getStateHelper().eval(PropertyKeys.view, null);
    }

    public void setView(String view) {
        getStateHelper().put(PropertyKeys.view, view);
    }

}