/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.treetable;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.row.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TreeTableRendererTest {

    private final TreeTableRenderer renderer = new TreeTableRenderer();

    @Test
    void collectColumnGroupWidthsFromLeafColumns() {
        Column name = column("Name");
        name.setRowspan(2);
        Column details = column("Details");
        details.setColspan(3);

        Row firstRow = row(name, details);

        Column size = column("Size");
        size.setStyle("width: 50px");
        Column type = column("Type");
        Column actions = column("Actions");
        actions.setStyle("width:3rem");
        Row secondRow = row(size, type, actions);

        ColumnGroup group = new ColumnGroup();
        group.getChildren().add(firstRow);
        group.getChildren().add(secondRow);

        List<String> widths = new ArrayList<>(Arrays.asList(null, null, null, null));
        renderer.collectColumnGroupWidths(group, widths);

        assertEquals(Arrays.asList(null, "50px", null, "3rem"), widths);
    }

    @Test
    void resolveColumnWidthFromAttribute() {
        Column column = column("Size");
        column.setWidth("50");

        assertEquals("50px", renderer.resolveColumnWidth(column));
    }

    @Test
    void resolveColumnWidthFromStyle() {
        Column column = column("Size");
        column.setStyle("text-align: right; width: 50%");

        assertEquals("50%", renderer.resolveColumnWidth(column));
    }

    @Test
    void resolveColumnWidthNotDefined() {
        assertNull(renderer.resolveColumnWidth(column("Size")));
    }

    private static Column column(String headerText) {
        Column column = new Column();
        column.setHeaderText(headerText);
        return column;
    }

    private static Row row(Column... columns) {
        Row row = new Row();
        row.getChildren().addAll(Arrays.asList(columns));
        return row;
    }
}
