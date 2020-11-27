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
        if (!(filter instanceof List) || ((List<?>) filter).size() != 2) {
            throw new IllegalArgumentException("Filter expects a " + List.class.getName() + " with a size equals to 2");
        }

        if (value instanceof LocalDate) {
            return isInRange((LocalDate) value, (List) filter);
        }
        else  if (value instanceof LocalDateTime) {
            return isInRange((LocalDateTime) value, (List) filter);
        }
        else if (value instanceof Date) {
            return isInRange((Date) value, (List) filter);
        }

        throw new UnsupportedOperationException("Unsupported type: " + value.getClass() + ". " +
                "Supported types: " + LocalDate.class.getName() + ", " + LocalDateTime.class.getName() + " and" + Date.class.getName());
    }

    protected boolean isInRange(Date value, List filter) {
        Date start = (Date) filter.get(0);
        Date end = (Date) filter.get(1);
        return value.after(start) && value.before(end);
    }

    protected boolean isInRange(LocalDateTime value, List filter) {
        LocalDateTime start = (LocalDateTime) filter.get(0);
        LocalDateTime end = (LocalDateTime) filter.get(1);
        return value.isAfter(start) && value.isBefore(end);
    }

    protected boolean isInRange(LocalDate value, List filter) {
        LocalDate start = (LocalDate) filter.get(0);
        LocalDate end = (LocalDate) filter.get(1);
        return value.isAfter(start) && value.isBefore(end);
    }
}
