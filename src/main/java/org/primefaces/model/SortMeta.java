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

import java.io.Serializable;
import javax.el.MethodExpression;
import org.primefaces.component.api.UIColumn;

public class SortMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient UIColumn column;

    private String sortField;

    private SortOrder sortOrder;

    private MethodExpression sortFunction;

    public SortMeta() {
    }

    public SortMeta(UIColumn column, String sortField, SortOrder sortOrder, MethodExpression sortFunction) {
        this.column = column;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.sortFunction = sortFunction;
    }

    public UIColumn getColumn() {
        return column;
    }

    public void setSortBy(UIColumn column) {
        this.column = column;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public MethodExpression getSortFunction() {
        return sortFunction;
    }

    public void setSortFunction(MethodExpression sortFunction) {
        this.sortFunction = sortFunction;
    }

    @Override
    public String toString() {
        return "SortMeta [column=" + column + ", sortField=" + sortField + ", sortOrder=" + sortOrder + ", sortFunction="
                + sortFunction + "]";
    }
}
