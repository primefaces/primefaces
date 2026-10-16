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
package org.primefaces.component.diagram;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.event.diagram.PositionChangeEvent;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.EndPoint;

import java.util.Map;
import java.util.function.IntPredicate;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;

@FacesComponent(value = Diagram.COMPONENT_TYPE, namespace = Diagram.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Diagram is a component to create visual elements and connect them on a web page.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "diagram/diagram.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "diagram/diagram.js")
public class Diagram extends DiagramBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Diagram";

    public static final String CONTAINER_CLASS = "ui-diagram ui-widget";
    public static final String ELEMENT_CLASS = "ui-diagram-element";
    public static final String DRAGGABLE_ELEMENT_CLASS = "ui-diagram-draggable";

    public boolean isConnectRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_connect");
    }

    public boolean isDisconnectRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_disconnect");
    }

    public boolean isConnectionChangeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_connectionChange");
    }

    public boolean isPositionChangeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_positionChange");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        if (isAjaxBehaviorEventSource(event)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            DiagramModel model = (DiagramModel) getValue();

            if (model != null) {
                if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.connect)) {
                    Element sourceElement = model.findElement(params.get(clientId + "_sourceId"));
                    Element targetElement = model.findElement(params.get(clientId + "_targetId"));
                    EndPoint sourceEndPoint = model.findEndPoint(sourceElement, params.get(clientId + "_sourceEndPointId"));
                    EndPoint targetEndPoint = model.findEndPoint(targetElement, params.get(clientId + "_targetEndPointId"));

                    ConnectEvent connectEvent =
                        new ConnectEvent(this, behaviorEvent.getBehavior(), sourceElement, targetElement, sourceEndPoint, targetEndPoint);
                    connectEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(connectEvent);
                }
                else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.disconnect)) {
                    Element sourceElement = model.findElement(params.get(clientId + "_sourceId"));
                    Element targetElement = model.findElement(params.get(clientId + "_targetId"));
                    EndPoint sourceEndPoint = model.findEndPoint(sourceElement, params.get(clientId + "_sourceEndPointId"));
                    EndPoint targetEndPoint = model.findEndPoint(targetElement, params.get(clientId + "_targetEndPointId"));

                    DisconnectEvent disconnectEvent =
                        new DisconnectEvent(this, behaviorEvent.getBehavior(), sourceElement, targetElement, sourceEndPoint, targetEndPoint);
                    disconnectEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(disconnectEvent);
                }
                else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.connectionChange)) {
                    Element originalSourceElement = model.findElement(params.get(clientId + "_originalSourceId"));
                    Element newSourceElement = model.findElement(params.get(clientId + "_newSourceId"));
                    Element originalTargetElement = model.findElement(params.get(clientId + "_originalTargetId"));
                    Element newTargetElement = model.findElement(params.get(clientId + "_newTargetId"));
                    EndPoint originalSourceEndPoint = model.findEndPoint(originalSourceElement, params.get(clientId + "_originalSourceEndPointId"));
                    EndPoint newSourceEndPoint = model.findEndPoint(newSourceElement, params.get(clientId + "_newSourceEndPointId"));
                    EndPoint originalTargetEndPoint = model.findEndPoint(originalTargetElement, params.get(clientId + "_originalTargetEndPointId"));
                    EndPoint newTargetEndPoint = model.findEndPoint(newTargetElement, params.get(clientId + "_newTargetEndPointId"));

                    ConnectionChangeEvent connectionChangeEvent = new ConnectionChangeEvent(this, behaviorEvent.getBehavior(),
                            originalSourceElement, newSourceElement, originalTargetElement, newTargetElement,
                            originalSourceEndPoint, newSourceEndPoint, originalTargetEndPoint, newTargetEndPoint);

                    connectionChangeEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(connectionChangeEvent);
                }
                else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.positionChange)) {
                    Element element = model.findElement(params.get(clientId + "_elementId"));
                    String[] position = params.get(clientId + "_position").split(",");

                    PositionChangeEvent positionChangeEvent = new PositionChangeEvent(this, behaviorEvent.getBehavior(),
                            element, position[0] + "px", position[1] + "px");

                    positionChangeEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(positionChangeEvent);
                }
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    /**
     * The <code>element</code> facet is rendered once per element, therefore it must not be processed a single time
     * with a rowIndex of <code>-1</code>. Otherwise the client ids of its children would not match the ones generated
     * while rendering and e.g. a nested <code>p:commandLink</code> would never be decoded.
     */
    @Override
    protected void processFacets(FacesContext context, PhaseId phaseId) {
        UIComponent elementFacet = getElementFacet();

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                if (facet != elementFacet) {
                    process(context, facet, phaseId);
                }
            }
        }

        if (elementFacet != null) {
            forEachElement(i -> {
                process(context, elementFacet, phaseId);
                return false;
            });
        }
    }

    /**
     * @see #processFacets(FacesContext, PhaseId)
     */
    @Override
    protected boolean visitFacets(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (visitRows) {
            setRowIndex(-1);
        }

        UIComponent elementFacet = getElementFacet();

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                if (facet != elementFacet && facet.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        if (elementFacet == null) {
            return false;
        }

        if (!visitRows) {
            return elementFacet.visitTree(context, callback);
        }

        return forEachElement(i -> elementFacet.visitTree(context, callback));
    }

    /**
     * Iterates over all elements of the model, exactly like the renderer does, and invokes the callback for each of
     * them. The iteration stops as soon as the callback returns <code>true</code>.
     *
     * @param callback the callback to invoke for each element
     * @return <code>true</code> if the callback returned <code>true</code> for one of the elements
     */
    private boolean forEachElement(IntPredicate callback) {
        boolean result = false;

        try {
            int rowCount = getRowCount();
            for (int i = 0; i < rowCount; i++) {
                setRowIndex(i);

                if (!isRowAvailable()) {
                    break;
                }

                if (callback.test(i)) {
                    result = true;
                    break;
                }
            }
        }
        finally {
            setRowIndex(-1);
        }

        return result;
    }
}