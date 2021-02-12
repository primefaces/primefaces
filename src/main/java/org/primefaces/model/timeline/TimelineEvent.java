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
     * The id of a subgroup. Groups all items within a group per subgroup, and positions them on the same height instead
     *  of staking them on top of each other. can be ordered by specifying the option subgroupOrder of a group.
     */
    private String subgroup;

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

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        checkStartDate(startDate);
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
        this.editableTime = editable;
        this.editableGroup = editable;
        this.editableRemove = editable;
    }

    public Boolean isEditableTime() {
        return editableTime;
    }

    public void setEditableTime(Boolean editableTime) {
        this.editableTime = editableTime;
    }

    public Boolean isEditableGroup() {
        return editableGroup;
    }

    public void setEditableGroup(Boolean editableGroup) {
        this.editableGroup = editableGroup;
    }

    public Boolean isEditableRemove() {
        return editableRemove;
    }

    public void setEditableRemove(Boolean editableRemove) {
        this.editableRemove = editableRemove;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Builder<T> builder(TimelineEvent<T> event) {
        Builder<T> builder = builder();

        builder.id(event.id);
        builder.data(event.data);
        builder.startDate(event.startDate);
        builder.endDate(event.endDate);
        builder.editable(event.editable);
        builder.editableTime(event.editableTime);
        builder.editableGroup(event.editableGroup);
        builder.editableRemove(event.editableRemove);
        builder.group(event.group);
        builder.subgroup(event.subgroup);
        builder.title(event.title);
        builder.styleClass(event.styleClass);

        return builder;
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
                + ", subgroup='" + subgroup + '\''
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

        public Builder<T> startDate(LocalDate startDate) {
            event.setStartDate(startDate.atStartOfDay());
            return this;
        }

        public Builder<T> endDate(LocalDateTime endDate) {
            event.setEndDate(endDate);
            return this;
        }

        public Builder<T> endDate(LocalDate endDate) {
            event.setEndDate(endDate.atStartOfDay());
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

        public Builder<T> subgroup(String subgroup) {
            event.setSubgroup(subgroup);
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
