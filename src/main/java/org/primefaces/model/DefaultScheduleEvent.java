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

import org.primefaces.util.CalendarUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultScheduleEvent implements ScheduleEvent, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private LocalDateTime startLocalDateTime;
    private LocalDateTime endLocalDateTime;
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

    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end) {
        this.title = title;
        this.startLocalDateTime = start;
        this.endLocalDateTime = end;
    }

    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end, boolean allDay) {
        this.title = title;
        this.startLocalDateTime = start;
        this.endLocalDateTime = end;
        this.allDay = allDay;
    }

    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end, String styleClass) {
        this.title = title;
        this.startLocalDateTime = start;
        this.endLocalDateTime = end;
        this.styleClass = styleClass;
    }

    public DefaultScheduleEvent(String title, LocalDateTime start, LocalDateTime end, Object data) {
        this.title = title;
        this.startLocalDateTime = start;
        this.endLocalDateTime = end;
        this.data = data;
    }

    /**
     * @deprecated  Use {@link #DefaultScheduleEvent(String, LocalDateTime, LocalDateTime)} instead.
     */
    public DefaultScheduleEvent(String title, Date start, Date end) {
        this.title = title;
        this.startLocalDateTime = CalendarUtils.convertDate2LocalDateTime(start);
        this.endLocalDateTime = CalendarUtils.convertDate2LocalDateTime(end);
    }

    /**
     * @deprecated  Use {@link #DefaultScheduleEvent(String, LocalDateTime, LocalDateTime, boolean)} instead.
     */
    public DefaultScheduleEvent(String title, Date start, Date end, boolean allDay) {
        this.title = title;
        this.startLocalDateTime = CalendarUtils.convertDate2LocalDateTime(start);
        this.endLocalDateTime = CalendarUtils.convertDate2LocalDateTime(end);
        this.allDay = allDay;
    }

    /**
     * @deprecated  Use {@link #DefaultScheduleEvent(String, LocalDateTime, LocalDateTime, String)} instead.
     */
    public DefaultScheduleEvent(String title, Date start, Date end, String styleClass) {
        this.title = title;
        this.startLocalDateTime = CalendarUtils.convertDate2LocalDateTime(start);
        this.endLocalDateTime = CalendarUtils.convertDate2LocalDateTime(end);
        this.styleClass = styleClass;
    }

    /**
     * @deprecated  Use {@link #DefaultScheduleEvent(String, LocalDateTime, LocalDateTime, Object)} instead.
     */
    public DefaultScheduleEvent(String title, Date start, Date end, Object data) {
        this.title = title;

        this.startLocalDateTime = CalendarUtils.convertDate2LocalDateTime(start);
        this.endLocalDateTime = CalendarUtils.convertDate2LocalDateTime(end);
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
    public LocalDateTime getStartLocalDateTime() {
        return startLocalDateTime;
    }

    /**
     * @deprecated Use {@link #getStartLocalDateTime()} instead.
     * @return
     */
    @Override
    public Date getStartDate() {
        return CalendarUtils.convertLocalDateTime2Date(startLocalDateTime);
    }

    public void setStartLocalDateTime(LocalDateTime startDate) {
        this.startLocalDateTime = startDate;
    }

    /**
     * @deprecated Use {@link #setStartLocalDateTime(LocalDateTime)} instead.
     * @param startLocalDate
     */
    public void setStartLocalDate(Date startLocalDate) {
        this.startLocalDateTime = CalendarUtils.convertDate2LocalDateTime(startLocalDate);
    }

    @Override
    public LocalDateTime getEndLocalDateTime() {
        return endLocalDateTime;
    }

    /**
     * @deprecated Use {@link #getEndLocalDateTime()} instead.
     * @return
     */
    @Override
    public Date getEndDate() {
        return CalendarUtils.convertLocalDateTime2Date(endLocalDateTime);
    }

    public void setEndLocalDateTime(LocalDateTime endDate) {
        this.endLocalDateTime = endDate;
    }

    /**
     * @deprecated Use {@link #setEndLocalDateTime(LocalDateTime)}  instead.
     * @param endLocalDate
     */
    public void setEndLocalDate(Date endLocalDate) {
        this.endLocalDateTime = CalendarUtils.convertDate2LocalDateTime(endLocalDate);
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
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.startLocalDateTime, other.startLocalDateTime)) {
            return false;
        }
        if (!Objects.equals(this.endLocalDateTime, other.endLocalDateTime)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 61 * hash + (this.startLocalDateTime != null ? this.startLocalDateTime.hashCode() : 0);
        hash = 61 * hash + (this.endLocalDateTime != null ? this.endLocalDateTime.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DefaultScheduleEvent{title=" + title + ",startDate=" + startLocalDateTime + ",endDate=" + endLocalDateTime + "}";
    }
}
