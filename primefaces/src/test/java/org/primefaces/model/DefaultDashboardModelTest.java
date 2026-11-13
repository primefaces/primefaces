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
package org.primefaces.model;

import org.primefaces.model.dashboard.DashboardItem;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardColumn;
import org.primefaces.model.dashboard.DefaultDashboardItem;
import org.primefaces.model.dashboard.DefaultDashboardModel;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultDashboardModelTest {

    private DashboardModel model;

    @BeforeEach
    void setup() {
        model = new DefaultDashboardModel();
        model.addColumn(new DefaultDashboardColumn("panel1", "panel2"));
        model.addColumn(new DefaultDashboardColumn("panel3", "panel4", "panel5"));
        model.addColumn(new DefaultDashboardColumn("panel6"));
    }

    @Test
    void columnsHaveItems() {
        assertEquals(3, model.getColumnCount());
        assertEquals("panel1,panel2|panel3,panel4,panel5|panel6", layout(model));
        assertEquals("panel5", model.getItem("panel5").getPanelId());
        assertNull(model.getItem("unknown"));
    }

    @Test
    void moveItemInSameColumn() {
        model.moveItem(model.getItem("panel2"), 0, 0);
        model.moveItem(model.getItem("panel3"), 1, 2);

        assertEquals("panel2,panel1|panel4,panel5,panel3|panel6", layout(model));
    }

    @Test
    void moveItemToOtherColumn() {
        model.moveItem(model.getItem("panel3"), 2, 0);
        model.moveItem(model.getItem("panel5"), 2, 2);

        assertEquals("panel1,panel2|panel4|panel3,panel6,panel5", layout(model));
    }

    @Test
    void emptyColumn() {
        model.moveItem(model.getItem("panel6"), 0, 2);

        assertEquals("panel1,panel2,panel6|panel3,panel4,panel5|", layout(model));
        assertEquals("panel4", model.getItem("panel4").getPanelId());
    }

    @Test
    void swapItemsWithStyleAndValue() {
        DashboardModel responsive = new DefaultDashboardModel();
        Object barValue = new Object();
        responsive.addItem(new DefaultDashboardItem("bar", "col-4", barValue));
        responsive.addItem(new DefaultDashboardItem("pie", "col-8"));
        responsive.addItem(new DefaultDashboardItem("line", "col-12"));

        responsive.swapItems(responsive.getItem("bar"), responsive.getItem("line"));

        assertEquals(1, responsive.getColumnCount());
        assertEquals("line,pie,bar", layout(responsive));
        DashboardItem bar = responsive.getItem("bar");
        assertEquals("col-4", bar.getStyleClass());
        assertSame(barValue, bar.getValue());
    }

    private static String layout(DashboardModel model) {
        return model.getColumns().stream()
                .map(c -> c.getItems().stream().map(DashboardItem::getPanelId).collect(Collectors.joining(",")))
                .collect(Collectors.joining("|"));
    }
}
