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

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.filter.GreaterThanEqualsFilterConstraint;

import java.util.Collections;
import java.util.List;

import jakarta.el.MethodExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MatchModeOptionsTest {

    private FacesContext context;
    private DataTable table;

    @BeforeEach
    void setup() {
        context = new FacesContextMock();
        table = new DataTable();
        table.setId("table");
    }

    private Column column(Object options, String filterMatchMode) {
        Column column = new Column();
        column.setId("col" + table.getChildCount());
        column.setField("name");
        column.setFilterMatchModeOptions(options);
        column.setFilterMatchMode(filterMatchMode);
        table.getChildren().add(column);
        return column;
    }

    @Test
    void notSet() {
        assertTrue(MatchModeOptions.resolve(context, table, column(null, null)).isEmpty());
    }

    @Test
    void presets() {
        assertEquals(MatchModeOptions.TEXT, MatchModeOptions.resolve(context, table, column("text", null)));
        assertEquals(MatchModeOptions.NUMERIC, MatchModeOptions.resolve(context, table, column("numeric", null)));
        assertEquals(MatchModeOptions.DATE, MatchModeOptions.resolve(context, table, column("date", null)));
    }

    @Test
    void noBooleanOrEnumPreset() {
        for (String preset : List.of("boolean", "enum")) {
            Column column = column(preset, null);
            assertThrows(FacesException.class, () -> MatchModeOptions.resolve(context, table, column), preset);
        }
    }

    @Test
    void tableDefault() {
        table.setFilterMatchModeOptions("numeric");
        assertEquals(MatchModeOptions.NUMERIC, MatchModeOptions.resolve(context, table, column(null, null)));
        assertEquals(MatchModeOptions.TEXT, MatchModeOptions.resolve(context, table, column("text", null)));
        assertTrue(MatchModeOptions.resolve(context, table, column("none", null)).isEmpty());
    }

    @Test
    void operatorList() {
        assertEquals(List.of(MatchMode.CONTAINS, MatchMode.STARTS_WITH, MatchMode.EQUALS),
                MatchModeOptions.resolve(context, table, column(" contains, startsWith ,equals,contains", null)));
    }

    @Test
    void collection() {
        assertEquals(List.of(MatchMode.GREATER_THAN, MatchMode.LESS_THAN),
                MatchModeOptions.resolve(context, table, column(List.of(MatchMode.GREATER_THAN, MatchMode.LESS_THAN), null)));
        assertTrue(MatchModeOptions.resolve(context, table, column(Collections.emptyList(), null)).isEmpty());

        Column invalid = column(List.of("gt"), null);
        assertThrows(FacesException.class, () -> MatchModeOptions.resolve(context, table, invalid));
    }

    @Test
    void unknownValue() {
        Column column = column("numerc", null);
        FacesException e = assertThrows(FacesException.class, () -> MatchModeOptions.resolve(context, table, column));
        assertTrue(e.getMessage().contains("'numerc'"), e.getMessage());
    }

    @Test
    void unsupportedMatchModes() {
        for (String operator : List.of("in", "notIn", "between", "notBetween", "global")) {
            Column column = column("equals," + operator, null);
            assertThrows(FacesException.class, () -> MatchModeOptions.resolve(context, table, column), operator);
        }
    }

    @Test
    void customFilterHasNoPicker() {
        table.setFilterMatchModeOptions("text");

        Column facet = column("text", null);
        facet.getFacets().put("filter", new HtmlOutputText());
        assertTrue(MatchModeOptions.resolve(context, table, facet).isEmpty());

        Column function = column(null, null);
        function.setFilterFunction(mock(MethodExpression.class));
        assertTrue(MatchModeOptions.resolve(context, table, function).isEmpty());
    }

    @Test
    void initialMatchMode() {
        List<MatchMode> options = List.of(MatchMode.CONTAINS, MatchMode.STARTS_WITH);
        // filterMatchMode if set, also the default startsWith
        assertEquals(MatchMode.GREATER_THAN_EQUALS,
                MatchModeOptions.initialMatchMode(column("numeric", "gte"), MatchModeOptions.NUMERIC));
        assertEquals(MatchMode.STARTS_WITH, MatchModeOptions.initialMatchMode(column(options, "startsWith"), options));
        // otherwise the first option, not the default startsWith
        assertEquals(MatchMode.CONTAINS, MatchModeOptions.initialMatchMode(column(options, null), options));
        assertEquals(MatchMode.EQUALS, MatchModeOptions.initialMatchMode(column("numeric", null), MatchModeOptions.NUMERIC));
    }

    @Test
    void initialMatchModeNotAnOption() {
        for (String filterMatchMode : List.of("contains", "startsWith")) {
            Column column = column("numeric", filterMatchMode);
            FacesException e = assertThrows(FacesException.class,
                    () -> MatchModeOptions.initialMatchMode(column, MatchModeOptions.NUMERIC));
            assertTrue(e.getMessage().contains("'" + filterMatchMode + "'"), e.getMessage());
        }
    }

    @Test
    void apply() {
        Column column = column("numeric", "gte");
        FilterMeta filterMeta = FilterMeta.of(context, "row", column, false);

        MatchModeOptions.apply(context, table, column, filterMeta);

        assertEquals(MatchModeOptions.NUMERIC, filterMeta.getMatchModeOptions());
        assertEquals(MatchMode.GREATER_THAN_EQUALS, filterMeta.getMatchMode());
        assertInstanceOf(GreaterThanEqualsFilterConstraint.class, filterMeta.getConstraint());

        Column implicit = column("numeric", null);
        FilterMeta implicitMeta = FilterMeta.of(context, "row", implicit, false);
        MatchModeOptions.apply(context, table, implicit, implicitMeta);
        assertEquals(MatchMode.EQUALS, implicitMeta.getMatchMode());
    }

    @Test
    void iconClass() {
        assertEquals("p-filter-matchmode-starts-with-icon", MatchModeOptions.iconClass(MatchMode.STARTS_WITH));
        assertEquals("p-filter-matchmode-not-equals-icon", MatchModeOptions.iconClass(MatchMode.NOT_EQUALS));
        assertEquals("p-filter-matchmode-gte-icon", MatchModeOptions.iconClass(MatchMode.GREATER_THAN_EQUALS));
    }
}
