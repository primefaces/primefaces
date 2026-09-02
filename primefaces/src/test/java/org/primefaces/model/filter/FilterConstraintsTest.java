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
package org.primefaces.model.filter;

import org.primefaces.model.MatchMode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link FilterConstraints#isRelativeDateMode} and {@link FilterConstraints#dateRange}, which expose the
 * relative date ranges to server-side implementations such as {@code JPALazyDataModel}.
 */
class FilterConstraintsTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 2); // a Wednesday, Q3

    @Test
    void testIsRelativeDateMode() {
        assertTrue(FilterConstraints.isRelativeDateMode(MatchMode.IS_TODAY));
        assertTrue(FilterConstraints.isRelativeDateMode(MatchMode.IS_THIS_QUARTER));
        assertTrue(FilterConstraints.isRelativeDateMode(MatchMode.LAST_N_DAYS));
        assertTrue(FilterConstraints.isRelativeDateMode(MatchMode.RELATIVE_DATE));

        assertFalse(FilterConstraints.isRelativeDateMode(MatchMode.CONTAINS));
        assertFalse(FilterConstraints.isRelativeDateMode(MatchMode.IS_NULL));
        assertFalse(FilterConstraints.isRelativeDateMode(MatchMode.LAST_N_MINUTES));
        assertFalse(FilterConstraints.isRelativeDateMode(MatchMode.ARRAY_CONTAINS));
    }

    @Test
    void testDateRange_valueLessModes() {
        assertArrayEquals(new LocalDate[] {TODAY, TODAY},
                FilterConstraints.dateRange(MatchMode.IS_TODAY, TODAY, Locale.US, null));
        assertArrayEquals(new LocalDate[] {TODAY.minusDays(1), TODAY.minusDays(1)},
                FilterConstraints.dateRange(MatchMode.IS_YESTERDAY, TODAY, Locale.US, null));
        assertArrayEquals(new LocalDate[] {LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)},
                FilterConstraints.dateRange(MatchMode.IS_THIS_MONTH, TODAY, Locale.US, null));
        assertArrayEquals(new LocalDate[] {LocalDate.of(2026, 7, 1), LocalDate.of(2026, 9, 30)},
                FilterConstraints.dateRange(MatchMode.IS_THIS_QUARTER, TODAY, Locale.US, null));
        assertArrayEquals(new LocalDate[] {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)},
                FilterConstraints.dateRange(MatchMode.IS_THIS_YEAR, TODAY, Locale.US, null));
    }

    @Test
    void testDateRange_weekIsLocaleAware() {
        LocalDate[] us = FilterConstraints.dateRange(MatchMode.IS_THIS_WEEK, TODAY, Locale.US, null);
        assertEquals(DayOfWeek.SUNDAY, us[0].getDayOfWeek());
        assertEquals(us[0].plusDays(6), us[1]);

        LocalDate[] germany = FilterConstraints.dateRange(MatchMode.IS_THIS_WEEK, TODAY, Locale.GERMANY, null);
        assertEquals(DayOfWeek.MONDAY, germany[0].getDayOfWeek());
        assertEquals(germany[0].plusDays(6), germany[1]);
    }

    @Test
    void testDateRange_nDaysModes() {
        assertArrayEquals(new LocalDate[] {TODAY.minusDays(7), TODAY},
                FilterConstraints.dateRange(MatchMode.LAST_N_DAYS, TODAY, Locale.US, 7));
        assertArrayEquals(new LocalDate[] {TODAY, TODAY.plusDays(7)},
                FilterConstraints.dateRange(MatchMode.NEXT_N_DAYS, TODAY, Locale.US, 7));
        assertArrayEquals(new LocalDate[] {TODAY.minusDays(7), TODAY.plusDays(7)},
                FilterConstraints.dateRange(MatchMode.RELATIVE_DATE, TODAY, Locale.US, 7));
    }

    @Test
    void testDateRange_notApplicable() {
        // "N days" without a parsable N
        assertNull(FilterConstraints.dateRange(MatchMode.LAST_N_DAYS, TODAY, Locale.US, null));
        // not a relative date match mode at all
        assertNull(FilterConstraints.dateRange(MatchMode.CONTAINS, TODAY, Locale.US, 7));
        assertNull(FilterConstraints.dateRange(MatchMode.LAST_N_HOURS, TODAY, Locale.US, 7));
    }

    /**
     * The whole point of exposing the ranges: a query built from {@link FilterConstraints#dateRange} has to
     * match exactly what the in-memory constraint of the same match mode matches.
     */
    @Test
    void testDateRange_agreesWithInMemoryConstraint() {
        LocalDate today = LocalDate.now();
        for (MatchMode mode : MatchMode.values()) {
            if (!FilterConstraints.isRelativeDateMode(mode)) {
                continue;
            }

            LocalDate[] range = FilterConstraints.dateRange(mode, today, Locale.US, 3);
            assertNotNull(range, mode.name());

            FilterConstraint constraint = FilterConstraints.of(mode);
            assertTrue(constraint.isMatching(null, range[0], 3, Locale.US), mode.name() + " start of range");
            assertTrue(constraint.isMatching(null, range[1], 3, Locale.US), mode.name() + " end of range");
            assertFalse(constraint.isMatching(null, range[0].minusDays(1), 3, Locale.US), mode.name() + " before range");
            assertFalse(constraint.isMatching(null, range[1].plusDays(1), 3, Locale.US), mode.name() + " after range");
        }
    }
}
