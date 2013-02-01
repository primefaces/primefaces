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
package org.primefaces.component.dock;

import org.primefaces.component.menu.AbstractMenu;
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
	@ResourceDependency(library="primefaces", name="dock/dock.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="dock/dock.js")
})
public class Dock extends AbstractMenu implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Dock";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.DockRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,model
		,position
		,itemWidth
		,maxWidth
		,proximity
		,halign;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Dock() {
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

	public org.primefaces.model.MenuModel getModel() {
		return (org.primefaces.model.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
	}
	public void setModel(org.primefaces.model.MenuModel _model) {
		getStateHelper().put(PropertyKeys.model, _model);
		handleAttribute("model", _model);
	}

	public java.lang.String getPosition() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.position, "bottom");
	}
	public void setPosition(java.lang.String _position) {
		getStateHelper().put(PropertyKeys.position, _position);
		handleAttribute("position", _position);
	}

	public int getItemWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.itemWidth, 40);
	}
	public void setItemWidth(int _itemWidth) {
		getStateHelper().put(PropertyKeys.itemWidth, _itemWidth);
		handleAttribute("itemWidth", _itemWidth);
	}

	public int getMaxWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxWidth, 50);
	}
	public void setMaxWidth(int _maxWidth) {
		getStateHelper().put(PropertyKeys.maxWidth, _maxWidth);
		handleAttribute("maxWidth", _maxWidth);
	}

	public int getProximity() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.proximity, 90);
	}
	public void setProximity(int _proximity) {
		getStateHelper().put(PropertyKeys.proximity, _proximity);
		handleAttribute("proximity", _proximity);
	}

	public java.lang.String getHalign() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.halign, "center");
	}
	public void setHalign(java.lang.String _halign) {
		getStateHelper().put(PropertyKeys.halign, _halign);
		handleAttribute("halign", _halign);
	}


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