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
package org.primefaces.component.keyboard;

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

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="keyboard/keyboard.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="keyboard/keyboard.js")
})
public class Keyboard extends HtmlInputText implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Keyboard";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.KeyboardRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,password
		,showMode
		,buttonImage
		,buttonImageOnly
		,effect
		,effectDuration
		,layout
		,layoutTemplate
		,keypadOnly
		,promptLabel
		,closeLabel
		,clearLabel
		,backspaceLabel;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Keyboard() {
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

	public boolean isPassword() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.password, false);
	}
	public void setPassword(boolean _password) {
		getStateHelper().put(PropertyKeys.password, _password);
		handleAttribute("password", _password);
	}

	public java.lang.String getShowMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.showMode, "focus");
	}
	public void setShowMode(java.lang.String _showMode) {
		getStateHelper().put(PropertyKeys.showMode, _showMode);
		handleAttribute("showMode", _showMode);
	}

	public java.lang.String getButtonImage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.buttonImage, null);
	}
	public void setButtonImage(java.lang.String _buttonImage) {
		getStateHelper().put(PropertyKeys.buttonImage, _buttonImage);
		handleAttribute("buttonImage", _buttonImage);
	}

	public boolean isButtonImageOnly() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.buttonImageOnly, false);
	}
	public void setButtonImageOnly(boolean _buttonImageOnly) {
		getStateHelper().put(PropertyKeys.buttonImageOnly, _buttonImageOnly);
		handleAttribute("buttonImageOnly", _buttonImageOnly);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fadeIn");
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public java.lang.String getEffectDuration() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effectDuration, null);
	}
	public void setEffectDuration(java.lang.String _effectDuration) {
		getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
		handleAttribute("effectDuration", _effectDuration);
	}

	public java.lang.String getLayout() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.layout, "qwerty");
	}
	public void setLayout(java.lang.String _layout) {
		getStateHelper().put(PropertyKeys.layout, _layout);
		handleAttribute("layout", _layout);
	}

	public java.lang.String getLayoutTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.layoutTemplate, null);
	}
	public void setLayoutTemplate(java.lang.String _layoutTemplate) {
		getStateHelper().put(PropertyKeys.layoutTemplate, _layoutTemplate);
		handleAttribute("layoutTemplate", _layoutTemplate);
	}

	public boolean isKeypadOnly() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.keypadOnly, false);
	}
	public void setKeypadOnly(boolean _keypadOnly) {
		getStateHelper().put(PropertyKeys.keypadOnly, _keypadOnly);
		handleAttribute("keypadOnly", _keypadOnly);
	}

	public java.lang.String getPromptLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.promptLabel, null);
	}
	public void setPromptLabel(java.lang.String _promptLabel) {
		getStateHelper().put(PropertyKeys.promptLabel, _promptLabel);
		handleAttribute("promptLabel", _promptLabel);
	}

	public java.lang.String getCloseLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.closeLabel, null);
	}
	public void setCloseLabel(java.lang.String _closeLabel) {
		getStateHelper().put(PropertyKeys.closeLabel, _closeLabel);
		handleAttribute("closeLabel", _closeLabel);
	}

	public java.lang.String getClearLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.clearLabel, null);
	}
	public void setClearLabel(java.lang.String _clearLabel) {
		getStateHelper().put(PropertyKeys.clearLabel, _clearLabel);
		handleAttribute("clearLabel", _clearLabel);
	}

	public java.lang.String getBackspaceLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.backspaceLabel, null);
	}
	public void setBackspaceLabel(java.lang.String _backspaceLabel) {
		getStateHelper().put(PropertyKeys.backspaceLabel, _backspaceLabel);
		handleAttribute("backspaceLabel", _backspaceLabel);
	}


    public final static String STYLE_CLASS = "ui-inputfield ui-keyboard-input ui-widget ui-state-default ui-corner-all";

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