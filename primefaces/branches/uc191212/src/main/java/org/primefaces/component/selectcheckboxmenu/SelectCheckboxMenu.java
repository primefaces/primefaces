/*
 * Generated, Do Not Modify
 */
/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.selectcheckboxmenu;

import javax.faces.component.html.HtmlSelectManyCheckbox;
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

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class SelectCheckboxMenu extends HtmlSelectManyCheckbox implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.SelectCheckboxMenu";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.SelectCheckboxMenuRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,scrollHeight
		,onShow
		,onHide
		,filter
		,filterMatchMode
		,filterFunction
		,caseSensitive
		,panelStyle
		,panelStyleClass;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public SelectCheckboxMenu() {
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
		handleAttribute("widgetVar", _widgetVar);
	}

	public int getScrollHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MAX_VALUE);
	}
	public void setScrollHeight(int _scrollHeight) {
		getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
		handleAttribute("scrollHeight", _scrollHeight);
	}

	public java.lang.String getOnShow() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onShow, null);
	}
	public void setOnShow(java.lang.String _onShow) {
		getStateHelper().put(PropertyKeys.onShow, _onShow);
		handleAttribute("onShow", _onShow);
	}

	public java.lang.String getOnHide() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onHide, null);
	}
	public void setOnHide(java.lang.String _onHide) {
		getStateHelper().put(PropertyKeys.onHide, _onHide);
		handleAttribute("onHide", _onHide);
	}

	public boolean isFilter() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.filter, false);
	}
	public void setFilter(boolean _filter) {
		getStateHelper().put(PropertyKeys.filter, _filter);
		handleAttribute("filter", _filter);
	}

	public java.lang.String getFilterMatchMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
	}
	public void setFilterMatchMode(java.lang.String _filterMatchMode) {
		getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
		handleAttribute("filterMatchMode", _filterMatchMode);
	}

	public java.lang.String getFilterFunction() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.filterFunction, null);
	}
	public void setFilterFunction(java.lang.String _filterFunction) {
		getStateHelper().put(PropertyKeys.filterFunction, _filterFunction);
		handleAttribute("filterFunction", _filterFunction);
	}

	public boolean isCaseSensitive() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitive, false);
	}
	public void setCaseSensitive(boolean _caseSensitive) {
		getStateHelper().put(PropertyKeys.caseSensitive, _caseSensitive);
		handleAttribute("caseSensitive", _caseSensitive);
	}

	public java.lang.String getPanelStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyle, null);
	}
	public void setPanelStyle(java.lang.String _panelStyle) {
		getStateHelper().put(PropertyKeys.panelStyle, _panelStyle);
		handleAttribute("panelStyle", _panelStyle);
	}

	public java.lang.String getPanelStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
	}
	public void setPanelStyleClass(java.lang.String _panelStyleClass) {
		getStateHelper().put(PropertyKeys.panelStyleClass, _panelStyleClass);
		handleAttribute("panelStyleClass", _panelStyleClass);
	}


    public final static String STYLE_CLASS = "ui-selectcheckboxmenu ui-widget ui-state-default ui-corner-all ui-helper-clearfix";
    public final static String LABEL_CONTAINER_CLASS = "ui-selectcheckboxmenu-label-container";
    public final static String LABEL_CLASS = "ui-selectcheckboxmenu-label ui-corner-all";
    public final static String TRIGGER_CLASS = "ui-selectcheckboxmenu-trigger ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-selectcheckboxmenu-panel ui-widget-content ui-corner-all ui-helper-hidden";
    public final static String LIST_CLASS = "ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_CLASS = "ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all ui-helper-clearfix";

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null)
			return userWidgetVar;
		 else
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
	}

	public void handleAttribute(String name, Object value) {
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}