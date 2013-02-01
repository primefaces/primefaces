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
package org.primefaces.component.resizable;

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
import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.ResizeEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class Resizable extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Resizable";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.ResizableRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,forValue("for")
		,aspectRatio
		,proxy
		,handles
		,ghost
		,animate
		,effect
		,effectDuration
		,maxWidth
		,maxHeight
		,minWidth
		,minHeight
		,containment
		,grid
		,onStart
		,onResize
		,onStop;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Resizable() {
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

	public java.lang.String getFor() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
	}
	public void setFor(java.lang.String _for) {
		getStateHelper().put(PropertyKeys.forValue, _for);
		handleAttribute("forValue", _for);
	}

	public boolean isAspectRatio() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.aspectRatio, false);
	}
	public void setAspectRatio(boolean _aspectRatio) {
		getStateHelper().put(PropertyKeys.aspectRatio, _aspectRatio);
		handleAttribute("aspectRatio", _aspectRatio);
	}

	public boolean isProxy() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.proxy, false);
	}
	public void setProxy(boolean _proxy) {
		getStateHelper().put(PropertyKeys.proxy, _proxy);
		handleAttribute("proxy", _proxy);
	}

	public java.lang.String getHandles() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.handles, null);
	}
	public void setHandles(java.lang.String _handles) {
		getStateHelper().put(PropertyKeys.handles, _handles);
		handleAttribute("handles", _handles);
	}

	public boolean isGhost() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ghost, false);
	}
	public void setGhost(boolean _ghost) {
		getStateHelper().put(PropertyKeys.ghost, _ghost);
		handleAttribute("ghost", _ghost);
	}

	public boolean isAnimate() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, false);
	}
	public void setAnimate(boolean _animate) {
		getStateHelper().put(PropertyKeys.animate, _animate);
		handleAttribute("animate", _animate);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "swing");
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

	public int getMaxWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxWidth, Integer.MAX_VALUE);
	}
	public void setMaxWidth(int _maxWidth) {
		getStateHelper().put(PropertyKeys.maxWidth, _maxWidth);
		handleAttribute("maxWidth", _maxWidth);
	}

	public int getMaxHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxHeight, Integer.MAX_VALUE);
	}
	public void setMaxHeight(int _maxHeight) {
		getStateHelper().put(PropertyKeys.maxHeight, _maxHeight);
		handleAttribute("maxHeight", _maxHeight);
	}

	public int getMinWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minWidth, Integer.MIN_VALUE);
	}
	public void setMinWidth(int _minWidth) {
		getStateHelper().put(PropertyKeys.minWidth, _minWidth);
		handleAttribute("minWidth", _minWidth);
	}

	public int getMinHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minHeight, Integer.MIN_VALUE);
	}
	public void setMinHeight(int _minHeight) {
		getStateHelper().put(PropertyKeys.minHeight, _minHeight);
		handleAttribute("minHeight", _minHeight);
	}

	public boolean isContainment() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.containment, false);
	}
	public void setContainment(boolean _containment) {
		getStateHelper().put(PropertyKeys.containment, _containment);
		handleAttribute("containment", _containment);
	}

	public int getGrid() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.grid, 1);
	}
	public void setGrid(int _grid) {
		getStateHelper().put(PropertyKeys.grid, _grid);
		handleAttribute("grid", _grid);
	}

	public java.lang.String getOnStart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onStart, null);
	}
	public void setOnStart(java.lang.String _onStart) {
		getStateHelper().put(PropertyKeys.onStart, _onStart);
		handleAttribute("onStart", _onStart);
	}

	public java.lang.String getOnResize() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onResize, null);
	}
	public void setOnResize(java.lang.String _onResize) {
		getStateHelper().put(PropertyKeys.onResize, _onResize);
		handleAttribute("onResize", _onResize);
	}

	public java.lang.String getOnStop() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onStop, null);
	}
	public void setOnStop(java.lang.String _onStop) {
		getStateHelper().put(PropertyKeys.onStop, _onStop);
		handleAttribute("onStop", _onStop);
	}


        private final static String DEFAULT_EVENT = "resize";

        private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

        @Override
        public Collection<String> getEventNames() {
            return EVENT_NAMES;
        }

        @Override
        public String getDefaultEventName() {
            return DEFAULT_EVENT;
        }

        @Override
        public void queueEvent(FacesEvent event) {
            FacesContext context = FacesContext.getCurrentInstance();
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            if(isRequestSource(context)) {
                String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
                String clientId = getClientId(context);
                
                if(eventName.equals("resize")) {
                    AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                    int width = Integer.parseInt(params.get(clientId + "_width"));
                    int height = Integer.parseInt(params.get(clientId + "_height"));

                    super.queueEvent(new ResizeEvent(this, behaviorEvent.getBehavior(), width, height));
                }
                
            } else {
                super.queueEvent(event);
            }
        }

        private boolean isRequestSource(FacesContext context) {
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