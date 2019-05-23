/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.util.Comparator;
import java.util.Locale;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

public class TreeNodeComparator implements Comparator {

    private ValueExpression sortBy;
    private boolean asc;
    private String var;
    private MethodExpression sortFunction;
    private boolean caseSensitive = false;
    private Locale locale;

    public TreeNodeComparator(ValueExpression sortBy, String var, SortOrder sortOrder, MethodExpression sortFunction, boolean caseSensitive,
            Locale locale) {
        this.sortBy = sortBy;
        this.var = var;
        this.asc = sortOrder.equals(SortOrder.ASCENDING);
        this.sortFunction = sortFunction;
        this.caseSensitive = caseSensitive;
        this.locale = locale;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object obj1, Object obj2) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            context.getExternalContext().getRequestMap().put(var, ((TreeNode) obj1).getData());
            Object value1 = sortBy.getValue(context.getELContext());
            context.getExternalContext().getRequestMap().put(var, ((TreeNode) obj2).getData());
            Object value2 = sortBy.getValue(context.getELContext());

            int result;

            //Empty check
            if (value1 == null && value2 == null) {
                return 0;
            }
            else if (value1 == null) {
                result = 1;
            }
            else if (value2 == null) {
                result = -1;
            }
            else if (sortFunction == null) {
                if (value1 instanceof String && value2 instanceof String) {
                    result = this.caseSensitive ? ((Comparable) value1).compareTo(value2)
                            : (((String) value1).toLowerCase(locale)).compareTo(((String) value2).toLowerCase(locale));
                }
                else {
                    result = ((Comparable) value1).compareTo(value2);
                }
            }
            else {
                result = (Integer) sortFunction.invoke(context.getELContext(), new Object[]{value1, value2});
            }

            return asc ? result : -1 * result;

        }
        catch (Exception e) {
            throw new FacesException(e);
        }
    }
}
