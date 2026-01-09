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
package org.primefaces.component.datagrid;

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
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.model.DataModel;

@FacesComponent(value = DataGrid.COMPONENT_TYPE, namespace = DataGrid.COMPONENT_FAMILY)
@FacesComponentDescription("DataGrid displays a collection of data in grid layout. "
        + " AJAX Pagination is a built-in feature and paginator UI is fully customizable via various options"
        + " like paginatorTemplate, rowPerPageOptions, pageLinks and more.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class DataGrid extends DataGridBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DataGrid";

    public static final String DATAGRID_CLASS = "ui-datagrid ui-widget";
    public static final String HEADER_CLASS = "ui-datagrid-header ui-widget-header";
    public static final String FOOTER_CLASS = "ui-datagrid-footer ui-widget-header";
    public static final String EMPTY_CONTENT_CLASS = "ui-datagrid-content ui-datagrid-content-empty ui-widget-content";
    public static final String TABLE_CLASS = "ui-datagrid-data";
    public static final String TABLE_ROW_CLASS = "ui-datagrid-row";
    public static final String GRID_CONTENT_CLASS = "ui-datagrid-content ui-widget-content";
    public static final String COLUMN_CLASS = "ui-datagrid-column";

    public void loadLazyData() {
        // duplicate of DataView#loadLazyData

        DataModel<?> model = getDataModel();

        if (model instanceof LazyDataModel) {
            LazyDataModel<?> lazyModel = (LazyDataModel<?>) model;

            lazyModel.setRowCount(lazyModel.count(Collections.emptyMap()));
            calculateFirst();

            List<?> data = lazyModel.load(getFirst(), getRows(), Collections.emptyMap(), Collections.emptyMap());

            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator for callback
            if (ComponentUtils.isRequestSource(this, getFacesContext()) && isPaginator()) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", lazyModel.getRowCount());
            }
        }
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            setRowIndex(-1);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.page)) {
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

    @Override
    protected boolean shouldSkipChildren(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String paramValue = params.get(Constants.RequestParams.SKIP_CHILDREN_PARAM);
        if (paramValue != null && !Boolean.parseBoolean(paramValue)) {
            return false;
        }
        else {
            return params.containsKey(getClientId(context) + "_skipChildren");
        }
    }

    @Override
    public void restoreMultiViewState() {
        DataGridState ls = getMultiViewState(false);
        if (ls != null && isPaginator()) {
            setFirst(ls.getFirst());
            int rows = (ls.getRows() == 0) ? getRows() : ls.getRows();
            setRows(rows);
        }
    }

    @Override
    public DataGridState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, DataGridState::new);
    }

    @Override
    public void resetMultiViewState() {
        setFirst(0);
    }
}