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
package org.primefaces.component.dnd;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.DragDropEvent;
import org.primefaces.expression.SearchExpressionUtils;

import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIData;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Droppable.COMPONENT_TYPE, namespace = Droppable.COMPONENT_FAMILY)
@FacesComponentDescription("Droppable is a component that makes its child elements accept draggable elements.")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Droppable extends DroppableBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Droppable";

    public static final String COMPONENT_FAMILY = "org.primefaces.component";


    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        if (isAjaxBehaviorEventSource(event)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.drop)) {
                String dragId = params.get(clientId + "_dragId");
                String dropId = params.get(clientId + "_dropId");
                DragDropEvent<?> dndEvent = null;
                String datasourceId = getDatasource();

                if (datasourceId != null) {
                    UIData datasource = findDatasource(context, this, datasourceId);
                    String[] idTokens = dragId.split(String.valueOf(UINamingContainer.getSeparatorChar(context)));
                    int rowIndex = Integer.parseInt(idTokens[idTokens.length - 2]);
                    datasource.setRowIndex(rowIndex);
                    Object data = datasource.getRowData();
                    datasource.setRowIndex(-1);

                    dndEvent = new DragDropEvent<>(this, behaviorEvent.getBehavior(), dragId, dropId, data);
                }
                else {
                    dndEvent = new DragDropEvent<>(this, behaviorEvent.getBehavior(), dragId, dropId);
                }

                dndEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(dndEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    protected UIData findDatasource(FacesContext context, Droppable droppable, String datasourceId) {
        return (UIData) SearchExpressionUtils.contextlessResolveComponent(context, droppable, datasourceId);
    }
}