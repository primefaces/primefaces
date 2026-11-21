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
package org.primefaces.component.api;

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.filter.GreaterThanEqualsFilterConstraint;

import java.util.Collections;
import java.util.List;

import jakarta.el.MethodExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FilterMatchModeOptionsTest {

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
        assertTrue(FilterMatchModeOptions.resolve(context, table, column(null, null)).isEmpty());
    }

    @Test
    void presets() {
        assertEquals(FilterMatchModeOptions.TEXT, FilterMatchModeOptions.resolve(context, table, column("text", null)));
        assertEquals(FilterMatchModeOptions.NUMERIC, FilterMatchModeOptions.resolve(context, table, column("numeric", null)));
        assertEquals(FilterMatchModeOptions.DATE, FilterMatchModeOptions.resolve(context, table, column("date", null)));
    }

    @Test
    void noBooleanOrEnumPreset() {
        for (String preset : List.of("boolean", "enum")) {
            Column column = column(preset, null);
            assertThrows(FacesException.class, () -> FilterMatchModeOptions.resolve(context, table, column), preset);
        }
    }

    @Test
    void tableDefault() {
        table.setFilterMatchModeOptions("numeric");
        assertEquals(FilterMatchModeOptions.NUMERIC, FilterMatchModeOptions.resolve(context, table, column(null, null)));
        assertEquals(FilterMatchModeOptions.TEXT, FilterMatchModeOptions.resolve(context, table, column("text", null)));
        assertTrue(FilterMatchModeOptions.resolve(context, table, column("none", null)).isEmpty());
    }

    @Test
    void operatorList() {
        assertEquals(List.of(MatchMode.CONTAINS, MatchMode.STARTS_WITH, MatchMode.EQUALS),
                FilterMatchModeOptions.resolve(context, table, column(" contains, startsWith ,equals,contains", null)));
    }

    @Test
    void collection() {
        assertEquals(List.of(MatchMode.GREATER_THAN, MatchMode.LESS_THAN),
                FilterMatchModeOptions.resolve(context, table, column(List.of(MatchMode.GREATER_THAN, MatchMode.LESS_THAN), null)));
        assertTrue(FilterMatchModeOptions.resolve(context, table, column(Collections.emptyList(), null)).isEmpty());

        Column invalid = column(List.of("gt"), null);
        assertThrows(FacesException.class, () -> FilterMatchModeOptions.resolve(context, table, invalid));
    }

    @Test
    void unknownValue() {
        Column column = column("numerc", null);
        FacesException e = assertThrows(FacesException.class, () -> FilterMatchModeOptions.resolve(context, table, column));
        assertTrue(e.getMessage().contains("'numerc'"), e.getMessage());
    }

    @Test
    void unsupportedMatchModes() {
        for (String operator : List.of("in", "notIn", "global")) {
            Column column = column("equals," + operator, null);
            assertThrows(FacesException.class, () -> FilterMatchModeOptions.resolve(context, table, column), operator);
        }
    }

    @Test
    void rangeMatchModes() {
        assertEquals(List.of(MatchMode.EQUALS, MatchMode.BETWEEN, MatchMode.NOT_BETWEEN),
                FilterMatchModeOptions.resolve(context, table, column("equals,between,notBetween", null)));
        assertTrue(FilterMatchModeOptions.NUMERIC.containsAll(List.of(MatchMode.BETWEEN, MatchMode.NOT_BETWEEN)));
        assertTrue(FilterMatchModeOptions.DATE.containsAll(List.of(MatchMode.BETWEEN, MatchMode.NOT_BETWEEN)));
        assertFalse(FilterMatchModeOptions.TEXT.contains(MatchMode.BETWEEN));
    }

    @Test
    void customFilterHasNoPicker() {
        table.setFilterMatchModeOptions("text");

        Column facet = column("text", null);
        facet.getFacets().put("filter", new HtmlOutputText());
        assertTrue(FilterMatchModeOptions.resolve(context, table, facet).isEmpty());

        Column function = column(null, null);
        function.setFilterFunction(mock(MethodExpression.class));
        assertTrue(FilterMatchModeOptions.resolve(context, table, function).isEmpty());
    }

    @Test
    void customFilterFacetNotRenderedHasPicker() {
        HtmlOutputText filter = new HtmlOutputText();
        filter.setRendered(false);
        Column column = column("text", null);
        column.getFacets().put("filter", filter);
        // the renderer encodes the default filter input, so the column gets a picker
        assertEquals(FilterMatchModeOptions.TEXT, FilterMatchModeOptions.resolve(context, table, column));

        table.setRenderEmptyFacets(true);
        assertTrue(FilterMatchModeOptions.resolve(context, table, column).isEmpty());
    }

    @Test
    void filterByMatchMode() {
        Column column = column("numeric", null);
        FilterMeta filterMeta = FilterMeta.of(context, "row", column, false);
        FilterMatchModeOptions.apply(context, table, column, filterMeta);

        FilterMatchModeOptions.validateFilterByMatchMode(filterMeta, MatchMode.GREATER_THAN);
        FilterMatchModeOptions.validateFilterByMatchMode(filterMeta, MatchMode.BETWEEN);
        for (MatchMode matchMode : List.of(MatchMode.CONTAINS, MatchMode.EXACT)) {
            FacesException e = assertThrows(FacesException.class,
                    () -> FilterMatchModeOptions.validateFilterByMatchMode(filterMeta, matchMode));
            assertTrue(e.getMessage().contains("'" + matchMode.operator() + "'"), e.getMessage());
        }

        // no picker, no restriction
        FilterMeta noPicker = FilterMeta.of(context, "row", column(null, null), false);
        FilterMatchModeOptions.validateFilterByMatchMode(noPicker, MatchMode.BETWEEN);
    }

    @Test
    void initialMatchMode() {
        List<MatchMode> options = List.of(MatchMode.CONTAINS, MatchMode.STARTS_WITH);
        // filterMatchMode if set, also the default startsWith
        assertEquals(MatchMode.GREATER_THAN_EQUALS,
                FilterMatchModeOptions.initialMatchMode(column("numeric", "gte"), FilterMatchModeOptions.NUMERIC));
        assertEquals(MatchMode.STARTS_WITH, FilterMatchModeOptions.initialMatchMode(column(options, "startsWith"), options));
        // otherwise the first option, not the default startsWith
        assertEquals(MatchMode.CONTAINS, FilterMatchModeOptions.initialMatchMode(column(options, null), options));
        assertEquals(MatchMode.EQUALS, FilterMatchModeOptions.initialMatchMode(column("numeric", null), FilterMatchModeOptions.NUMERIC));
    }

    @Test
    void initialMatchModeNotAnOption() {
        for (String filterMatchMode : List.of("contains", "startsWith")) {
            Column column = column("numeric", filterMatchMode);
            FacesException e = assertThrows(FacesException.class,
                    () -> FilterMatchModeOptions.initialMatchMode(column, FilterMatchModeOptions.NUMERIC));
            assertTrue(e.getMessage().contains("'" + filterMatchMode + "'"), e.getMessage());
        }
    }

    @Test
    void apply() {
        Column column = column("numeric", "gte");
        FilterMeta filterMeta = FilterMeta.of(context, "row", column, false);

        FilterMatchModeOptions.apply(context, table, column, filterMeta);

        assertEquals(FilterMatchModeOptions.NUMERIC, filterMeta.getMatchModeOptions());
        assertEquals(MatchMode.GREATER_THAN_EQUALS, filterMeta.getMatchMode());
        assertInstanceOf(GreaterThanEqualsFilterConstraint.class, filterMeta.getConstraint());

        Column implicit = column("numeric", null);
        FilterMeta implicitMeta = FilterMeta.of(context, "row", implicit, false);
        FilterMatchModeOptions.apply(context, table, implicit, implicitMeta);
        assertEquals(MatchMode.EQUALS, implicitMeta.getMatchMode());
    }

    private Column integerColumn() {
        Column column = column("numeric", null);
        column.setConverter(new Converter<Integer>() {
            @Override
            public Integer getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    return value == null ? null : Integer.valueOf(value);
                }
                catch (NumberFormatException e) {
                    throw new ConverterException(e);
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Integer value) {
                return String.valueOf(value);
            }
        });
        return column;
    }

    @Test
    void convertSingleValue() {
        Column column = integerColumn();
        assertEquals(42, FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.EQUALS, "42", null));
        assertNull(FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.EQUALS, "x", null));
        assertNull(FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.EQUALS, " ", null));
    }

    @Test
    void convertBetween() {
        Column column = integerColumn();
        assertEquals(List.of(1, 5), FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.BETWEEN, " 1", "5 "));
        assertEquals(List.of(1, 5), FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.NOT_BETWEEN, "1", "5"));
        // no open-ended ranges, and inactive if one value does not convert
        assertNull(FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.BETWEEN, "1", " "));
        assertNull(FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.BETWEEN, null, "5"));
        assertNull(FilterMatchModeOptions.convertFilterValue(context, column, MatchMode.BETWEEN, "1", "x"));
    }
}
