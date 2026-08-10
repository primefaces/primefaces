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

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchModeTest {

    @Test
    void symbol_definedForComparisonOperators() {
        assertEquals("=", MatchMode.EQUALS.symbol());
        assertEquals("!=", MatchMode.NOT_EQUALS.symbol());
        assertEquals("<", MatchMode.LESS_THAN.symbol());
        assertEquals("<=", MatchMode.LESS_THAN_EQUALS.symbol());
        assertEquals(">", MatchMode.GREATER_THAN.symbol());
        assertEquals(">=", MatchMode.GREATER_THAN_EQUALS.symbol());
    }

    @Test
    void symbol_undefinedForStringMatchingOperators() {
        assertNull(MatchMode.CONTAINS.symbol());
        assertNull(MatchMode.NOT_CONTAINS.symbol());
        assertNull(MatchMode.STARTS_WITH.symbol());
        assertNull(MatchMode.NOT_STARTS_WITH.symbol());
        assertNull(MatchMode.ENDS_WITH.symbol());
        assertNull(MatchMode.NOT_ENDS_WITH.symbol());
        assertNull(MatchMode.EXACT.symbol());
        assertNull(MatchMode.NOT_EXACT.symbol());
        assertNull(MatchMode.IN.symbol());
        assertNull(MatchMode.NOT_IN.symbol());
        assertNull(MatchMode.BETWEEN.symbol());
        assertNull(MatchMode.NOT_BETWEEN.symbol());
        assertNull(MatchMode.GLOBAL.symbol());
    }

    @Test
    void numericAndDatePresets_areFullySymbolic() {
        // DataTableRenderer renders "=", "!=", "<", ... instead of spelled-out labels only when every
        // option in the dropdown has a symbol - both presets consist entirely of comparison operators.
        assertTrue(MatchMode.NUMERIC_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
        assertTrue(MatchMode.DATE_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
    }

    @Test
    void textPreset_isNotFullySymbolic() {
        // TEXT_OPTIONS mixes comparison operators (equals/notEquals) with string-matching operators
        // (contains, startsWith, ...) that have no symbol, so its dropdown must keep spelled-out labels.
        assertFalse(MatchMode.TEXT_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
    }

    @Test
    void textPreset_includesAllSevenAdditionalModes() {
        // See GitHub #7427 - "is (not) empty", "is (not) null", "matches regex" and "(not) in list"
        assertTrue(MatchMode.TEXT_OPTIONS.containsAll(List.of(
                MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                MatchMode.MATCHES_REGEX, MatchMode.IN, MatchMode.NOT_IN)));
    }

    @Test
    void requiresValue_falseForValueLessPredicates() {
        assertFalse(MatchMode.IS_EMPTY.requiresValue());
        assertFalse(MatchMode.NOT_EMPTY.requiresValue());
        assertFalse(MatchMode.IS_NULL.requiresValue());
        assertFalse(MatchMode.NOT_NULL.requiresValue());
    }

    @Test
    void requiresValue_trueForEverythingElse() {
        for (MatchMode mode : MatchMode.values()) {
            if (mode == MatchMode.IS_EMPTY || mode == MatchMode.NOT_EMPTY
                    || mode == MatchMode.IS_NULL || mode == MatchMode.NOT_NULL) {
                continue;
            }
            assertTrue(mode.requiresValue(), mode + " should require a value");
        }
    }

    @Test
    void parseOptions_blank_returnsEmptyList() {
        assertTrue(MatchMode.parseOptions(null).isEmpty());
        assertTrue(MatchMode.parseOptions("").isEmpty());
        assertTrue(MatchMode.parseOptions("   ").isEmpty());
    }

    @Test
    void parseOptions_numericKeyword_returnsComparatorPreset() {
        assertEquals(MatchMode.NUMERIC_OPTIONS, MatchMode.parseOptions("numeric"));
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN,
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS),
                MatchMode.parseOptions("numeric"));
    }

    @Test
    void parseOptions_textKeyword_returnsTextPreset() {
        assertEquals(MatchMode.TEXT_OPTIONS, MatchMode.parseOptions("text"));
    }

    @Test
    void parseOptions_dateKeyword_returnsDatePreset() {
        assertEquals(MatchMode.DATE_OPTIONS, MatchMode.parseOptions("date"));
    }

    @Test
    void parseOptions_explicitCommaList_isParsedInOrder() {
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN, MatchMode.GREATER_THAN),
                MatchMode.parseOptions("equals,notEquals,lt,gt"));
    }

    @Test
    void parseOptions_explicitCommaList_trimsWhitespace() {
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS),
                MatchMode.parseOptions(" equals , notEquals "));
    }

    @Test
    void parseOptions_unknownOperator_throws() {
        assertThrows(UnsupportedOperationException.class, () -> MatchMode.parseOptions("bogus"));
    }
}
