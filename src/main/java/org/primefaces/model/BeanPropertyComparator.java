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
package org.primefaces.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

/**
 * Generic comparator for column sorting.
 */
public class BeanPropertyComparator implements Comparator<Object> {

    private String var;
    private Locale locale;
    private Collator collator;
    private SortMeta sortMeta;

    public BeanPropertyComparator(String var, SortMeta sortMeta, Locale locale) {
        this.sortMeta = sortMeta;
        this.var = var;
        this.locale = locale;
        this.collator = Collator.getInstance(locale);
    }

    @Override
    public int compare(Object obj1, Object obj2) {
        FacesContext context = FacesContext.getCurrentInstance();
        ValueExpression sortBy = sortMeta.getSortBy();

        try {
            context.getExternalContext().getRequestMap().put(var, obj1);
            Object value1 = sortBy.getValue(context.getELContext());

            context.getExternalContext().getRequestMap().put(var, obj2);
            Object value2 = sortBy.getValue(context.getELContext());

            int result;

            if (sortMeta.getFunction() == null) {
                //Empty check
                if (value1 == null && value2 == null) {
                    return 0;
                }
                else if (value1 == null) {
                    result = sortMeta.getNullSortOrder();
                }
                else if (value2 == null) {
                    result = -1 * sortMeta.getNullSortOrder();
                }
                else if (value1 instanceof String && value2 instanceof String) {
                    if (sortMeta.isCaseSensitiveSort()) {
                        result = collator.compare(value1, value2);
                    }
                    else {
                        String str1 = (((String) value1).toLowerCase(locale));
                        String str2 = (((String) value2).toLowerCase(locale));

                        result = collator.compare(str1, str2);
                    }
                }
                else {
                    result = ((Comparable<Object>) value1).compareTo(value2);
                }
            }
            else {
                result = (Integer) sortMeta.getFunction().invoke(context.getELContext(), new Object[]{value1, value2});
            }

            return sortMeta.getOrder().isAscending() ? result : -1 * result;

        }
        catch (Exception e) {
            throw new FacesException(e);
        }
    }
}