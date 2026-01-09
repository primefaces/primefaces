/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.PrimeFaces;
import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.model.DataModel;

@FacesComponent(value = DataView.COMPONENT_TYPE, namespace = DataView.COMPONENT_FAMILY)
@FacesComponentDescription("DataView displays data in grid or list layout.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class DataView extends DataViewBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DataView";

    public static final String DATAVIEW_CLASS = "ui-dataview ui-widget";
    public static final String LIST_LAYOUT_CLASS = "ui-dataview-list";
    public static final String GRID_LAYOUT_CLASS = "ui-dataview-grid";
    public static final String HEADER_CLASS = "ui-dataview-header ui-widget-header ui-helper-clearfix";
    public static final String FOOTER_CLASS = "ui-dataview-footer ui-widget-header";
    public static final String CONTENT_CLASS = "ui-dataview-content ui-widget-content";
    public static final String BUTTON_CONTAINER_CLASS = "ui-dataview-layout-options ui-selectonebutton ui-buttonset";
    public static final String BUTTON_CLASS = "ui-button ui-button-icon-only ui-state-default";
    public static final String LIST_LAYOUT_CONTAINER_CLASS = "ui-dataview-list-container";
    public static final String ROW_CLASS = "ui-dataview-row";
    public static final String GRID_LAYOUT_ROW_CLASS = "ui-dataview-row";
    public static final String GRID_LAYOUT_COLUMN_CLASS = "ui-dataview-column";

    private DataViewGridItem gridItem;
    private DataViewListItem listItem;

    public boolean isLayoutRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_layout");
    }

    @Override
    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if ("page".equals(eventName)) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                String clientId = getClientId(context);
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;
                String rowsPerPageParam = params.get(clientId + "_rows");
                Integer rowsPerPage = LangUtils.isNotBlank(rowsPerPageParam) ? Integer.parseInt(rowsPerPageParam) : null;

                PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page, rowsPerPage);
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
        DataModel<?> model = getDataModel();

        if (model instanceof LazyDataModel) {
            LazyDataModel<?> lazyModel = (LazyDataModel<?>) model;

            lazyModel.setRowCount(lazyModel.count(Collections.emptyMap()));
            calculateFirst();

            List<?> data = lazyModel.load(getFirst(), getRows(), Collections.emptyMap(), Collections.emptyMap());

            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator
            if (ComponentUtils.isRequestSource(this, getFacesContext()) && isPaginator()) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", lazyModel.getRowCount());
            }
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        gridItem = null;
        listItem = null;

        return super.saveState(context);
    }

    public void reset() {
        setFirst(0);
        resetRows();
        setLayout(null);
    }

    @Override
    public void resetMultiViewState() {
        reset();
    }

    @Override
    public void restoreMultiViewState() {
        DataViewState viewState = getMultiViewState(false);
        if (viewState != null) {
            if (LangUtils.isNotEmpty(viewState.getLayout())) {
                setLayout(viewState.getLayout());
            }

            if (isPaginator()) {
                setFirst(viewState.getFirst());
                int rows = (viewState.getRows() == 0) ? getRows() : viewState.getRows();
                setRows(rows);
            }
        }
    }

    @Override
    public DataViewState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, DataViewState::new);
    }
}