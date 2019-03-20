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
package org.primefaces.component.organigram;

import java.util.Collection;
import java.util.Map;
import javax.faces.FacesException;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.OrganigramNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "organigram/organigram.js"),
        @ResourceDependency(library = "primefaces", name = "organigram/organigram.css")
})
public class Organigram extends OrganigramBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Organigram";

    private static final String DEFAULT_EVENT = "select";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("select", OrganigramNodeSelectEvent.class)
            .put("expand", OrganigramNodeExpandEvent.class)
            .put("collapse", OrganigramNodeCollapseEvent.class)
            .put("dragdrop", OrganigramNodeDragDropEvent.class)
            .put("contextmenu", OrganigramNodeSelectEvent.class)
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

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("expand")) {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_expandNode"));
                node.setExpanded(true);

                wrapperEvent = new OrganigramNodeExpandEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (eventName.equals("collapse")) {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_collapseNode"));
                node.setExpanded(false);

                wrapperEvent = new OrganigramNodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (eventName.equals("select") || eventName.equals("contextmenu")) {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_selectNode"));

                wrapperEvent = new OrganigramNodeSelectEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (eventName.equals("dragdrop")) {
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
        if (rowKey != null && rowKey.equals("root")) {
            return getValue();
        }

        return OrganigramHelper.findTreeNode(searchRoot, rowKey);
    }
}