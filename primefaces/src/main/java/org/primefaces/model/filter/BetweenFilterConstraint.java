/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import java.time.LocalDateTime;
import java.util.Date;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Locale;
import org.primefaces.util.CalendarUtils;

public class BetweenFilterConstraint implements FilterConstraint {

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (!(filter instanceof List) || ((List) filter).size() != 2 || value == null) {
            return false;
        }

        if (value instanceof Comparable) {
            return isBetween((Comparable) value, (List) filter);
        }

        throw new IllegalArgumentException("Invalid type: " + value.getClass() + ". Valid type: " + Comparable.class.getName());
    }

    protected boolean isBetween(Comparable value, List filter) {
        Comparable start = (Comparable) filter.get(0);
        Comparable end = (Comparable) filter.get(1);
        if (start == null || end == null) {
            return false;
        }
        Comparable convertedValue = (end instanceof LocalDate) ? toLocalDate(value) : value;
        return convertedValue.compareTo(start) >= 0 && convertedValue.compareTo(end) <= 0;
    }

    protected LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        }
        if (value instanceof Date) {
            return CalendarUtils.convertDate2LocalDate((Date) value);
        }
        throw new IllegalArgumentException("Invalid type: " + value.getClass() + ". Valid types are LocalDate, LocalDateTime, Date.");
    }
}
