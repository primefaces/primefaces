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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * DashBoard widgets used in responsive mode only.
 */
public class DefaultDashboardWidget implements DashboardWidget, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> widgets;
    private String style;
    private String styleClass;
    private Object value;

    public DefaultDashboardWidget() {
        widgets = new LinkedList<>();
    }

    public DefaultDashboardWidget(String style, String styleClass, Object value, Collection<String> widgets) {
        this();
        this.widgets.addAll(widgets);
        this.style = style;
        this.styleClass = styleClass;
        this.value = value;
    }

    public DefaultDashboardWidget(String style, String styleClass, Collection<String> widgets) {
        this(style, styleClass, null, widgets);
    }

    public DefaultDashboardWidget(String widgetId, String styleClass, Object value) {
        this();
        getWidgets().addAll(Arrays.asList(widgetId));
        setStyleClass(styleClass);
        setValue(value);
    }

    public DefaultDashboardWidget(String widgetId, String styleClass) {
        this();
        getWidgets().addAll(Arrays.asList(widgetId));
        setStyleClass(styleClass);
    }

    public DefaultDashboardWidget(Collection<String> widgets) {
        this();
        this.widgets.addAll(widgets);
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

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    @Override
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

}
