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
package org.primefaces.component.organigram;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.OrganigramNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Organigram.COMPONENT_TYPE, namespace = Organigram.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Organigram is a component to display hierarchical organizational structures.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "organigram/organigram.js")
@ResourceDependency(library = "primefaces", name = "organigram/organigram.css")
public class Organigram extends OrganigramBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Organigram";

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.expand)) {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_expandNode"));
                node.setExpanded(true);

                wrapperEvent = new OrganigramNodeExpandEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.collapse)) {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_collapseNode"));
                node.setExpanded(false);

                wrapperEvent = new OrganigramNodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.select) || isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.contextmenu)) {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_selectNode"));

                wrapperEvent = new OrganigramNodeSelectEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.dragdrop)) {
                OrganigramNode dragNode = findTreeNode(getValue(), params.get(clientId + "_dragNode"));
                OrganigramNode dropNode = findTreeNode(getValue(), params.get(clientId + "_dropNode"));

                // remove node from current parent
                if (dragNode != null && dropNode != null) {
                    OrganigramNode sourceNode = dragNode.getParent();

                    if (sourceNode != null) {
                        sourceNode.getChildren().remove(dragNode);
                    }

                    // set new parent
                    dragNode.setParent(dropNode);

                    wrapperEvent = new OrganigramNodeDragDropEvent(this, behaviorEvent.getBehavior(), dragNode, dropNode, sourceNode);
                }
            }

            if (wrapperEvent == null) {
                String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
                throw new FacesException("Component " + this.getClass().getName() + " does not support event " + eventName + "!");
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }


    public OrganigramNode findTreeNode(OrganigramNode searchRoot, String rowKey) {
        if ("root".equals(rowKey)) {
            return getValue();
        }

        return OrganigramHelper.findTreeNode(searchRoot, rowKey);
    }
}