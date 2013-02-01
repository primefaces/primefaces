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
package org.primefaces.component.tabview;

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
import org.primefaces.event.TabCloseEvent;
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
public class TabView extends UIData implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.TabView";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.TabViewRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,activeIndex
		,effect
		,effectDuration
		,dynamic
		,cache
		,onTabChange
		,onTabShow
		,style
		,styleClass
		,orientation
		,onTabClose
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

	public TabView() {
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

	public int getActiveIndex() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.activeIndex, 0);
	}
	public void setActiveIndex(int _activeIndex) {
		getStateHelper().put(PropertyKeys.activeIndex, _activeIndex);
		handleAttribute("activeIndex", _activeIndex);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public java.lang.String getEffectDuration() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effectDuration, "normal");
	}
	public void setEffectDuration(java.lang.String _effectDuration) {
		getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
		handleAttribute("effectDuration", _effectDuration);
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

	public java.lang.String getOrientation() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.orientation, "top");
	}
	public void setOrientation(java.lang.String _orientation) {
		getStateHelper().put(PropertyKeys.orientation, _orientation);
		handleAttribute("orientation", _orientation);
	}

	public java.lang.String getOnTabClose() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabClose, null);
	}
	public void setOnTabClose(java.lang.String _onTabClose) {
		getStateHelper().put(PropertyKeys.onTabClose, _onTabClose);
		handleAttribute("onTabClose", _onTabClose);
	}

	public java.lang.String getDir() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
	}
	public void setDir(java.lang.String _dir) {
		getStateHelper().put(PropertyKeys.dir, _dir);
		handleAttribute("dir", _dir);
	}


    public static final String CONTAINER_CLASS = "ui-tabs ui-widget ui-widget-content ui-corner-all ui-hidden-container";
    public static final String NAVIGATOR_CLASS = "ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all";
    public static final String INACTIVE_TAB_HEADER_CLASS = "ui-state-default";
    public static final String ACTIVE_TAB_HEADER_CLASS = "ui-state-default ui-tabs-selected ui-state-active";
    public static final String PANELS_CLASS = "ui-tabs-panels";
    public static final String ACTIVE_TAB_CONTENT_CLASS = "ui-tabs-panel ui-widget-content ui-corner-bottom";
    public static final String INACTIVE_TAB_CONTENT_CLASS = "ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("tabChange","tabClose"));

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
                    int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setRowIndex(tabindex);
                    changeEvent.setData(this.getRowData());
                    changeEvent.setTab((Tab) getChildren().get(0));
                    setRowIndex(-1);
                }

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(changeEvent);
            }
            else if(eventName.equals("tabClose")) {
                String tabClientId = params.get(clientId + "_closeTab");
                TabCloseEvent closeEvent = new TabCloseEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(this.getVar() != null) {
                    int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setRowIndex(tabindex);
                    closeEvent.setData(this.getRowData());
                    closeEvent.setTab((Tab) getChildren().get(0));
                    setRowIndex(-1);
                }

                closeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(closeEvent);
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

        //only process loaded tabs on dynamic case without tab model
        if(isDynamic() && getVar() == null) {
            for(Tab tab : getLoadedTabs()) {
                tab.processDecodes(context);
            }
            this.decode(context);
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

        //only process loaded tabs on dynamic case without tab model
        if(isDynamic() && getVar() == null) {
            for(Tab tab : getLoadedTabs()) {
                tab.processValidators(context);
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

        //only process loaded tabs on dynamic case without tab model
        if(isDynamic() && getVar() == null) {
            for(Tab tab : getLoadedTabs()) {
                tab.processUpdates(context);
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