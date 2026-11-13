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

public class DefaultDashboardColumn implements DashboardColumn, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<DashboardItem> items = new ArrayList<>();
    private String style;
    private String styleClass;

    public DefaultDashboardColumn() {
        // NOOP
    }

    /**
     * Creates a column with an item for each of the given panel ids.
     *
     * @param panelIds the ids of the panels of the column
     */
    public DefaultDashboardColumn(String... panelIds) {
        for (String panelId : panelIds) {
            items.add(new DefaultDashboardItem(panelId));
        }
    }

    @Override
    public List<DashboardItem> getItems() {
        return items;
    }

    @Override
    public void addItem(DashboardItem item) {
        items.add(item);
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
}
