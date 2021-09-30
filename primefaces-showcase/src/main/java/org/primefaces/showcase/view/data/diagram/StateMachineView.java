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

import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StateMachineConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("diagramStateMachineView")
@RequestScoped
public class StateMachineView {

    private DefaultDiagramModel model;

    @PostConstruct
    public void init() {
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);

        StateMachineConnector connector = new StateMachineConnector();
        connector.setOrientation(StateMachineConnector.Orientation.ANTICLOCKWISE);
        connector.setPaintStyle("{stroke:'#7D7463',strokeWidth:3}");
        model.setDefaultConnector(connector);

        Element start = new Element(null, "15em", "5em");
        start.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
        start.setStyleClass("start-node");

        Element idle = new Element("Idle", "10em", "20em");
        idle.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
        idle.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_RIGHT));
        idle.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_LEFT));

        Element turnedOn = new Element("TurnedOn", "10em", "35em");
        turnedOn.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
        turnedOn.addEndPoint(new BlankEndPoint(EndPointAnchor.RIGHT));
        turnedOn.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_RIGHT));

        Element activity = new Element("Activity", "45em", "35em");
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.LEFT));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_LEFT));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP_RIGHT));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP_LEFT));

        model.addElement(start);
        model.addElement(idle);
        model.addElement(turnedOn);
        model.addElement(activity);

        model.connect(createConnection(start.getEndPoints().get(0), idle.getEndPoints().get(0), null));
        model.connect(createConnection(idle.getEndPoints().get(1), turnedOn.getEndPoints().get(0), "Turn On"));
        model.connect(createConnection(turnedOn.getEndPoints().get(0), idle.getEndPoints().get(2), "Turn Off"));
        model.connect(createConnection(turnedOn.getEndPoints().get(1), activity.getEndPoints().get(0), null));
        model.connect(createConnection(activity.getEndPoints().get(1), turnedOn.getEndPoints().get(2), "Request Turn Off"));
        model.connect(createConnection(activity.getEndPoints().get(2), activity.getEndPoints().get(2), "Talk"));
        model.connect(createConnection(activity.getEndPoints().get(3), activity.getEndPoints().get(3), "Run"));
        model.connect(createConnection(activity.getEndPoints().get(4), activity.getEndPoints().get(4), "Walk"));
    }

    public DiagramModel getModel() {
        return model;
    }

    private Connection createConnection(EndPoint from, EndPoint to, String label) {
        Connection conn = new Connection(from, to);
        conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));

        if (label != null) {
            conn.getOverlays().add(new LabelOverlay(label, "flow-label", 0.5));
        }

        return conn;
    }
}
