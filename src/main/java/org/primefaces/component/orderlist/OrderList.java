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
package org.primefaces.component.orderlist;

import java.util.*;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class OrderList extends OrderListBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.OrderList";

    public static final String CONTAINER_CLASS = "ui-orderlist ui-grid ui-widget";
    public static final String LIST_CLASS = "ui-widget-content ui-orderlist-list";
    public static final String CONTROLS_CLASS = "ui-orderlist-controls ui-g-12 ui-md-2";
    public static final String CAPTION_CLASS = "ui-orderlist-caption ui-widget-header ui-corner-top";
    public static final String ITEM_CLASS = "ui-orderlist-item ui-corner-all";
    public static final String MOVE_UP_BUTTON_CLASS = "ui-orderlist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = "ui-orderlist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = "ui-orderlist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = "ui-orderlist-button-move-bottom";
    public static final String MOVE_UP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String MOVE_DOWN_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String MOVE_TOP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_BOTTOM_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("select", SelectEvent.class)
            .put("unselect", UnselectEvent.class)
            .put("reorder", null)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }


    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && (event instanceof AjaxBehaviorEvent)) {
            String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            customEvents.put(eventName, (AjaxBehaviorEvent) event);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if (isValid()) {
            for (Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext(); ) {
                String eventName = customEventIter.next();
                AjaxBehaviorEvent behaviorEvent = customEvents.get(eventName);
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = getClientId(context);
                List<?> list = (List) getValue();
                FacesEvent wrapperEvent = null;

                if (eventName.equals("select")) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    boolean metaKey = Boolean.parseBoolean(params.get(clientId + "_metaKey"));
                    boolean ctrlKey = Boolean.parseBoolean(params.get(clientId + "_ctrlKey"));
                    wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.get(itemIndex), metaKey, ctrlKey);
                }
                else if (eventName.equals("unselect")) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.get(itemIndex));
                }
                else if (eventName.equals("reorder")) {
                    wrapperEvent = behaviorEvent;
                }

                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(wrapperEvent);
            }
        }
    }

}