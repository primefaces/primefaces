/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.model.timeline;

import java.util.Date;

public class DefaultTimelineEvent implements TimelineEvent {
    
    private String id;
    
    private String title;
    
    private String description;
    
    private Date startDate;
    
    private Date endDate;
    
    private int importance = 35;
    
    private String icon;
    
    public DefaultTimelineEvent() {
        
    }
    
    public DefaultTimelineEvent(String title) {
        this.title = title;
    }

    public DefaultTimelineEvent(String title, String description, Date startDate, Date endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public DefaultTimelineEvent(String title, String description, Date startDate, Date endDate, String icon) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final DefaultTimelineEvent other = (DefaultTimelineEvent) obj;
        if((this.title == null) ? (other.title != null) : !this.title.equals(other.title))
            return false;
        if((this.description == null) ? (other.description != null) : !this.description.equals(other.description))
            return false;
        if(this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate)))
            return false;
        if(this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 79 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 79 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DefaultTimelineEvent{" + "id=" + id + ", title=" + title + ", description=" + description + ", startDate=" + startDate + ", endDate=" + endDate + ", importance=" + importance + '}';
    }
}
