/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RangeFilterConstraint implements FilterConstraint {

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (value instanceof LocalDate
                && filter instanceof List) {
            LocalDate in = (LocalDate) value;
            LocalDate start = (LocalDate) ((List)filter).get(0);
            LocalDate end = (LocalDate) ((List)filter).get(1);
            return in.isAfter(start) && in.isBefore(end);
        }
        else  if (value instanceof LocalDateTime
                && filter instanceof List) {
            LocalDateTime in = (LocalDateTime) value;
            LocalDateTime start = (LocalDateTime) ((List) filter).get(0);
            LocalDateTime end = (LocalDateTime) ((List) filter).get(1);
            return in.isAfter(start) && in.isBefore(end);
        }
        else if (value instanceof Date
                && filter instanceof List) {
            Date in = (Date) value;
            Date start = (Date) ((List) filter).get(0);
            Date end = (Date) ((List) filter).get(1);
            return in.after(start) && in.before(end);
        }

        throw new UnsupportedOperationException("Unsupported type: " + filter.getClass());
    }
}
