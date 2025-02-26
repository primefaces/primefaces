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
package org.primefaces.component.timeline;

import org.primefaces.PrimeFaces;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.FastStringWriter;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

public class DefaultTimelineUpdater extends TimelineUpdater implements PhaseListener {

    private static final long serialVersionUID = 20130317L;

    private static final Logger LOGGER = Logger.getLogger(DefaultTimelineUpdater.class.getName());

    private String widgetVar;
    private List<CrudOperationData> crudOperationDatas;

    // serialization
    public DefaultTimelineUpdater() {
        super();
    }

    public DefaultTimelineUpdater(String clientId, String widgetVar) {
        super(clientId);
        this.widgetVar = widgetVar;
    }

    enum CrudOperation {

        ADD,
        UPDATE,
        DELETE,
        SELECT,
        CLEAR
    }

    @Override
    public void add(TimelineEvent<?> event) {
        if (event == null) {
            return;
        }

        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.ADD, event));
    }

    @Override
    public void update(TimelineEvent<?> event) {
        if (event == null) {
            return;
        }

        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.UPDATE, event));
    }

    @Override
    public void delete(String id) {
        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.DELETE, id));
    }

    @Override
    public void select(String id) {
        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.SELECT, id));
    }

    @Override
    public void clear() {
        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.CLEAR));
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (PhaseId.APPLY_REQUEST_VALUES.equals(event.getPhaseId())) {
            populateTimelineUpdater(event.getFacesContext());
        }
        else if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
            processCrudOperations(event.getFacesContext());
        }
    }

    private void populateTimelineUpdater(FacesContext context) {
        Map<String, TimelineUpdater> map = (Map<String, TimelineUpdater>) context.getAttributes().get(TimelineUpdater.class.getName());
        if (map == null) {
            map = new HashMap<>();
            context.getAttributes().put(TimelineUpdater.class.getName(), map);
        }
        if (!map.containsKey(widgetVar)) {
            map.put(widgetVar, this);
        }
    }

    private void processCrudOperations(FacesContext context) {
        if (crudOperationDatas == null) {
            return;
        }

        context.getViewRoot().invokeOnComponent(context, clientId, (ctx, component) -> {
            StringBuilder sb = new StringBuilder();

            Timeline timeline = (Timeline) component;
            TimelineRenderer timelineRenderer = ComponentUtils.getUnwrappedRenderer(
                    context,
                    Timeline.COMPONENT_FAMILY,
                    Timeline.DEFAULT_RENDERER);

            TimelineModel<Object, Object> model = timeline.getValue();
            List<TimelineGroup<Object>> groups = timelineRenderer.calculateGroupsFromModel(model);
            UIComponent groupFacet = timeline.getFacet("group");
            // buffer for groups' content
            Map<String, String> groupsContent = new HashMap<>();
            UIComponent eventTitleFacet = timeline.getFacet("eventTitle");

            ZoneId zoneId = CalendarUtils.calculateZoneId(timeline.getTimeZone());

            try (FastStringWriter fsw = new FastStringWriter();
                 FastStringWriter fswHtml = new FastStringWriter()) {

                Consumer<CrudOperationData> updateGroupIfNecessary = data -> {
                    TimelineGroup<?> foundGroup = null;
                    if (data.getEvent().getGroup() != null) {
                        Integer orderGroup = null;
                        for (int i = 0; i < groups.size(); i++) {
                            TimelineGroup<?> group = groups.get(i);
                            if (group.getId() != null && group.getId().equals(data.getEvent().getGroup())) {
                                orderGroup = i;
                                foundGroup = group;
                                break;
                            }
                        }
                        if (foundGroup != null) {
                            //If groups was not set in model then order by content.
                            orderGroup = model.getGroups() != null ? orderGroup : null;
                            sb.append(";PF('");
                            sb.append(widgetVar);
                            sb.append("').updateGroup(");
                            try {
                                sb.append(timelineRenderer.encodeGroup(context, fsw, fswHtml, timeline, groupFacet, groupsContent, foundGroup, orderGroup));
                            }
                            catch (IOException e) {
                                LOGGER.log(Level.WARNING, e,
                                        () -> "Timeline with clientId " + clientId + " could not be updated, at least one CRUD operation failed");
                            }
                            sb.append(")");
                        }
                    }
                };
                boolean renderComponent = false;
                for (CrudOperationData crudOperationData : crudOperationDatas) {
                    switch (crudOperationData.getCrudOperation()) {
                        case ADD:
                            updateGroupIfNecessary.accept(crudOperationData);

                            sb.append(";PF('");
                            sb.append(widgetVar);
                            sb.append("').addEvent(");
                            sb.append(EscapeUtils.forCDATA(timelineRenderer.encodeEvent(context, fsw, fswHtml, timeline, eventTitleFacet, zoneId,
                                    groups, crudOperationData.getEvent())));
                            sb.append(")");
                            renderComponent = true;
                            break;

                        case UPDATE:
                            updateGroupIfNecessary.accept(crudOperationData);

                            sb.append(";PF('");
                            sb.append(widgetVar);
                            sb.append("').changeEvent(");
                            sb.append(EscapeUtils.forCDATA(timelineRenderer.encodeEvent(context, fsw, fswHtml, timeline, eventTitleFacet, zoneId,
                                    groups, crudOperationData.getEvent())));
                            sb.append(")");
                            renderComponent = true;
                            break;

                        case DELETE:

                            sb.append(";PF('");
                            sb.append(widgetVar);
                            sb.append("').deleteEvent(\"");
                            sb.append(EscapeUtils.forJavaScript(crudOperationData.calculateId()));
                            sb.append("\")");
                            renderComponent = true;
                            break;

                        case SELECT:

                            sb.append(";PF('");
                            sb.append(widgetVar);
                            sb.append("').setSelection(\"");
                            sb.append(EscapeUtils.forJavaScript(crudOperationData.calculateId()));
                            sb.append("\")");
                            break;

                        case CLEAR:

                            sb.append(";PF('");
                            sb.append(widgetVar);
                            sb.append("').deleteAllEvents()");
                            break;
                    }
                }

                if (renderComponent) {
                    sb.append(";PF('");
                    sb.append(widgetVar);
                    sb.append("').renderTimeline()");
                }

                // execute JS script
                PrimeFaces.current().executeScript(sb.toString());
            }
            catch (IOException e) {
                LOGGER.log(Level.WARNING, e, () -> "Timeline with id " + clientId + " could not be updated, at least one CRUD operation failed");
            }
        });
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        // NOOP.
    }

    public String getWidgetVar() {
        return widgetVar;
    }

    public void setWidgetVar(String widgetVar) {
        this.widgetVar = widgetVar;
    }

    private void checkCrudOperationDataList() {
        if (crudOperationDatas == null) {
            crudOperationDatas = new ArrayList<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultTimelineUpdater that = (DefaultTimelineUpdater) o;

        return !(widgetVar != null ? !widgetVar.equals(that.widgetVar) : that.widgetVar != null);
    }

    @Override
    public int hashCode() {
        return widgetVar != null ? widgetVar.hashCode() : 0;
    }

    static class CrudOperationData implements Serializable {

        private static final long serialVersionUID = 1L;

        private final CrudOperation crudOperation;
        private final TimelineEvent<?> event;
        private final String id;

        CrudOperationData(CrudOperation crudOperation) {
            this(crudOperation, null, null);
        }

        CrudOperationData(CrudOperation crudOperation, TimelineEvent<?> event) {
            this(crudOperation, event, event.getId());
        }

        CrudOperationData(CrudOperation crudOperation, String id) {
            this(crudOperation, null, id);
        }

        private CrudOperationData(CrudOperation crudOperation, TimelineEvent<?> event, String id) {
            this.crudOperation = crudOperation;
            this.event = event;
            this.id = id;
        }

        CrudOperation getCrudOperation() {
            return crudOperation;
        }

        TimelineEvent<?> getEvent() {
            return event;
        }

        String calculateId() {
            return id;
        }
    }
}
