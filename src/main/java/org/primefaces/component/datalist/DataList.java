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
package org.primefaces.component.datalist;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
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

        if (type.equalsIgnoreCase("unordered")) {
            return "ul";
        }
        else if (type.equalsIgnoreCase("ordered")) {
            return "ol";
        }
        else if (type.equalsIgnoreCase("definition")) {
            return "dl";
        }
        else if (type.equalsIgnoreCase("none")) {
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
        DataModel model = getDataModel();

        if (model instanceof LazyDataModel) {
            LazyDataModel lazyModel = (LazyDataModel) model;

            List<?> data = lazyModel.load(getFirst(), getRows(), null, null, null);

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

            if (eventName.equals("page")) {
                String clientId = getClientId(context);
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;

                PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                pageEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(pageEvent);
            }
            else if (eventName.equals("tap") || eventName.equals("taphold")) {
                String clientId = getClientId(context);
                int index = Integer.parseInt(params.get(clientId + "_item"));
                setRowIndex(index);

                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getRowData());
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
        int first = getFirst();
        int rows = getRows();
        int last = rows == 0 ? getRowCount() : (first + rows);

        for (int rowIndex = first; rowIndex < last; rowIndex++) {
            setRowIndex(rowIndex);

            if (!isRowAvailable()) {
                break;
            }

            for (UIComponent child : getIterableChildren()) {
                if (child.isRendered()) {
                    process(context, child, phaseId);
                }
            }

            UIComponent descriptionFacet = getFacet("description");
            if (descriptionFacet != null && isDefinition()) {
                process(context, descriptionFacet, phaseId);
            }
        }
    }

    public void restoreDataListState() {
        DataListState ls = getDataListState(false);
        if (ls != null && isPaginator()) {
            setFirst(ls.getFirst());
            int rows = (ls.getRows() == 0) ? getRows() : ls.getRows();
            setRows(rows);
        }
    }

    public DataListState getDataListState(boolean create) {
        FacesContext fc = getFacesContext();
        Map<String, Object> sessionMap = fc.getExternalContext().getSessionMap();
        Map<String, DataListState> dlState = (Map) sessionMap.get(Constants.DATALIST_STATE);
        String viewId = fc.getViewRoot().getViewId().replaceFirst("^/*", "");
        String stateKey = viewId + "_" + getClientId(fc);

        if (dlState == null) {
            dlState = new HashMap<>();
            sessionMap.put(Constants.DATALIST_STATE, dlState);
        }

        DataListState ls = dlState.get(stateKey);
        if (ls == null && create) {
            ls = new DataListState();
            dlState.put(stateKey, ls);
        }

        return ls;
    }

}