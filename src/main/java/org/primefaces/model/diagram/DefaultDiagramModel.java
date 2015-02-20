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

import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.overlay.Overlay;

public class DefaultDiagramModel implements DiagramModel {
    
    private List<Element> elements;
    
    private List<Connection> connections;
    
    private Connector defaultConnector;
    
    private List<Overlay> defaultConnectionOverlays;
    
    private boolean connectionsDetachable = true;
    
    private int maxConnections = 1;

    public DefaultDiagramModel() {
        elements = new ElementList();
        connections = new ArrayList<Connection>();
        defaultConnectionOverlays = new ArrayList<Overlay>();
    }
    
    public List<Element> getElements() {
        return elements;
    }

    public void addElement(Element element) {
        elements.add(element);
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public void clear() {
        elements.clear();
    }
    
    public void clearElements() {
        elements.clear();
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void connect(Connection connection) {
        this.connections.add(connection);
    }

    public void disconnect(Connection connection) {
        this.connections.remove(connection);
    }
    
    public Connector getDefaultConnector() {
        return defaultConnector;
    }

    public void setDefaultConnector(Connector defaultConnector) {
        this.defaultConnector = defaultConnector;
    }

    public List<Overlay> getDefaultConnectionOverlays() {
        return this.defaultConnectionOverlays;
    }

    public boolean isConnectionsDetachable() {
        return connectionsDetachable;
    }

    public void setConnectionsDetachable(boolean connectionsDetachable) {
        this.connectionsDetachable = connectionsDetachable;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public Element findElement(String id) {
        Element element = null;
        if(elements != null && !elements.isEmpty()) {
            for(int i = 0; i < elements.size(); i++) {
                Element el = elements.get(i);
                
                if(el.getId().equals(id)) {
                    element = el;
                    break;
                }
            }
        }
        
        return element;
    }

    public EndPoint findEndPoint(Element element, String id) {
        EndPoint endPoint = null;
        List<EndPoint> endPoints = element.getEndPoints();
        
        if(endPoints != null && !endPoints.isEmpty()) {
            for(int i = 0; i < endPoints.size(); i++) {
                EndPoint ep = endPoints.get(i);
                
                if(ep.getId().equals(id)) {
                    endPoint = ep;
                    break;
                }
            }
        }
        
        return endPoint;
    }
}
