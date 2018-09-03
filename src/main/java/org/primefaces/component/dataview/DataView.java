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
package org.primefaces.component.dataview;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.data.PageEvent;
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
public class DataView extends DataViewBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DataView";

    public static final String DATAVIEW_CLASS = "ui-dataview ui-widget";
    public static final String LIST_LAYOUT_CLASS = "ui-dataview-list";
    public static final String GRID_LAYOUT_CLASS = "ui-dataview-grid";
    public static final String HEADER_CLASS = "ui-dataview-header ui-widget-header ui-helper-clearfix ui-corner-top";
    public static final String FOOTER_CLASS = "ui-dataview-footer ui-widget-header ui-corner-bottom";
    public static final String CONTENT_CLASS = "ui-dataview-content ui-widget-content";
    public static final String BUTTON_CONTAINER_CLASS = "ui-dataview-layout-options ui-selectonebutton ui-buttonset";
    public static final String BUTTON_CLASS = "ui-button ui-button-icon-only ui-state-default";
    public static final String LIST_LAYOUT_CONTAINER_CLASS = "ui-dataview-list-container";
    public static final String ROW_CLASS = "ui-dataview-row";
    public static final String GRID_LAYOUT_ROW_CLASS = "ui-dataview-row ui-g";
    public static final String GRID_LAYOUT_COLUMN_CLASS = "ui-dataview-column";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("page", PageEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private DataViewGridItem gridItem = null;
    private DataViewListItem listItem = null;

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isLayoutRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_layout");
    }

    @Override
    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if (eventName.equals("page")) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                String clientId = getClientId(context);
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;

                PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                pageEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(pageEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    public DataViewGridItem getGridItem() {
        return gridItem;
    }

    public DataViewListItem getListItem() {
        return listItem;
    }

    public void findViewItems() {
        for (UIComponent kid : getChildren()) {
            if (kid.isRendered()) {
                if (kid instanceof DataViewListItem) {
                    listItem = (DataViewListItem) kid;
                }
                else if (kid instanceof DataViewGridItem) {
                    gridItem = (DataViewGridItem) kid;
                }
            }
        }
    }


}