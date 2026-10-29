/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.orderlist;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = OrderList.COMPONENT_TYPE, namespace = OrderList.COMPONENT_FAMILY)
@FacesComponentInfo(description = "OrderList is used to reorder a list via drag and drop.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class OrderList extends OrderListBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.OrderList";

    public static final String CONTAINER_CLASS = "p-orderlist p-widget";
    public static final String LIST_CLASS = "p-widget-content p-orderlist-list";
    public static final String CONTROLS_CLASS = "p-orderlist-controls";
    public static final String CAPTION_CLASS = "p-orderlist-caption p-widget-header";
    public static final String ITEM_CLASS = "p-orderlist-item";
    public static final String MOVE_UP_BUTTON_CLASS = "p-orderlist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = "p-orderlist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = "p-orderlist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = "p-orderlist-button-move-bottom";
    public static final String MOVE_UP_BUTTON_ICON_CLASS = "p-icon p-up-icon p-orderlist-move-up-icon";
    public static final String MOVE_DOWN_BUTTON_ICON_CLASS = "p-icon p-down-icon p-orderlist-move-down-icon";
    public static final String MOVE_TOP_BUTTON_ICON_CLASS = "p-icon p-top-icon p-orderlist-move-top-icon";
    public static final String MOVE_BOTTOM_BUTTON_ICON_CLASS = "p-icon p-bottom-icon p-orderlist-move-bottom-icon";
    public static final String CONTROLS_RIGHT_CLASS = "p-orderlist-controls-right";

    private Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>(1);

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && isAjaxBehaviorEventSource(event)) {
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

        if (isValid() && customEvents != null) {
            for (Map.Entry<String, AjaxBehaviorEvent> event : customEvents.entrySet()) {
                AjaxBehaviorEvent behaviorEvent = event.getValue();

                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = getClientId(context);
                List<?> list = (List) getValue();
                FacesEvent wrapperEvent = null;

                if (isAjaxBehaviorEvent(behaviorEvent, ClientBehaviorEventKeys.select)) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    boolean metaKey = Boolean.parseBoolean(params.get(clientId + "_metaKey"));
                    boolean ctrlKey = Boolean.parseBoolean(params.get(clientId + "_ctrlKey"));
                    wrapperEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), list.get(itemIndex), metaKey, ctrlKey);
                }
                else if (isAjaxBehaviorEvent(behaviorEvent, ClientBehaviorEventKeys.unselect)) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    wrapperEvent = new UnselectEvent<>(this, behaviorEvent.getBehavior(), list.get(itemIndex));
                }
                else if (isAjaxBehaviorEvent(behaviorEvent, ClientBehaviorEventKeys.reorder)) {
                    wrapperEvent = behaviorEvent;
                }

                if (wrapperEvent == null) {
                    String eventName = event.getKey();
                    throw new FacesException("Component " + this.getClass().getName() + " does not support event " + eventName + "!");
                }

                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(wrapperEvent);
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