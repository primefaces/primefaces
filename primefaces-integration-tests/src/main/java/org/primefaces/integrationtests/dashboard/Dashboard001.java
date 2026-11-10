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
package org.primefaces.integrationtests.dashboard;

import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DashboardWidget;
import org.primefaces.model.dashboard.DefaultDashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardWidget;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Dashboard001 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private static final String COLUMN_STYLE = "width: 250px";

    private DashboardModel model;
    private DashboardModel lockedModel;

    @PostConstruct
    public void init() {
        model = new DefaultDashboardModel();
        model.addWidget(new DefaultDashboardWidget(COLUMN_STYLE, null, List.of("sports", "finance")));
        model.addWidget(new DefaultDashboardWidget(COLUMN_STYLE, null, List.of("lifestyle", "weather")));
        model.addWidget(new DefaultDashboardWidget(COLUMN_STYLE, null, List.of("politics")));

        lockedModel = new DefaultDashboardModel();
        lockedModel.addWidget(new DefaultDashboardWidget(COLUMN_STYLE, null, List.of("news", "music")));
    }

    /**
     * The layout of the dashboard model, e.g. {@code sports,finance|lifestyle,weather|politics}.
     *
     * @return the panel ids of the columns
     */
    public String getLayout() {
        return toLayout(model);
    }

    public void onReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reordered: " + event.getWidgetId(),
                    "Sender:" + event.getSenderColumnIndex() + " Column:" + event.getColumnIndex() + " Item:" + event.getItemIndex());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    static String toLayout(DashboardModel model) {
        return model.getWidgets().stream()
                    .map(DashboardWidget::getWidgets)
                    .map(widgets -> String.join(",", widgets))
                    .collect(Collectors.joining("|"));
    }
}
