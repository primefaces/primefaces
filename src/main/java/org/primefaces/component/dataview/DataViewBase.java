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
package org.primefaces.component.dataview;

import org.primefaces.component.api.UIData;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.util.Constants;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.event.BehaviorEvent;


public abstract class DataViewBase extends UIData implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder,org.primefaces.component.api.Pageable {


	public static final String COMPONENT_TYPE = "org.primefaces.component.DataView";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.DataViewRenderer";

	public enum PropertyKeys {

		widgetVar
		,style
		,styleClass
		,layout
		,gridIcon
		,listIcon;
	}

	public DataViewBase() {
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

	public java.lang.String getLayout() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.layout, "list");
	}
	public void setLayout(java.lang.String _layout) {
		getStateHelper().put(PropertyKeys.layout, _layout);
	}

	public java.lang.String getGridIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.gridIcon, null);
	}
	public void setGridIcon(java.lang.String _gridIcon) {
		getStateHelper().put(PropertyKeys.gridIcon, _gridIcon);
	}

	public java.lang.String getListIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.listIcon, null);
	}
	public void setListIcon(java.lang.String _listIcon) {
		getStateHelper().put(PropertyKeys.listIcon, _listIcon);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}