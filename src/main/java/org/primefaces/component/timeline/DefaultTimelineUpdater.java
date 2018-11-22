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

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.primefaces.PrimeFaces;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FastStringWriter;

public class DefaultTimelineUpdater extends TimelineUpdater implements PhaseListener {

    private static final long serialVersionUID = 20130317L;

    private static final String PREVENT_RENDER = Boolean.TRUE.toString();

    private static final Logger LOGGER = Logger.getLogger(DefaultTimelineUpdater.class.getName());

    private String widgetVar;
    private List<CrudOperationData> crudOperationDatas;

    enum CrudOperation {

        ADD,
        UPDATE,
        DELETE,
        SELECT,
        CLEAR
    }

    @Override
    public void add(TimelineEvent event) {
        if (event == null) {
            return;
        }

        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.ADD, event));
    }

    @Override
    public void update(TimelineEvent event, int index) {
        if (event == null) {
            return;
        }

        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.UPDATE, event, index));
    }

    @Override
    public void delete(int index) {
        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.DELETE, index));
    }

    @Override
    public void select(int index) {
        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.SELECT, index));
    }

    @Override
    public void clear() {
        checkCrudOperationDataList();
        crudOperationDatas.add(new CrudOperationData(CrudOperation.CLEAR));
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (crudOperationDatas == null) {
            return;
        }

        FacesContext fc = event.getFacesContext();
        StringBuilder sb = new StringBuilder();

        Timeline timeline = (Timeline) fc.getViewRoot().findComponent(id);
        TimelineRenderer timelineRenderer = ComponentUtils.getUnwrappedRenderer(
                fc,
                Timeline.COMPONENT_FAMILY,
                Timeline.DEFAULT_RENDERER,
                TimelineRenderer.class);

        Map<String, String> groupsContent = null;
        List<TimelineGroup> groups = timeline.getValue().getGroups();
        UIComponent groupFacet = timeline.getFacet("group");
        if (groups != null && groupFacet != null) {
            // buffer for groups' content
            groupsContent = new HashMap<>();
        }

        TimeZone targetTZ = ComponentUtils.resolveTimeZone(timeline.getTimeZone());
        TimeZone browserTZ = ComponentUtils.resolveTimeZone(timeline.getBrowserTimeZone());

        try (FastStringWriter fsw = new FastStringWriter();
             FastStringWriter fswHtml = new FastStringWriter()) {

            boolean renderComponent = false;
            for (CrudOperationData crudOperationData : crudOperationDatas) {
                switch (crudOperationData.getCrudOperation()) {
                    case ADD:

                        sb.append(";PF('");
                        sb.append(widgetVar);
                        sb.append("').addEvent(");
                        sb.append(timelineRenderer.encodeEvent(fc, fsw, fswHtml, timeline, browserTZ, targetTZ,
                                groups, groupFacet, groupsContent, crudOperationData.getEvent()));
                        sb.append(", " + PREVENT_RENDER + ")");
                        renderComponent = true;
                        break;

                    case UPDATE:

                        sb.append(";PF('");
                        sb.append(widgetVar);
                        sb.append("').changeEvent(");
                        sb.append(crudOperationData.getIndex());
                        sb.append(",");
                        sb.append(timelineRenderer.encodeEvent(fc, fsw, fswHtml, timeline, browserTZ, targetTZ,
                                groups, groupFacet, groupsContent, crudOperationData.getEvent()));
                        sb.append(", " + PREVENT_RENDER + ")");
                        renderComponent = true;
                        break;

                    case DELETE:

                        sb.append(";PF('");
                        sb.append(widgetVar);
                        sb.append("').deleteEvent(");
                        sb.append(crudOperationData.getIndex());
                        sb.append(", " + PREVENT_RENDER + ")");
                        renderComponent = true;
                        break;

                    case SELECT:

                        sb.append(";PF('");
                        sb.append(widgetVar);
                        sb.append("').setSelection(");
                        sb.append(crudOperationData.getIndex());
                        sb.append(")");
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
            LOGGER.log(Level.WARNING, "Timeline with id " + id + " could not be updated, at least one CRUD operation failed", e);
        }
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

        if (widgetVar != null ? !widgetVar.equals(that.widgetVar) : that.widgetVar != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return widgetVar != null ? widgetVar.hashCode() : 0;
    }

    class CrudOperationData implements Serializable {

        private static final long serialVersionUID = 1L;

        private final CrudOperation crudOperation;
        private TimelineEvent event;
        private int index;

        CrudOperationData(CrudOperation crudOperation) {
            this.crudOperation = crudOperation;
        }

        CrudOperationData(CrudOperation crudOperation, TimelineEvent event) {
            this.crudOperation = crudOperation;
            this.event = event;
        }

        CrudOperationData(CrudOperation crudOperation, int index) {
            this.crudOperation = crudOperation;
            this.index = index;
        }

        CrudOperationData(CrudOperation crudOperation, TimelineEvent event, int index) {
            this.crudOperation = crudOperation;
            this.event = event;
            this.index = index;
        }

        public CrudOperation getCrudOperation() {
            return crudOperation;
        }

        public TimelineEvent getEvent() {
            return event;
        }

        public int getIndex() {
            return index;
        }
    }
}
