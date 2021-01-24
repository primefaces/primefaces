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
package org.primefaces.component.datalist;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.IterationStatus;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class DataList extends DataListBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DataList";

    public static final String DATALIST_CLASS = "ui-datalist ui-widget";
    public static final String CONTENT_CLASS = "ui-datalist-content ui-widget-content";
    public static final String LIST_CLASS = "ui-datalist-data";
    public static final String NO_BULLETS_CLASS = "ui-datalist-nobullets";
    public static final String LIST_ITEM_CLASS = "ui-datalist-item";
    public static final String HEADER_CLASS = "ui-datalist-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datalist-footer ui-widget-header ui-corner-bottom";
    public static final String DATALIST_EMPTY_MESSAGE_CLASS = "ui-datalist-empty-message";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("page", PageEvent.class)
            .put("tap", SelectEvent.class)
            .put("taphold", SelectEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public String getListTag() {
        String type = getType();

        if ("unordered".equalsIgnoreCase(type)) {
            return "ul";
        }
        else if ("ordered".equalsIgnoreCase(type)) {
            return "ol";
        }
        else if ("definition".equalsIgnoreCase(type)) {
            return "dl";
        }
        else if ("none".equalsIgnoreCase(type)) {
            return null;
        }
        else {
            throw new FacesException("DataList '" + getClientId() + "' has invalid list type:'" + type + "'");
        }
    }

    public boolean isDefinition() {
        return getType().equalsIgnoreCase("definition");
    }

    public void loadLazyData() {
        DataModel<?> model = getDataModel();

        if (model instanceof LazyDataModel) {
            LazyDataModel<?> lazyModel = (LazyDataModel) model;

            List<?> data = lazyModel.load(getFirst(), getRows(), Collections.emptyMap(),  Collections.emptyMap());

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

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if ("page".equals(eventName)) {
                String clientId = getClientId(context);
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? first / rows : 0;

                PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                pageEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(pageEvent);
            }
            else if ("tap".equals(eventName) || "taphold".equals(eventName)) {
                String clientId = getClientId(context);
                int index = Integer.parseInt(params.get(clientId + "_item"));
                setRowIndex(index);

                SelectEvent<?> selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getRowData());
                selectEvent.setPhaseId(behaviorEvent.getPhaseId());

                setRowIndex(-1);
                super.queueEvent(selectEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    protected void processFacets(FacesContext context, PhaseId phaseId) {
        if (getFacetCount() > 0) {
            UIComponent descriptionFacet = getFacet("description");
            for (UIComponent facet : getFacets().values()) {
                if (facet.equals(descriptionFacet)) {
                    continue;
                }
                process(context, facet, phaseId);
            }
        }
    }

    @Override
    protected void processChildren(FacesContext context, PhaseId phaseId) {
        boolean definition = isDefinition();
        UIComponent descriptionFacet = definition ? getFacet("description") : null;
        int childCount = getChildCount();
        List<UIComponent> children = childCount > 0 ? getIterableChildren() : null;

        forEachRow((status) -> {
            for (int i = 0; i < childCount; i++) {
                UIComponent child = children.get(i);
                if (child.isRendered()) {
                    process(context, child, phaseId);
                }
            }

            if (definition && ComponentUtils.shouldRenderFacet(descriptionFacet)) {
                process(context, descriptionFacet, phaseId);
            }
        });
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

    public void forEachRow(Consumer<IterationStatus> callback) {
        FacesContext context = getFacesContext();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        String varStatus = getVarStatus();
        String rowIndexVar = getRowIndexVar();

        Object varStatusBackup = varStatus == null ? null : requestMap.get(varStatus);
        Object rowIndexVarBackup = rowIndexVar == null ? null : requestMap.get(rowIndexVar);

        int first = getFirst();
        int rows = getRows();
        int pageSize = first + rows;
        int rowCount = getRowCount();
        int last = rows == 0 ? rowCount : (first + rows);

        for (int i = first; i < last; i++) {
            setRowIndex(i);

            if (!isRowAvailable()) {
                break;
            }

            IterationStatus status = new IterationStatus((i == 0), (i == (rowCount - 1)), i, i, first, (pageSize - 1), 1);
            if (varStatus != null) {
                requestMap.put(varStatus, status);
            }
            if (rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }

            callback.accept(status);
        }

        //cleanup
        setRowIndex(-1);

        if (varStatus != null) {
            if (varStatusBackup == null) {
                requestMap.remove(varStatus);
            }
            else {
                requestMap.put(varStatus, varStatusBackup);
            }
        }
        if (rowIndexVar != null) {
            if (rowIndexVarBackup == null) {
                requestMap.remove(rowIndexVar);
            }
            else {
                requestMap.put(rowIndexVar, rowIndexVarBackup);
            }
        }
    }

    @Override
    public void restoreMultiViewState() {
        DataListState ls = getMultiViewState(false);
        if (ls != null && isPaginator()) {
            setFirst(ls.getFirst());
            int rows = (ls.getRows() == 0) ? getRows() : ls.getRows();
            setRows(rows);
        }
    }

    @Override
    public DataListState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, DataListState::new);
    }

    @Override
    public void resetMultiViewState() {
        setFirst(0);
    }
}