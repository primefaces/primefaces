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

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchModeTest {

    private static final String RANGE_HINT = "primefaces.datatable.filterMatchMode.placeholderHint.RANGE";
    private static final String LIST_HINT = "primefaces.datatable.filterMatchMode.placeholderHint.LIST";
    private static final String COUNT_HINT = "primefaces.datatable.filterMatchMode.placeholderHint.COUNT";

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
        assertEquals("≠", MatchMode.NOT_EQUALS.symbol());
        assertEquals("<", MatchMode.LESS_THAN.symbol());
        assertEquals("≤", MatchMode.LESS_THAN_EQUALS.symbol());
        assertEquals(">", MatchMode.GREATER_THAN.symbol());
        assertEquals("≥", MatchMode.GREATER_THAN_EQUALS.symbol());
    }

    @Test
    void symbol_definedForEveryMatchMode() {
        // every MatchMode - not just the comparison operators - carries its own single-character symbol,
        // rendered as the visible glyph in the filter match-mode menu and the column header's active-mode badge
        for (MatchMode mode : MatchMode.values()) {
            assertNotNull(mode.symbol(), mode + " should have a symbol");
            assertEquals(1, mode.symbol().codePointCount(0, mode.symbol().length()),
                    mode + "'s symbol should be a single character: " + mode.symbol());
        }
    }

    @Test
    void symbol_isUniquePerMatchMode() {
        // every dropdown preset mixes modes freely, so distinctness is guaranteed the simple way: no two
        // MatchMode values share a symbol anywhere, not just within one preset
        long distinctSymbols = java.util.Arrays.stream(MatchMode.values()).map(MatchMode::symbol).distinct().count();
        assertEquals(MatchMode.values().length, distinctSymbols);
    }

    @Test
    void textPreset_includesAllSevenAdditionalModes() {
        // "is (not) empty", "is (not) null", "matches regex" and "(not) in list"
        assertTrue(MatchMode.TEXT_MATCH_MODES.containsAll(List.of(
                MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                MatchMode.MATCHES_REGEX, MatchMode.IN, MatchMode.NOT_IN)));
    }

    @Test
    void numericPreset_includesAllSixAdditionalModes() {
        // "(not) between", "is (not) null" and "(not) in list"
        assertTrue(MatchMode.NUMERIC_MATCH_MODES.containsAll(List.of(
                MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                MatchMode.IN, MatchMode.NOT_IN)));
    }

    @Test
    void placeholderHint_definedForMultiValueModes() {
        assertEquals(RANGE_HINT, MatchMode.BETWEEN.placeholderHintKey());
        assertEquals(RANGE_HINT, MatchMode.NOT_BETWEEN.placeholderHintKey());
        assertEquals(LIST_HINT, MatchMode.IN.placeholderHintKey());
        assertEquals(LIST_HINT, MatchMode.NOT_IN.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.LAST_N_DAYS.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.NEXT_N_DAYS.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.RELATIVE_DATE.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.LAST_N_MINUTES.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.NEXT_N_MINUTES.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.LAST_N_HOURS.placeholderHintKey());
        assertEquals(COUNT_HINT, MatchMode.NEXT_N_HOURS.placeholderHintKey());
        assertEquals(LIST_HINT, MatchMode.CONTAINS_ANY.placeholderHintKey());
        assertEquals(LIST_HINT, MatchMode.CONTAINS_ALL.placeholderHintKey());
        assertEquals(LIST_HINT, MatchMode.CONTAINS_NONE.placeholderHintKey());
    }

    @Test
    void placeholderHint_undefinedForArrayContains() {
        // single-value modes, like EQUALS - no special hint needed
        assertNull(MatchMode.ARRAY_CONTAINS.placeholderHintKey());
        assertNull(MatchMode.ARRAY_NOT_CONTAINS.placeholderHintKey());
    }

    @Test
    void placeholderHint_undefinedForSingleValueModes() {
        assertNull(MatchMode.EQUALS.placeholderHintKey());
        assertNull(MatchMode.CONTAINS.placeholderHintKey());
        assertNull(MatchMode.IS_EMPTY.placeholderHintKey());
        assertNull(MatchMode.IS_TODAY.placeholderHintKey());
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
        assertTrue(MatchMode.BOOLEAN_MATCH_MODES.stream().noneMatch(MatchMode::requiresValue));
        assertEquals(List.of(MatchMode.ALL, MatchMode.IS_TRUE, MatchMode.IS_FALSE, MatchMode.IS_NULL, MatchMode.NOT_NULL),
                MatchMode.BOOLEAN_MATCH_MODES);
    }

    @Test
    void booleanPreset_defaultsToAll_soAFreshColumnIsNotSilentlyFiltered() {
        // unlike "contains" (requires a value that starts empty), every BOOLEAN_MATCH_MODES mode
        // is its own complete predicate; without "All" as the first/default option, a column with no explicit
        // filterMatchMode would silently start filtered to whichever mode happened to be listed first.
        assertEquals(MatchMode.ALL, MatchMode.BOOLEAN_MATCH_MODES.get(0));
    }

    @Test
    void parseOptions_booleanKeyword_returnsBooleanPreset() {
        assertEquals(MatchMode.BOOLEAN_MATCH_MODES, MatchMode.parseOptions("boolean"));
    }

    @Test
    void enumPreset_has6Modes_everyOneAlreadyExistsForOtherPresets() {
        // "is"/"is not", "is any of"/"is none of" (In/NotIn) and "is (not) empty" - no new
        // MatchMode constants needed, this preset just curates a subset with enum-appropriate labels
        assertEquals(6, MatchMode.ENUM_MATCH_MODES.size());
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.IN, MatchMode.NOT_IN,
                        MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY),
                MatchMode.ENUM_MATCH_MODES);
    }

    @Test
    void parseOptions_enumKeyword_returnsEnumPreset() {
        assertEquals(MatchMode.ENUM_MATCH_MODES, MatchMode.parseOptions("enum"));
    }

    @Test
    void arrayPreset_has7Modes() {
        // "contains"/"does not contain" (single value), "contains any"/"contains all"/
        // "contains none" (multi-value), and "is (not) empty" (reused, now collection/array-aware)
        assertEquals(7, MatchMode.ARRAY_MATCH_MODES.size());
        assertEquals(
                List.of(MatchMode.ARRAY_CONTAINS, MatchMode.ARRAY_NOT_CONTAINS, MatchMode.CONTAINS_ANY,
                        MatchMode.CONTAINS_ALL, MatchMode.CONTAINS_NONE, MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY),
                MatchMode.ARRAY_MATCH_MODES);
    }

    @Test
    void parseOptions_arrayKeyword_returnsArrayPreset() {
        assertEquals(MatchMode.ARRAY_MATCH_MODES, MatchMode.parseOptions("array"));
    }

    @Test
    void parseOptions_blank_returnsEmptyList() {
        assertTrue(MatchMode.parseOptions(null).isEmpty());
        assertTrue(MatchMode.parseOptions("").isEmpty());
        assertTrue(MatchMode.parseOptions("   ").isEmpty());
    }

    @Test
    void parseOptions_numericKeyword_returnsComparatorPreset() {
        assertEquals(MatchMode.NUMERIC_MATCH_MODES, MatchMode.parseOptions("numeric"));
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN,
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS,
                        MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_NULL, MatchMode.NOT_NULL,
                        MatchMode.IN, MatchMode.NOT_IN),
                MatchMode.parseOptions("numeric"));
    }

    @Test
    void parseOptions_textKeyword_returnsTextPreset() {
        assertEquals(MatchMode.TEXT_MATCH_MODES, MatchMode.parseOptions("text"));
    }

    @Test
    void parseOptions_dateKeyword_returnsDatePreset() {
        assertEquals(MatchMode.DATE_MATCH_MODES, MatchMode.parseOptions("date"));
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN,
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS,
                        MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY),
                MatchMode.parseOptions("date"));
    }

    @Test
    void datePreset_has10Modes_andNoShortcuts() {
        // just the comparators a typed/picked date needs; the 18 calendar shortcuts are opt-in
        assertEquals(10, MatchMode.DATE_MATCH_MODES.size());
        assertFalse(MatchMode.DATE_MATCH_MODES.contains(MatchMode.IS_TODAY));
        assertFalse(MatchMode.DATE_MATCH_MODES.contains(MatchMode.LAST_N_DAYS));
        assertFalse(MatchMode.DATE_MATCH_MODES.contains(MatchMode.RELATIVE_DATE));
    }

    @Test
    void parseOptions_dateWithShortcuts_appendsTheCalendarShortcuts() {
        List<MatchMode> options = MatchMode.parseOptions("date,shortcuts");

        assertEquals(28, options.size());
        assertEquals(MatchMode.DATE_MATCH_MODES, options.subList(0, 10));
        assertEquals(MatchMode.DATE_SHORTCUT_MATCH_MODES, options.subList(10, 28));
        // a bare LocalDate has no time component, so the minute/hour windows stay out
        assertFalse(options.contains(MatchMode.LAST_N_MINUTES));
    }

    @Test
    void parseOptions_shortcutsToken_resolvesItsGroupRegardlessOfPosition() {
        // the token's POSITION still decides where the group lands in the menu (entries are concatenated
        // left to right), but which group it stands for is read off the whole list
        assertEquals(
                new HashSet<>(MatchMode.parseOptions("date,shortcuts")),
                new HashSet<>(MatchMode.parseOptions("shortcuts,date")));
        assertEquals(MatchMode.IS_TODAY, MatchMode.parseOptions("shortcuts,date").get(0));
    }

    @Test
    void parseOptions_individualShortcutsCanBePickedInsteadOfTheWholeGroup() {
        assertEquals(12, MatchMode.parseOptions("date,today,thisWeek").size());
        assertEquals(
                List.of(MatchMode.IS_TODAY, MatchMode.IS_THIS_WEEK),
                MatchMode.parseOptions("date,today,thisWeek").subList(10, 12));
    }

    @Test
    void parseOptions_timeKeyword_returnsTimePreset() {
        assertEquals(MatchMode.TIME_MATCH_MODES, MatchMode.parseOptions("time"));
        assertEquals(
                List.of(MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN,
                        MatchMode.LESS_THAN_EQUALS, MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS,
                        MatchMode.BETWEEN, MatchMode.NOT_BETWEEN, MatchMode.IS_EMPTY, MatchMode.NOT_EMPTY),
                MatchMode.parseOptions("time"));
    }

    @Test
    void timePreset_has10Modes_andNoShortcuts() {
        assertEquals(10, MatchMode.TIME_MATCH_MODES.size());
        assertFalse(MatchMode.TIME_MATCH_MODES.contains(MatchMode.LAST_N_MINUTES));
    }

    @Test
    void parseOptions_timeWithShortcuts_appendsOnlyTheMinuteAndHourWindows() {
        List<MatchMode> options = MatchMode.parseOptions("time,shortcuts");

        assertEquals(14, options.size());
        assertEquals(MatchMode.TIME_SHORTCUT_MATCH_MODES, options.subList(10, 14));
        // no day/week/month/... predicates - a bare LocalTime has no date component
        assertFalse(options.contains(MatchMode.IS_TODAY));
        assertFalse(options.contains(MatchMode.LAST_N_DAYS));
    }

    @Test
    void parseOptions_datetimeKeyword_returnsDatetimePreset() {
        assertEquals(MatchMode.DATETIME_MATCH_MODES, MatchMode.parseOptions("datetime"));
        assertEquals(10, MatchMode.parseOptions("datetime").size());
    }

    @Test
    void parseOptions_datetimeWithShortcuts_appendsBothShortcutGroups() {
        // a datetime carries both a date and a time-of-day, so it gets the calendar shortcuts (18) and the
        // minute/hour windows (4) on top of the 10 comparators
        List<MatchMode> options = MatchMode.parseOptions("datetime,shortcuts");

        assertEquals(32, options.size());
        assertTrue(options.containsAll(MatchMode.DATE_MATCH_MODES));
        assertTrue(options.containsAll(MatchMode.DATE_SHORTCUT_MATCH_MODES));
        assertTrue(options.containsAll(MatchMode.TIME_SHORTCUT_MATCH_MODES));
    }

    @Test
    void parseOptions_shortcutsWithoutAPresetKeyword_fallsBackToBothGroups() {
        // a hand-written operator list has no keyword to key off, so "shortcuts" means the superset
        List<MatchMode> options = MatchMode.parseOptions("equals,shortcuts");

        assertEquals(MatchMode.EQUALS, options.get(0));
        assertEquals(1 + MatchMode.DATETIME_SHORTCUT_MATCH_MODES.size(), options.size());
    }

    @Test
    void parseOptions_duplicatesAreCollapsed() {
        assertEquals(MatchMode.parseOptions("date"), MatchMode.parseOptions("date,date,equals"));
    }

    @Test
    void parseOptions_noneAnywhereInTheListWins() {
        assertTrue(MatchMode.parseOptions("date,shortcuts,none").isEmpty());
        assertTrue(MatchMode.parseOptions("none").isEmpty());
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
    void parseOptions_matchesTheThreeConfigurationsTheShowcaseDocuments() {
        // the "Opting Into the Relative Date Shortcuts" card in the showcase's filter.xhtml puts these three
        // side by side on one date field and tells the reader to expect "ten entries, twenty-eight, and
        // thirteen" - keep those numbers honest
        assertEquals(10, MatchMode.parseOptions("date").size());
        assertEquals(28, MatchMode.parseOptions("date,shortcuts").size());
        assertEquals(13, MatchMode.parseOptions("date,today,thisWeek,lastNDays").size());
    }

    @Test
    void parseOptions_unknownOperator_throws() {
        assertThrows(UnsupportedOperationException.class, () -> MatchMode.parseOptions("bogus"));
    }
}
