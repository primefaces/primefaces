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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import org.primefaces.component.datatable.feature.FilterFeature;
import org.primefaces.model.filter.FilterConstraint;

public class ColumnState implements Serializable {

    private String columnKey;

    private String field;

    private transient Object component;

    private boolean displayed;
    private int displayPriority;

    private String width;

    private SortOrder sortOrder = SortOrder.UNSORTED;
    private ValueExpression sortBy;
    private MethodExpression sortFunction;
    private Integer sortPriority;
    private int nullSortOrder;
    private boolean caseSensitiveSort;
    
    private ValueExpression filterBy;
    private Object filterValue;
    private MatchMode filterMatchMode = MatchMode.CONTAINS;
    private transient FilterConstraint filterConstraint;

    public boolean isSortable()
    {
        return true;
    }
    
    public boolean isFilterable()
    {
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnState that = (ColumnState) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(columnKey, that.columnKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, columnKey);
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        is.defaultReadObject();

        FilterConstraint c = FilterFeature.FILTER_CONSTRAINTS.get(filterMatchMode);
        if (c != null) {
            this.filterConstraint = c;
        }
    }
}
