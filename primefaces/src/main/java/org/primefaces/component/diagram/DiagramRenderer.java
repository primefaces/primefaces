/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.diagram;

import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.overlay.Overlay;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Diagram.DEFAULT_RENDERER, componentFamily = Diagram.COMPONENT_FAMILY)
public class DiagramRenderer extends CoreRenderer<Diagram> {

    private static final String SB_DIAGRAM = CoreRenderer.class.getName() + "#diagram";

    @Override
    public void decode(FacesContext context, Diagram component) {
        if (component.isConnectRequest(context)) {
            decodeNewConnection(context, component);
        }
        else if (component.isDisconnectRequest(context)) {
            decodeDisconnection(context, component);
        }
        else if (component.isConnectionChangeRequest(context)) {
            decodeConnectionChange(context, component);
        }
        else if (component.isPositionChangeRequest(context)) {
            decodePositionChange(context, component);
        }

        decodeBehaviors(context, component);
    }

    private void decodeNewConnection(FacesContext context, Diagram component) {
        //no need to decode since state is synced in previous connection move request
        if (context.getExternalContext().getRequestParameterMap().containsKey(component.getClientId(context) + "_connectionChanged")) {
            return;
        }

        DiagramModel model = (DiagramModel) component.getValue();
        if (model != null) {
            Connection connection = decodeConnection(context, component, true);
            if (connection != null) {
                model.connect(connection);
            }
        }
    }

    private void decodeDisconnection(FacesContext context, Diagram component) {
        DiagramModel model = (DiagramModel) component.getValue();
        if (model != null) {
            Connection connection = decodeConnection(context, component, false);
            if (connection != null) {
                model.disconnect(connection);
            }
        }
    }

    private void decodeConnectionChange(FacesContext context, Diagram component) {
        DiagramModel model = (DiagramModel) component.getValue();
        if (model != null) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = component.getClientId(context);

            Element originalSourceElement = model.findElement(params.get(clientId + "_originalSourceId"));
            Element newSourceElement = model.findElement(params.get(clientId + "_newSourceId"));
            Element originalTargetElement = model.findElement(params.get(clientId + "_originalTargetId"));
            Element newTargetElement = model.findElement(params.get(clientId + "_newTargetId"));
            EndPoint originalSourceEndPoint = model.findEndPoint(originalSourceElement, params.get(clientId + "_originalSourceEndPointId"));
            EndPoint newSourceEndPoint = model.findEndPoint(newSourceElement, params.get(clientId + "_newSourceEndPointId"));
            EndPoint originalTargetEndPoint = model.findEndPoint(originalTargetElement, params.get(clientId + "_originalTargetEndPointId"));
            EndPoint newTargetEndPoint = model.findEndPoint(newTargetElement, params.get(clientId + "_newTargetEndPointId"));

            model.disconnect(findConnection(model, originalSourceEndPoint, originalTargetEndPoint));
            model.connect(new Connection(newSourceEndPoint, newTargetEndPoint));
        }
    }

    private void decodePositionChange(FacesContext context, Diagram component) {
        DiagramModel model = (DiagramModel) component.getValue();
        if (model != null) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = component.getClientId(context);

            Element element = model.findElement(params.get(clientId + "_elementId"));
            String[] position = params.get(clientId + "_position").split(",");

            if (element != null) {
                element.setX(position[0] + "px");
                element.setY(position[1] + "px");
            }
        }
    }

    private Connection decodeConnection(FacesContext context, Diagram component, boolean createNew) {
        DiagramModel model = (DiagramModel) component.getValue();
        Connection connection = null;

        if (model != null) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = component.getClientId(context);

            Element sourceElement = model.findElement(params.get(clientId + "_sourceId"));
            Element targetElement = model.findElement(params.get(clientId + "_targetId"));
            EndPoint sourceEndPoint = model.findEndPoint(sourceElement, params.get(clientId + "_sourceEndPointId"));
            EndPoint targetEndPoint = model.findEndPoint(targetElement, params.get(clientId + "_targetEndPointId"));

            if (createNew) {
                connection = new Connection(sourceEndPoint, targetEndPoint);
            }
            else {
                connection = findConnection(model, sourceEndPoint, targetEndPoint);
            }
        }

        return connection;
    }

    private Connection findConnection(DiagramModel model, EndPoint source, EndPoint target) {
        Connection connection = null;

        if (model != null) {
            List<Connection> connections = model.getConnections();
            if (connections != null) {
                int connectionsSize = connections.size();
                for (int i = 0; i < connectionsSize; i++) {
                    Connection conn = connections.get(i);

                    if (conn.getSource().equals(source) && conn.getTarget().equals(target)) {
                        connection = conn;
                        break;
                    }
                }
            }
        }

        return connection;
    }

    @Override
    public void encodeEnd(FacesContext context, Diagram component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, Diagram component) throws IOException {
        String clientId = component.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Diagram", component);

        DiagramModel model = (DiagramModel) component.getValue();
        if (model != null) {
            encodeDefaults(wb, model);

            encodeEndPoints(wb, model, clientId);
            encodeConnections(wb, model);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeDefaults(WidgetBuilder wb, DiagramModel model) throws IOException {
        StringBuilder sb = SharedStringBuilder.get(SB_DIAGRAM);

        Connector defaultConnector = model.getDefaultConnector();
        if (defaultConnector != null) {
            String paintStyle = defaultConnector.getPaintStyle();
            String hoverPaintStyle = defaultConnector.getHoverPaintStyle();

            wb.append(",defaultConnector:").append(defaultConnector.toJS(sb));
            wb.append(",containment:").append("" + model.isContainment());

            if (paintStyle != null) {
                wb.append(",paintStyle:").append(paintStyle);
            }
            if (hoverPaintStyle != null) {
                wb.append(",hoverPaintStyle:").append(hoverPaintStyle);
            }

            sb.setLength(0);
        }

        encodeOverlays(wb, model.getDefaultConnectionOverlays(), "connectionOverlays");

        if (!model.isConnectionsDetachable()) {
            wb.append(",connectionsDetachable:false");
        }

        int maxConnections = model.getMaxConnections();
        if (maxConnections != 0) {
            wb.append(",maxConnections:").append(maxConnections);
        }
    }

    protected void encodeEndPoints(WidgetBuilder wb, DiagramModel model, String clientId) throws IOException {
        List<Element> elements = model.getElements();
        if (elements != null && !elements.isEmpty()) {
            int elementsSize = elements.size();

            wb.append(",endPoints:[");
            for (int i = 0; i < elementsSize; i++) {
                Element element = elements.get(i);
                String elementClientId = clientId + "-" + element.getId();
                List<EndPoint> endPoints = element.getEndPoints();

                if (endPoints != null && !endPoints.isEmpty()) {
                    int endPointsSize = endPoints.size();

                    for (int j = 0; j < endPointsSize; j++) {
                        EndPoint endPoint = endPoints.get(j);
                        encodeEndPoint(wb, endPoint, elementClientId);

                        if (j < (endPointsSize - 1)) {
                            wb.append(",");
                        }
                    }
                }

                if (i < (elementsSize - 1)) {
                    wb.append(",");
                }
            }
            wb.append("]");
        }
    }

    protected void encodeEndPoint(WidgetBuilder wb, EndPoint endPoint, String elementClientId) throws IOException {
        String type = endPoint.getType();
        StringBuilder sb = SharedStringBuilder.get(SB_DIAGRAM);
        String styleClass = endPoint.getStyleClass();
        String hoverStyleClass = endPoint.getHoverStyleClass();
        String style = endPoint.getStyle();
        String hoverStyle = endPoint.getHoverStyle();
        int maxConnections = endPoint.getMaxConnections();
        String scope = endPoint.getScope();

        wb.append("{uuid:'").append(endPoint.getId()).append("'")
                .append(",element:'").append(elementClientId).append("'")
                .append(",anchor:'").append(endPoint.getAnchor().toString()).append("'");

        if (maxConnections != 1) {
            wb.append(",maxConnections:").append(maxConnections);
        }
        if (style != null) {
            wb.append(",paintStyle:").append(style);
        }
        if (hoverStyle != null) {
            wb.append(",hoverPaintStyle:").append(hoverStyle);
        }
        if (endPoint.isSource()) {
            wb.append(",isSource:true");
        }
        if (endPoint.isTarget()) {
            wb.append(",isTarget:true");
        }
        if (styleClass != null) {
            wb.append(",cssClass:'").append(styleClass).append("'");
        }
        if (hoverStyleClass != null) {
            wb.append(",hoverClass:'").append(hoverStyleClass).append("'");
        }
        if (scope != null) {
            wb.append(",scope:'").append(scope).append("'");
        }

        if (type != null) {
            wb.append(",endpoint:").append(endPoint.toJS(sb));
        }

        encodeOverlays(wb, endPoint.getOverlays(), "overlays");

        wb.append("}");
    }

    protected void encodeConnections(WidgetBuilder wb, DiagramModel model) throws IOException {
        List<Connection> connections = model.getConnections();
        if (connections != null && !connections.isEmpty()) {
            int connectionsSize = connections.size();

            wb.append(",connections:[");
            for (int i = 0; i < connectionsSize; i++) {
                Connection connection = connections.get(i);
                StringBuilder sb = SharedStringBuilder.get(SB_DIAGRAM);
                Connector connector = connection.getConnector();
                List<Overlay> overlays = connection.getOverlays();

                wb.append("{uuids:['").append(connection.getSource().getId()).append("'")
                        .append(",'").append(connection.getTarget().getId()).append("']");

                if (connector != null && connector.getType() != null) {
                    wb.append(",connector:").append(connector.toJS(sb));

                    String paintStyle = connector.getPaintStyle();
                    String hoverPaintStyle = connector.getHoverPaintStyle();

                    if (paintStyle != null) {
                        wb.append(",paintStyle:").append(paintStyle);
                    }
                    if (hoverPaintStyle != null) {
                        wb.append(",hoverPaintStyle:").append(hoverPaintStyle);
                    }
                }

                if (!connection.isDetachable()) {
                    wb.append(",detachable:false");
                }

                encodeOverlays(wb, overlays, "overlays");

                wb.append("}");

                if (i < (connectionsSize - 1)) {
                    wb.append(",");
                }

            }
            wb.append("]");
        }
    }

    protected void encodeOverlays(WidgetBuilder wb, List<Overlay> overlays, String propertyName) throws IOException {
        StringBuilder sb = SharedStringBuilder.get(SB_DIAGRAM);

        if (overlays != null && !overlays.isEmpty()) {
            int overlaysSize = overlays.size();

            wb.append(",").append(propertyName).append(":[");
            for (int j = 0; j < overlaysSize; j++) {
                Overlay overlay = overlays.get(j);
                sb.setLength(0);

                wb.append(overlay.toJS(sb));

                if (j < (overlaysSize - 1)) {
                    wb.append(",");
                }
            }
            wb.append("]");
        }
    }

    protected void encodeMarkup(FacesContext context, Diagram component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DiagramModel model = (DiagramModel) component.getValue();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? Diagram.CONTAINER_CLASS : Diagram.CONTAINER_CLASS + " " + styleClass;
        UIComponent elementFacet = component.getElementFacet();

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (model != null) {
            int rowCount = component.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                component.setRowIndex(i);

                Element element = (Element) component.getRowData();
                String elementClass = element.getStyleClass();
                elementClass = (elementClass == null) ? Diagram.ELEMENT_CLASS : Diagram.ELEMENT_CLASS + " " + elementClass;
                if (element.isDraggable()) {
                    elementClass = elementClass + " " + Diagram.DRAGGABLE_ELEMENT_CLASS;
                }
                Object data = element.getData();
                String x = element.getX();
                String y = element.getY();
                String coords = "left:" + x + ";top:" + y;
                String title = element.getTitle();

                writer.startElement("div", null);
                writer.writeAttribute("id", clientId + "-" + element.getId(), null);
                writer.writeAttribute("class", elementClass, null);
                writer.writeAttribute("style", coords, null);
                writer.writeAttribute("data-tooltip", title, null);

                if (FacetUtils.shouldRenderFacet(elementFacet)) {
                    elementFacet.encodeAll(context);
                }
                else if (data != null) {
                    writer.writeText(data, null);
                }
                writer.endElement("div");
            }

            component.setRowIndex(-1);
        }

        writer.endElement("div");
    }
}
