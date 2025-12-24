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
package org.primefaces.component.mindmap;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.util.Constants;

import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Mindmap.COMPONENT_TYPE, namespace = Mindmap.COMPONENT_FAMILY)
@FacesComponentDescription("MindMap is an interactive mindmapping component.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "raphael/raphael.js")
@ResourceDependency(library = "primefaces", name = "mindmap/mindmap.js")
public class Mindmap extends MindmapBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Mindmap";

    public static final String STYLE_CLASS = "ui-mindmap ui-widget ui-widget-content";

    private MindmapNode selectedNode;

    public MindmapNode getSelectedNode() {
        return selectedNode;
    }

    public String getSelectedNodeKey(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().get(getClientId(context) + "_nodeKey");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (isAjaxBehaviorEventSource(event)) {
            FacesContext context = getFacesContext();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            String clientId = getClientId(context);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.select, ClientBehaviorEventKeys.dblselect)) {
                String nodeKey = params.get(clientId + "_nodeKey");
                MindmapNode node = "root".equals(nodeKey) ? getValue() : findNode(getValue(), nodeKey);
                selectedNode = node;

                super.queueEvent(new SelectEvent<>(this, behaviorEvent.getBehavior(), node));
            }
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
            String relativeRowKey = rowKey.substring(rowKey.indexOf('_') + 1);

            return findNode(searchRoot, relativeRowKey);
        }
    }

    public boolean isNodeSelectRequest(FacesContext context) {
        if (!isAjaxRequestSource(context)) {
            return false;
        }
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        return "select".equals(eventName);
    }
}