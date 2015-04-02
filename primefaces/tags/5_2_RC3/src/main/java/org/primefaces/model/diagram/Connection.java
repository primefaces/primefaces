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
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.overlay.Overlay;

public class Connection implements Serializable {
    
    private EndPoint source;
    
    private EndPoint target;
    
    private Connector connector;
    
    private List<Overlay> overlays;
    
    private boolean detachable = true;

    public Connection() {
        overlays = new ArrayList<Overlay>();
    }
    
    public Connection(EndPoint source, EndPoint target) {
        this();
        this.source = source;
        this.target = target;
    }
    
    public Connection(EndPoint source, EndPoint target, Connector connector) {
        this(source, target);
        this.connector = connector;
    }

    public EndPoint getSource() {
        return source;
    }

    public void setSource(EndPoint source) {
        this.source = source;
    }

    public EndPoint getTarget() {
        return target;
    }

    public void setTarget(EndPoint target) {
        this.target = target;
    }
    
    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public List<Overlay> getOverlays() {
        return overlays;
    }

    public void setOverlays(List<Overlay> overlays) {
        this.overlays = overlays;
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 89 * hash + (this.target != null ? this.target.hashCode() : 0);
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
        final Connection other = (Connection) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.target != other.target && (this.target == null || !this.target.equals(other.target))) {
            return false;
        }
        return true;
    }
}
