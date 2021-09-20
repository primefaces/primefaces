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
package org.primefaces.util;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class ELUtils {

    private ELUtils() {
    }

    public static Class<?> getType(FacesContext context, ValueExpression ve, Lazy<Object> value) {

        // no ValueExpression and no literal defined... skip
        if (ve == null && value.get() == null) {
            return null;
        }

        Class<?> type = null;

        // try getExpectedType first, likely returns Object.class
        if (ve != null) {
            type = ve.getExpectedType();
        }

        // fallback to getType
        if ((type == null || type == Object.class) && ve != null) {
            try {
                type = ve.getType(context.getELContext());
            }
            catch (ELException e) {
                // fails if the ValueExpression is actually a MethodExpression, see #7058
            }
        }

        // fallback to the type of the value instance
        if ((type == null || type == Object.class) && value.get() != null) {
            type = value.get().getClass();
        }

        return type;
    }
}
