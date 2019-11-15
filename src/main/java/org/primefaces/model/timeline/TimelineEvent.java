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
package org.primefaces.model.timeline;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TimelineEvent<T> implements Serializable {

    private static final long serialVersionUID = 20130316L;

    /**
     * a unique id for this event.
     */
    private String id;

    /**
     * any custom data object (required to show content of the event)
     */
    private T data;

    /**
     * event's start date (required)
     */
    private LocalDateTime startDate;

    /**
     * event's end date (optional)
     */
    private LocalDateTime endDate;

    /**
     * is this event editable? (optional). if null, see the timeline's attribute "editable"
     */
    private Boolean editable;

    /**
     * is this event time editable? (optional). Overrides editable. if null, see the timeline's attribute "editableTime"
     */
    private Boolean editableTime;

    /**
     * is this event group editable? (optional). Overrides editable. if null, see the timeline's attribute "editable"
     */
    private Boolean editableGroup;

    /**
     * is this event removable? (optional). Overrides editable. if null, see the timeline's attribute "editable"
     */
    private Boolean editableRemove;

    /**
     * group this event belongs to (optional). this can be either the group's content or group's position in the list of all groups
     */
    private String group;

    /**
     * A title that is displayed when holding the mouse on the item. The title can be a string containing plain text or HTML (optional).
     */
    private String title;

    /**
     * any custom style class for this event in UI (optional)
     */
    private String styleClass;

    public TimelineEvent() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate) {
        this(data, startDate, null, null, null, null);
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDate startDate) {
        this(data, startDate.atStartOfDay(), null, null, null, null);
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, Boolean editable) {
        this(data, startDate, null, editable, null, null);
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, Boolean editable, String group) {
        this(data, startDate, null, editable, group, null);
    }

    /**
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, Boolean editable, String group, String styleClass) {
        this(data, startDate, null, editable, group, styleClass);
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, LocalDateTime endDate) {
        this(data, startDate, endDate, null, null, null);
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDate startDate, LocalDate endDate) {
        this(data, startDate.atStartOfDay(), endDate.atStartOfDay(), null, null, null);
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, LocalDateTime endDate, Boolean editable) {
        this(data, startDate, endDate, editable, null, null);
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, LocalDateTime endDate, Boolean editable, String group) {
        this(data, startDate, endDate, editable, group, null);
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(T data, LocalDateTime startDate, LocalDateTime endDate, Boolean editable, String group, String styleClass) {
        this(UUID.randomUUID().toString(), data, startDate, endDate, editable, group, styleClass);
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(String id, T data, LocalDateTime startDate, LocalDateTime endDate, Boolean editable, String group, String styleClass) {
        checkStartDate(startDate);
        this.id = id;
        this.data = data;
        this.startDate = startDate;
        this.endDate = endDate;
        this.editable = editable;
        this.editableTime = editable;
        this.editableGroup = editable;
        this.editableRemove = editable;
        this.group = group;
        this.styleClass = styleClass;
    }

    /**
     * @deprecated Use {@link TimelineEvent.Builder} instead.
     */
    @Deprecated
    public TimelineEvent(TimelineEvent<T> event) {
        this.id = event.id;
        this.data = event.data;
        this.startDate = event.startDate;
        this.endDate = event.endDate;
        this.editable = event.editable;
        this.editableTime = event.editableTime;
        this.editableGroup = event.editableGroup;
        this.editableRemove = event.editableRemove;
        this.group = event.group;
        this.title = event.title;
        this.styleClass = event.styleClass;
    }

    public TimelineEvent<T> clone() {
        return new TimelineEvent<T>(this);
    }

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public TimelineEvent<T> setData(T data) {
        this.data = data;
        return this;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public TimelineEvent<T> setStartDate(LocalDateTime startDate) {
        checkStartDate(startDate);
        this.startDate = startDate;
        return this;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public TimelineEvent<T> setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public Boolean isEditable() {
        return editable;
    }

    public TimelineEvent<T> setEditable(Boolean editable) {
        this.editable = editable;
        this.editableTime = editable;
        this.editableGroup = editable;
        this.editableRemove = editable;
        return this;
    }

    public Boolean isEditableTime() {
        return editableTime;
    }

    public TimelineEvent<T> setEditableTime(Boolean editableTime) {
        this.editableTime = editableTime;
        return this;
    }

    public Boolean isEditableGroup() {
        return editableGroup;
    }

    public TimelineEvent<T> setEditableGroup(Boolean editableGroup) {
        this.editableGroup = editableGroup;
        return this;
    }

    public Boolean isEditableRemove() {
        return editableRemove;
    }

    public TimelineEvent<T> setEditableRemove(Boolean editableRemove) {
        this.editableRemove = editableRemove;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public TimelineEvent<T> setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public TimelineEvent<T> setStyleClass(String styleClass) {
        this.styleClass = styleClass;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TimelineEvent<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimelineEvent<?> other = (TimelineEvent<?>) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "TimelineEvent{"
                + "id=" + id
                + ", data=" + data
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + ", editable=" + editable
                + ", group='" + group + '\''
                + ", styleClass='" + styleClass + '\''
                + '}';
    }

    private void checkStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Event start date can not be null!");
        }
    }

    public static final class Builder<T> {
        private final TimelineEvent<T> event;

        private Builder() {
            event = new TimelineEvent<>();
        }

        public Builder<T> id(String id) {
            event.id = id;
            return this;
        }

        public Builder<T> data(T data) {
            event.setData(data);
            return this;
        }

        public Builder<T> startDate(LocalDateTime startDate) {
            event.setStartDate(startDate);
            return this;
        }

        public Builder<T> endDate(LocalDateTime endDate) {
            event.setEndDate(endDate);
            return this;
        }

        public Builder<T> editable(Boolean editable) {
            event.setEditable(editable);
            return this;
        }

        public Builder<T> group(String group) {
            event.setGroup(group);
            return this;
        }

        public Builder<T> styleClass(String styleClass) {
            event.setStyleClass(styleClass);
            return this;
        }

        public Builder<T> title(String title) {
            event.setTitle(title);
            return this;
        }

        public Builder<T> editableTime(Boolean editableTime) {
            event.setEditableTime(editableTime);
            return this;
        }

        public Builder<T> editableGroup(Boolean editableGroup) {
            event.setEditableGroup(editableGroup);
            return this;
        }

        public Builder<T> editableRemove(Boolean editableRemove) {
            event.setEditableRemove(editableRemove);
            return this;
        }

        public TimelineEvent<T> build() {
            return event;
        }
    }
}
