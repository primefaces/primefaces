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
package org.primefaces.component.datalist;

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
import javax.faces.FacesException;
import javax.faces.event.PhaseId;
import javax.faces.component.UIColumn;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.event.SelectEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.data.PageEvent;
import javax.faces.event.BehaviorEvent;
import org.primefaces.PrimeFaces;


public abstract class DataListBase extends UIData implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder,org.primefaces.component.api.Pageable {


	public static final String COMPONENT_TYPE = "org.primefaces.component.DataList";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.DataListRenderer";

	public enum PropertyKeys {

		widgetVar
		,type
		,itemType
		,style
		,styleClass
		,varStatus
		,emptyMessage
		,itemStyleClass;
	}

	public DataListBase() {
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

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, "unordered");
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
	}

	public java.lang.String getItemType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemType, null);
	}
	public void setItemType(java.lang.String _itemType) {
		getStateHelper().put(PropertyKeys.itemType, _itemType);
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

	public java.lang.String getVarStatus() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.varStatus, null);
	}
	public void setVarStatus(java.lang.String _varStatus) {
		getStateHelper().put(PropertyKeys.varStatus, _varStatus);
	}

	public java.lang.String getEmptyMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
	}
	public void setEmptyMessage(java.lang.String _emptyMessage) {
		getStateHelper().put(PropertyKeys.emptyMessage, _emptyMessage);
	}

	public java.lang.String getItemStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemStyleClass, null);
	}
	public void setItemStyleClass(java.lang.String _itemStyleClass) {
		getStateHelper().put(PropertyKeys.itemStyleClass, _itemStyleClass);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}