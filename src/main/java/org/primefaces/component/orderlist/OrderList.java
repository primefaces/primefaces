/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.*;
import javax.faces.FacesException;

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

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
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

    private Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>(1);

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

        if (isValid() && customEvents != null) {
            for (Map.Entry<String, AjaxBehaviorEvent> event : customEvents.entrySet()) {
                String eventName = event.getKey();
                AjaxBehaviorEvent behaviorEvent = event.getValue();

                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = getClientId(context);
                List<?> list = (List) getValue();
                FacesEvent wrapperEvent = null;

                if ("select".equals(eventName)) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    boolean metaKey = Boolean.parseBoolean(params.get(clientId + "_metaKey"));
                    boolean ctrlKey = Boolean.parseBoolean(params.get(clientId + "_ctrlKey"));
                    wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.get(itemIndex), metaKey, ctrlKey);
                }
                else if ("unselect".equals(eventName)) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.get(itemIndex));
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
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        if (customEvents != null) {
            customEvents.clear();
        }

        return super.saveState(context);
    }
}