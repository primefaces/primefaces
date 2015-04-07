/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

/**
 * Generic comparator for column sorting.
 */
public class BeanPropertyComparator implements Comparator {

    private ValueExpression sortBy;
    private boolean asc;
    private String var;
    private MethodExpression sortFunction;
    private boolean caseSensitive = false;
    private Locale locale;
    private Collator collator;
    private int nullSortOrder;

    public BeanPropertyComparator(ValueExpression sortBy, String var, SortOrder sortOrder, MethodExpression sortFunction, boolean caseSensitive, Locale locale, int nullSortOrder) {
        this.sortBy = sortBy;
        this.var = var;
        this.asc = sortOrder.equals(SortOrder.ASCENDING);
        this.sortFunction = sortFunction;
        this.caseSensitive = caseSensitive;
        this.locale = locale;
        this.collator = Collator.getInstance(locale);
        this.nullSortOrder = nullSortOrder;
    }

    @SuppressWarnings("unchecked")
    public int compare(Object obj1, Object obj2) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            context.getExternalContext().getRequestMap().put(var, obj1);
            Object value1 = sortBy.getValue(context.getELContext());
            context.getExternalContext().getRequestMap().put(var, obj2);
            Object value2 = sortBy.getValue(context.getELContext());

            int result;
            
            //Empty check
            if (value1 == null && value2 == null) {
            	return 0;
            } else if (value1 == null) {
            	result = 1 * nullSortOrder;
            } else if (value2 == null) {
            	result = -1 * nullSortOrder;
            } else if (sortFunction == null) {
                if(value1 instanceof String && value2 instanceof String) {
                    if(this.caseSensitive) {
                        result = collator.compare(value1, value2);
                    }
                    else {
                        String str1 = (((String) value1).toLowerCase(locale));
                        String str2 = (((String) value2).toLowerCase(locale));
                        
                        result = collator.compare(str1, str2);
                    }
                } else {
                    result = ((Comparable) value1).compareTo(value2);
                }
            } else {
                result = (Integer) sortFunction.invoke(context.getELContext(), new Object[]{value1, value2});
            }

            return asc ? result : -1 * result;

        } catch (Exception e) {
            throw new FacesException(e);
        }
    }
}