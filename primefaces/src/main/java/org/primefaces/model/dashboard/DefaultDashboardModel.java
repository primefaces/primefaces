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
package org.primefaces.model.dashboard;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultDashboardModel implements DashboardModel, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<DashboardColumn> columns = new ArrayList<>();

    @Override
    public List<DashboardColumn> getColumns() {
        return columns;
    }

    @Override
    public void addColumn(DashboardColumn column) {
        columns.add(column);
    }

    @Override
    public DashboardColumn getColumn(int index) {
        return columns.get(index);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public void addItem(DashboardItem item) {
        if (columns.isEmpty()) {
            columns.add(new DefaultDashboardColumn());
        }
        columns.get(columns.size() - 1).addItem(item);
    }

    @Override
    public DashboardItem getItem(String panelId) {
        for (DashboardColumn column : columns) {
            for (DashboardItem item : column.getItems()) {
                if (item.getPanelId().equals(panelId)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public void moveItem(DashboardItem item, int columnIndex, int itemIndex) {
        for (DashboardColumn column : columns) {
            column.getItems().remove(item);
        }
        List<DashboardItem> target = columns.get(columnIndex).getItems();
        target.add(Math.min(itemIndex, target.size()), item);
    }

    @Override
    public void swapItems(DashboardItem item1, DashboardItem item2) {
        List<DashboardItem> list1 = findColumnItems(item1);
        List<DashboardItem> list2 = findColumnItems(item2);
        int index1 = list1.indexOf(item1);
        int index2 = list2.indexOf(item2);
        list1.set(index1, item2);
        list2.set(index2, item1);
    }

    private List<DashboardItem> findColumnItems(DashboardItem item) {
        for (DashboardColumn column : columns) {
            if (column.getItems().contains(item)) {
                return column.getItems();
            }
        }
        throw new IllegalArgumentException("Item of panel " + item.getPanelId() + " is not part of the model");
    }
}
