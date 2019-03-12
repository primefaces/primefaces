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
import java.util.Date;

public class TimelineEvent implements Serializable {

    private static final long serialVersionUID = 20130316L;

    /**
     * any custom data object (required to show content of the event)
     */
    private Object data;

    /**
     * event's start date (required)
     */
    private Date startDate;

    /**
     * event's end date (optional)
     */
    private Date endDate;

    /**
     * is this event editable? (optional. if null, see the timeline's attribute "editable"
     */
    private Boolean editable;

    /**
     * group this event belongs to (optional). this can be either the group's content or group's position in the list of all groups
     */
    private String group;

    /**
     * any custom style class for this event in UI (optional)
     */
    private String styleClass;

    public TimelineEvent() {
    }

    public TimelineEvent(Object data, Date startDate) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
    }

    public TimelineEvent(Object data, Date startDate, Boolean editable) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.editable = editable;
    }

    public TimelineEvent(Object data, Date startDate, Boolean editable, String group) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.editable = editable;
        this.group = group;
    }

    public TimelineEvent(Object data, Date startDate, Boolean editable, String group, String styleClass) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.editable = editable;
        this.group = group;
        this.styleClass = styleClass;
    }

    public TimelineEvent(Object data, Date startDate, Date endDate) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TimelineEvent(Object data, Date startDate, Date endDate, Boolean editable) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.endDate = endDate;
        this.editable = editable;
    }

    public TimelineEvent(Object data, Date startDate, Date endDate, Boolean editable, String group) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.endDate = endDate;
        this.editable = editable;
        this.group = group;
    }

    public TimelineEvent(Object data, Date startDate, Date endDate, Boolean editable, String group, String styleClass) {
        checkStartDate(startDate);
        this.data = data;
        this.startDate = startDate;
        this.endDate = endDate;
        this.editable = editable;
        this.group = group;
        this.styleClass = styleClass;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimelineEvent that = (TimelineEvent) o;

        if (data != null ? !data.equals(that.data) : that.data != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TimelineEvent{"
                + "data=" + data
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + ", editable=" + editable
                + ", group='" + group + '\''
                + ", styleClass='" + styleClass + '\''
                + '}';
    }

    private void checkStartDate(Date startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Event start date can not be null!");
        }
    }
}
