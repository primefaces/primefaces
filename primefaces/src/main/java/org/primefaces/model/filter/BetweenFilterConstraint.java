/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.util.List;
import java.util.Locale;

import jakarta.faces.context.FacesContext;

public class BetweenFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (filter != null && !(filter instanceof List)) {
            throw new IllegalArgumentException("Filter should be a java.util.List");
        }
        List<?> filterList = (List<?>) filter;
        if (filterList == null || filterList.size() != 2 || value == null) {
            return false;
        }
        ComparableFilterConstraint.assertComparable(value);
        Object start = filterList.get(0);
        Object end = filterList.get(1);
        if (start == null || end == null) {
            return false;
        }
        ComparableFilterConstraint.assertAssignable(end, value);
        return isBetween((Comparable) value, (Comparable) start, (Comparable) end);
    }

    protected boolean isBetween(Comparable value, Comparable start, Comparable end) {
        return value.compareTo(start) >= 0 && value.compareTo(end) <= 0;
    }
}
