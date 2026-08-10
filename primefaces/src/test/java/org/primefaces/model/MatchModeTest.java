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
        assertNull(MatchMode.IS_TRUE.symbol());
        assertNull(MatchMode.IS_FALSE.symbol());
        assertNull(MatchMode.ALL.symbol());
        assertNull(MatchMode.GLOBAL.symbol());
    }

    @Test
    void datePreset_isFullySymbolic() {
        // DataTableRenderer renders "=", "!=", "<", ... instead of spelled-out labels only when every
        // option in the dropdown has a symbol - DATE_OPTIONS consists entirely of comparison operators.
        assertTrue(MatchMode.DATE_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
    }

    @Test
    void textAndNumericPresets_areNotFullySymbolic() {
        // TEXT_OPTIONS mixes comparison operators (equals/notEquals) with string-matching operators
        // (contains, startsWith, ...) that have no symbol. NUMERIC_OPTIONS, since it was extended with
        // "between"/"is null"/"in list" (see GitHub #7427), is no longer purely comparison operators either -
        // both dropdowns must keep spelled-out labels so their options read consistently.
        assertFalse(MatchMode.TEXT_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
        assertFalse(MatchMode.NUMERIC_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
    }

    @Test
    void textPreset_includesAllSevenAdditionalModes() {
        // See GitHub #7427 - "is (not) empty", "is (not) null", "matches regex" and "(not) in list"
        assertTrue(MatchMode.TEXT_OPTIONS.containsAll(List.of(
                MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                MatchMode.MATCHES_REGEX, MatchMode.IN, MatchMode.NOT_IN)));
    }

    @Test
    void numericPreset_includesAllSixAdditionalModes() {
        // See GitHub #7427 - "(not) between", "is (not) null" and "(not) in list"
        assertTrue(MatchMode.NUMERIC_OPTIONS.containsAll(List.of(
                MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                MatchMode.IN, MatchMode.NOT_IN)));
    }

    @Test
    void placeholderHint_definedForMultiValueModes() {
        assertEquals("min,max", MatchMode.BETWEEN.placeholderHint());
        assertEquals("min,max", MatchMode.NOT_BETWEEN.placeholderHint());
        assertEquals("value1, value2, ...", MatchMode.IN.placeholderHint());
        assertEquals("value1, value2, ...", MatchMode.NOT_IN.placeholderHint());
    }

    @Test
    void placeholderHint_undefinedForSingleValueModes() {
        assertNull(MatchMode.EQUALS.placeholderHint());
        assertNull(MatchMode.CONTAINS.placeholderHint());
        assertNull(MatchMode.IS_EMPTY.placeholderHint());
    }

    @Test
    void requiresValue_falseForValueLessPredicates() {
        assertFalse(MatchMode.IS_EMPTY.requiresValue());
        assertFalse(MatchMode.NOT_EMPTY.requiresValue());
        assertFalse(MatchMode.IS_NULL.requiresValue());
        assertFalse(MatchMode.NOT_NULL.requiresValue());
        assertFalse(MatchMode.IS_TRUE.requiresValue());
        assertFalse(MatchMode.IS_FALSE.requiresValue());
        assertFalse(MatchMode.ALL.requiresValue());
    }

    @Test
    void requiresValue_trueForEverythingElse() {
        List<MatchMode> valueLess = List.of(MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY, MatchMode.IS_NULL,
                MatchMode.NOT_NULL, MatchMode.IS_TRUE, MatchMode.IS_FALSE, MatchMode.ALL);
        for (MatchMode mode : MatchMode.values()) {
            if (valueLess.contains(mode)) {
                continue;
            }
            assertTrue(mode.requiresValue(), mode + " should require a value");
        }
    }

    @Test
    void booleanPreset_isEntirelyValueLess() {
        // See GitHub #7427 - "All", "true", "false", "is null", "is not null" - the value input never shows
        assertTrue(MatchMode.BOOLEAN_OPTIONS.stream().noneMatch(MatchMode::requiresValue));
        assertEquals(List.of(MatchMode.ALL, MatchMode.IS_TRUE, MatchMode.IS_FALSE, MatchMode.IS_NULL, MatchMode.NOT_NULL),
                MatchMode.BOOLEAN_OPTIONS);
    }

    @Test
    void booleanPreset_defaultsToAll_soAFreshColumnIsNotSilentlyFiltered() {
        // See GitHub #7427 - unlike "contains" (requires a value that starts empty), every BOOLEAN_OPTIONS mode
        // is its own complete predicate; without "All" as the first/default option, a column with no explicit
        // filterMatchMode would silently start filtered to whichever mode happened to be listed first.
        assertEquals(MatchMode.ALL, MatchMode.BOOLEAN_OPTIONS.get(0));
    }

    @Test
    void parseOptions_booleanKeyword_returnsBooleanPreset() {
        assertEquals(MatchMode.BOOLEAN_OPTIONS, MatchMode.parseOptions("boolean"));
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
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS,
                        MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                        MatchMode.IN, MatchMode.NOT_IN),
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
