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

import java.util.Locale;
import java.util.function.BiPredicate;

import jakarta.faces.context.FacesContext;

public abstract class ComparableFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (value == null || filter == null) {
            return false;
        }
        assertComparable(value);
        assertAssignable(filter, value);
        return getPredicate().test((Comparable) value, (Comparable) filter);
    }

    protected abstract BiPredicate<Comparable, Comparable> getPredicate();

    static void assertComparable(Object value) {
        if (!(value instanceof Comparable)) {
            throw new IllegalArgumentException("Value should be a java.lang.Comparable");
        }
    }

    static void assertAssignable(Object filter, Object value) {
        if (!filter.getClass().isAssignableFrom(value.getClass())) {
            String msg = String.format("Filter '%s' of type '%s' cannot be casted to value type '%s'. Forgot to add a converter?",
                    filter, filter.getClass().getName(), value);
            throw new IllegalArgumentException(msg);
        }
    }

}
