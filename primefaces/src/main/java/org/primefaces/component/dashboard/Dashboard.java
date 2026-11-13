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
package org.primefaces.component.dashboard;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.dashboard.DashboardItem;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardItem;

import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Dashboard.COMPONENT_TYPE, namespace = Dashboard.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Dashboard provides a portal like layout with drag-drop based reorder capabilities.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Dashboard extends DashboardBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Dashboard";

    public static final String CONTAINER_CLASS = "p-dashboard";
    public static final String COLUMN_CLASS = "p-dashboard-column";
    public static final String PANEL_CLASS = "p-dashboard-panel";

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.reorder)) {
                DashboardModel model = getModel();
                String panelId = toPanelId(context, params.get(clientId + "_panelId"));
                DashboardItem item = model.getItem(panelId);
                int[] sender;
                if (item == null) {
                    // a new panel, dropped from outside of the dashboard (p:draggable with dashboard attribute)
                    item = new DefaultDashboardItem(panelId);
                    sender = new int[] {-1, -1};
                }
                else {
                    sender = findPosition(model, item);
                }

                String targetPanelId = params.get(clientId + "_targetPanelId");
                if (targetPanelId != null) {
                    // responsive: the panels swap their positions
                    DashboardItem target = model.getItem(toPanelId(context, targetPanelId));
                    if (target == null) {
                        throw new FacesException("Panel " + targetPanelId + " is not part of the dashboard model");
                    }
                    model.swapItems(item, target);
                }
                else {
                    model.moveItem(item, Integer.parseInt(params.get(clientId + "_columnIndex")),
                            Integer.parseInt(params.get(clientId + "_itemIndex")));
                }
                int[] receiver = findPosition(model, item);

                DashboardReorderEvent reorderEvent = new DashboardReorderEvent(this, behaviorEvent.getBehavior(), item.getPanelId(),
                        receiver[0], receiver[1], sender[0], sender[1]);
                reorderEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(reorderEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    /**
     * @param context the FacesContext
     * @param panelClientId the client id of a panel
     * @return the id of the panel (the last part of the client id)
     */
    protected String toPanelId(FacesContext context, String panelClientId) {
        String separator = Character.toString(UINamingContainer.getSeparatorChar(context));
        String[] idTokens = panelClientId.split(separator);
        return idTokens[idTokens.length - 1];
    }

    /**
     * @param model the model
     * @param item the item
     * @return the index of the column and the index of the item in the column
     */
    protected int[] findPosition(DashboardModel model, DashboardItem item) {
        for (int c = 0; c < model.getColumnCount(); c++) {
            int i = model.getColumn(c).getItems().indexOf(item);
            if (i >= 0) {
                return new int[] {c, i};
            }
        }
        throw new FacesException("Panel " + item.getPanelId() + " is not part of the dashboard model");
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