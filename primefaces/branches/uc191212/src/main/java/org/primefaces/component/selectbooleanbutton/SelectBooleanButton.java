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
import org.primefaces.util.HTML;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class SelectBooleanButton extends HtmlSelectBooleanCheckbox implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.SelectBooleanButton";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.SelectBooleanButtonRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,onLabel
		,offLabel
		,onIcon
		,offIcon;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public SelectBooleanButton() {
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

	public java.lang.String getOnLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onLabel, null);
	}
	public void setOnLabel(java.lang.String _onLabel) {
		getStateHelper().put(PropertyKeys.onLabel, _onLabel);
		handleAttribute("onLabel", _onLabel);
	}

	public java.lang.String getOffLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.offLabel, null);
	}
	public void setOffLabel(java.lang.String _offLabel) {
		getStateHelper().put(PropertyKeys.offLabel, _offLabel);
		handleAttribute("offLabel", _offLabel);
	}

	public java.lang.String getOnIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onIcon, null);
	}
	public void setOnIcon(java.lang.String _onIcon) {
		getStateHelper().put(PropertyKeys.onIcon, _onIcon);
		handleAttribute("onIcon", _onIcon);
	}

	public java.lang.String getOffIcon() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.offIcon, null);
	}
	public void setOffIcon(java.lang.String _offIcon) {
		getStateHelper().put(PropertyKeys.offIcon, _offIcon);
		handleAttribute("offIcon", _offIcon);
	}


    public final static String STYLE_CLASS = "ui-selectbooleanbutton ui-widget";

    public String resolveStyleClass(boolean checked, boolean disabled) {
        String icon = checked ? getOnIcon() : getOffIcon();
        String styleClass = icon != null ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
    
        if(disabled) {
            styleClass = styleClass + " ui-state-disabled";
        } 

        if(checked) {
            styleClass = styleClass + " ui-state-active";
        }

        if(!isValid()) {
            styleClass = styleClass + " ui-state-error";
        }

        String userStyleClass = getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
    
        return styleClass;
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