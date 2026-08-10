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

import jakarta.el.ValueExpression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

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
                .matchModeOptions(MatchMode.NUMERIC_OPTIONS)
                .build();

        assertTrue(filterMeta.isMatchModeSelectable());
        assertEquals(MatchMode.NUMERIC_OPTIONS, filterMeta.getMatchModeOptions());
    }

    @Test
    void setMatchModeOptions_null_resetsToEmptyList() {
        FilterMeta filterMeta = new FilterMeta();
        filterMeta.setMatchModeOptions(MatchMode.NUMERIC_OPTIONS);
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
        // See GitHub #7427 - "is empty"/"is null" are a complete predicate on their own
        FilterMeta filterMeta = FilterMeta.builder()
                .field("name")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.IS_EMPTY)
                .build();

        assertTrue(filterMeta.isActive());
    }

    @Test
    void isActive_falseForMatchModeAll_evenThoughItIsValueLess() {
        // See GitHub #7427 - MatchMode.ALL is the "no filter selected" placeholder for a dropdown built
        // entirely from value-less modes (e.g. "boolean") - it must stay inactive despite requiresValue()==false,
        // unlike every other value-less mode (is empty/is null/true/false), which IS its own active predicate.
        FilterMeta filterMeta = FilterMeta.builder()
                .field("active")
                .filterBy(mock(ValueExpression.class))
                .matchMode(MatchMode.ALL)
                .build();

        assertFalse(filterMeta.isActive());
    }
}
