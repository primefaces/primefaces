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
package org.primefaces.component.dnd;

import java.util.Collection;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.DragDropEvent;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Droppable extends DroppableBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Droppable";

    private static final String DEFAULT_EVENT = "drop";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("drop", null)
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

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("drop")) {
                String dragId = params.get(clientId + "_dragId");
                String dropId = params.get(clientId + "_dropId");
                DragDropEvent dndEvent = null;
                String datasourceId = getDatasource();

                if (datasourceId != null) {
                    UIData datasource = findDatasource(context, this, datasourceId);
                    String[] idTokens = dragId.split(String.valueOf(UINamingContainer.getSeparatorChar(context)));
                    int rowIndex = Integer.parseInt(idTokens[idTokens.length - 2]);
                    datasource.setRowIndex(rowIndex);
                    Object data = datasource.getRowData();
                    datasource.setRowIndex(-1);

                    dndEvent = new DragDropEvent(this, behaviorEvent.getBehavior(), dragId, dropId, data);
                }
                else {
                    dndEvent = new DragDropEvent(this, behaviorEvent.getBehavior(), dragId, dropId);
                }

                super.queueEvent(dndEvent);
            }

        }
        else {
            super.queueEvent(event);
        }
    }

    protected UIData findDatasource(FacesContext context, Droppable droppable, String datasourceId) {
        UIComponent datasource = SearchExpressionFacade.resolveComponent(context, droppable, datasourceId);

        if (datasource == null) {
            throw new FacesException("Cannot find component \"" + datasourceId + "\" in view.");
        }
        else {
            return (UIData) datasource;
        }
    }
}