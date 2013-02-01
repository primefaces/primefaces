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
package org.primefaces.component.accordionpanel;

import org.primefaces.component.api.UIData;
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
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
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
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class AccordionPanel extends UIData implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.AccordionPanel";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.AccordionPanelRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,activeIndex
		,style
		,styleClass
		,onTabChange
		,onTabShow
		,dynamic
		,cache
		,multiple
		,dir;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public AccordionPanel() {
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

	public java.lang.String getActiveIndex() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.activeIndex, "0");
	}
	public void setActiveIndex(java.lang.String _activeIndex) {
		getStateHelper().put(PropertyKeys.activeIndex, _activeIndex);
		handleAttribute("activeIndex", _activeIndex);
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

	public java.lang.String getOnTabChange() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabChange, null);
	}
	public void setOnTabChange(java.lang.String _onTabChange) {
		getStateHelper().put(PropertyKeys.onTabChange, _onTabChange);
		handleAttribute("onTabChange", _onTabChange);
	}

	public java.lang.String getOnTabShow() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabShow, null);
	}
	public void setOnTabShow(java.lang.String _onTabShow) {
		getStateHelper().put(PropertyKeys.onTabShow, _onTabShow);
		handleAttribute("onTabShow", _onTabShow);
	}

	public boolean isDynamic() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
	}
	public void setDynamic(boolean _dynamic) {
		getStateHelper().put(PropertyKeys.dynamic, _dynamic);
		handleAttribute("dynamic", _dynamic);
	}

	public boolean isCache() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
	}
	public void setCache(boolean _cache) {
		getStateHelper().put(PropertyKeys.cache, _cache);
		handleAttribute("cache", _cache);
	}

	public boolean isMultiple() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
	}
	public void setMultiple(boolean _multiple) {
		getStateHelper().put(PropertyKeys.multiple, _multiple);
		handleAttribute("multiple", _multiple);
	}

	public java.lang.String getDir() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
	}
	public void setDir(java.lang.String _dir) {
		getStateHelper().put(PropertyKeys.dir, _dir);
		handleAttribute("dir", _dir);
	}


    private final static String DEFAULT_EVENT = "tabChange";

    public final static String CONTAINER_CLASS = "ui-accordion ui-widget ui-helper-reset ui-hidden-container";
    public final static String ACTIVE_TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-state-active ui-corner-top";
    public final static String TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-corner-all";
    public final static String TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public final static String TAB_HEADER_ICON_RTL_CLASS = "ui-icon ui-icon-triangle-1-w";
    public final static String ACTIVE_TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public final static String ACTIVE_TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content";
    public final static String INACTIVE_TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content ui-helper-hidden";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

	public Tab findTab(String tabClientId) {
        for(UIComponent component : getChildren()) {
            if(component.getClientId().equals(tabClientId))
                return (Tab) component;
        }

        return null;
    }

    List<Tab> loadedTabs;
    public List<Tab> getLoadedTabs() {
        if(loadedTabs == null) {
            loadedTabs = new ArrayList<Tab>();

            for(UIComponent component : getChildren()) {
                if(component instanceof Tab) {
                    Tab tab =  (Tab) component;
                    
                    if(tab.isLoaded())
                        loadedTabs.add(tab);
                }
            }
        }

        return loadedTabs;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("tabChange")) {
                String tabClientId = params.get(clientId + "_newTab");
                TabChangeEvent changeEvent = new TabChangeEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(this.getVar() != null) {
                    int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setRowIndex(index);
                    changeEvent.setData(this.getRowData());
                    changeEvent.setTab((Tab) getChildren().get(0));
                    setRowIndex(-1);
                }

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(changeEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        if(isDynamic()) {
            if(this.getVar() == null) {
                for(Tab tab : getLoadedTabs()) {
                    tab.processDecodes(context);
                }
                this.decode(context);
            }
            else {
                super.processDecodes(context);
            }
        }
        else {
            if(this.getVar() == null) {
                Iterator kids = getFacetsAndChildren();
                while (kids.hasNext()) {
                    UIComponent kid = (UIComponent) kids.next();
                    kid.processDecodes(context);
                }
                this.decode(context);
            }
            else {
                super.processDecodes(context);
            }
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        //only process loaded tabs on dynamic case
        if(isDynamic()) {
            if(this.getVar() == null) {
                for(Tab tab : getLoadedTabs()) {
                    tab.processValidators(context);
                }
            } 
            else {
                super.processValidators(context);
            }
        }
        else {
            if(this.getVar() == null) {
                Iterator kids = getFacetsAndChildren();
                while (kids.hasNext()) {
                    UIComponent kid = (UIComponent) kids.next();
                    kid.processValidators(context);
                }
            }
            else {
                super.processValidators(context);
            }
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        ValueExpression expr = this.getValueExpression("activeIndex");
        if(expr != null) {
            expr.setValue(getFacesContext().getELContext(), getActiveIndex());
            resetActiveIndex();
        }

        //only process loaded tabs on dynamic case
        if(isDynamic()) {
            if(this.getVar() == null) {
                for(Tab tab : getLoadedTabs()) {
                    tab.processUpdates(context);
                }
            }
            else {
                super.processUpdates(context);
            }
        }
        else {
            if(this.getVar() == null) {
                Iterator kids = getFacetsAndChildren();
                while (kids.hasNext()) {
                    UIComponent kid = (UIComponent) kids.next();
                    kid.processUpdates(context);
                }
            }
            else {
                super.processUpdates(context);
            }  
        }
    }

    protected void resetActiveIndex() {
		getStateHelper().remove(PropertyKeys.activeIndex);
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public boolean visitTree(VisitContext context,  VisitCallback callback) {
    
        if(this.getVar() == null) {
            if (!isVisitable(context))
                return false;

            FacesContext facesContext = context.getFacesContext();
            pushComponentToEL(facesContext, null);

            try {
                VisitResult result = context.invokeVisitCallback(this, callback);

                if (result == VisitResult.COMPLETE)
                  return true;

                if (result == VisitResult.ACCEPT) {
                    Iterator<UIComponent> kids = this.getFacetsAndChildren();

                    while(kids.hasNext()) {
                        boolean done = kids.next().visitTree(context, callback);

                        if (done)
                            return true;
                    }
                }
            }
            finally {
                popComponentFromEL(facesContext);
            }

            return false;
        }
        else {
            return super.visitTree(context, callback);
        }
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