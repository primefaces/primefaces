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
package org.primefaces.component.mindmap;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "raphael/raphael.js"),
        @ResourceDependency(library = "primefaces", name = "mindmap/mindmap.js")
})
public class Mindmap extends MindmapBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Mindmap";

    public static final String STYLE_CLASS = "ui-mindmap ui-widget ui-widget-content ui-corner-all";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("select", SelectEvent.class)
            .put("dblselect", SelectEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private MindmapNode selectedNode = null;

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public MindmapNode getSelectedNode() {
        return selectedNode;
    }

    public String getSelectedNodeKey(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().get(getClientId(context) + "_nodeKey");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);
        AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if (eventName.equals("select") || eventName.equals("dblselect")) {
            String nodeKey = params.get(clientId + "_nodeKey");
            MindmapNode node = nodeKey.equals("root") ? getValue() : findNode(getValue(), nodeKey);
            selectedNode = node;

            super.queueEvent(new SelectEvent(this, behaviorEvent.getBehavior(), node));
        }
    }

    protected MindmapNode findNode(MindmapNode searchRoot, String rowKey) {
        String[] paths = rowKey.split("_");

        if (paths.length == 0) {
            return null;
        }

        int childIndex = Integer.parseInt(paths[0]);
        searchRoot = searchRoot.getChildren().get(childIndex);

        if (paths.length == 1) {
            return searchRoot;
        }
        else {
            String relativeRowKey = rowKey.substring(rowKey.indexOf("_") + 1);

            return findNode(searchRoot, relativeRowKey);
        }
    }

    public boolean isNodeSelectRequest(FacesContext context) {
        if (!ComponentUtils.isRequestSource(this, context)) {
            return false;
        }
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        return eventName.equals("select");
    }
}