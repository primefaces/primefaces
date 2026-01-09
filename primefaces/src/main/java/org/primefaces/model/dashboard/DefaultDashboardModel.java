/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.model.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultDashboardModel implements DashboardModel, Serializable {

    private static final long serialVersionUID = 1L;

    private List<DashboardWidget> widgets;

    public DefaultDashboardModel() {
        widgets = new ArrayList<>();
    }

    @Override
    public List<DashboardWidget> getWidgets() {
        return widgets;
    }

    @Override
    public void addWidget(DashboardWidget widget) {
        widgets.add(widget);
    }

    @Override
    public int getWidgetCount() {
        return widgets.size();
    }

    @Override
    public DashboardWidget getWidget(int index) {
        return widgets.get(index);
    }

    @Override
    public DashboardWidget getWidget(String widgetId) {
        return widgets.stream().filter(w -> w.getWidget(0).equalsIgnoreCase(widgetId))
                .findFirst().orElse(null);
    }

    @Override
    public void transferWidget(DashboardWidget fromWidget, DashboardWidget toWidget, String widgetId, int index, boolean responsive) {
        if (responsive) {
            String fromWidgetId = fromWidget.getWidget(0);
            String toWidgetId = toWidget.getWidget(0);
            String fromCss = fromWidget.getStyleClass();
            String toCss = toWidget.getStyleClass();
            fromWidget.removeWidget(fromWidgetId);
            fromWidget.addWidget(index, toWidgetId);
            toWidget.removeWidget(toWidgetId);
            toWidget.addWidget(index, fromWidgetId);
            fromWidget.setStyleClass(toCss);
            toWidget.setStyleClass(fromCss);
        }
        else {
            fromWidget.removeWidget(widgetId);
            toWidget.addWidget(index, widgetId);
        }
    }

}
