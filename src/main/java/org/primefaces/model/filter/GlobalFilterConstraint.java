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

import org.primefaces.model.FilterMeta;

import javax.faces.context.FacesContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class GlobalFilterConstraint extends ContainsFilterConstraint {

    private final Collection<FilterMeta> filterBy;

    public GlobalFilterConstraint(Collection<FilterMeta> filterBy) {
        this.filterBy = filterBy;
    }

    public GlobalFilterConstraint() {
        this(Collections.emptyList());
    }

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        // tweak for treetable (to remove once TreeTable filtering is cleaned up)
        if (filterBy == null || filterBy.isEmpty()) {
            return super.isMatching(ctxt, value, filter, locale);
        }

        for (FilterMeta f : filterBy) {
            if (f.isGlobalFilter()) {
                continue;
            }

            Object o = f.getLocalValue(ctxt.getELContext());
            if (super.isMatching(ctxt, o, filter, locale)) {
                return true;
            }
        }

        return false;
    }
}
