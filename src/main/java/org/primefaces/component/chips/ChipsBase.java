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
package org.primefaces.component.chips;

import javax.faces.component.html.HtmlInputText;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.convert.Converter;
import javax.faces.component.behavior.Behavior;


public abstract class ChipsBase extends HtmlInputText implements org.primefaces.component.api.Widget,org.primefaces.component.api.InputHolder,org.primefaces.component.api.MixedClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Chips";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.ChipsRenderer";

	public enum PropertyKeys {

		placeholder
		,widgetVar
		,max
		,inputStyle
		,inputStyleClass;
	}

	public ChipsBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getPlaceholder() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
	}
	public void setPlaceholder(java.lang.String _placeholder) {
		getStateHelper().put(PropertyKeys.placeholder, _placeholder);
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public int getMax() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.max, java.lang.Integer.MAX_VALUE);
	}
	public void setMax(int _max) {
		getStateHelper().put(PropertyKeys.max, _max);
	}

	public java.lang.String getInputStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.inputStyle, null);
	}
	public void setInputStyle(java.lang.String _inputStyle) {
		getStateHelper().put(PropertyKeys.inputStyle, _inputStyle);
	}

	public java.lang.String getInputStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
	}
	public void setInputStyleClass(java.lang.String _inputStyleClass) {
		getStateHelper().put(PropertyKeys.inputStyleClass, _inputStyleClass);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}