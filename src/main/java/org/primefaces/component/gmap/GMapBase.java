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
package org.primefaces.component.gmap;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class GMapBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GMapRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        style,
        styleClass,
        type,
        center,
        zoom,
        streetView,
        disableDefaultUI,
        navigationControl,
        mapTypeControl,
        draggable,
        disableDoubleClickZoom,
        onPointClick,
        fitBounds,
        scrollWheel
    }

    public GMapBase() {
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

    public org.primefaces.model.map.MapModel getModel() {
        return (org.primefaces.model.map.MapModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.map.MapModel model) {
        getStateHelper().put(PropertyKeys.model, model);
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

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, null);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public String getCenter() {
        return (String) getStateHelper().eval(PropertyKeys.center, null);
    }

    public void setCenter(String center) {
        getStateHelper().put(PropertyKeys.center, center);
    }

    public int getZoom() {
        return (Integer) getStateHelper().eval(PropertyKeys.zoom, 8);
    }

    public void setZoom(int zoom) {
        getStateHelper().put(PropertyKeys.zoom, zoom);
    }

    public boolean isStreetView() {
        return (Boolean) getStateHelper().eval(PropertyKeys.streetView, false);
    }

    public void setStreetView(boolean streetView) {
        getStateHelper().put(PropertyKeys.streetView, streetView);
    }

    public boolean isDisableDefaultUI() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disableDefaultUI, false);
    }

    public void setDisableDefaultUI(boolean disableDefaultUI) {
        getStateHelper().put(PropertyKeys.disableDefaultUI, disableDefaultUI);
    }

    public boolean isNavigationControl() {
        return (Boolean) getStateHelper().eval(PropertyKeys.navigationControl, true);
    }

    public void setNavigationControl(boolean navigationControl) {
        getStateHelper().put(PropertyKeys.navigationControl, navigationControl);
    }

    public boolean isMapTypeControl() {
        return (Boolean) getStateHelper().eval(PropertyKeys.mapTypeControl, true);
    }

    public void setMapTypeControl(boolean mapTypeControl) {
        getStateHelper().put(PropertyKeys.mapTypeControl, mapTypeControl);
    }

    public boolean isDraggable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
    }

    public void setDraggable(boolean draggable) {
        getStateHelper().put(PropertyKeys.draggable, draggable);
    }

    public boolean isDisableDoubleClickZoom() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disableDoubleClickZoom, false);
    }

    public void setDisableDoubleClickZoom(boolean disableDoubleClickZoom) {
        getStateHelper().put(PropertyKeys.disableDoubleClickZoom, disableDoubleClickZoom);
    }

    public String getOnPointClick() {
        return (String) getStateHelper().eval(PropertyKeys.onPointClick, null);
    }

    public void setOnPointClick(String onPointClick) {
        getStateHelper().put(PropertyKeys.onPointClick, onPointClick);
    }

    public boolean isFitBounds() {
        return (Boolean) getStateHelper().eval(PropertyKeys.fitBounds, false);
    }

    public void setFitBounds(boolean fitBounds) {
        getStateHelper().put(PropertyKeys.fitBounds, fitBounds);
    }

    public boolean isScrollWheel() {
        return (Boolean) getStateHelper().eval(PropertyKeys.scrollWheel, true);
    }

    public void setScrollWheel(boolean scrollWheel) {
        getStateHelper().put(PropertyKeys.scrollWheel, scrollWheel);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}