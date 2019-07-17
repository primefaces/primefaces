/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.dataview;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.model.DataModel;

import org.primefaces.PrimeFaces;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
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

    public void loadLazyData() {
        DataModel model = getDataModel();
        if (model instanceof LazyDataModel) {
            LazyDataModel lazyModel = (LazyDataModel) model;
            List<?> data = lazyModel.load(getFirst(), getRows(), null, null, null);

            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator
            if (ComponentUtils.isRequestSource(this, getFacesContext()) && isPaginator() ) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", lazyModel.getRowCount());
            }
        }
    }
}