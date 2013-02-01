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
package org.primefaces.component.picklist;

import javax.faces.component.UIInput;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.primefaces.model.DualListModel;
import org.primefaces.util.MessageFactory;
import org.primefaces.component.picklist.PickList;
import org.primefaces.event.TransferEvent;
import org.primefaces.util.Constants;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class PickList extends UIInput implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.PickList";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.PickListRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,var
		,itemLabel
		,itemValue
		,style
		,styleClass
		,disabled
		,effect
		,effectSpeed
		,addLabel
		,addAllLabel
		,removeLabel
		,removeAllLabel
		,moveUpLabel
		,moveTopLabel
		,moveDownLabel
		,moveBottomLabel
		,showSourceControls
		,showTargetControls
		,onTransfer
		,label
		,itemDisabled
		,showSourceFilter
		,showTargetFilter
		,filterMatchMode
		,filterFunction;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public PickList() {
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

	public java.lang.String getVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(java.lang.String _var) {
		getStateHelper().put(PropertyKeys.var, _var);
		handleAttribute("var", _var);
	}

	public java.lang.String getItemLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemLabel, null);
	}
	public void setItemLabel(java.lang.String _itemLabel) {
		getStateHelper().put(PropertyKeys.itemLabel, _itemLabel);
		handleAttribute("itemLabel", _itemLabel);
	}

	public java.lang.Object getItemValue() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.itemValue, null);
	}
	public void setItemValue(java.lang.Object _itemValue) {
		getStateHelper().put(PropertyKeys.itemValue, _itemValue);
		handleAttribute("itemValue", _itemValue);
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

	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	public void setDisabled(boolean _disabled) {
		getStateHelper().put(PropertyKeys.disabled, _disabled);
		handleAttribute("disabled", _disabled);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fade");
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public java.lang.String getEffectSpeed() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effectSpeed, "fast");
	}
	public void setEffectSpeed(java.lang.String _effectSpeed) {
		getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
		handleAttribute("effectSpeed", _effectSpeed);
	}

	public java.lang.String getAddLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.addLabel, "Add");
	}
	public void setAddLabel(java.lang.String _addLabel) {
		getStateHelper().put(PropertyKeys.addLabel, _addLabel);
		handleAttribute("addLabel", _addLabel);
	}

	public java.lang.String getAddAllLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.addAllLabel, "Add All");
	}
	public void setAddAllLabel(java.lang.String _addAllLabel) {
		getStateHelper().put(PropertyKeys.addAllLabel, _addAllLabel);
		handleAttribute("addAllLabel", _addAllLabel);
	}

	public java.lang.String getRemoveLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.removeLabel, "Remove");
	}
	public void setRemoveLabel(java.lang.String _removeLabel) {
		getStateHelper().put(PropertyKeys.removeLabel, _removeLabel);
		handleAttribute("removeLabel", _removeLabel);
	}

	public java.lang.String getRemoveAllLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.removeAllLabel, "Remove All");
	}
	public void setRemoveAllLabel(java.lang.String _removeAllLabel) {
		getStateHelper().put(PropertyKeys.removeAllLabel, _removeAllLabel);
		handleAttribute("removeAllLabel", _removeAllLabel);
	}

	public java.lang.String getMoveUpLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.moveUpLabel, "Move Up");
	}
	public void setMoveUpLabel(java.lang.String _moveUpLabel) {
		getStateHelper().put(PropertyKeys.moveUpLabel, _moveUpLabel);
		handleAttribute("moveUpLabel", _moveUpLabel);
	}

	public java.lang.String getMoveTopLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.moveTopLabel, "Move Top");
	}
	public void setMoveTopLabel(java.lang.String _moveTopLabel) {
		getStateHelper().put(PropertyKeys.moveTopLabel, _moveTopLabel);
		handleAttribute("moveTopLabel", _moveTopLabel);
	}

	public java.lang.String getMoveDownLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.moveDownLabel, "Move Down");
	}
	public void setMoveDownLabel(java.lang.String _moveDownLabel) {
		getStateHelper().put(PropertyKeys.moveDownLabel, _moveDownLabel);
		handleAttribute("moveDownLabel", _moveDownLabel);
	}

	public java.lang.String getMoveBottomLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.moveBottomLabel, "Move Bottom");
	}
	public void setMoveBottomLabel(java.lang.String _moveBottomLabel) {
		getStateHelper().put(PropertyKeys.moveBottomLabel, _moveBottomLabel);
		handleAttribute("moveBottomLabel", _moveBottomLabel);
	}

	public boolean isShowSourceControls() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showSourceControls, false);
	}
	public void setShowSourceControls(boolean _showSourceControls) {
		getStateHelper().put(PropertyKeys.showSourceControls, _showSourceControls);
		handleAttribute("showSourceControls", _showSourceControls);
	}

	public boolean isShowTargetControls() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showTargetControls, false);
	}
	public void setShowTargetControls(boolean _showTargetControls) {
		getStateHelper().put(PropertyKeys.showTargetControls, _showTargetControls);
		handleAttribute("showTargetControls", _showTargetControls);
	}

	public java.lang.String getOnTransfer() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTransfer, null);
	}
	public void setOnTransfer(java.lang.String _onTransfer) {
		getStateHelper().put(PropertyKeys.onTransfer, _onTransfer);
		handleAttribute("onTransfer", _onTransfer);
	}

	public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
	}
	public void setLabel(java.lang.String _label) {
		getStateHelper().put(PropertyKeys.label, _label);
		handleAttribute("label", _label);
	}

	public boolean isItemDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.itemDisabled, false);
	}
	public void setItemDisabled(boolean _itemDisabled) {
		getStateHelper().put(PropertyKeys.itemDisabled, _itemDisabled);
		handleAttribute("itemDisabled", _itemDisabled);
	}

	public boolean isShowSourceFilter() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showSourceFilter, false);
	}
	public void setShowSourceFilter(boolean _showSourceFilter) {
		getStateHelper().put(PropertyKeys.showSourceFilter, _showSourceFilter);
		handleAttribute("showSourceFilter", _showSourceFilter);
	}

	public boolean isShowTargetFilter() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showTargetFilter, false);
	}
	public void setShowTargetFilter(boolean _showTargetFilter) {
		getStateHelper().put(PropertyKeys.showTargetFilter, _showTargetFilter);
		handleAttribute("showTargetFilter", _showTargetFilter);
	}

	public java.lang.String getFilterMatchMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
	}
	public void setFilterMatchMode(java.lang.String _filterMatchMode) {
		getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
		handleAttribute("filterMatchMode", _filterMatchMode);
	}

	public java.lang.String getFilterFunction() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.filterFunction, null);
	}
	public void setFilterFunction(java.lang.String _filterFunction) {
		getStateHelper().put(PropertyKeys.filterFunction, _filterFunction);
		handleAttribute("filterFunction", _filterFunction);
	}


    public static final String CONTAINER_CLASS = "ui-picklist ui-widget";
    public static final String LIST_CLASS = "ui-widget-content ui-picklist-list";
    public static final String SOURCE_CLASS = LIST_CLASS + " ui-picklist-source";
    public static final String TARGET_CLASS = LIST_CLASS + " ui-picklist-target";
    public static final String SOURCE_CONTROLS = "ui-picklist-source-controls";
    public static final String TARGET_CONTROLS = "ui-picklist-target-controls";
    public static final String ITEM_CLASS = "ui-picklist-item ui-corner-all";
    public static final String ITEM_DISABLED_CLASS = "ui-state-disabled";
    public static final String CAPTION_CLASS = "ui-picklist-caption ui-widget-header ui-corner-tl ui-corner-tr";
    public static final String ADD_BUTTON_CLASS = "ui-picklist-button-add";
    public static final String ADD_ALL_BUTTON_CLASS = "ui-picklist-button-add-all";
    public static final String REMOVE_BUTTON_CLASS = "ui-picklist-button-remove";
    public static final String REMOVE_ALL_BUTTON_CLASS = "ui-picklist-button-remove-all";
    public static final String ADD_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-e";
    public static final String ADD_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-e";
    public static final String REMOVE_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-w";
    public static final String REMOVE_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-w";
    public static final String MOVE_UP_BUTTON_CLASS = "ui-picklist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = "ui-picklist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = "ui-picklist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = "ui-picklist-button-move-bottom";
    public static final String MOVE_UP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String MOVE_DOWN_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String MOVE_TOP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_BOTTOM_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";
    public static final String FILTER_CLASS = "ui-picklist-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String FILTER_CONTAINER = "ui-picklist-filter-container";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("transfer"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

	protected void validateValue(FacesContext facesContext, Object newValue) {
		super.validateValue(facesContext, newValue);
		
		DualListModel model = (DualListModel) newValue;
		if(isRequired() && model.getTarget().isEmpty()) {
			String requiredMessage = getRequiredMessage();
			FacesMessage message = null;
			
			if(requiredMessage != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
            }
            else {
                String label = this.getLabel();
                if(label == null) {
                    label = this.getClientId(facesContext);
                }

	        	message = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{label});

            }

			facesContext.addMessage(getClientId(facesContext), message);
	        setValid(false);
		}
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String[]> paramValues = context.getExternalContext().getRequestParameterValuesMap();
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("transfer")) {
                String[] items = paramValues.get(clientId + "_transferred");
                boolean isAdd = Boolean.valueOf(params.get(clientId + "_add"));
                List transferredItems = new ArrayList();
                this.populateModel(context, items, transferredItems);
                TransferEvent transferEvent = new TransferEvent(this, behaviorEvent.getBehavior(), transferredItems, isAdd);
                transferEvent.setPhaseId(transferEvent.getPhaseId());

                super.queueEvent(transferEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    @SuppressWarnings("unchecked")
	public void populateModel(FacesContext context, String[] values, List model) {
		Converter converter = this.getConverter();

        for(String item : values) {            
			if(item == null || item.trim().equals(""))
				continue;
			                    
			Object convertedValue = converter != null ? converter.getAsObject(context, this, item) : item;
			
			if(convertedValue != null) {
				model.add(convertedValue);
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