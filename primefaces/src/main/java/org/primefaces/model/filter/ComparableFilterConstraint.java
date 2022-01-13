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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.function.BiPredicate;

public abstract class ComparableFilterConstraint implements FilterConstraint {

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (value == null || filter == null) {
            return false;
        }
        if (value instanceof Number) {
            BigDecimal filterBigDecimal = toBigDecimal(filter, locale);
            return filterBigDecimal != null && getPredicate().test(toBigDecimal(value, locale), filterBigDecimal);
        }
        else {
            return getPredicate()
                    .test(StringFilterConstraint.toString(value, locale),
                            StringFilterConstraint.toString(filter, locale));
        }
    }

    static BigDecimal toBigDecimal(Object object, Locale locale) {
        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        if (object instanceof Number) {
            return new BigDecimal(object.toString());
        }
        if (object instanceof String) {
            try {
                DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
                decimalFormat.setParseBigDecimal(true);
                return (BigDecimal) decimalFormat.parseObject((String) object);
            }
            catch (ParseException e) {
                return null;
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
    }

    protected abstract BiPredicate<Comparable, Comparable> getPredicate();

}
