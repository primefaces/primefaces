/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class DefaultDashboardColumn implements DashboardColumn, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> widgets;
    private String style;
    private String styleClass;

    public DefaultDashboardColumn() {
        widgets = new LinkedList<String>();
    }

    @Override
    public void removeWidget(String widgetId) {
        widgets.remove(widgetId);
    }

    @Override
    public List<String> getWidgets() {
        return widgets;
    }

    @Override
    public int getWidgetCount() {
        return widgets.size();
    }

    @Override
    public String getWidget(int index) {
        return widgets.get(index);
    }

    @Override
    public void addWidget(int index, String widgetId) {
        widgets.add(index, widgetId);
    }

    @Override
    public void reorderWidget(int index, String widgetId) {
        widgets.remove(widgetId);
        widgets.add(index, widgetId);
    }

    @Override
    public void addWidget(String widgetId) {
        widgets.add(widgetId);
    }

    @Override
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
}
