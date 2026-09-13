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

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelativeMinutesOrHoursFilterConstraintTest {

    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2026, 8, 11, 12, 0, 0);

    private final RelativeMinutesOrHoursFilterConstraint lastNMinutes =
            new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, false);

    // -- LocalDateTime (linear, no wraparound), "now" fixed via an injected Clock for determinism --
    // Two independent real-clock now() reads (one in the test, one inside isMatching()) would otherwise
    // race on exact-boundary assertions since real time elapses between the two calls.

    @Test
    void localDateTime_lastNMinutes_withinRange() {
        RelativeMinutesOrHoursFilterConstraint constraint =
                new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, false, fixedClockAt(FIXED_NOW));

        assertTrue(constraint.isMatching(null, FIXED_NOW.minusMinutes(10), "30", null));
        assertTrue(constraint.isMatching(null, FIXED_NOW, "30", null));
    }

    @Test
    void localDateTime_lastNMinutes_outsideRange() {
        RelativeMinutesOrHoursFilterConstraint constraint =
                new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, false, fixedClockAt(FIXED_NOW));

        assertFalse(constraint.isMatching(null, FIXED_NOW.minusMinutes(31), "30", null));
        assertFalse(constraint.isMatching(null, FIXED_NOW.plusMinutes(1), "30", null));
    }

    @Test
    void localDateTime_nextNMinutes_withinRange() {
        RelativeMinutesOrHoursFilterConstraint constraint =
                new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, true, fixedClockAt(FIXED_NOW));

        assertTrue(constraint.isMatching(null, FIXED_NOW, "30", null));
        assertTrue(constraint.isMatching(null, FIXED_NOW.plusMinutes(30), "30", null));
    }

    @Test
    void localDateTime_lastNHours_withinRange() {
        RelativeMinutesOrHoursFilterConstraint constraint =
                new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.HOURS, false, fixedClockAt(FIXED_NOW));

        assertTrue(constraint.isMatching(null, FIXED_NOW.minusHours(1), "2", null));
        assertFalse(constraint.isMatching(null, FIXED_NOW.minusHours(3), "2", null));
    }

    @Test
    void javaUtilDate_isNormalizedToLocalDateTime() {
        java.util.Date now = new java.util.Date();
        assertTrue(lastNMinutes.isMatching(null, now, "30", null));
    }

    // -- LocalTime (cyclic 24h clock), "now" fixed via an injected Clock for determinism --

    @Test
    void localTime_lastNMinutes_noWraparound_atNoon() {
        Clock noon = fixedClockAt(LocalTime.NOON);
        RelativeMinutesOrHoursFilterConstraint constraint = new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, false, noon);

        assertTrue(constraint.isMatching(null, LocalTime.of(11, 45), "30", null));
        assertFalse(constraint.isMatching(null, LocalTime.of(11, 0), "30", null));
        assertFalse(constraint.isMatching(null, LocalTime.of(12, 1), "30", null));
    }

    @Test
    void localTime_lastNMinutes_wrapsPastMidnight() {
        // "now" = 00:05 -> "last 30 minutes" is the wrapped window [23:35, 00:05]
        Clock justAfterMidnight = fixedClockAt(LocalTime.of(0, 5));
        RelativeMinutesOrHoursFilterConstraint constraint =
                new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, false, justAfterMidnight);

        // yesterday's side of the wrapped window
        assertTrue(constraint.isMatching(null, LocalTime.of(23, 45), "30", null));
        // today's side of the wrapped window
        assertTrue(constraint.isMatching(null, LocalTime.of(0, 2), "30", null));
        // outside the window on both sides
        assertFalse(constraint.isMatching(null, LocalTime.of(23, 0), "30", null));
        assertFalse(constraint.isMatching(null, LocalTime.of(12, 0), "30", null));
    }

    @Test
    void localTime_nextNMinutes_wrapsPastMidnight() {
        // "now" = 23:50 -> "next 30 minutes" is the wrapped window [23:50, 00:20]
        Clock justBeforeMidnight = fixedClockAt(LocalTime.of(23, 50));
        RelativeMinutesOrHoursFilterConstraint constraint =
                new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, true, justBeforeMidnight);

        assertTrue(constraint.isMatching(null, LocalTime.of(23, 55), "30", null));
        assertTrue(constraint.isMatching(null, LocalTime.of(0, 15), "30", null));
        assertFalse(constraint.isMatching(null, LocalTime.of(1, 0), "30", null));
        assertFalse(constraint.isMatching(null, LocalTime.of(23, 40), "30", null));
    }

    private static Clock fixedClockAt(LocalTime time) {
        return fixedClockAt(LocalDateTime.of(2026, 8, 11, time.getHour(), time.getMinute(), time.getSecond()));
    }

    private static Clock fixedClockAt(LocalDateTime dateTime) {
        return Clock.fixed(dateTime.atZone(ZoneOffset.UTC).toInstant(), ZoneId.of("UTC"));
    }

    @Test
    void isMatching_nullValue() {
        assertFalse(lastNMinutes.isMatching(null, null, "30", null));
    }

    @Test
    void isMatching_nullOrUnparsableFilter() {
        assertFalse(lastNMinutes.isMatching(null, LocalDateTime.now(), null, null));
        assertFalse(lastNMinutes.isMatching(null, LocalDateTime.now(), "not-a-number", null));
    }
}
