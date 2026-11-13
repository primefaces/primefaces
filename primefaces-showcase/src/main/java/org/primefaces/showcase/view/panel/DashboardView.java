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
package org.primefaces.showcase.view.panel;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.dashboard.DashboardItem;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardColumn;
import org.primefaces.model.dashboard.DefaultDashboardItem;
import org.primefaces.model.dashboard.DefaultDashboardModel;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DashboardView implements Serializable {

    @Serial private static final long serialVersionUID = 1L;
    private static final String RESPONSIVE_CLASS = "col-12 lg:col-6 xl:col-6";

    private DashboardModel responsiveModel;
    private DashboardModel legacyModel;

    @PostConstruct
    public void init() {
        // responsive
        responsiveModel = new DefaultDashboardModel();
        responsiveModel.addItem(new DefaultDashboardItem("bar", RESPONSIVE_CLASS));
        responsiveModel.addItem(new DefaultDashboardItem("stacked", RESPONSIVE_CLASS));
        responsiveModel.addItem(new DefaultDashboardItem("donut", RESPONSIVE_CLASS.replaceFirst("xl:col-\\d+", "xl:col-4")));
        responsiveModel.addItem(new DefaultDashboardItem("cartesian", RESPONSIVE_CLASS.replaceFirst("xl:col-\\d+", "xl:col-8")));

        // legacy
        legacyModel = new DefaultDashboardModel();
        legacyModel.addColumn(new DefaultDashboardColumn("sports", "finance"));
        legacyModel.addColumn(new DefaultDashboardColumn("lifestyle", "weather"));
        legacyModel.addColumn(new DefaultDashboardColumn("politics"));
    }

    public void handleReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getPanelId());
        String result = String.format("From column %d, index %d to column %d, index %d",
                event.getSenderColumnIndex(), event.getSenderItemIndex(), event.getColumnIndex(), event.getItemIndex());
        message.setDetail(result);

        addMessage(message);
    }

    public void handleClose(CloseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Panel Closed",
                "Closed panel ID:'" + event.getComponent().getId() + "'");

        addMessage(message);
    }

    public void handleToggle(ToggleEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Toggled",
                "Toggle panel ID:'" + event.getComponent().getId() + "' Status:" + event.getVisibility().name());

        addMessage(message);
    }

    /**
     * Dashboard panel has been resized.
     *
     * @param panelId the id of the panel
     * @param size the new size CSS
     */
    public void onDashboardResize(final String panelId, final String size) {
        final DashboardItem item = responsiveModel.getItem(panelId);
        if (item != null) {
            final String newCss = item.getStyleClass().replaceFirst("xl:col-\\d+", size);
            item.setStyleClass(newCss);
        }
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getLegacyModel() {
        return legacyModel;
    }

    public DashboardModel getResponsiveModel() {
        return responsiveModel;
    }
}
