/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import java.util.*;

import javax.faces.context.FacesContext;

import org.primefaces.util.LangUtils;

public class InFilterConstraint implements FilterConstraint {

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
            if (Objects.equals(value, filterValue)) {
                return true;
            }

            // GitHub #8106 check for "" comparison
            if (filterValue instanceof String && LangUtils.isEmpty((String) filterValue) && value == null) {
                return true;
            }

            // #10730
            // the value might be a enum but the filter is a string
            if (filterValue instanceof String && LangUtils.isNotEmpty((String) filterValue)
                    && value != null && value.getClass().isEnum()) {
                if (Objects.equals(value.toString(), filterValue)) {
                    return true;
                }
            }
        }

        return false;
    }
}
