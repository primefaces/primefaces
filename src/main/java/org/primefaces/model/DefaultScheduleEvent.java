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
package org.primefaces.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultScheduleEvent<T> implements ScheduleEvent<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String groupId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean allDay = false;
    private String styleClass;
    private T data;
    private boolean editable = true;
    private boolean overlapAllowed = false;
    private String description;
    private String url;
    private ScheduleRenderingMode renderingMode;
    private Map<String, Object> dynamicProperties;

    public DefaultScheduleEvent() {
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end, boolean allDay) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.allDay = allDay;
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end, String styleClass) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.styleClass = styleClass;
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end, T data) {
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
    public DefaultScheduleEvent<T> setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    public DefaultScheduleEvent<T> setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public DefaultScheduleEvent<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @Override
    public DefaultScheduleEvent<T> setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Override
    public DefaultScheduleEvent<T> setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    @Override
    public boolean isAllDay() {
        return allDay;
    }

    public DefaultScheduleEvent<T> setAllDay(boolean allDay) {
        this.allDay = allDay;
        return this;
    }

    public DefaultScheduleEvent<T> setStyleClass(String styleClass) {
        this.styleClass = styleClass;
        return this;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    @Override
    public T getData() {
        return data;
    }

    public DefaultScheduleEvent<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public boolean isOverlapAllowed() {
        return overlapAllowed;
    }

    public DefaultScheduleEvent<T> setOverlapAllowed(boolean overlapAllowed) {
        this.overlapAllowed = overlapAllowed;
        return this;
    }

    public DefaultScheduleEvent<T> setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public DefaultScheduleEvent<T> setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public DefaultScheduleEvent<T> setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public ScheduleRenderingMode getRenderingMode() {
        return renderingMode;
    }

    public DefaultScheduleEvent<T> setRenderingMode(ScheduleRenderingMode renderingMode) {
        this.renderingMode = renderingMode;
        return this;
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
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.endDate, other.endDate)) {
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

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static final class Builder<T> {

        private DefaultScheduleEvent<T> scheduleEvent;

        private Builder() {
            scheduleEvent = new DefaultScheduleEvent();
        }

        public DefaultScheduleEvent.Builder id(String id) {
            scheduleEvent.setId(id);
            return this;
        }

        public DefaultScheduleEvent.Builder groupId(String groupId) {
            scheduleEvent.setGroupId(groupId);
            return this;
        }

        public DefaultScheduleEvent.Builder title(String title) {
            scheduleEvent.setTitle(title);
            return this;
        }

        public DefaultScheduleEvent.Builder startDate(LocalDateTime startDate) {
            scheduleEvent.setStartDate(startDate);
            return this;
        }

        public DefaultScheduleEvent.Builder endDate(LocalDateTime endDate) {
            scheduleEvent.setEndDate(endDate);
            return this;
        }

        public DefaultScheduleEvent.Builder allDay(boolean allDay) {
            scheduleEvent.setAllDay(allDay);
            return this;
        }

        public DefaultScheduleEvent.Builder styleClass(String styleClass) {
            scheduleEvent.setStyleClass(styleClass);
            return this;
        }

        public DefaultScheduleEvent.Builder data(T data) {
            scheduleEvent.setData(data);
            return this;
        }

        public DefaultScheduleEvent.Builder editable(boolean editable) {
            scheduleEvent.setEditable(editable);
            return this;
        }

        public DefaultScheduleEvent.Builder overlapAllowed(boolean overlapAllowed) {
            scheduleEvent.setOverlapAllowed(overlapAllowed);
            return this;
        }

        public DefaultScheduleEvent.Builder description(String description) {
            scheduleEvent.setDescription(description);
            return this;
        }

        public DefaultScheduleEvent.Builder url(String url) {
            scheduleEvent.setUrl(url);
            return this;
        }

        public DefaultScheduleEvent.Builder renderingMode(ScheduleRenderingMode renderingMode) {
            scheduleEvent.setRenderingMode(renderingMode);
            return this;
        }

        public DefaultScheduleEvent.Builder dynamicProperty(String key, Object value) {
            scheduleEvent.setDynamicProperty(key, value);
            return this;
        }

        public DefaultScheduleEvent build() {
            return scheduleEvent;
        }
    }
}
