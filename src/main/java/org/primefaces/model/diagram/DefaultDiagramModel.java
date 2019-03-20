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
package org.primefaces.model.diagram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.overlay.Overlay;

public class DefaultDiagramModel implements DiagramModel, Serializable {

    private static final long serialVersionUID = 1L;

    private List<Element> elements;

    private List<Connection> connections;

    private Connector defaultConnector;

    private List<Overlay> defaultConnectionOverlays;

    private boolean connectionsDetachable = true;

    private int maxConnections = 1;

    private boolean containment = true;

    public DefaultDiagramModel() {
        elements = new ElementList();
        connections = new ArrayList<>();
        defaultConnectionOverlays = new ArrayList<>();
    }

    @Override
    public List<Element> getElements() {
        return elements;
    }

    @Override
    public void addElement(Element element) {
        elements.add(element);
    }

    @Override
    public void removeElement(Element element) {
        elements.remove(element);
    }

    public void clear() {
        elements.clear();
    }

    @Override
    public void clearElements() {
        elements.clear();
    }

    @Override
    public List<Connection> getConnections() {
        return connections;
    }

    @Override
    public void connect(Connection connection) {
        this.connections.add(connection);
    }

    @Override
    public void disconnect(Connection connection) {
        this.connections.remove(connection);
    }

    @Override
    public Connector getDefaultConnector() {
        return defaultConnector;
    }

    public void setDefaultConnector(Connector defaultConnector) {
        this.defaultConnector = defaultConnector;
    }

    @Override
    public List<Overlay> getDefaultConnectionOverlays() {
        return this.defaultConnectionOverlays;
    }

    @Override
    public boolean isConnectionsDetachable() {
        return connectionsDetachable;
    }

    public void setConnectionsDetachable(boolean connectionsDetachable) {
        this.connectionsDetachable = connectionsDetachable;
    }

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public Element findElement(String id) {
        Element element = null;
        if (elements != null && !elements.isEmpty()) {
            for (int i = 0; i < elements.size(); i++) {
                Element el = elements.get(i);

                if (el.getId().equals(id)) {
                    element = el;
                    break;
                }
            }
        }

        return element;
    }

    @Override
    public EndPoint findEndPoint(Element element, String id) {
        EndPoint endPoint = null;
        List<EndPoint> endPoints = element.getEndPoints();

        if (endPoints != null && !endPoints.isEmpty()) {
            for (int i = 0; i < endPoints.size(); i++) {
                EndPoint ep = endPoints.get(i);

                if (ep.getId().equals(id)) {
                    endPoint = ep;
                    break;
                }
            }
        }

        return endPoint;
    }

    @Override
    public boolean isContainment() {
        return containment;
    }

    public void setContainment(boolean containment) {
        this.containment = containment;
    }
}
