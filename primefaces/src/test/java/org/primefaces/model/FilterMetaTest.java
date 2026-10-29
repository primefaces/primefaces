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

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;

import java.util.List;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterMetaTest {

    @Test
    void newFilterMeta_hasNoMatchModeOptions() {
        FilterMeta filterMeta = FilterMeta.builder()
                .field("name")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.CONTAINS)
                .build();

        assertTrue(filterMeta.getMatchModeOptions().isEmpty());
        assertFalse(filterMeta.isMatchModeSelectable());
    }

    @Test
    void setMatchModeOptions_makesMatchModeSelectable() {
        FilterMeta filterMeta = FilterMeta.builder()
                .field("age")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.EQUALS)
                .matchModeOptions(MatchMode.NUMERIC_MATCH_MODES)
                .build();

        assertTrue(filterMeta.isMatchModeSelectable());
        assertEquals(MatchMode.NUMERIC_MATCH_MODES, filterMeta.getMatchModeOptions());
    }

    @Test
    void setMatchModeOptions_null_resetsToEmptyList() {
        FilterMeta filterMeta = new FilterMeta();
        filterMeta.setMatchModeOptions(MatchMode.NUMERIC_MATCH_MODES);
        assertTrue(filterMeta.isMatchModeSelectable());

        filterMeta.setMatchModeOptions(null);
        assertFalse(filterMeta.isMatchModeSelectable());
        assertTrue(filterMeta.getMatchModeOptions().isEmpty());
    }

    @Test
    void setMatchMode_changesConstraintIndependently() {
        FilterMeta filterMeta = new FilterMeta();
        filterMeta.setMatchMode(MatchMode.GREATER_THAN);
        assertEquals(MatchMode.GREATER_THAN, filterMeta.getMatchMode());
    }

    @Test
    void isActive_falseWithoutValue_whenMatchModeRequiresOne() {
        FilterMeta filterMeta = FilterMeta.builder()
                .field("name")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.CONTAINS)
                .build();

        assertFalse(filterMeta.isActive());
    }

    @Test
    void isActive_trueWithValue() {
        FilterMeta filterMeta = FilterMeta.builder()
                .field("name")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.CONTAINS)
                .filterValue("foo")
                .build();

        assertTrue(filterMeta.isActive());
    }

    @Test
    void isActive_trueWithoutValue_whenMatchModeIsValueLess() {
        // "is empty"/"is null" are a complete predicate on their own
        FilterMeta filterMeta = FilterMeta.builder()
                .field("name")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.IS_EMPTY)
                .build();

        assertTrue(filterMeta.isActive());
    }

    @Test
    void isActive_falseForMatchModeAll_evenThoughItIsValueLess() {
        // MatchMode.ALL is the "no filter selected" placeholder for a dropdown built
        // entirely from value-less modes (e.g., "boolean") - it must stay inactive despite requiresValue()==false,
        // unlike every other value-less mode (is empty/is null/true/false), which IS its own active predicate.
        FilterMeta filterMeta = FilterMeta.builder()
                .field("active")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.ALL)
                .build();

        assertFalse(filterMeta.isActive());
    }

    /**
     * The column's own {@code filterValueType} is the most specific statement the page author can make, so it
     * must win over the table-wide default - including when it opts a single column out of an otherwise
     * type-wide table, or back in on a table that switched the picker off with {@code "none"}.
     */
    @Test
    void resolveFilterValueType_columnWinsOverTableDefault() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);
        when(table.getFilterValueType()).thenReturn("none");
        when(column.getFilterValueType()).thenReturn("numeric");

        assertEquals("numeric", FilterMeta.resolveFilterValueType(context, table, column));
        assertEquals(MatchMode.NUMERIC_MATCH_MODES, FilterMeta.resolveMatchModeOptions(context, table, column));
    }

    @Test
    void resolveFilterValueType_tableDefaultAppliesWhenColumnHasNone() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);
        when(table.getFilterValueType()).thenReturn("numeric");

        assertEquals("numeric", FilterMeta.resolveFilterValueType(context, table, column));
        assertEquals(MatchMode.NUMERIC_MATCH_MODES, FilterMeta.resolveMatchModeOptions(context, table, column));
    }

    /**
     * The whole point of the table-level attribute: one place to switch the match-mode picker off for every
     * column of a table, rather than repeating {@code filterValueType="none"} on each of them.
     */
    @Test
    void resolveFilterValueType_tableNoneDisablesPickerForEveryColumn() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);
        when(table.getFilterValueType()).thenReturn("none");

        assertEquals("none", FilterMeta.resolveFilterValueType(context, table, column));
        assertTrue(FilterMeta.resolveMatchModeOptions(context, table, column).isEmpty());
    }

    /**
     * A custom filter facet or filterFunction has opted out of match-mode-driven filtering entirely - the
     * table-level default must not be able to drag such a column back in and break it.
     */
    @Test
    void resolveFilterValueType_filterFacetOptsOutDespiteTableDefault() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);
        when(table.getFilterValueType()).thenReturn("numeric");
        when(column.getFacet("filter")).thenReturn(mock(UIComponent.class));

        assertNull(FilterMeta.resolveFilterValueType(context, table, column));
        assertTrue(FilterMeta.resolveMatchModeOptions(context, table, column).isEmpty());
    }

    @Test
    void resolveFilterValueType_filterFunctionOptsOutDespiteTableDefault() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);
        when(table.getFilterValueType()).thenReturn("numeric");
        when(column.getFilterFunction()).thenReturn(mock(MethodExpression.class));

        assertNull(FilterMeta.resolveFilterValueType(context, table, column));
        assertTrue(FilterMeta.resolveMatchModeOptions(context, table, column).isEmpty());
    }

    /**
     * With neither attribute set, nothing changes: the auto-derivation added by this feature still decides,
     * here falling back to "text" for a column whose type can't be resolved.
     */
    @Test
    void resolveFilterValueType_withoutTableDefaultAutoDerivationStillApplies() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);

        assertEquals("text", FilterMeta.resolveFilterValueType(context, table, column));
        assertEquals(MatchMode.TEXT_MATCH_MODES, FilterMeta.resolveMatchModeOptions(context, table, column));
    }

    /**
     * A column's explicitly configured filterMatchMode is still prepended to the table-level preset when that
     * preset doesn't already offer it, so a table-wide filterValueType can't silently discard it.
     */
    @Test
    void resolveMatchModeOptions_tableDefaultKeepsColumnConfiguredMatchMode() {
        FacesContext context = mock(FacesContext.class);
        UITable<?> table = mock(UITable.class);
        UIColumn column = mock(UIColumn.class);
        when(table.getFilterValueType()).thenReturn("numeric");
        when(column.getFilterMatchMode()).thenReturn(MatchMode.EXACT.operator());

        List<MatchMode> options = FilterMeta.resolveMatchModeOptions(context, table, column);

        assertEquals(MatchMode.EXACT, options.get(0));
        assertTrue(options.containsAll(MatchMode.NUMERIC_MATCH_MODES));
    }
}
