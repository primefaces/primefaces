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

    /** Every value-less MatchMode - the value <input> is hidden while any of these is selected. */
    private static final List<MatchMode> VALUE_LESS_MODES = List.of(
            MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY, MatchMode.IS_NULL, MatchMode.NOT_NULL,
            MatchMode.IS_TRUE, MatchMode.IS_FALSE, MatchMode.ALL,
            MatchMode.IS_TODAY, MatchMode.IS_YESTERDAY, MatchMode.IS_TOMORROW,
            MatchMode.IS_THIS_WEEK, MatchMode.IS_LAST_WEEK, MatchMode.IS_NEXT_WEEK,
            MatchMode.IS_THIS_MONTH, MatchMode.IS_LAST_MONTH, MatchMode.IS_NEXT_MONTH,
            MatchMode.IS_THIS_QUARTER, MatchMode.IS_LAST_QUARTER, MatchMode.IS_NEXT_QUARTER,
            MatchMode.IS_THIS_YEAR, MatchMode.IS_LAST_YEAR, MatchMode.IS_NEXT_YEAR);

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
    void textNumericAndDatePresets_areNotFullySymbolic() {
        // TEXT_OPTIONS mixes comparison operators (equals/notEquals) with string-matching operators
        // (contains, startsWith, ...) that have no symbol. NUMERIC_OPTIONS and DATE_OPTIONS, since both were
        // extended with "between"/"is null"/"in list"/relative-date predicates, are no longer
        // purely comparison operators either - all three dropdowns keep spelled-out labels for consistency.
        assertFalse(MatchMode.TEXT_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
        assertFalse(MatchMode.NUMERIC_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
        assertFalse(MatchMode.DATE_OPTIONS.stream().allMatch(mode -> mode.symbol() != null));
    }

    @Test
    void textPreset_includesAllSevenAdditionalModes() {
        // "is (not) empty", "is (not) null", "matches regex" and "(not) in list"
        assertTrue(MatchMode.TEXT_OPTIONS.containsAll(List.of(
                MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                MatchMode.MATCHES_REGEX, MatchMode.IN, MatchMode.NOT_IN)));
    }

    @Test
    void numericPreset_includesAllSixAdditionalModes() {
        // "(not) between", "is (not) null" and "(not) in list"
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
        assertEquals("e.g., 30", MatchMode.LAST_N_DAYS.placeholderHint());
        assertEquals("e.g., 30", MatchMode.NEXT_N_DAYS.placeholderHint());
        assertEquals("e.g., 30", MatchMode.RELATIVE_DATE.placeholderHint());
        assertEquals("e.g., 30", MatchMode.LAST_N_MINUTES.placeholderHint());
        assertEquals("e.g., 30", MatchMode.NEXT_N_MINUTES.placeholderHint());
        assertEquals("e.g., 30", MatchMode.LAST_N_HOURS.placeholderHint());
        assertEquals("e.g., 30", MatchMode.NEXT_N_HOURS.placeholderHint());
        assertEquals("value1, value2, ...", MatchMode.CONTAINS_ANY.placeholderHint());
        assertEquals("value1, value2, ...", MatchMode.CONTAINS_ALL.placeholderHint());
        assertEquals("value1, value2, ...", MatchMode.CONTAINS_NONE.placeholderHint());
    }

    @Test
    void placeholderHint_undefinedForArrayContains() {
        // single-value modes, like EQUALS - no special hint needed
        assertNull(MatchMode.ARRAY_CONTAINS.placeholderHint());
        assertNull(MatchMode.ARRAY_NOT_CONTAINS.placeholderHint());
    }

    @Test
    void placeholderHint_undefinedForSingleValueModes() {
        assertNull(MatchMode.EQUALS.placeholderHint());
        assertNull(MatchMode.CONTAINS.placeholderHint());
        assertNull(MatchMode.IS_EMPTY.placeholderHint());
        assertNull(MatchMode.IS_TODAY.placeholderHint());
    }

    @Test
    void requiresValue_falseForValueLessPredicates() {
        for (MatchMode mode : VALUE_LESS_MODES) {
            assertFalse(mode.requiresValue(), mode + " should be value-less");
        }
    }

    @Test
    void requiresValue_trueForEverythingElse() {
        for (MatchMode mode : MatchMode.values()) {
            if (VALUE_LESS_MODES.contains(mode)) {
                continue;
            }
            assertTrue(mode.requiresValue(), mode + " should require a value");
        }
    }

    @Test
    void booleanPreset_isEntirelyValueLess() {
        // "All", "true", "false", "is null", "is not null" - the value input never shows
        assertTrue(MatchMode.BOOLEAN_OPTIONS.stream().noneMatch(MatchMode::requiresValue));
        assertEquals(List.of(MatchMode.ALL, MatchMode.IS_TRUE, MatchMode.IS_FALSE, MatchMode.IS_NULL, MatchMode.NOT_NULL),
                MatchMode.BOOLEAN_OPTIONS);
    }

    @Test
    void booleanPreset_defaultsToAll_soAFreshColumnIsNotSilentlyFiltered() {
        // unlike "contains" (requires a value that starts empty), every BOOLEAN_OPTIONS mode
        // is its own complete predicate; without "All" as the first/default option, a column with no explicit
        // filterMatchMode would silently start filtered to whichever mode happened to be listed first.
        assertEquals(MatchMode.ALL, MatchMode.BOOLEAN_OPTIONS.get(0));
    }

    @Test
    void parseOptions_booleanKeyword_returnsBooleanPreset() {
        assertEquals(MatchMode.BOOLEAN_OPTIONS, MatchMode.parseOptions("boolean"));
    }

    @Test
    void enumPreset_has6Modes_everyOneAlreadyExistsForOtherPresets() {
        // "is"/"is not", "is any of"/"is none of" (In/NotIn) and "is (not) empty" - no new
        // MatchMode constants needed, this preset just curates a subset with enum-appropriate labels
        assertEquals(6, MatchMode.ENUM_OPTIONS.size());
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.IN, MatchMode.NOT_IN,
                        MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY),
                MatchMode.ENUM_OPTIONS);
    }

    @Test
    void parseOptions_enumKeyword_returnsEnumPreset() {
        assertEquals(MatchMode.ENUM_OPTIONS, MatchMode.parseOptions("enum"));
    }

    @Test
    void arrayPreset_has7Modes() {
        // "contains"/"does not contain" (single value), "contains any"/"contains all"/
        // "contains none" (multi-value), and "is (not) empty" (reused, now collection/array-aware)
        assertEquals(7, MatchMode.ARRAY_OPTIONS.size());
        assertEquals(
                List.of(MatchMode.ARRAY_CONTAINS, MatchMode.ARRAY_NOT_CONTAINS, MatchMode.CONTAINS_ANY,
                        MatchMode.CONTAINS_ALL, MatchMode.CONTAINS_NONE, MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY),
                MatchMode.ARRAY_OPTIONS);
    }

    @Test
    void parseOptions_arrayKeyword_returnsArrayPreset() {
        assertEquals(MatchMode.ARRAY_OPTIONS, MatchMode.parseOptions("array"));
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
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN,
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS,
                        MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY,
                        MatchMode.IS_TODAY, MatchMode.IS_YESTERDAY, MatchMode.IS_TOMORROW,
                        MatchMode.IS_THIS_WEEK, MatchMode.IS_LAST_WEEK, MatchMode.IS_NEXT_WEEK,
                        MatchMode.IS_THIS_MONTH, MatchMode.IS_LAST_MONTH, MatchMode.IS_NEXT_MONTH,
                        MatchMode.IS_THIS_QUARTER, MatchMode.IS_LAST_QUARTER, MatchMode.IS_NEXT_QUARTER,
                        MatchMode.IS_THIS_YEAR, MatchMode.IS_LAST_YEAR, MatchMode.IS_NEXT_YEAR,
                        MatchMode.LAST_N_DAYS, MatchMode.NEXT_N_DAYS, MatchMode.RELATIVE_DATE),
                MatchMode.parseOptions("date"));
    }

    @Test
    void datePreset_has28Modes() {
        // 10 reused (6 comparators + between/not between + is (not) empty) + 18 new
        // (15 value-less relative-date predicates + last/next N days + relative date)
        assertEquals(28, MatchMode.DATE_OPTIONS.size());
    }

    @Test
    void parseOptions_timeKeyword_returnsTimePreset() {
        assertEquals(MatchMode.TIME_OPTIONS, MatchMode.parseOptions("time"));
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN,
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS,
                        MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY,
                        MatchMode.LAST_N_MINUTES, MatchMode.NEXT_N_MINUTES, MatchMode.LAST_N_HOURS, MatchMode.NEXT_N_HOURS),
                MatchMode.parseOptions("time"));
    }

    @Test
    void timePreset_has14Modes_andNoCalendarPredicates() {
        // 10 reused (6 comparators + between/not between + is (not) empty) + the 4 new
        // minute/hour modes. No day/week/month/... predicates - a bare LocalTime has no date component.
        assertEquals(14, MatchMode.TIME_OPTIONS.size());
        assertFalse(MatchMode.TIME_OPTIONS.contains(MatchMode.IS_TODAY));
        assertFalse(MatchMode.TIME_OPTIONS.contains(MatchMode.LAST_N_DAYS));
    }

    @Test
    void parseOptions_datetimeKeyword_returnsDatetimePreset() {
        assertEquals(MatchMode.DATETIME_OPTIONS, MatchMode.parseOptions("datetime"));
    }

    @Test
    void datetimePreset_has32Modes_andIncludesEveryDateOptionPlusTheFourNewModes() {
        // every DATE_OPTIONS mode (28) plus last/next N minutes/hours (4)
        assertEquals(32, MatchMode.DATETIME_OPTIONS.size());
        assertTrue(MatchMode.DATETIME_OPTIONS.containsAll(MatchMode.DATE_OPTIONS));
        assertTrue(MatchMode.DATETIME_OPTIONS.containsAll(List.of(
                MatchMode.LAST_N_MINUTES, MatchMode.NEXT_N_MINUTES, MatchMode.LAST_N_HOURS, MatchMode.NEXT_N_HOURS)));
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
