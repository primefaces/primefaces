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
package org.primefaces.component.layout;

import javax.faces.component.UIPanel;
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
import javax.faces.component.UIComponent;
import org.primefaces.component.layout.LayoutUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.util.Constants;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.BehaviorEvent;


public abstract class LayoutBase extends UIPanel implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Layout";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.LayoutRenderer";

	public enum PropertyKeys {

		widgetVar
		,fullPage
		,style
		,styleClass
		,onResize
		,onClose
		,onToggle
		,resizeTitle
		,collapseTitle
		,expandTitle
		,closeTitle
		,stateful;
	}

	public LayoutBase() {
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

	public boolean isFullPage() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.fullPage, false);
	}
	public void setFullPage(boolean _fullPage) {
		getStateHelper().put(PropertyKeys.fullPage, _fullPage);
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

	public java.lang.String getOnResize() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onResize, null);
	}
	public void setOnResize(java.lang.String _onResize) {
		getStateHelper().put(PropertyKeys.onResize, _onResize);
	}

	public java.lang.String getOnClose() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onClose, null);
	}
	public void setOnClose(java.lang.String _onClose) {
		getStateHelper().put(PropertyKeys.onClose, _onClose);
	}

	public java.lang.String getOnToggle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onToggle, null);
	}
	public void setOnToggle(java.lang.String _onToggle) {
		getStateHelper().put(PropertyKeys.onToggle, _onToggle);
	}

	public java.lang.String getResizeTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.resizeTitle, null);
	}
	public void setResizeTitle(java.lang.String _resizeTitle) {
		getStateHelper().put(PropertyKeys.resizeTitle, _resizeTitle);
	}

	public java.lang.String getCollapseTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.collapseTitle, "Collapse");
	}
	public void setCollapseTitle(java.lang.String _collapseTitle) {
		getStateHelper().put(PropertyKeys.collapseTitle, _collapseTitle);
	}

	public java.lang.String getExpandTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.expandTitle, null);
	}
	public void setExpandTitle(java.lang.String _expandTitle) {
		getStateHelper().put(PropertyKeys.expandTitle, _expandTitle);
	}

	public java.lang.String getCloseTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.closeTitle, "Close");
	}
	public void setCloseTitle(java.lang.String _closeTitle) {
		getStateHelper().put(PropertyKeys.closeTitle, _closeTitle);
	}

	public boolean isStateful() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stateful, false);
	}
	public void setStateful(boolean _stateful) {
		getStateHelper().put(PropertyKeys.stateful, _stateful);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}