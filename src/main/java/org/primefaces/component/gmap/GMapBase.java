/*
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
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.event.map.ReverseGeocodeEvent;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.GeocodeResult;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import javax.faces.event.BehaviorEvent;
import org.primefaces.PrimeFaces;


public abstract class GMapBase extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.GMap";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.GMapRenderer";

	public enum PropertyKeys {

		widgetVar
		,model
		,style
		,styleClass
		,type
		,center
		,zoom
		,streetView
		,disableDefaultUI
		,navigationControl
		,mapTypeControl
		,draggable
		,disableDoubleClickZoom
		,onPointClick
		,fitBounds
		,scrollWheel;
	}

	public GMapBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public org.primefaces.model.map.MapModel getModel() {
		return (org.primefaces.model.map.MapModel) getStateHelper().eval(PropertyKeys.model, null);
	}
	public void setModel(org.primefaces.model.map.MapModel _model) {
		getStateHelper().put(PropertyKeys.model, _model);
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

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, null);
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
	}

	public java.lang.String getCenter() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.center, null);
	}
	public void setCenter(java.lang.String _center) {
		getStateHelper().put(PropertyKeys.center, _center);
	}

	public int getZoom() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.zoom, 8);
	}
	public void setZoom(int _zoom) {
		getStateHelper().put(PropertyKeys.zoom, _zoom);
	}

	public boolean isStreetView() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.streetView, false);
	}
	public void setStreetView(boolean _streetView) {
		getStateHelper().put(PropertyKeys.streetView, _streetView);
	}

	public boolean isDisableDefaultUI() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableDefaultUI, false);
	}
	public void setDisableDefaultUI(boolean _disableDefaultUI) {
		getStateHelper().put(PropertyKeys.disableDefaultUI, _disableDefaultUI);
	}

	public boolean isNavigationControl() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.navigationControl, true);
	}
	public void setNavigationControl(boolean _navigationControl) {
		getStateHelper().put(PropertyKeys.navigationControl, _navigationControl);
	}

	public boolean isMapTypeControl() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.mapTypeControl, true);
	}
	public void setMapTypeControl(boolean _mapTypeControl) {
		getStateHelper().put(PropertyKeys.mapTypeControl, _mapTypeControl);
	}

	public boolean isDraggable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
	}
	public void setDraggable(boolean _draggable) {
		getStateHelper().put(PropertyKeys.draggable, _draggable);
	}

	public boolean isDisableDoubleClickZoom() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableDoubleClickZoom, false);
	}
	public void setDisableDoubleClickZoom(boolean _disableDoubleClickZoom) {
		getStateHelper().put(PropertyKeys.disableDoubleClickZoom, _disableDoubleClickZoom);
	}

	public java.lang.String getOnPointClick() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onPointClick, null);
	}
	public void setOnPointClick(java.lang.String _onPointClick) {
		getStateHelper().put(PropertyKeys.onPointClick, _onPointClick);
	}

	public boolean isFitBounds() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.fitBounds, false);
	}
	public void setFitBounds(boolean _fitBounds) {
		getStateHelper().put(PropertyKeys.fitBounds, _fitBounds);
	}

	public boolean isScrollWheel() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.scrollWheel, true);
	}
	public void setScrollWheel(boolean _scrollWheel) {
		getStateHelper().put(PropertyKeys.scrollWheel, _scrollWheel);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}