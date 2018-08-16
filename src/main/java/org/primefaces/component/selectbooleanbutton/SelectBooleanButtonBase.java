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
package org.primefaces.component.selectbooleanbutton;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
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
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.HTML;


public abstract class SelectBooleanButtonBase extends HtmlSelectBooleanCheckbox implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.SelectBooleanButton";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectBooleanButtonRenderer";

	public enum PropertyKeys {

		widgetVar
		,onLabel
		,offLabel
		,onIcon
		,offIcon;
	}

	public SelectBooleanButtonBase() {
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

	public java.lang.String getOnLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onLabel, null);
	}
	public void setOnLabel(java.lang.String _onLabel) {
		getStateHelper().put(PropertyKeys.onLabel, _onLabel);
	}

	public java.lang.String getOffLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.offLabel, null);
	}
	public void setOffLabel(java.lang.String _offLabel) {
		getStateHelper().put(PropertyKeys.offLabel, _offLabel);
	}

	public java.lang.String getOnIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onIcon, null);
	}
	public void setOnIcon(java.lang.String _onIcon) {
		getStateHelper().put(PropertyKeys.onIcon, _onIcon);
	}

	public java.lang.String getOffIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.offIcon, null);
	}
	public void setOffIcon(java.lang.String _offIcon) {
		getStateHelper().put(PropertyKeys.offIcon, _offIcon);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}