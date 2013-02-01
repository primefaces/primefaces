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
package org.primefaces.component.datalist;

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
import javax.faces.FacesException;
import javax.faces.event.PhaseId;
import javax.faces.component.UIColumn;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class DataList extends UIData implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.DataList";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.DataListRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,type
		,itemType
		,style
		,styleClass
		,varStatus;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public DataList() {
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

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, "unordered");
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
		handleAttribute("type", _type);
	}

	public java.lang.String getItemType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemType, null);
	}
	public void setItemType(java.lang.String _itemType) {
		getStateHelper().put(PropertyKeys.itemType, _itemType);
		handleAttribute("itemType", _itemType);
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

	public java.lang.String getVarStatus() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.varStatus, null);
	}
	public void setVarStatus(java.lang.String _varStatus) {
		getStateHelper().put(PropertyKeys.varStatus, _varStatus);
		handleAttribute("varStatus", _varStatus);
	}


	public static final String DATALIST_CLASS = "ui-datalist ui-widget";
    public static final String CONTENT_CLASS = "ui-datalist-content ui-widget-content";
	public static final String LIST_CLASS = "ui-datalist-data";
	public static final String LIST_ITEM_CLASS = "ui-datalist-item";
    public static final String HEADER_CLASS = "ui-datalist-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datalist-footer ui-widget-header ui-corner-top";
	
	public String getListTag() {
		String type = getType();
		
		if(type.equalsIgnoreCase("unordered"))
			return "ul";
		else if(type.equalsIgnoreCase("ordered"))
			return "ol";
		else if(type.equalsIgnoreCase("definition"))
			return "dl";
        else if(type.equalsIgnoreCase("none"))
            return null;
        else
			throw new FacesException("DataList '" + this.getClientId() + "' has invalid list type:'" + type + "'");
	}
	
	public boolean isDefinition() {
		return getType().equalsIgnoreCase("definition");
	}

    @Override
    public void processDecodes(FacesContext context) {
        if(!isRendered()) {
            return;
        }

		if(getVar() == null) {
            pushComponentToEL(context, this);
            iterateChildren(context, PhaseId.APPLY_REQUEST_VALUES);
            decode(context);
            popComponentFromEL(context);
        } 
        else {
            super.processDecodes(context);
        }
	}

    @Override
    public void processValidators(FacesContext context) {
        if(!isRendered()) {
            return;
        }

		if(getVar() == null) {
            pushComponentToEL(context, this);
            iterateChildren(context, PhaseId.PROCESS_VALIDATIONS);
            popComponentFromEL(context);
        } 
        else {
            super.processValidators(context);
        }
	}
    
    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }

		if(getVar() == null) {
            pushComponentToEL(context, this);
            iterateChildren(context, PhaseId.UPDATE_MODEL_VALUES);
            popComponentFromEL(context);
        } 
        else {
            super.processUpdates(context);
        }
	}

    protected void iterateChildren(FacesContext context, PhaseId phaseId) {
        setRowIndex(-1);
        if(getChildCount() > 0) {
            for(UIComponent kid : this.getChildren()) {
                if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                    kid.processDecodes(context);
                else if (phaseId == PhaseId.PROCESS_VALIDATIONS)
                    kid.processValidators(context);
                else if (phaseId == PhaseId.UPDATE_MODEL_VALUES)
                    kid.processUpdates(context);
                else
                    throw new IllegalArgumentException();
            }
        }
    }

    public void queueEvent(FacesEvent event) {
        if(getVar() != null) {
            super.queueEvent(event);
        }
        else {
            if(event == null) {
                throw new NullPointerException();
            }
            
            UIComponent parent = getParent();
            if(parent == null)
                throw new IllegalStateException();
            else
                parent.queueEvent(event);
        }
    }


    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if(getVar() != null) {
            super.broadcast(event);
        }
        else {

            if(event == null) {
                throw new NullPointerException();
            }
            if(event instanceof BehaviorEvent) {
                BehaviorEvent behaviorEvent = (BehaviorEvent) event;
                Behavior behavior = behaviorEvent.getBehavior();
                behavior.broadcast(behaviorEvent);
            }
        }
    }

    public void loadLazyData() {
        DataModel model = getDataModel();
        
        if(model != null && model instanceof LazyDataModel) {            
            LazyDataModel lazyModel = (LazyDataModel) model;

            List<?> data = lazyModel.load(getFirst(), getRows(), null, null, null);
            
            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator for callback
            if(this.isPaginator()) {
                RequestContext requestContext = RequestContext.getCurrentInstance();

                if(requestContext != null) {
                    requestContext.addCallbackParam("totalRecords", lazyModel.getRowCount());
                }
            }
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