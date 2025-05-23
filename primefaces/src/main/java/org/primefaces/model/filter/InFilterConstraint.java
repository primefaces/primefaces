/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import jakarta.faces.context.FacesContext;

public class InFilterConstraint extends EqualsFilterConstraint {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (filter == null) {
            return false;
        }

        Collection<?> collection = null;
        if (filter.getClass().isArray()) {
            collection = Arrays.asList((Object[]) filter);
        }
        else if (filter instanceof Collection) {
            collection = (Collection<?>) filter;
        }
        else {
            collection = Collections.singletonList(filter);
        }

        for (Object filterValue : collection) {
            // Return true on the first matching value
            if (super.isMatching(ctxt, value, filterValue, locale)) {
                return true;
            }
        }

        return false;
    }
}
