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
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class Inplace extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Inplace";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.InplaceRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

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

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Inplace() {
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

	public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
	}
	public void setLabel(java.lang.String _label) {
		getStateHelper().put(PropertyKeys.label, _label);
		handleAttribute("label", _label);
	}

	public java.lang.String getEmptyLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyLabel, null);
	}
	public void setEmptyLabel(java.lang.String _emptyLabel) {
		getStateHelper().put(PropertyKeys.emptyLabel, _emptyLabel);
		handleAttribute("emptyLabel", _emptyLabel);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fade");
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public java.lang.String getEffectSpeed() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effectSpeed, "normal");
	}
	public void setEffectSpeed(java.lang.String _effectSpeed) {
		getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
		handleAttribute("effectSpeed", _effectSpeed);
	}

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
		handleAttribute("disabled", _disabled);
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

	public boolean isEditor() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editor, false);
	}
	public void setEditor(boolean _editor) {
		getStateHelper().put(PropertyKeys.editor, _editor);
		handleAttribute("editor", _editor);
	}

	public java.lang.String getSaveLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.saveLabel, "Save");
	}
	public void setSaveLabel(java.lang.String _saveLabel) {
		getStateHelper().put(PropertyKeys.saveLabel, _saveLabel);
		handleAttribute("saveLabel", _saveLabel);
	}

	public java.lang.String getCancelLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.cancelLabel, "Cancel");
	}
	public void setCancelLabel(java.lang.String _cancelLabel) {
		getStateHelper().put(PropertyKeys.cancelLabel, _cancelLabel);
		handleAttribute("cancelLabel", _cancelLabel);
	}

	public java.lang.String getEvent() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.event, "click");
	}
	public void setEvent(java.lang.String _event) {
		getStateHelper().put(PropertyKeys.event, _event);
		handleAttribute("event", _event);
	}

	public boolean isToggleable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleable, true);
	}
	public void setToggleable(boolean _toggleable) {
		getStateHelper().put(PropertyKeys.toggleable, _toggleable);
		handleAttribute("toggleable", _toggleable);
	}


    public static final String CONTAINER_CLASS = "ui-inplace ui-hidden-container";
    public static final String DISPLAY_CLASS = "ui-inplace-display";
    public static final String DISABLED_DISPLAY_CLASS = "ui-inplace-display-disabled";
    public static final String CONTENT_CLASS = "ui-inplace-content";
    public static final String EDITOR_CLASS = "ui-inplace-editor";
    public static final String SAVE_BUTTON_CLASS = "ui-inplace-save";
    public static final String CANCEL_BUTTON_CLASS = "ui-inplace-cancel";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("save", "cancel"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }


    @Override
    public void processDecodes(FacesContext context) {
        if(isCancelRequest(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isCancelRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isCancelRequest(context)) {
            super.processUpdates(context);
        }
    }
    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    private boolean isCancelRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_cancel");
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