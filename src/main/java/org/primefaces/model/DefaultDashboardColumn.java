/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
