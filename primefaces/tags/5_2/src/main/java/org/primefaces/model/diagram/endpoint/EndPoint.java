/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.model.diagram.endpoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.diagram.overlay.Overlay;

public abstract class EndPoint implements Serializable {
    
    private String id;
    
    private EndPointAnchor anchor;
    
    private int maxConnections = 1;
    
    private String styleClass;
    
    private String hoverStyleClass;
    
    private String style;
    
    private String hoverStyle;
    
    private boolean source;
    
    private boolean target;
    
    private List<Overlay> overlays;
    
    private String scope;
    
    public abstract String getType();
    
    public abstract String toJS(StringBuilder sb);

    public EndPoint() {
        overlays = new ArrayList<Overlay>();
    }

    public EndPoint(EndPointAnchor anchor) {
        this();
        this.anchor = anchor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EndPointAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(EndPointAnchor anchor) {
        this.anchor = anchor;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getHoverStyleClass() {
        return hoverStyleClass;
    }

    public void setHoverStyleClass(String hoverStyleClass) {
        this.hoverStyleClass = hoverStyleClass;
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    public boolean isTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getHoverStyle() {
        return hoverStyle;
    }

    public void setHoverStyle(String hoverStyle) {
        this.hoverStyle = hoverStyle;
    }

    public List<Overlay> getOverlays() {
        return overlays;
    }

    public void setOverlays(List<Overlay> overlays) {
        this.overlays = overlays;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EndPoint other = (EndPoint) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
