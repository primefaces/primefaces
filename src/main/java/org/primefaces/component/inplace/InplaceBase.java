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
package org.primefaces.component.inplace;

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
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.component.inplace.Inplace;
import org.primefaces.util.Constants;
import javax.faces.event.BehaviorEvent;


public abstract class InplaceBase extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Inplace";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.InplaceRenderer";

	public enum PropertyKeys {

		widgetVar
		,label
		,emptyLabel
		,effect
		,effectSpeed
		,disabled
		,style
		,styleClass
		,editor
		,saveLabel
		,cancelLabel
		,event
		,toggleable;
	}

	public InplaceBase() {
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

	public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
	}
	public void setLabel(java.lang.String _label) {
		getStateHelper().put(PropertyKeys.label, _label);
	}

	public java.lang.String getEmptyLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyLabel, null);
	}
	public void setEmptyLabel(java.lang.String _emptyLabel) {
		getStateHelper().put(PropertyKeys.emptyLabel, _emptyLabel);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fade");
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
	}

	public java.lang.String getEffectSpeed() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effectSpeed, "normal");
	}
	public void setEffectSpeed(java.lang.String _effectSpeed) {
		getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
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

	public boolean isEditor() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editor, false);
	}
	public void setEditor(boolean _editor) {
		getStateHelper().put(PropertyKeys.editor, _editor);
	}

	public java.lang.String getSaveLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.saveLabel, "Save");
	}
	public void setSaveLabel(java.lang.String _saveLabel) {
		getStateHelper().put(PropertyKeys.saveLabel, _saveLabel);
	}

	public java.lang.String getCancelLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.cancelLabel, "Cancel");
	}
	public void setCancelLabel(java.lang.String _cancelLabel) {
		getStateHelper().put(PropertyKeys.cancelLabel, _cancelLabel);
	}

	public java.lang.String getEvent() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.event, "click");
	}
	public void setEvent(java.lang.String _event) {
		getStateHelper().put(PropertyKeys.event, _event);
	}

	public boolean isToggleable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleable, true);
	}
	public void setToggleable(boolean _toggleable) {
		getStateHelper().put(PropertyKeys.toggleable, _toggleable);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}