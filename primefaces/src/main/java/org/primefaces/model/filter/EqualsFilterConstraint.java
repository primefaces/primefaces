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

import org.primefaces.util.LangUtils;

import java.util.Locale;
import java.util.Objects;

import jakarta.faces.context.FacesContext;

public class EqualsFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (Objects.equals(value, filter)) {
            return true;
        }
        // #8106
        // Ported from InFilterConstraint: check for "" comparison
        if (filter instanceof String && LangUtils.isEmpty((String) filter) && value == null) {
            return true;
        }

        // If either are null and we get this far, they don't equal
        if (value == null || filter == null) {
            return false;
        }

        // #10730
        // Handle enum values by comparing their string representations
        // NOTE: This needs to happen _before_ `Comparable` check, even though both Enum and String
        // are Comparable, because they are different types
        if (value.getClass().isEnum() || filter.getClass().isEnum()) {
            return value.toString().equals(filter.toString());
        }

        // #12666
        // If Comparable, prefer compareTo to handle BigDecimal, etc.
        if (value instanceof Comparable) {
            ComparableFilterConstraint.assertAssignable(filter, value);
            return compareToEquals((Comparable) value, (Comparable) filter);
        }
        return value.equals(filter);
    }

    protected boolean compareToEquals(Comparable value, Comparable filter) {
        return value.compareTo(filter) == 0;
    }

}
