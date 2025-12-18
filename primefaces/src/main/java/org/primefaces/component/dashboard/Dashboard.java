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
package org.primefaces.component.dashboard;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DashboardWidget;

import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Dashboard.COMPONENT_TYPE, namespace = Dashboard.COMPONENT_FAMILY)
@FacesComponentDescription("Dashboard provides a portal like layout with drag-drop based reorder capabilities.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Dashboard extends DashboardBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Dashboard";

    public static final String CONTAINER_CLASS = "ui-dashboard";
    public static final String COLUMN_CLASS = "ui-dashboard-column";
    public static final String PANEL_CLASS = "ui-dashboard-panel";

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.reorder)) {
                String widgetClientId = params.get(clientId + "_widgetId");
                Integer itemIndex = Integer.valueOf(params.get(clientId + "_itemIndex"));
                Integer receiverColumnIndex = Integer.valueOf(params.get(clientId + "_receiverColumnIndex"));
                String senderIndexParam = clientId + "_senderColumnIndex";
                Integer senderColumnIndex = null;

                if (params.containsKey(senderIndexParam)) {
                    senderColumnIndex = Integer.valueOf(params.get(senderIndexParam));
                }

                String separator = Character.toString(UINamingContainer.getSeparatorChar(context));
                String[] idTokens = widgetClientId.split(separator);
                String widgetId = idTokens.length == 1 ? idTokens[0] : idTokens[idTokens.length - 1];

                DashboardReorderEvent reorderEvent =
                        new DashboardReorderEvent(this, behaviorEvent.getBehavior(), widgetId, itemIndex, receiverColumnIndex, senderColumnIndex);
                reorderEvent.setPhaseId(behaviorEvent.getPhaseId());

                updateDashboardModel(getModel(), widgetId, itemIndex, receiverColumnIndex, senderColumnIndex);

                super.queueEvent(reorderEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    protected void updateDashboardModel(DashboardModel model, String widgetId, Integer itemIndex, Integer receiverColumnIndex, Integer senderColumnIndex) {
        if (senderColumnIndex == null) {
            //Reorder widget in same column
            DashboardWidget column = model.getWidget(receiverColumnIndex);
            column.reorderWidget(itemIndex, widgetId);
        }
        else {
            //Transfer widget
            DashboardWidget oldColumn = model.getWidget(senderColumnIndex);
            DashboardWidget newColumn = model.getWidget(receiverColumnIndex);
            model.transferWidget(oldColumn, newColumn, widgetId, itemIndex, this.isResponsive());
        }
    }


    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered() || isDisabled()) {
            return;
        }

        if (isAjaxRequestSource(context)) {
            decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isAjaxRequestSource(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isAjaxRequestSource(context)) {
            super.processUpdates(context);
        }
    }
}