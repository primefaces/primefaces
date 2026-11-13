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

import java.util.List;

/**
 * The layout of a dashboard: columns that hold the items (panels). The responsive dashboard renders the items of all
 * columns as cells of a grid, it usually has one column (see {@link #addItem(DashboardItem)}).
 */
public interface DashboardModel {

    List<DashboardColumn> getColumns();

    void addColumn(DashboardColumn column);

    DashboardColumn getColumn(int index);

    int getColumnCount();

    /**
     * Adds the item to the last column, a column is created when there is none yet. This is the usual way to fill the
     * model of a responsive dashboard.
     *
     * @param item the item to add
     */
    void addItem(DashboardItem item);

    /**
     * @param panelId the id of the panel
     * @return the item of the panel or {@code null}
     */
    DashboardItem getItem(String panelId);

    /**
     * Moves the item to the given index of the given column (classic dashboard).
     *
     * @param item the item to move
     * @param columnIndex the index of the target column
     * @param itemIndex the index in the target column
     */
    void moveItem(DashboardItem item, int columnIndex, int itemIndex);

    /**
     * Swaps the positions of two items (responsive dashboard).
     *
     * @param item1 the first item
     * @param item2 the second item
     */
    void swapItems(DashboardItem item1, DashboardItem item2);
}
