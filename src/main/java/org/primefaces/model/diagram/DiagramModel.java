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

import java.util.List;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.overlay.Overlay;

public interface DiagramModel {
    
    public List<Element> getElements();
    
    public List<Connection> getConnections();
    
    public void addElement(Element element);
    
    public void removeElement(Element element);
    
    public void clearElements();
    
    public void connect(Connection connection);
    
    public void disconnect(Connection connection);
    
    public Connector getDefaultConnector();
    
    public List<Overlay> getDefaultConnectionOverlays();
    
    public boolean isConnectionsDetachable();
    
    public Element findElement(String id);
    
    public EndPoint findEndPoint(Element element, String id);
    
    public int getMaxConnections();
}
