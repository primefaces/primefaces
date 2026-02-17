/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.picklist;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = PickList.COMPONENT_TYPE, namespace = PickList.COMPONENT_FAMILY)
@FacesComponentInfo(description = "PickList is a component to transfer items between two lists.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class PickList extends PickListBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.PickList";

    public static final String CONTAINER_CLASS = "ui-picklist ui-widget ui-helper-clearfix";
    public static final String LIST_CLASS = "ui-widget-content ui-picklist-list";
    public static final String LIST_WRAPPER_CLASS = "ui-picklist-list-wrapper";
    public static final String SOURCE_CLASS = LIST_CLASS + " ui-picklist-source";
    public static final String TARGET_CLASS = LIST_CLASS + " ui-picklist-target";
    public static final String BUTTONS_CLASS = "ui-picklist-buttons";
    public static final String BUTTONS_CELL_CLASS = "ui-picklist-buttons-cell";
    public static final String SOURCE_CONTROLS = "ui-picklist-source-controls ui-picklist-buttons";
    public static final String TARGET_CONTROLS = "ui-picklist-target-controls ui-picklist-buttons";
    public static final String ITEM_CLASS = "ui-picklist-item";
    public static final String ITEM_DISABLED_CLASS = "ui-state-disabled";
    public static final String CAPTION_CLASS = "ui-picklist-caption ui-widget-header";
    public static final String ADD_BUTTON_CLASS = "ui-picklist-button-add";
    public static final String ADD_ALL_BUTTON_CLASS = "ui-picklist-button-add-all";
    public static final String REMOVE_BUTTON_CLASS = "ui-picklist-button-remove";
    public static final String REMOVE_ALL_BUTTON_CLASS = "ui-picklist-button-remove-all";
    public static final String ADD_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-e";
    public static final String ADD_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-e";
    public static final String REMOVE_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-w";
    public static final String REMOVE_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-w";
    public static final String VERTICAL_ADD_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String VERTICAL_ADD_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";
    public static final String VERTICAL_REMOVE_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String VERTICAL_REMOVE_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_UP_BUTTON_CLASS = "ui-picklist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = "ui-picklist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = "ui-picklist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = "ui-picklist-button-move-bottom";
    public static final String MOVE_UP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String MOVE_DOWN_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String MOVE_TOP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_BOTTOM_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";
    public static final String FILTER_CLASS = "ui-picklist-filter ui-inputfield ui-inputtext ui-widget ui-state-default";
    public static final String FILTER_CONTAINER = "ui-picklist-filter-container";

    private Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>(1);

    @Override
    protected void validateValue(FacesContext facesContext, Object newValue) {
        super.validateValue(facesContext, newValue);

        DualListModel<?> newModel = (DualListModel<?>) newValue;
        DualListModel<?> oldModel = (DualListModel<?>) getValue();

        String clientId = getClientId(facesContext);
        String label = getLabel();
        if (label == null) {
            label = clientId;
        }

        if (isRequired() && newModel.getTarget().isEmpty()) {
            String requiredMessage = getRequiredMessage();
            FacesMessage message = null;

            if (requiredMessage != null) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
            }
            else {
                message = MessageFactory.getFacesMessage(facesContext, REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, label);
            }
            facesContext.addMessage(clientId, message);
            setValid(false);
        }

        if (isValid()) {
            List<?> oldModelSource = oldModel == null ? Collections.emptyList() : oldModel.getSource();
            List<?> oldModelTarget = oldModel == null ? Collections.emptyList() : oldModel.getTarget();

            validateTarget(facesContext, label, newModel.getTarget(), oldModelSource, oldModelTarget);
            validateDisabled(facesContext, label, newModel.getSource(), oldModelSource);
            validateDisabled(facesContext, label, newModel.getTarget(), oldModelTarget);
        }
    }

    /**
     * Validates the target entries against the source entries. If a target entry is not found in the source entries,
     * an error message is added to the FacesContext and the component is marked as invalid.
     *
     * @param facesContext The current FacesContext.
     * @param label The label to be used in the error message.
     * @param targetEntries The list of target entries to be validated.
     * @param oldSource The original list of source entries to validate against.
     * @param oldTarget The original list of target entries to validate against.
     * @see <a href="https://github.com/primefaces/primefaces/issues/12059">GitHub #12059</a>
     */
    private void validateTarget(FacesContext facesContext, String label, List<?> targetEntries, List<?> oldSource, List<?> oldTarget) {
        String clientId = getClientId(facesContext);

        for (int i = 0; i < targetEntries.size(); i++) {
            Object targetItem = targetEntries.get(i);
            // Check if target item exists in source list
            if (!oldSource.contains(targetItem) && !oldTarget.contains(targetItem)) {
                FacesMessage message = MessageFactory.getFacesMessage(facesContext, UPDATE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, label);
                facesContext.addMessage(clientId, message);
                setValid(false);
                break;
            }
        }
    }

    /**
     * Prohibits client-side manipulation of disabled entries, when CSS style-class ui-state-disabled is removed.
     *
     * @param facesContext The current FacesContext.
     * @param label The label to be used in the error message.
     * @param newEntries new/set entries of model source/target list
     * @param oldEntries old/former entries of model source/target list
     * @see <a href="https://github.com/primefaces/primefaces/issues/2127">GitHub #2127</a>
     */
    protected void validateDisabled(FacesContext facesContext, String label, List<?> newEntries, List<?> oldEntries) {
        if (!isValid()) {
            return;
        }

        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
        String varName = getVar();
        String clientId = getClientId(facesContext);
        Object originalItem = requestMap.get(varName);

        for (int i = 0; i < newEntries.size(); i++) {
            Object item = newEntries.get(i);
            // Set the current item in request map to get its properties via stateHelper().eval() call
            requestMap.put(varName, item);
            boolean itemDisabled = isItemDisabled();
            // Check if disabled item has been moved from its former/original list
            if (itemDisabled && !oldEntries.contains(item)) {
                FacesMessage message = MessageFactory.getFacesMessage(facesContext, UPDATE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, label);
                facesContext.addMessage(clientId, message);
                setValid(false);
                break;
            }
        }

        // put the original value back
        requestMap.put(varName, originalItem);
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);
        if (isValid() && customEvents != null) {
            for (Map.Entry<String, AjaxBehaviorEvent> event : customEvents.entrySet()) {
                String eventName = event.getKey();
                AjaxBehaviorEvent behaviorEvent = event.getValue();

                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = getClientId(context);
                DualListModel<?> list = (DualListModel<?>) getValue();
                FacesEvent wrapperEvent = null;

                if ("select".equals(eventName)) {
                    String listName = params.get(clientId + "_listName");
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

                    if ("target".equals(listName)) {
                        wrapperEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), list.getTarget().get(itemIndex));
                    }
                    else {
                        wrapperEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), list.getSource().get(itemIndex));
                    }
                }
                else if ("unselect".equals(eventName)) {
                    String listName = params.get(clientId + "_listName");
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

                    if ("target".equals(listName)) {
                        wrapperEvent = new UnselectEvent<>(this, behaviorEvent.getBehavior(), list.getTarget().get(itemIndex));
                    }
                    else {
                        wrapperEvent = new UnselectEvent<>(this, behaviorEvent.getBehavior(), list.getSource().get(itemIndex));
                    }
                }
                else if ("reorder".equals(eventName)) {
                    wrapperEvent = behaviorEvent;
                }

                if (wrapperEvent == null) {
                    throw new FacesException("Component " + this.getClass().getName() + " does not support event " + eventName + "!");
                }

                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(wrapperEvent);
            }
        }
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String[]> paramValues = context.getExternalContext().getRequestParameterValuesMap();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();

            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if ("transfer".equals(eventName)) {
                String[] items = paramValues.get(clientId + "_transferred");
                boolean isAdd = Boolean.parseBoolean(params.get(clientId + "_add"));
                List transferredItems = new ArrayList();
                populateModel(context, items, transferredItems);
                TransferEvent transferEvent = new TransferEvent(this, behaviorEvent.getBehavior(), transferredItems, isAdd);
                transferEvent.setPhaseId(event.getPhaseId());

                super.queueEvent(transferEvent);
            }
            else if ("select".equals(eventName) || "unselect".equals(eventName) || "reorder".equals(eventName)) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @SuppressWarnings("unchecked")
    public void populateModel(FacesContext context, String[] values, List model) {
        Converter<?> converter = getConverter();

        if (values != null) {
            for (String item : values) {
                if (LangUtils.isBlank(item)) {
                    continue;
                }

                Object convertedValue = converter != null ? converter.getAsObject(context, this, item) : item;

                if (convertedValue != null) {
                    model.add(convertedValue);
                }
            }
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        if (customEvents != null) {
            customEvents.clear();
        }

        return super.saveState(context);
    }
}