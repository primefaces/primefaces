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

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelativeNDaysFilterConstraintTest {

    // [today - n, today]
    private final RelativeNDaysFilterConstraint lastNDays = new RelativeNDaysFilterConstraint(
            (today, n) -> new LocalDate[] {today.minusDays(n), today});

    // [today, today + n]
    private final RelativeNDaysFilterConstraint nextNDays = new RelativeNDaysFilterConstraint(
            (today, n) -> new LocalDate[] {today, today.plusDays(n)});

    // [today - n, today + n]
    private final RelativeNDaysFilterConstraint relativeDate = new RelativeNDaysFilterConstraint(
            (today, n) -> new LocalDate[] {today.minusDays(n), today.plusDays(n)});

    @Test
    void testLastNDays_withinRange() {
        LocalDate today = LocalDate.now();
        assertTrue(lastNDays.isMatching(null, today.minusDays(5), "30", null));
        assertTrue(lastNDays.isMatching(null, today, "30", null));
    }

    @Test
    void testLastNDays_outsideRange() {
        LocalDate today = LocalDate.now();
        assertFalse(lastNDays.isMatching(null, today.minusDays(31), "30", null));
        assertFalse(lastNDays.isMatching(null, today.plusDays(1), "30", null));
    }

    @Test
    void testNextNDays_withinRange() {
        LocalDate today = LocalDate.now();
        assertTrue(nextNDays.isMatching(null, today, "30", null));
        assertTrue(nextNDays.isMatching(null, today.plusDays(30), "30", null));
    }

    @Test
    void testNextNDays_outsideRange() {
        LocalDate today = LocalDate.now();
        assertFalse(nextNDays.isMatching(null, today.plusDays(31), "30", null));
        assertFalse(nextNDays.isMatching(null, today.minusDays(1), "30", null));
    }

    @Test
    void testRelativeDate_withinWindowBothDirections() {
        LocalDate today = LocalDate.now();
        assertTrue(relativeDate.isMatching(null, today.minusDays(7), "7", null));
        assertTrue(relativeDate.isMatching(null, today.plusDays(7), "7", null));
        assertTrue(relativeDate.isMatching(null, today, "7", null));
    }

    @Test
    void testRelativeDate_outsideWindow() {
        LocalDate today = LocalDate.now();
        assertFalse(relativeDate.isMatching(null, today.minusDays(8), "7", null));
        assertFalse(relativeDate.isMatching(null, today.plusDays(8), "7", null));
    }

    @Test
    void testIsMatching_NullValue() {
        assertFalse(lastNDays.isMatching(null, null, "30", null));
    }

    @Test
    void testIsMatching_NullFilter() {
        assertFalse(lastNDays.isMatching(null, LocalDate.now(), null, null));
    }

    @Test
    void testIsMatching_NonParsableFilter() {
        assertFalse(lastNDays.isMatching(null, LocalDate.now(), "not-a-number", null));
    }

    @Test
    void testIsMatching_IntegerFilter() {
        // already converted (not a raw String) - UITable.parseIntegerFilter() produces an Integer
        assertTrue(lastNDays.isMatching(null, LocalDate.now(), 30, null));
    }
}
