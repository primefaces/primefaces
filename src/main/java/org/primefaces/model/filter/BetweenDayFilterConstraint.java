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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.primefaces.model.filter.CalendarUtils.getEndOfDay;
import static org.primefaces.model.filter.CalendarUtils.getStartOfDay;

public class BetweenDayFilterConstraint implements FilterConstraint {

    @Override
    public boolean applies(Object value, Object filter, Locale locale) {
        if (filter == null) {
            return true;
        }
        if (value == null) {
            return false;
        }

        List<Date> filterAsList = (List<Date>) filter;
        return ComparableUtils.isBetween(getStartOfDay(filterAsList.get(0)), getEndOfDay(filterAsList.get(1)), (Comparable) value);
    }

}
