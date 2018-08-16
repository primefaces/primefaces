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
package org.primefaces.component.menuitem;

import javax.faces.component.UICommand;
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
import java.util.List;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.ActionEvent;
import javax.el.MethodExpression;
import javax.faces.component.UIParameter;
import org.primefaces.util.ComponentUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public abstract class UIMenuItemBase extends UICommand implements org.primefaces.component.api.AjaxSource,org.primefaces.component.api.UIOutcomeTarget,org.primefaces.model.menu.MenuItem,org.primefaces.component.api.Confirmable,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.UIMenuItem";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";

	public enum PropertyKeys {

		url
		,target
		,style
		,styleClass
		,onclick
		,update
		,process
		,onstart
		,disabled
		,oncomplete
		,onerror
		,onsuccess
		,global
		,delay
		,timeout
		,async
		,ajax
		,icon
		,iconPos
		,partialSubmit
		,resetValues
		,ignoreAutoUpdate
		,title
		,outcome
		,includeViewParams
		,fragment
		,disableClientWindow
		,containerStyle
		,containerStyleClass
		,partialSubmitFilter
		,form
		,escape
		,rel;
	}

	public UIMenuItemBase() {
		setRendererType(null);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getUrl() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.url, null);
	}
	public void setUrl(java.lang.String _url) {
		getStateHelper().put(PropertyKeys.url, _url);
	}

	public java.lang.String getTarget() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.target, null);
	}
	public void setTarget(java.lang.String _target) {
		getStateHelper().put(PropertyKeys.target, _target);
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

	public java.lang.String getOnclick() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onclick, null);
	}
	public void setOnclick(java.lang.String _onclick) {
		getStateHelper().put(PropertyKeys.onclick, _onclick);
	}

	public java.lang.String getUpdate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.update, null);
	}
	public void setUpdate(java.lang.String _update) {
		getStateHelper().put(PropertyKeys.update, _update);
	}

	public java.lang.String getProcess() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.process, null);
	}
	public void setProcess(java.lang.String _process) {
		getStateHelper().put(PropertyKeys.process, _process);
	}

	public java.lang.String getOnstart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onstart, null);
	}
	public void setOnstart(java.lang.String _onstart) {
		getStateHelper().put(PropertyKeys.onstart, _onstart);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
	}

	public java.lang.String getOncomplete() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
	}
	public void setOncomplete(java.lang.String _oncomplete) {
		getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
	}

	public java.lang.String getOnerror() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onerror, null);
	}
	public void setOnerror(java.lang.String _onerror) {
		getStateHelper().put(PropertyKeys.onerror, _onerror);
	}

	public java.lang.String getOnsuccess() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onsuccess, null);
	}
	public void setOnsuccess(java.lang.String _onsuccess) {
		getStateHelper().put(PropertyKeys.onsuccess, _onsuccess);
	}

	public boolean isGlobal() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.global, true);
	}
	public void setGlobal(boolean _global) {
		getStateHelper().put(PropertyKeys.global, _global);
	}

	public java.lang.String getDelay() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.delay, null);
	}
	public void setDelay(java.lang.String _delay) {
		getStateHelper().put(PropertyKeys.delay, _delay);
	}

	public int getTimeout() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.timeout, 0);
	}
	public void setTimeout(int _timeout) {
		getStateHelper().put(PropertyKeys.timeout, _timeout);
	}

	public boolean isAsync() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.async, false);
	}
	public void setAsync(boolean _async) {
		getStateHelper().put(PropertyKeys.async, _async);
	}

	public boolean isAjax() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ajax, true);
	}
	public void setAjax(boolean _ajax) {
		getStateHelper().put(PropertyKeys.ajax, _ajax);
	}

	public java.lang.String getIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.icon, null);
	}
	public void setIcon(java.lang.String _icon) {
		getStateHelper().put(PropertyKeys.icon, _icon);
	}

	public java.lang.String getIconPos() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.iconPos, "right");
	}
	public void setIconPos(java.lang.String _iconPos) {
		getStateHelper().put(PropertyKeys.iconPos, _iconPos);
	}

	public boolean isPartialSubmit() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.partialSubmit, false);
	}
	public void setPartialSubmit(boolean _partialSubmit) {
		getStateHelper().put(PropertyKeys.partialSubmit, _partialSubmit);
	}

	public boolean isResetValues() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resetValues, false);
	}
	public void setResetValues(boolean _resetValues) {
		getStateHelper().put(PropertyKeys.resetValues, _resetValues);
	}

	public boolean isIgnoreAutoUpdate() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ignoreAutoUpdate, false);
	}
	public void setIgnoreAutoUpdate(boolean _ignoreAutoUpdate) {
		getStateHelper().put(PropertyKeys.ignoreAutoUpdate, _ignoreAutoUpdate);
	}

	public java.lang.String getTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.title, null);
	}
	public void setTitle(java.lang.String _title) {
		getStateHelper().put(PropertyKeys.title, _title);
	}

	public java.lang.String getOutcome() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.outcome, null);
	}
	public void setOutcome(java.lang.String _outcome) {
		getStateHelper().put(PropertyKeys.outcome, _outcome);
	}

	public boolean isIncludeViewParams() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.includeViewParams, false);
	}
	public void setIncludeViewParams(boolean _includeViewParams) {
		getStateHelper().put(PropertyKeys.includeViewParams, _includeViewParams);
	}

	public java.lang.String getFragment() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.fragment, null);
	}
	public void setFragment(java.lang.String _fragment) {
		getStateHelper().put(PropertyKeys.fragment, _fragment);
	}

	public boolean isDisableClientWindow() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableClientWindow, false);
	}
	public void setDisableClientWindow(boolean _disableClientWindow) {
		getStateHelper().put(PropertyKeys.disableClientWindow, _disableClientWindow);
	}

	public java.lang.String getContainerStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.containerStyle, null);
	}
	public void setContainerStyle(java.lang.String _containerStyle) {
		getStateHelper().put(PropertyKeys.containerStyle, _containerStyle);
	}

	public java.lang.String getContainerStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.containerStyleClass, null);
	}
	public void setContainerStyleClass(java.lang.String _containerStyleClass) {
		getStateHelper().put(PropertyKeys.containerStyleClass, _containerStyleClass);
	}

	public java.lang.String getPartialSubmitFilter() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.partialSubmitFilter, null);
	}
	public void setPartialSubmitFilter(java.lang.String _partialSubmitFilter) {
		getStateHelper().put(PropertyKeys.partialSubmitFilter, _partialSubmitFilter);
	}

	public java.lang.String getForm() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.form, null);
	}
	public void setForm(java.lang.String _form) {
		getStateHelper().put(PropertyKeys.form, _form);
	}

	public boolean isEscape() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escape, true);
	}
	public void setEscape(boolean _escape) {
		getStateHelper().put(PropertyKeys.escape, _escape);
	}

	public java.lang.String getRel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rel, null);
	}
	public void setRel(java.lang.String _rel) {
		getStateHelper().put(PropertyKeys.rel, _rel);
	}

}