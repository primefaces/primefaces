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
 * "Last N days" / "next N days" / "relative date (within N days)" - matches when the field value falls within an
 * {@code [start, end]} range computed from {@code LocalDate.now()} and the user-typed number of days N. Unlike
 * {@link RelativeDateRangeFilterConstraint}, this one DOES need a value; {@code UITable} parses the raw typed
 * string as a plain {@code Integer} rather than running it through the column's (date) converter.
 */
public class RelativeNDaysFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    /**
     * Computes the inclusive {@code [start, end]} date range this match mode covers, given "today" and N days.
     */
    @FunctionalInterface
    public interface RangeFunction extends Serializable {
        LocalDate[] apply(LocalDate today, int days);
    }

    private final RangeFunction rangeFunction;

    public RelativeNDaysFilterConstraint(RangeFunction rangeFunction) {
        this.rangeFunction = rangeFunction;
    }

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        LocalDate valueDate = DateFilterUtils.toLocalDate(value);
        Integer days = DateFilterUtils.toInteger(filter);
        if (valueDate == null || days == null) {
            return false;
        }

        LocalDate[] range = rangeFunction.apply(LocalDate.now(), days);
        return !valueDate.isBefore(range[0]) && !valueDate.isAfter(range[1]);
    }
}
