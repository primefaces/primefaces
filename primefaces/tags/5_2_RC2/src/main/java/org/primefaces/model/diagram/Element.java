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
package org.primefaces.model.diagram;

import java.io.Serializable;
import java.util.List;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointList;

public class Element implements Serializable {
    
    private String id;
    
    private Object data;
    
    private String styleClass;
    
    private String x;
    
    private String y;
    
    private List<EndPoint> endPoints;
    
    private boolean draggable = true;

    public Element() {
        endPoints = new EndPointList();
    }
    
    public Element(Object data) {
        this();
        this.data = data;
    }
    
    public Element(Object data, String x, String y) {
        this(data);
        this.x = x;
        this.y = y;
    }
        
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public List<EndPoint> getEndPoints() {
        return endPoints;
    }
    
    public void addEndPoint(EndPoint endPoint) {
        this.endPoints.add(endPoint);
    }
    
    public void removeEndPoint(EndPoint endPoint) {
        this.endPoints.remove(endPoint);
    }
        
    public void clearEndPoints(EndPoint endPoint) {
        this.endPoints.clear();
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Element other = (Element) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
