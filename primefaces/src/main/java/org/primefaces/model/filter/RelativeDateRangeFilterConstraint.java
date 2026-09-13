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

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;

import jakarta.faces.context.FacesContext;

/**
 * Value-less relative-date predicate (e.g., {@code MatchMode.IS_TODAY}, {@code IS_THIS_WEEK}) - matches when the
 * field value falls within an {@code [start, end]} range computed from {@code LocalDate.now()} at the moment the
 * filter runs. The typed filter value is ignored, same as {@link IsEmptyFilterConstraint} etc.
 */
public class RelativeDateRangeFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    /**
     * Computes the inclusive {@code [start, end]} date range this match mode covers, relative to "today".
     */
    @FunctionalInterface
    public interface RangeFunction extends Serializable {
        LocalDate[] apply(LocalDate today, Locale locale);
    }

    private final RangeFunction rangeFunction;

    public RelativeDateRangeFilterConstraint(RangeFunction rangeFunction) {
        this.rangeFunction = rangeFunction;
    }

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        LocalDate valueDate = DateFilterUtils.toLocalDate(value);
        if (valueDate == null) {
            return false;
        }

        LocalDate[] range = resolveRange(LocalDate.now(), locale);
        return !valueDate.isBefore(range[0]) && !valueDate.isAfter(range[1]);
    }

    /**
     * The inclusive {@code [start, end]} day range this match mode covers. Exposed so a server-side
     * implementation - e.g., {@link org.primefaces.model.JPALazyDataModel} building a JPA/Criteria
     * {@code WHERE} clause - can filter on exactly the same range instead of re-deriving it.
     *
     * @param today the date the range is anchored to, usually {@link LocalDate#now()}
     * @param locale the locale deciding the first day of the week, for the "week" match modes
     * @return the inclusive {@code [start, end]} range, as a two-element array
     */
    public LocalDate[] resolveRange(LocalDate today, Locale locale) {
        return rangeFunction.apply(today, locale);
    }
}
