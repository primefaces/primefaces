/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.showcase.view.data.diagram;

import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.ArrowOverlay;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;

@Named("diagramEditableView")
@ViewScoped
public class EditableView implements Serializable {

    private DefaultDiagramModel model;

    private boolean suspendEvent;

    @PostConstruct
    public void init() {
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        model.setContainment(false);

        model.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        StraightConnector connector = new StraightConnector();
        connector.setPaintStyle("{stroke:'#98AFC7', strokeWidth:3}");
        connector.setHoverPaintStyle("{stroke:'#5C738B'}");
        model.setDefaultConnector(connector);

        Element computerA = new Element(new NetworkElement("Computer A", "computer-icon.png"), "10em", "6em");
        EndPoint endPointCA = createRectangleEndPoint(EndPointAnchor.BOTTOM);
        endPointCA.setSource(true);
        computerA.addEndPoint(endPointCA);

        Element computerB = new Element(new NetworkElement("Computer B", "computer-icon.png"), "25em", "6em");
        EndPoint endPointCB = createRectangleEndPoint(EndPointAnchor.BOTTOM);
        endPointCB.setSource(true);
        computerB.addEndPoint(endPointCB);

        Element computerC = new Element(new NetworkElement("Computer C", "computer-icon.png"), "40em", "6em");
        EndPoint endPointCC = createRectangleEndPoint(EndPointAnchor.BOTTOM);
        endPointCC.setSource(true);
        computerC.addEndPoint(endPointCC);

        Element serverA = new Element(new NetworkElement("Server A", "server-icon.png"), "15em", "24em");
        EndPoint endPointSA = createDotEndPoint(EndPointAnchor.AUTO_DEFAULT);
        serverA.setDraggable(false);
        endPointSA.setTarget(true);
        serverA.addEndPoint(endPointSA);

        Element serverB = new Element(new NetworkElement("Server B", "server-icon.png"), "35em", "24em");
        EndPoint endPointSB = createDotEndPoint(EndPointAnchor.AUTO_DEFAULT);
        serverB.setDraggable(false);
        endPointSB.setTarget(true);
        serverB.addEndPoint(endPointSB);

        model.addElement(computerA);
        model.addElement(computerB);
        model.addElement(computerC);
        model.addElement(serverA);
        model.addElement(serverB);
    }

    public DiagramModel getModel() {
        return model;
    }

    public void onConnect(ConnectEvent event) {
        if (!suspendEvent) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Connected",
                    "From " + event.getSourceElement().getData() + " To " + event.getTargetElement().getData());

            FacesContext.getCurrentInstance().addMessage(null, msg);

            PrimeFaces.current().ajax().update("form:msgs");
        }
        else {
            suspendEvent = false;
        }
    }

    public void onDisconnect(DisconnectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Disconnected",
                "From " + event.getSourceElement().getData() + " To " + event.getTargetElement().getData());

        FacesContext.getCurrentInstance().addMessage(null, msg);

        PrimeFaces.current().ajax().update("form:msgs");
    }

    public void onConnectionChange(ConnectionChangeEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Connection Changed",
                "Original Source:" + event.getOriginalSourceElement().getData()
                + ", New Source: " + event.getNewSourceElement().getData()
                + ", Original Target: " + event.getOriginalTargetElement().getData()
                + ", New Target: " + event.getNewTargetElement().getData());

        FacesContext.getCurrentInstance().addMessage(null, msg);

        PrimeFaces.current().ajax().update("form:msgs");
        suspendEvent = true;
    }

    private EndPoint createDotEndPoint(EndPointAnchor anchor) {
        DotEndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setTarget(true);
        endPoint.setStyle("{fill:'#98AFC7'}");
        endPoint.setHoverStyle("{fill:'#5C738B'}");

        return endPoint;
    }

    private EndPoint createRectangleEndPoint(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setSource(true);
        endPoint.setStyle("{fill:'#98AFC7'}");
        endPoint.setHoverStyle("{fill:'#5C738B'}");

        return endPoint;
    }

    public class NetworkElement implements Serializable {

        private String name;
        private String image;

        public NetworkElement() {
        }

        public NetworkElement(String name, String image) {
            this.name = name;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
