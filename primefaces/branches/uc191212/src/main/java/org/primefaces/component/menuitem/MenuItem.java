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
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import java.util.Map;
import javax.faces.event.ActionEvent;

@ResourceDependencies({

})
public class MenuItem extends UICommand implements org.primefaces.component.api.AjaxSource,java.io.Serializable,org.primefaces.component.api.UIOutcomeTarget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.MenuItem";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

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
		,async
		,ajax
		,icon
		,partialSubmit
		,title
		,outcome
		,includeViewParams
		,fragment;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public MenuItem() {
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
		handleAttribute("url", _url);
	}

	public java.lang.String getTarget() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.target, null);
	}
	public void setTarget(java.lang.String _target) {
		getStateHelper().put(PropertyKeys.target, _target);
		handleAttribute("target", _target);
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

	public java.lang.String getOnclick() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onclick, null);
	}
	public void setOnclick(java.lang.String _onclick) {
		getStateHelper().put(PropertyKeys.onclick, _onclick);
		handleAttribute("onclick", _onclick);
	}

	public java.lang.String getUpdate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.update, null);
	}
	public void setUpdate(java.lang.String _update) {
		getStateHelper().put(PropertyKeys.update, _update);
		handleAttribute("update", _update);
	}

	public java.lang.String getProcess() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.process, null);
	}
	public void setProcess(java.lang.String _process) {
		getStateHelper().put(PropertyKeys.process, _process);
		handleAttribute("process", _process);
	}

	public java.lang.String getOnstart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onstart, null);
	}
	public void setOnstart(java.lang.String _onstart) {
		getStateHelper().put(PropertyKeys.onstart, _onstart);
		handleAttribute("onstart", _onstart);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
		handleAttribute("disabled", _disabled);
	}

	public java.lang.String getOncomplete() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
	}
	public void setOncomplete(java.lang.String _oncomplete) {
		getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
		handleAttribute("oncomplete", _oncomplete);
	}

	public java.lang.String getOnerror() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onerror, null);
	}
	public void setOnerror(java.lang.String _onerror) {
		getStateHelper().put(PropertyKeys.onerror, _onerror);
		handleAttribute("onerror", _onerror);
	}

	public java.lang.String getOnsuccess() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onsuccess, null);
	}
	public void setOnsuccess(java.lang.String _onsuccess) {
		getStateHelper().put(PropertyKeys.onsuccess, _onsuccess);
		handleAttribute("onsuccess", _onsuccess);
	}

	public boolean isGlobal() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.global, true);
	}
	public void setGlobal(boolean _global) {
		getStateHelper().put(PropertyKeys.global, _global);
		handleAttribute("global", _global);
	}

	public boolean isAsync() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.async, false);
	}
	public void setAsync(boolean _async) {
		getStateHelper().put(PropertyKeys.async, _async);
		handleAttribute("async", _async);
	}

	public boolean isAjax() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ajax, true);
	}
	public void setAjax(boolean _ajax) {
		getStateHelper().put(PropertyKeys.ajax, _ajax);
		handleAttribute("ajax", _ajax);
	}

	public java.lang.String getIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.icon, null);
	}
	public void setIcon(java.lang.String _icon) {
		getStateHelper().put(PropertyKeys.icon, _icon);
		handleAttribute("icon", _icon);
	}

	public boolean isPartialSubmit() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.partialSubmit, false);
	}
	public void setPartialSubmit(boolean _partialSubmit) {
		getStateHelper().put(PropertyKeys.partialSubmit, _partialSubmit);
		handleAttribute("partialSubmit", _partialSubmit);
	}

	public java.lang.String getTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.title, null);
	}
	public void setTitle(java.lang.String _title) {
		getStateHelper().put(PropertyKeys.title, _title);
		handleAttribute("title", _title);
	}

	public java.lang.String getOutcome() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.outcome, null);
	}
	public void setOutcome(java.lang.String _outcome) {
		getStateHelper().put(PropertyKeys.outcome, _outcome);
		handleAttribute("outcome", _outcome);
	}

	public boolean isIncludeViewParams() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.includeViewParams, false);
	}
	public void setIncludeViewParams(boolean _includeViewParams) {
		getStateHelper().put(PropertyKeys.includeViewParams, _includeViewParams);
		handleAttribute("includeViewParams", _includeViewParams);
	}

	public java.lang.String getFragment() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.fragment, null);
	}
	public void setFragment(java.lang.String _fragment) {
		getStateHelper().put(PropertyKeys.fragment, _fragment);
		handleAttribute("fragment", _fragment);
	}


    public final static String STYLE_CLASS = "wijmo-wijmenu-link ui-corner-all";

	public void decode(FacesContext facesContext) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = getClientId(facesContext);
		
		if(params.containsKey(clientId)) {
			this.queueEvent(new ActionEvent(this));
		}
	}
	
	public boolean shouldRenderChildren() {
		if(getChildCount() == 0)
			return false;
		else {
			for(UIComponent child : getChildren()) {
				if(! (child instanceof UIParameter) ) {
					return true;
				}
			}
		}
		
		return false;
	}

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression("partialSubmit") != null); 
    }

    public String getHref() {
        return this.getUrl();
    }

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
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