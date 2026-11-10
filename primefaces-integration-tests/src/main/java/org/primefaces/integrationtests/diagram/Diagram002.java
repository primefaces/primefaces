/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.diagram;

import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Diagram002 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private DefaultDiagramModel model;

    @PostConstruct
    public void init() {
        model = new DefaultDiagramModel();

        Element a = createElement("A", "2em", "2em");
        EndPoint aRight = createEndPoint(a, "a-right", EndPointAnchor.RIGHT, true);
        createEndPoint(a, "a-bottom", EndPointAnchor.BOTTOM, true);

        Element b = createElement("B", "20em", "2em");
        EndPoint bLeft = createEndPoint(b, "b-left", EndPointAnchor.LEFT, false);

        Element c = createElement("C", "2em", "15em");
        createEndPoint(c, "c-top", EndPointAnchor.TOP, false);

        model.connect(new Connection(aRight, bLeft));
    }

    public int getConnectionCount() {
        return model.getConnections().size();
    }

    public void onConnect(ConnectEvent event) {
        TestUtils.addMessage("Connected", event.getSourceElement().getData() + "-" + event.getTargetElement().getData());
    }

    public void onDisconnect(DisconnectEvent event) {
        TestUtils.addMessage("Disconnected", event.getSourceElement().getData() + "-" + event.getTargetElement().getData());
    }

    private Element createElement(String data, String x, String y) {
        Element element = new Element(data, x, y);
        element.setId(data);
        model.addElement(element);
        return element;
    }

    private EndPoint createEndPoint(Element element, String id, EndPointAnchor anchor, boolean source) {
        EndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setId(id);
        endPoint.setSource(source);
        endPoint.setTarget(!source);
        element.addEndPoint(endPoint);
        return endPoint;
    }
}
