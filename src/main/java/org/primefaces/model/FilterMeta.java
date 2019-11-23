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

import javax.el.ValueExpression;
import org.primefaces.component.api.UIColumn;

public class FilterMeta {

    private String filterField;
    private UIColumn column;
    private ValueExpression filterByVE;
    private MatchMode filterMatchMode;
    private Object filterValue;

    public FilterMeta(String filterField, Object filterValue) {
        this.filterField = filterField;
        this.filterValue = filterValue;
    }

    public FilterMeta(String filterField, UIColumn column, ValueExpression filterByVE, MatchMode filterMatchMode, Object filterValue) {
        this.filterField = filterField;
        this.column = column;
        this.filterByVE = filterByVE;
        this.filterMatchMode = filterMatchMode;
        this.filterValue = filterValue;
    }

    public String getFilterField() {
        return filterField;
    }

    public UIColumn getColumn() {
        return column;
    }

    public ValueExpression getFilterByVE() {
        return filterByVE;
    }

    public MatchMode getFilterMatchMode() {
        return filterMatchMode;
    }

    public Object getFilterValue() {
        return filterValue;
    }


}
