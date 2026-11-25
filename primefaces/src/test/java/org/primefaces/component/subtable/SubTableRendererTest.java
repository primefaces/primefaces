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
package org.primefaces.component.subtable;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.row.Row;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;

import java.util.Arrays;
import java.util.Collections;

import jakarta.faces.component.UIOutput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.Renderer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SubTableRendererTest {

    private static final String HEADER_TEXT = "<b>header</b>";
    private static final String FOOTER_TEXT = "<b>footer</b>";

    /**
     * The row the sub table stands on ends up in its own client id and in the client id of everything below it, and it
     * puts the row it holds in the request map under the sub table's var. So a render which iterated the sub table
     * leaves it standing on no row.
     */
    @Test
    void encodeEndLeavesTheSubTableStandingOnNoRow() throws Exception {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        Column column = new Column();
        column.getChildren().add(new UIOutput());

        SubTable subTable = new SubTable() {
            @Override
            protected Renderer<?> getRenderer(FacesContext context) {
                return new SubTableRenderer();
            }
        };
        subTable.setId("subTable");
        subTable.setVar("stat");
        subTable.setValue(Arrays.asList("one", "two"));
        subTable.getChildren().add(column);

        subTable.encodeEnd(context);

        assertEquals(-1, subTable.getRowIndex());
        assertEquals(subTable.getId(), subTable.getClientId(context));
        assertFalse(context.getExternalContext().getRequestMap().containsKey("stat"));
    }

    @Test
    void textsOfAColumnGroupAreEscaped() throws Exception {
        ResponseWriter writer = mock(ResponseWriter.class);
        SubTable subTable = subTableWithColumnGroups();

        subTable.encodeEnd(new FacesContextMock(writer));

        verify(writer).writeText(HEADER_TEXT, null);
        verify(writer).writeText(FOOTER_TEXT, null);
        verify(writer, never()).write(HEADER_TEXT);
        verify(writer, never()).write(FOOTER_TEXT);
    }

    @Test
    void textsOfAColumnGroupFollowEscapeTextOfTheTable() throws Exception {
        ResponseWriter writer = mock(ResponseWriter.class);
        SubTable subTable = subTableWithColumnGroups();
        DataTable table = new DataTable();
        table.setEscapeText(false);
        table.getChildren().add(subTable);

        subTable.encodeEnd(new FacesContextMock(writer));

        verify(writer).write(HEADER_TEXT);
        verify(writer).write(FOOTER_TEXT);
    }

    private static SubTable subTableWithColumnGroups() {
        SubTable subTable = new SubTable() {
            @Override
            protected Renderer<?> getRenderer(FacesContext context) {
                return new SubTableRenderer();
            }
        };
        subTable.setId("subTable");
        subTable.setValue(Collections.emptyList());
        subTable.getChildren().add(columnGroup("header"));
        subTable.getChildren().add(columnGroup("footer"));
        return subTable;
    }

    private static ColumnGroup columnGroup(String type) {
        Column column = new Column();
        column.setHeaderText(HEADER_TEXT);
        column.setFooterText(FOOTER_TEXT);

        Row row = new Row();
        row.getChildren().add(column);

        ColumnGroup group = new ColumnGroup();
        group.setType(type);
        group.getChildren().add(row);
        return group;
    }
}
