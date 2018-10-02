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
package org.primefaces.component.timeline;

import java.util.*;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.timeline.*;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.DateUtils;
import org.primefaces.util.MapBuilder;
import org.primefaces.visit.UIDataContextCallback;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "timeline/timeline.css"),
        @ResourceDependency(library = "primefaces", name = "timeline/timeline.js")
})
public class Timeline extends TimelineBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Timeline";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("add", TimelineAddEvent.class)
            .put("change", TimelineModificationEvent.class)
            .put("changed", TimelineModificationEvent.class)
            .put("edit", TimelineModificationEvent.class)
            .put("delete", TimelineModificationEvent.class)
            .put("select", TimelineSelectEvent.class)
            .put("rangechange", TimelineRangeEvent.class)
            .put("rangechanged", TimelineRangeEvent.class)
            .put("lazyload", TimelineLazyLoadEvent.class)
            .put("drop", TimelineDragDropEvent.class)
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
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if (isSelfRequest(context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if ("add".equals(eventName)) {
                // preset start / end date and the group
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineAddEvent te =
                        new TimelineAddEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")),
                                getGroup(params.get(clientId + "_group")));
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
            else if ("change".equals(eventName) || "changed".equals(eventName)) {
                TimelineEvent clonedEvent = null;
                TimelineEvent timelineEvent = getValue().getEvent(params.get(clientId + "_eventIdx"));

                if (timelineEvent != null) {
                    clonedEvent = new TimelineEvent();
                    clonedEvent.setData(timelineEvent.getData());
                    clonedEvent.setEditable(timelineEvent.isEditable());
                    clonedEvent.setStyleClass(timelineEvent.getStyleClass());

                    // update start / end date and the group
                    TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                    TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());
                    clonedEvent.setStartDate(DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")));
                    clonedEvent.setEndDate(DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")));
                    clonedEvent.setGroup(getGroup(params.get(clientId + "_group")));
                }

                TimelineModificationEvent te = new TimelineModificationEvent(this, behaviorEvent.getBehavior(), clonedEvent);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
            else if ("edit".equals(eventName) || "delete".equals(eventName)) {
                TimelineEvent clonedEvent = null;
                TimelineEvent timelineEvent = getValue().getEvent(params.get(clientId + "_eventIdx"));

                if (timelineEvent != null) {
                    clonedEvent = new TimelineEvent();
                    clonedEvent.setData(timelineEvent.getData());
                    clonedEvent.setStartDate((Date) timelineEvent.getStartDate().clone());
                    clonedEvent.setEndDate(timelineEvent.getEndDate() != null ? (Date) timelineEvent.getEndDate().clone() : null);
                    clonedEvent.setEditable(timelineEvent.isEditable());
                    clonedEvent.setGroup(timelineEvent.getGroup());
                    clonedEvent.setStyleClass(timelineEvent.getStyleClass());
                }

                TimelineModificationEvent te = new TimelineModificationEvent(this, behaviorEvent.getBehavior(), clonedEvent);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
            else if ("select".equals(eventName)) {
                TimelineEvent timelineEvent = getValue().getEvent(params.get(clientId + "_eventIdx"));
                TimelineSelectEvent te = new TimelineSelectEvent(this, behaviorEvent.getBehavior(), timelineEvent);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
            else if ("rangechange".equals(eventName) || "rangechanged".equals(eventName)) {
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineRangeEvent te =
                        new TimelineRangeEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")));
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
            else if ("lazyload".equals(eventName)) {
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineLazyLoadEvent te =
                        new TimelineLazyLoadEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDateFirst")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDateFirst")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDateSecond")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDateSecond")));
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
            else if ("drop".equals(eventName)) {
                Object data = null;
                final String dragId = params.get(clientId + "_dragId");
                final String uiDataId = params.get(clientId + "_uiDataId");

                if (dragId != null && uiDataId != null) {
                    // draggable is within a data iteration component
                    UIDataContextCallback contextCallback = new UIDataContextCallback(dragId);
                    context.getViewRoot().invokeOnComponent(context, uiDataId, contextCallback);
                    data = contextCallback.getData();
                }

                // preset start / end date, group, dragId and data object
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineDragDropEvent te =
                        new TimelineDragDropEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")),
                                getGroup(params.get(clientId + "_group")), dragId, data);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
        }

        super.queueEvent(event);
    }

    private String getGroup(String groupParam) {
        List<TimelineGroup> groups = getValue().getGroups();
        if (groups == null || groupParam == null) {
            return groupParam;
        }

        int idx = groupParam.indexOf("</span>");
        if (idx > -1) {
            groupParam = groupParam.substring(0, idx);
            int idxGroupOrder = groupParam.indexOf("#");
            if (idxGroupOrder > -1) {
                String groupOrder = groupParam.substring(idxGroupOrder + 1);
                return groups.get(Integer.valueOf(groupOrder)).getId();
            }
        }

        return groupParam;
    }

    private boolean isSelfRequest(FacesContext context) {
        return getClientId(context)
                .equals(context.getExternalContext().getRequestParameterMap().get(
                        Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }


}