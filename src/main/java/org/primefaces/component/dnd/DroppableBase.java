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
package org.primefaces.component.dnd;

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
import org.primefaces.event.DragDropEvent;
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.FacesException;
import org.primefaces.util.Constants;
import org.primefaces.expression.SearchExpressionFacade;
import javax.faces.event.BehaviorEvent;


public abstract class DroppableBase extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Droppable";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.DroppableRenderer";

	public enum PropertyKeys {

		widgetVar
		,forValue("for")
		,disabled
		,hoverStyleClass
		,activeStyleClass
		,onDrop
		,accept
		,scope
		,tolerance
		,datasource
		,greedy;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public DroppableBase() {
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

	public java.lang.String getFor() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
	}
	public void setFor(java.lang.String _for) {
		getStateHelper().put(PropertyKeys.forValue, _for);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
	}

	public java.lang.String getHoverStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.hoverStyleClass, null);
	}
	public void setHoverStyleClass(java.lang.String _hoverStyleClass) {
		getStateHelper().put(PropertyKeys.hoverStyleClass, _hoverStyleClass);
	}

	public java.lang.String getActiveStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.activeStyleClass, null);
	}
	public void setActiveStyleClass(java.lang.String _activeStyleClass) {
		getStateHelper().put(PropertyKeys.activeStyleClass, _activeStyleClass);
	}

	public java.lang.String getOnDrop() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onDrop, null);
	}
	public void setOnDrop(java.lang.String _onDrop) {
		getStateHelper().put(PropertyKeys.onDrop, _onDrop);
	}

	public java.lang.String getAccept() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.accept, null);
	}
	public void setAccept(java.lang.String _accept) {
		getStateHelper().put(PropertyKeys.accept, _accept);
	}

	public java.lang.String getScope() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.scope, null);
	}
	public void setScope(java.lang.String _scope) {
		getStateHelper().put(PropertyKeys.scope, _scope);
	}

	public java.lang.String getTolerance() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tolerance, null);
	}
	public void setTolerance(java.lang.String _tolerance) {
		getStateHelper().put(PropertyKeys.tolerance, _tolerance);
	}

	public java.lang.String getDatasource() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.datasource, null);
	}
	public void setDatasource(java.lang.String _datasource) {
		getStateHelper().put(PropertyKeys.datasource, _datasource);
	}

	public boolean isGreedy() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.greedy, false);
	}
	public void setGreedy(boolean _greedy) {
		getStateHelper().put(PropertyKeys.greedy, _greedy);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}