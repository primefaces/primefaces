/**
 * Copyright 2009-2018 PrimeTek.
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

import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;
import org.primefaces.util.MessageFactory;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class PickList extends PickListBase {

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
    public static final String FILTER_CLASS = "ui-picklist-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String FILTER_CONTAINER = "ui-picklist-filter-container";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("transfer", TransferEvent.class)
            .put("select", SelectEvent.class)
            .put("unselect", UnselectEvent.class)
            .put("reorder", null)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private final Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    protected void validateValue(FacesContext facesContext, Object newValue) {
        super.validateValue(facesContext, newValue);

        DualListModel model = (DualListModel) newValue;
        if (isRequired() && model.getTarget().isEmpty()) {
            String requiredMessage = getRequiredMessage();
            FacesMessage message = null;

            if (requiredMessage != null) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
            }
            else {
                String label = getLabel();
                if (label == null) {
                    label = getClientId(facesContext);
                }

                message = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{label});

            }

            facesContext.addMessage(getClientId(facesContext), message);
            setValid(false);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);
        if (isValid() && customEvents != null) {
            for (Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext(); ) {
                String eventName = customEventIter.next();
                AjaxBehaviorEvent behaviorEvent = customEvents.get(eventName);
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = getClientId(context);
                DualListModel<?> list = (DualListModel<?>) getValue();
                FacesEvent wrapperEvent = null;

                if (eventName.equals("select")) {
                    String listName = params.get(clientId + "_listName");
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

                    if (listName.equals("target")) {
                        wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.getTarget().get(itemIndex));
                    }
                    else {
                        wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.getSource().get(itemIndex));
                    }
                }
                else if (eventName.equals("unselect")) {
                    String listName = params.get(clientId + "_listName");
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

                    if (listName.equals("target")) {
                        wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.getTarget().get(itemIndex));
                    }
                    else {
                        wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.getSource().get(itemIndex));
                    }
                }
                else if (eventName.equals("reorder")) {
                    wrapperEvent = behaviorEvent;
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

            if (eventName.equals("transfer")) {
                String[] items = paramValues.get(clientId + "_transferred");
                boolean isAdd = Boolean.valueOf(params.get(clientId + "_add"));
                List transferredItems = new ArrayList();
                populateModel(context, items, transferredItems);
                TransferEvent transferEvent = new TransferEvent(this, behaviorEvent.getBehavior(), transferredItems, isAdd);
                transferEvent.setPhaseId(event.getPhaseId());

                super.queueEvent(transferEvent);
            }
            else if (eventName.equals("select")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
            else if (eventName.equals("unselect")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
            else if (eventName.equals("reorder")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @SuppressWarnings("unchecked")
    public void populateModel(FacesContext context, String[] values, List model) {
        Converter converter = getConverter();

        if (values != null) {
            for (String item : values) {
                if (item == null || item.trim().equals("")) {
                    continue;
                }

                Object convertedValue = converter != null ? converter.getAsObject(context, this, item) : item;

                if (convertedValue != null) {
                    model.add(convertedValue);
                }
            }
        }
    }
}