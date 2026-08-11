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
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import jakarta.faces.context.FacesContext;

/**
 * "Last/next N minutes/hours" - matches when the field value falls within a range computed from "now" and the
 * user-typed count N. Used by both {@code MatchMode.TIME_OPTIONS} and {@code MatchMode.DATETIME_OPTIONS}, so it
 * has to handle two different kinds of "now" depending on the field's actual runtime type:
 * <ul>
 *   <li>a bare {@link LocalTime} (no date component) is a <b>cyclic</b> 24h clock - a "last 30 minutes" window
 *   can wrap past midnight (e.g. now is 00:05, so the window is [23:35, 00:05]) - handled by
 *   {@link #isWithinCyclicRange};</li>
 *   <li>anything with a date component (via {@link DateFilterUtils#toLocalDateTime}) is <b>linear</b> - ordinary
 *   {@code isBefore}/{@code isAfter} range logic applies, same as {@link RelativeNDaysFilterConstraint}.</li>
 * </ul>
 * See GitHub #7427.
 */
public class RelativeMinutesOrHoursFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    private final ChronoUnit unit;
    private final boolean forward;
    private final Clock clock;

    /**
     * @param unit {@link ChronoUnit#MINUTES} or {@link ChronoUnit#HOURS}
     * @param forward {@code true} for "next N" (now to now+N), {@code false} for "last N" (now-N to now)
     */
    public RelativeMinutesOrHoursFilterConstraint(ChronoUnit unit, boolean forward) {
        this(unit, forward, Clock.systemDefaultZone());
    }

    /**
     * @param clock the clock "now" is read from - fixed to a specific instant by tests to deterministically
     *              exercise the cyclic midnight-wraparound branch without waiting for the real clock
     */
    RelativeMinutesOrHoursFilterConstraint(ChronoUnit unit, boolean forward, Clock clock) {
        this.unit = unit;
        this.forward = forward;
        this.clock = clock;
    }

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        Integer amount = DateFilterUtils.toInteger(filter);
        if (amount == null) {
            return false;
        }

        if (value instanceof LocalTime) {
            LocalTime timeValue = (LocalTime) value;
            LocalTime now = LocalTime.now(clock);
            LocalTime start = forward ? now : now.minus(amount, unit);
            LocalTime end = forward ? now.plus(amount, unit) : now;
            return isWithinCyclicRange(timeValue, start, end);
        }

        LocalDateTime dateTimeValue = DateFilterUtils.toLocalDateTime(value);
        if (dateTimeValue == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime start = forward ? now : now.minus(amount, unit);
        LocalDateTime end = forward ? now.plus(amount, unit) : now;
        return !dateTimeValue.isBefore(start) && !dateTimeValue.isAfter(end);
    }

    /**
     * Inclusive {@code [start, end]} range check on a cyclic 24h clock. When {@code start} is after {@code end}
     * (the window wraps past midnight), a value matches if it's on either side of midnight within the window,
     * so the check becomes an OR instead of the usual AND.
     */
    private static boolean isWithinCyclicRange(LocalTime value, LocalTime start, LocalTime end) {
        if (!start.isAfter(end)) {
            return !value.isBefore(start) && !value.isAfter(end);
        }
        return !value.isBefore(start) || !value.isAfter(end);
    }
}
