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

class RelativeDateRangeFilterConstraintTest {

    /**
     * A fixed [2026-08-05, 2026-08-11] range, independent of the real "today", so the boundary checks below are
     * deterministic regardless of when this test runs.
     */
    private final RelativeDateRangeFilterConstraint constraint = new RelativeDateRangeFilterConstraint(
            (today, locale) -> new LocalDate[] {LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 11)});

    @Test
    void testIsMatching_WithinRange() {
        assertTrue(constraint.isMatching(null, LocalDate.of(2026, 8, 8), null, null));
    }

    @Test
    void testIsMatching_OnStartBoundary() {
        assertTrue(constraint.isMatching(null, LocalDate.of(2026, 8, 5), null, null));
    }

    @Test
    void testIsMatching_OnEndBoundary() {
        assertTrue(constraint.isMatching(null, LocalDate.of(2026, 8, 11), null, null));
    }

    @Test
    void testIsMatching_BeforeRange() {
        assertFalse(constraint.isMatching(null, LocalDate.of(2026, 8, 4), null, null));
    }

    @Test
    void testIsMatching_AfterRange() {
        assertFalse(constraint.isMatching(null, LocalDate.of(2026, 8, 12), null, null));
    }

    @Test
    void testIsMatching_NullValue() {
        assertFalse(constraint.isMatching(null, null, null, null));
    }

    @Test
    void testIsMatching_UnsupportedValueType() {
        assertFalse(constraint.isMatching(null, "not a date", null, null));
    }

    @Test
    void testIsMatching_IgnoresFilterValue() {
        assertTrue(constraint.isMatching(null, LocalDate.of(2026, 8, 8), "ignored", null));
    }
}
