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
import java.util.ArrayList;
import java.util.List;

public class DefaultDashboardModel implements DashboardModel, Serializable {

    private static final long serialVersionUID = 1L;

    private List<DashboardColumn> columns;

    public DefaultDashboardModel() {
        columns = new ArrayList<>();
    }

    @Override
    public List<DashboardColumn> getColumns() {
        return columns;
    }

    @Override
    public void addColumn(DashboardColumn column) {
        columns.add(column);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public DashboardColumn getColumn(int index) {
        return columns.get(index);
    }

    @Override
    public void transferWidget(DashboardColumn fromColumn, DashboardColumn toColumn, String widgetId, int index) {
        fromColumn.removeWidget(widgetId);
        toColumn.addWidget(index, widgetId);
    }
}
