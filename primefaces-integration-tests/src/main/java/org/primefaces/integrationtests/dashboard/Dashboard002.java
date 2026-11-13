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
import org.primefaces.model.dashboard.DefaultDashboardItem;
import org.primefaces.model.dashboard.DefaultDashboardModel;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Dashboard002 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private DashboardModel model;

    @PostConstruct
    public void init() {
        model = new DefaultDashboardModel();
        model.addItem(new DefaultDashboardItem("bar", "col-12"));
        model.addItem(new DefaultDashboardItem("pie", "col-12"));
        model.addItem(new DefaultDashboardItem("line", "col-12"));
    }

    /**
     * The layout of the dashboard model, e.g. {@code bar|pie|line}.
     *
     * @return the panel ids of the cells
     */
    public String getLayout() {
        // responsive: all items are in one column, each is a cell of the grid
        return Dashboard001.toLayout(model).replace(',', '|');
    }

    public void onReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reordered: " + event.getPanelId(),
                    "Column:" + event.getColumnIndex() + " Item:" + event.getItemIndex()
                    + " SenderColumn:" + event.getSenderColumnIndex() + " SenderItem:" + event.getSenderItemIndex());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
