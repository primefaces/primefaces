/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.model.tagcloud;

import java.io.Serializable;

public class DefaultTagCloudItem implements TagCloudItem, Serializable {

    private String label;

    private String url;

    private int strength = 1;

    public DefaultTagCloudItem() {

    }
    
    public DefaultTagCloudItem(String label, int strength) {
        this.label = label;
        this.strength = strength;
    }

    public DefaultTagCloudItem(String label, String url, int strength) {
        this(label, strength);
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TagCloudItem other = (TagCloudItem) obj;
        if ((this.label == null) ? (other.getLabel() != null) : !this.label.equals(other.getLabel())) {
            return false;
        }
        if ((this.url == null) ? (other.getUrl() != null) : !this.url.equals(other.getUrl())) {
            return false;
        }
        if (this.strength != other.getStrength()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 73 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 73 * hash + this.strength;
        return hash;
    }
}
