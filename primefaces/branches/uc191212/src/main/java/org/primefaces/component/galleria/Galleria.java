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
package org.primefaces.component.galleria;

import javax.faces.component.UIOutput;
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
	@ResourceDependency(library="primefaces", name="galleria/galleria.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="galleria/galleria.js")
})
public class Galleria extends UIOutput implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Galleria";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.GalleriaRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,value
		,var
		,style
		,styleClass
		,effect
		,effectSpeed
		,frameWidth
		,frameHeight
		,showFilmstrip
		,autoPlay
		,transitionInterval
		,panelWidth
		,panelHeight
		,showCaption;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Galleria() {
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

	public java.lang.Object getValue() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(java.lang.Object _value) {
		getStateHelper().put(PropertyKeys.value, _value);
		handleAttribute("value", _value);
	}

	public java.lang.String getVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(java.lang.String _var) {
		getStateHelper().put(PropertyKeys.var, _var);
		handleAttribute("var", _var);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
		handleAttribute("style", _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
		handleAttribute("styleClass", _styleClass);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fade");
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public int getEffectSpeed() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectSpeed, 500);
	}
	public void setEffectSpeed(int _effectSpeed) {
		getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
		handleAttribute("effectSpeed", _effectSpeed);
	}

	public int getFrameWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frameWidth, 60);
	}
	public void setFrameWidth(int _frameWidth) {
		getStateHelper().put(PropertyKeys.frameWidth, _frameWidth);
		handleAttribute("frameWidth", _frameWidth);
	}

	public int getFrameHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frameHeight, 40);
	}
	public void setFrameHeight(int _frameHeight) {
		getStateHelper().put(PropertyKeys.frameHeight, _frameHeight);
		handleAttribute("frameHeight", _frameHeight);
	}

	public boolean isShowFilmstrip() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showFilmstrip, true);
	}
	public void setShowFilmstrip(boolean _showFilmstrip) {
		getStateHelper().put(PropertyKeys.showFilmstrip, _showFilmstrip);
		handleAttribute("showFilmstrip", _showFilmstrip);
	}

	public boolean isAutoPlay() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoPlay, true);
	}
	public void setAutoPlay(boolean _autoPlay) {
		getStateHelper().put(PropertyKeys.autoPlay, _autoPlay);
		handleAttribute("autoPlay", _autoPlay);
	}

	public int getTransitionInterval() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.transitionInterval, 4000);
	}
	public void setTransitionInterval(int _transitionInterval) {
		getStateHelper().put(PropertyKeys.transitionInterval, _transitionInterval);
		handleAttribute("transitionInterval", _transitionInterval);
	}

	public int getPanelWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.panelWidth, java.lang.Integer.MIN_VALUE);
	}
	public void setPanelWidth(int _panelWidth) {
		getStateHelper().put(PropertyKeys.panelWidth, _panelWidth);
		handleAttribute("panelWidth", _panelWidth);
	}

	public int getPanelHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.panelHeight, java.lang.Integer.MIN_VALUE);
	}
	public void setPanelHeight(int _panelHeight) {
		getStateHelper().put(PropertyKeys.panelHeight, _panelHeight);
		handleAttribute("panelHeight", _panelHeight);
	}

	public boolean isShowCaption() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showCaption, false);
	}
	public void setShowCaption(boolean _showCaption) {
		getStateHelper().put(PropertyKeys.showCaption, _showCaption);
		handleAttribute("showCaption", _showCaption);
	}


    public final static String CONTAINER_CLASS = "ui-galleria ui-widget ui-widget-content ui-corner-all";
    public final static String PANEL_WRAPPER_CLASS = "ui-galleria-panel-wrapper";
    public final static String PANEL_CLASS = "ui-galleria-panel ui-helper-hidden";
    public final static String PANEL_CONTENT_CLASS = "ui-galleria-panel-content";

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