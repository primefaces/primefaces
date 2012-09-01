/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import java.util.Comparator;
import java.util.logging.Logger;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * Generic comparator for column sorting.
 */
public class BeanPropertyComparator implements Comparator {

    private ValueExpression sortBy;
    private boolean asc;
    private String var;
    private MethodExpression sortFunction;
    private final static Logger logger = Logger.getLogger(BeanPropertyComparator.class.getName());

    public BeanPropertyComparator(ValueExpression sortBy, String var, SortOrder sortOrder, MethodExpression sortFunction) {
        this.sortBy = sortBy;
        this.var = var;
        this.asc = sortOrder.equals(SortOrder.ASCENDING);
        this.sortFunction = sortFunction;
    }

    @SuppressWarnings("unchecked")
    public int compare(Object obj1, Object obj2) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            context.getExternalContext().getRequestMap().put(var, obj1);
            Object value1 = sortBy.getValue(context.getELContext());
            context.getExternalContext().getRequestMap().put(var, obj2);
            Object value2 = sortBy.getValue(context.getELContext());

            //Empty check
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return 1;
            } else if (value2 == null) {
                return -1;
            }

            int result;
            if (sortFunction == null) {
                result = ((Comparable) value1).compareTo(value2);
            } else {
                result = (Integer) sortFunction.invoke(context.getELContext(), new Object[]{value1, value2});
            }

            return asc ? result : -1 * result;

        } catch (Exception e) {
            logger.severe("Error in sorting");

            throw new RuntimeException(e);
        }
    }
}