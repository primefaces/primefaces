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

import java.util.Collection;
import java.util.Locale;

import jakarta.faces.context.FacesContext;

/**
 * Matches when a {@code Collection}/array field value ({@link MatchMode#ARRAY_CONTAINS}) contains an element
 * equal to the single typed filter value. Per-element comparison is delegated to {@link EqualsFilterConstraint}
 * so enum/{@code Comparable} handling stays in one place.
 */
public class ArrayContainsFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    private final EqualsFilterConstraint elementEquals = new EqualsFilterConstraint();

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (filter == null) {
            return false;
        }

        Collection<?> collection = CollectionFilterUtils.toCollection(value);
        for (Object element : collection) {
            if (elementEquals.isMatching(ctxt, element, filter, locale)) {
                return true;
            }
        }
        return false;
    }
}
