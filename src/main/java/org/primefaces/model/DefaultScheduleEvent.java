/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
    private Boolean resizable;
    private Boolean draggable;
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
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean isAllDay() {
        return allDay;
    }

    @Override
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
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public Boolean isDraggable() {
        return draggable;
    }

    @Override
    public Boolean isResizable() {
        return resizable;
    }

    @Override
    public boolean isOverlapAllowed() {
        return overlapAllowed;
    }

    public void setOverlapAllowed(boolean overlapAllowed) {
        this.overlapAllowed = overlapAllowed;
    }

    /**
     * Shortcut for calling both {@link #setDraggable(Boolean)} and
     * {@link #setResizable(Boolean)}.
     */
    public void setEditable(boolean editable) {
        this.draggable = editable;
        this.resizable = editable;
    }

    /**
     * @param draggable Whether the event should be draggable. Setting
     * {@code null} means that the default of the schedule is applied.
     * Otherwise, this setting overrides the default of the schedule.
     */
    public void setDraggable(Boolean draggable) {
        this.draggable = draggable;
    }

    /**
     * @param resizable Whether the event should be resizable. Setting
     * {@code null} means that the default of the schedule is applied.
     * Otherwise, this setting overrides the default of the schedule.
     */
    public void setResizable(Boolean resizable) {
        this.resizable = resizable;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultScheduleEvent<?> that = (DefaultScheduleEvent<?>) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, startDate, endDate);
    }

    @Override
    public String toString() {
        return "DefaultScheduleEvent{title=" + title + ",startDate=" + startDate + ",endDate=" + endDate + "}";
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {

        private DefaultScheduleEvent<T> scheduleEvent;

        private Builder() {
            scheduleEvent = new DefaultScheduleEvent<>();
        }

        public DefaultScheduleEvent.Builder<T> id(String id) {
            scheduleEvent.setId(id);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> groupId(String groupId) {
            scheduleEvent.setGroupId(groupId);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> title(String title) {
            scheduleEvent.setTitle(title);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> startDate(LocalDateTime startDate) {
            scheduleEvent.setStartDate(startDate);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> endDate(LocalDateTime endDate) {
            scheduleEvent.setEndDate(endDate);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> allDay(boolean allDay) {
            scheduleEvent.setAllDay(allDay);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> styleClass(String styleClass) {
            scheduleEvent.setStyleClass(styleClass);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> data(T data) {
            scheduleEvent.setData(data);
            return this;
        }

        /**
         * Makes the event both resizable and draggable.This is a shortcut
         * for calling {@link #resizable(Boolean)} and
         * {@link #draggable(Boolean)}.
         * @param editable Whether the event should be editable.
         */
        public DefaultScheduleEvent.Builder<T> editable(boolean editable) {
            scheduleEvent.setEditable(editable);
            return this;
        }

        /**
         * @param draggable Whether the event should be draggable. Passing
         * {@code null} means that the default of the schedule is applied.
         * Otherwise, this setting overrides the default of the schedule.
         */
        public DefaultScheduleEvent.Builder<T> draggable(Boolean draggable) {
            scheduleEvent.setDraggable(draggable);
            return this;
        }

        /**
         * @param resizable Whether the event should be resizable. Passing
         * {@code null} means that the default of the schedule is applied.
         * Otherwise, this setting overrides the default of the schedule.
         */
        public DefaultScheduleEvent.Builder<T> resizable(Boolean resizable) {
            scheduleEvent.setResizable(resizable);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> overlapAllowed(boolean overlapAllowed) {
            scheduleEvent.setOverlapAllowed(overlapAllowed);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> description(String description) {
            scheduleEvent.setDescription(description);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> url(String url) {
            scheduleEvent.setUrl(url);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> renderingMode(ScheduleRenderingMode renderingMode) {
            scheduleEvent.setRenderingMode(renderingMode);
            return this;
        }

        public DefaultScheduleEvent.Builder<T> dynamicProperty(String key, Object value) {
            scheduleEvent.setDynamicProperty(key, value);
            return this;
        }

        public DefaultScheduleEvent<T> build() {
            return scheduleEvent;
        }
    }
}
