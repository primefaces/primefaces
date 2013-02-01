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
package org.primefaces.component.panel;

import javax.faces.component.UIPanel;
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
import org.primefaces.component.menu.Menu;
import javax.faces.component.UIComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.CloseEvent;
import org.primefaces.model.Visibility;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.component.panel.Panel;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class Panel extends UIPanel implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Panel";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.PanelRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,header
		,footer
		,toggleable
		,toggleSpeed
		,style
		,styleClass
		,collapsed
		,closable
		,closeSpeed
		,visible
		,closeTitle
		,toggleTitle
		,menuTitle
		,toggleOrientation;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Panel() {
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

	public java.lang.String getHeader() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.header, null);
	}
	public void setHeader(java.lang.String _header) {
		getStateHelper().put(PropertyKeys.header, _header);
		handleAttribute("header", _header);
	}

	public java.lang.String getFooter() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.footer, null);
	}
	public void setFooter(java.lang.String _footer) {
		getStateHelper().put(PropertyKeys.footer, _footer);
		handleAttribute("footer", _footer);
	}

	public boolean isToggleable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
	}
	public void setToggleable(boolean _toggleable) {
		getStateHelper().put(PropertyKeys.toggleable, _toggleable);
		handleAttribute("toggleable", _toggleable);
	}

	public int getToggleSpeed() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.toggleSpeed, 500);
	}
	public void setToggleSpeed(int _toggleSpeed) {
		getStateHelper().put(PropertyKeys.toggleSpeed, _toggleSpeed);
		handleAttribute("toggleSpeed", _toggleSpeed);
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

	public boolean isCollapsed() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
	}
	public void setCollapsed(boolean _collapsed) {
		getStateHelper().put(PropertyKeys.collapsed, _collapsed);
		handleAttribute("collapsed", _collapsed);
	}

	public boolean isClosable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.closable, false);
	}
	public void setClosable(boolean _closable) {
		getStateHelper().put(PropertyKeys.closable, _closable);
		handleAttribute("closable", _closable);
	}

	public int getCloseSpeed() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.closeSpeed, 500);
	}
	public void setCloseSpeed(int _closeSpeed) {
		getStateHelper().put(PropertyKeys.closeSpeed, _closeSpeed);
		handleAttribute("closeSpeed", _closeSpeed);
	}

	public boolean isVisible() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.visible, true);
	}
	public void setVisible(boolean _visible) {
		getStateHelper().put(PropertyKeys.visible, _visible);
		handleAttribute("visible", _visible);
	}

	public java.lang.String getCloseTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.closeTitle, null);
	}
	public void setCloseTitle(java.lang.String _closeTitle) {
		getStateHelper().put(PropertyKeys.closeTitle, _closeTitle);
		handleAttribute("closeTitle", _closeTitle);
	}

	public java.lang.String getToggleTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.toggleTitle, null);
	}
	public void setToggleTitle(java.lang.String _toggleTitle) {
		getStateHelper().put(PropertyKeys.toggleTitle, _toggleTitle);
		handleAttribute("toggleTitle", _toggleTitle);
	}

	public java.lang.String getMenuTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.menuTitle, null);
	}
	public void setMenuTitle(java.lang.String _menuTitle) {
		getStateHelper().put(PropertyKeys.menuTitle, _menuTitle);
		handleAttribute("menuTitle", _menuTitle);
	}

	public java.lang.String getToggleOrientation() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.toggleOrientation, "vertical");
	}
	public void setToggleOrientation(java.lang.String _toggleOrientation) {
		getStateHelper().put(PropertyKeys.toggleOrientation, _toggleOrientation);
		handleAttribute("toggleOrientation", _toggleOrientation);
	}


	public static final String PANEL_CLASS = "ui-panel ui-widget ui-widget-content ui-corner-all";
	public static final String PANEL_TITLEBAR_CLASS = "ui-panel-titlebar ui-widget-header ui-helper-clearfix ui-corner-all";
	public static final String PANEL_TITLE_CLASS = "ui-panel-title";
	public static final String PANEL_TITLE_ICON_CLASS = "ui-panel-titlebar-icon ui-corner-all ui-state-default";
	public static final String PANEL_CONTENT_CLASS = "ui-panel-content ui-widget-content";
	public static final String PANEL_FOOTER_CLASS = "ui-panel-footer ui-widget-content";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("toggle","close"));
	
	private Menu optionsMenu;
	
	public Menu  getOptionsMenu() {
		if(optionsMenu == null) {
			UIComponent optionsFacet = getFacet("options");
			if(optionsFacet != null) {
                if(optionsFacet instanceof Menu)
                    optionsMenu = (Menu) optionsFacet;
                else
                    optionsMenu = (Menu) optionsFacet.getChildren().get(0);
            }

		}

		return optionsMenu;
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String source = params.get(Constants.PARTIAL_SOURCE_PARAM);
        String clientId = this.getClientId(context);
        
        if(isSelfRequest(context)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            
            if(eventName.equals("toggle")) {
                boolean collapsed = Boolean.valueOf(params.get(clientId + "_collapsed"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;

                super.queueEvent(new ToggleEvent(this, behaviorEvent.getBehavior(), visibility));

            } else if(eventName.equals("close")) {
                super.queueEvent(new CloseEvent(this, behaviorEvent.getBehavior()));
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isSelfRequest(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isSelfRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isSelfRequest(context)) {
            super.processUpdates(context);
        }
        
        FacesContext facesContext = getFacesContext();
        ELContext eLContext = facesContext.getELContext();
        
        ValueExpression collapsedVE = this.getValueExpression("collapsed");
        if(collapsedVE != null) {
            collapsedVE.setValue(eLContext, this.isCollapsed());
            getStateHelper().put(Panel.PropertyKeys.collapsed, null);
        }
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
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