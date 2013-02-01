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
package org.primefaces.component.mindmap;

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
import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.event.SelectEvent;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.util.Constants;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="raphael/raphael.js"),
	@ResourceDependency(library="primefaces", name="mindmap/mindmap.js")
})
public class Mindmap extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.Mindmap";
	public static final String COMPONENT_FAMILY = "org.primefaces";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.MindmapRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,value
		,style
		,styleClass
		,effectSpeed;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Mindmap() {
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

	public org.primefaces.model.mindmap.MindmapNode getValue() {
		return (org.primefaces.model.mindmap.MindmapNode) getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(org.primefaces.model.mindmap.MindmapNode _value) {
		getStateHelper().put(PropertyKeys.value, _value);
		handleAttribute("value", _value);
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

	public int getEffectSpeed() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectSpeed, 300);
	}
	public void setEffectSpeed(int _effectSpeed) {
		getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
		handleAttribute("effectSpeed", _effectSpeed);
	}


    public final static String STYLE_CLASS = "ui-mindmap ui-widget ui-widget-content ui-corner-all";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select", "dblselect"));

    private MindmapNode selectedNode = null;

    public MindmapNode getSelectedNode() {
        return selectedNode;
    }

    public String getSelectedNodeKey(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().get(this.getClientId(context) + "_nodeKey");
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = this.getClientId(context);
        AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName.equals("select")||eventName.equals("dblselect")) {
            String nodeKey = params.get(clientId + "_nodeKey");
            MindmapNode node = nodeKey.equals("root") ? this.getValue() : this.findNode(this.getValue(), nodeKey);
            this.selectedNode = node;
        
            super.queueEvent(new SelectEvent(this, behaviorEvent.getBehavior(), node));
        }
    }

    protected MindmapNode findNode(MindmapNode searchRoot, String rowKey) {
		String[] paths = rowKey.split("_");
		
		if(paths.length == 0)
			return null;
		
		int childIndex = Integer.parseInt(paths[0]);
		searchRoot = searchRoot.getChildren().get(childIndex);

		if(paths.length == 1) {
			return searchRoot;
		} 
		else {
			String relativeRowKey = rowKey.substring(rowKey.indexOf("_") + 1);
				
			return findNode(searchRoot, relativeRowKey);
		}
	}

    public boolean isNodeSelectRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_nodeKey");
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