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
package org.primefaces.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DefaultScheduleEvent implements ScheduleEvent, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Date startDate;
    private Date endDate;
    private boolean allDay = false;
    private String styleClass;
    private Object data;
    private boolean editable = true;
    private String description;
    private String url;
    private ScheduleRenderingMode renderingMode;
    private Map<String, Object> dynamicProperties;

    public DefaultScheduleEvent() {
    }

    public DefaultScheduleEvent(String title, Date start, Date end) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
    }

    public DefaultScheduleEvent(String title, Date start, Date end, boolean allDay) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.allDay = allDay;
    }

    public DefaultScheduleEvent(String title, Date start, Date end, String styleClass) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.styleClass = styleClass;
    }

    public DefaultScheduleEvent(String title, Date start, Date end, Object data) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.data = data;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    @Override
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public ScheduleRenderingMode getRenderingMode() {
        return renderingMode;
    }

    public void setRenderingMode(ScheduleRenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    @Override
    public Map<String, Object> getDynamicProperties() {
        return dynamicProperties;
    }

    public Object setDynamicProperty(String key, Object value) {
        if (dynamicProperties == null) {
            dynamicProperties = new HashMap<>();
        }
        return dynamicProperties.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultScheduleEvent other = (DefaultScheduleEvent) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 61 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 61 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DefaultScheduleEvent{title=" + title + ",startDate=" + startDate + ",endDate=" + endDate + "}";
    }
}
