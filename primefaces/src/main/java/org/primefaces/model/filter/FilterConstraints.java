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
import org.primefaces.util.MapBuilder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import jakarta.faces.FacesException;

public final class FilterConstraints {

    private static final Map<MatchMode, FilterConstraint> ALL_CONSTRAINTS = MapBuilder.<MatchMode, FilterConstraint>builder()
            .put(MatchMode.STARTS_WITH, new StartsWithFilterConstraint())
            .put(MatchMode.NOT_STARTS_WITH, new NegationFilterConstraintWrapper(new StartsWithFilterConstraint()))
            .put(MatchMode.ENDS_WITH, new EndsWithFilterConstraint())
            .put(MatchMode.NOT_ENDS_WITH, new NegationFilterConstraintWrapper(new EndsWithFilterConstraint()))
            .put(MatchMode.CONTAINS, new ContainsFilterConstraint())
            .put(MatchMode.NOT_CONTAINS, new NegationFilterConstraintWrapper(new ContainsFilterConstraint()))
            .put(MatchMode.EXACT, new ExactFilterConstraint())
            .put(MatchMode.NOT_EXACT, new NegationFilterConstraintWrapper(new ExactFilterConstraint()))
            .put(MatchMode.LESS_THAN, new LessThanFilterConstraint())
            .put(MatchMode.LESS_THAN_EQUALS, new LessThanEqualsFilterConstraint())
            .put(MatchMode.GREATER_THAN, new GreaterThanFilterConstraint())
            .put(MatchMode.GREATER_THAN_EQUALS, new GreaterThanEqualsFilterConstraint())
            .put(MatchMode.EQUALS, new EqualsFilterConstraint())
            .put(MatchMode.NOT_EQUALS, new NegationFilterConstraintWrapper(new EqualsFilterConstraint()))
            .put(MatchMode.IN, new InFilterConstraint())
            .put(MatchMode.NOT_IN, new NegationFilterConstraintWrapper(new InFilterConstraint()))
            .put(MatchMode.GLOBAL, new GlobalFilterConstraint())
            .put(MatchMode.BETWEEN, new BetweenFilterConstraint())
            .put(MatchMode.NOT_BETWEEN, new NegationFilterConstraintWrapper(new BetweenFilterConstraint()))
            .put(MatchMode.IS_EMPTY, new IsEmptyFilterConstraint())
            .put(MatchMode.NOT_EMPTY, new NegationFilterConstraintWrapper(new IsEmptyFilterConstraint()))
            .put(MatchMode.IS_NULL, new IsNullFilterConstraint())
            .put(MatchMode.NOT_NULL, new NegationFilterConstraintWrapper(new IsNullFilterConstraint()))
            .put(MatchMode.MATCHES_REGEX, new MatchesRegexFilterConstraint())
            .put(MatchMode.IS_TRUE, new IsTrueFilterConstraint())
            .put(MatchMode.IS_FALSE, new IsFalseFilterConstraint())
            .put(MatchMode.ALL, new AllFilterConstraint())
            .put(MatchMode.IS_TODAY, new RelativeDateRangeFilterConstraint((today, locale) -> new LocalDate[] {today, today}))
            .put(MatchMode.IS_YESTERDAY, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate d = today.minusDays(1);
                return new LocalDate[] {d, d};
            }))
            .put(MatchMode.IS_TOMORROW, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate d = today.plusDays(1);
                return new LocalDate[] {d, d};
            }))
            .put(MatchMode.IS_THIS_WEEK, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate start = DateFilterUtils.startOfWeek(today, locale);
                return new LocalDate[] {start, start.plusDays(6)};
            }))
            .put(MatchMode.IS_LAST_WEEK, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate start = DateFilterUtils.startOfWeek(today, locale).minusWeeks(1);
                return new LocalDate[] {start, start.plusDays(6)};
            }))
            .put(MatchMode.IS_NEXT_WEEK, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate start = DateFilterUtils.startOfWeek(today, locale).plusWeeks(1);
                return new LocalDate[] {start, start.plusDays(6)};
            }))
            .put(MatchMode.IS_THIS_MONTH, new RelativeDateRangeFilterConstraint((today, locale) ->
                    new LocalDate[] {DateFilterUtils.startOfMonth(today), DateFilterUtils.endOfMonth(today)}))
            .put(MatchMode.IS_LAST_MONTH, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate lastMonth = today.minusMonths(1);
                return new LocalDate[] {DateFilterUtils.startOfMonth(lastMonth), DateFilterUtils.endOfMonth(lastMonth)};
            }))
            .put(MatchMode.IS_NEXT_MONTH, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate nextMonth = today.plusMonths(1);
                return new LocalDate[] {DateFilterUtils.startOfMonth(nextMonth), DateFilterUtils.endOfMonth(nextMonth)};
            }))
            .put(MatchMode.IS_THIS_QUARTER, new RelativeDateRangeFilterConstraint((today, locale) ->
                    new LocalDate[] {DateFilterUtils.startOfQuarter(today), DateFilterUtils.endOfQuarter(today)}))
            .put(MatchMode.IS_LAST_QUARTER, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate start = DateFilterUtils.startOfQuarter(today).minusMonths(3);
                return new LocalDate[] {start, start.plusMonths(3).minusDays(1)};
            }))
            .put(MatchMode.IS_NEXT_QUARTER, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate start = DateFilterUtils.startOfQuarter(today).plusMonths(3);
                return new LocalDate[] {start, start.plusMonths(3).minusDays(1)};
            }))
            .put(MatchMode.IS_THIS_YEAR, new RelativeDateRangeFilterConstraint((today, locale) ->
                    new LocalDate[] {DateFilterUtils.startOfYear(today), DateFilterUtils.endOfYear(today)}))
            .put(MatchMode.IS_LAST_YEAR, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate lastYear = today.minusYears(1);
                return new LocalDate[] {DateFilterUtils.startOfYear(lastYear), DateFilterUtils.endOfYear(lastYear)};
            }))
            .put(MatchMode.IS_NEXT_YEAR, new RelativeDateRangeFilterConstraint((today, locale) -> {
                LocalDate nextYear = today.plusYears(1);
                return new LocalDate[] {DateFilterUtils.startOfYear(nextYear), DateFilterUtils.endOfYear(nextYear)};
            }))
            .put(MatchMode.LAST_N_DAYS, new RelativeNDaysFilterConstraint((today, n) -> new LocalDate[] {today.minusDays(n), today}))
            .put(MatchMode.NEXT_N_DAYS, new RelativeNDaysFilterConstraint((today, n) -> new LocalDate[] {today, today.plusDays(n)}))
            .put(MatchMode.RELATIVE_DATE,
                    new RelativeNDaysFilterConstraint((today, n) -> new LocalDate[] {today.minusDays(n), today.plusDays(n)}))
            .put(MatchMode.LAST_N_MINUTES, new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, false))
            .put(MatchMode.NEXT_N_MINUTES, new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.MINUTES, true))
            .put(MatchMode.LAST_N_HOURS, new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.HOURS, false))
            .put(MatchMode.NEXT_N_HOURS, new RelativeMinutesOrHoursFilterConstraint(ChronoUnit.HOURS, true))
            .put(MatchMode.ARRAY_CONTAINS, new ArrayContainsFilterConstraint())
            .put(MatchMode.ARRAY_NOT_CONTAINS, new NegationFilterConstraintWrapper(new ArrayContainsFilterConstraint()))
            .put(MatchMode.CONTAINS_ANY, new ContainsAnyFilterConstraint())
            .put(MatchMode.CONTAINS_ALL, new ContainsAllFilterConstraint())
            .put(MatchMode.CONTAINS_NONE, new NegationFilterConstraintWrapper(new ContainsAnyFilterConstraint()))
            .build();

    private FilterConstraints() {
        // NOOP
    }

    public static FilterConstraint of(MatchMode mode) {
        return Optional.ofNullable(ALL_CONSTRAINTS.get(mode))
                .orElseThrow(() -> new FacesException("No filter constraint found for match mode: " + mode));
    }

    public static FilterConstraint of(String matchMode) {
        MatchMode mode = MatchMode.of(matchMode);
        return of(mode);
    }
}
