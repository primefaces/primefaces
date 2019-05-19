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
package org.primefaces.component.datatable;

import java.io.Serializable;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;

public class TableState implements Serializable {

    private static final long serialVersionUID = 1L;

    private int first;

    private int rows;

    private List<MultiSortState> multiSortState;

    private ValueExpression sortBy;

    private String sortOrder;

    private String sortField;

    private MethodExpression sortFunction;

    private ValueExpression defaultSortBy;

    private String defaultSortOrder;

    private MethodExpression defaultSortFunction;

    private List<Object> rowKeys;

    private List<FilterState> filters;

    private String globalFilterValue;

    private String orderedColumnsAsString;

    private String togglableColumnsAsString;

    private String resizableColumnsAsString;

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<MultiSortState> getMultiSortState() {
        return multiSortState;
    }

    public void setMultiSortState(List<MultiSortState> multiSortState) {
        this.multiSortState = multiSortState;
    }

    public ValueExpression getSortBy() {
        return sortBy;
    }

    public void setSortBy(ValueExpression sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public MethodExpression getSortFunction() {
        return sortFunction;
    }

    public void setSortFunction(MethodExpression sortFunction) {
        this.sortFunction = sortFunction;
    }

    public ValueExpression getDefaultSortBy() {
        return defaultSortBy;
    }

    public void setDefaultSortBy(ValueExpression defaultSortBy) {
        this.defaultSortBy = defaultSortBy;
    }

    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(String defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public MethodExpression getDefaultSortFunction() {
        return defaultSortFunction;
    }

    public void setDefaultSortFunction(MethodExpression defaultSortFunction) {
        this.defaultSortFunction = defaultSortFunction;
    }

    public List<Object> getRowKeys() {
        return rowKeys;
    }

    public void setRowKeys(List<Object> rowKeys) {
        this.rowKeys = rowKeys;
    }

    public List<FilterState> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterState> filters) {
        this.filters = filters;
    }

    public String getGlobalFilterValue() {
        return globalFilterValue;
    }

    public void setGlobalFilterValue(String globalFilterValue) {
        this.globalFilterValue = globalFilterValue;
    }

    public String getOrderedColumnsAsString() {
        return orderedColumnsAsString;
    }

    public void setOrderedColumnsAsString(String orderedColumnsAsString) {
        this.orderedColumnsAsString = orderedColumnsAsString;
    }

    public String getTogglableColumnsAsString() {
        return togglableColumnsAsString;
    }

    public void setTogglableColumnsAsString(String togglableColumnsAsString) {
        this.togglableColumnsAsString = togglableColumnsAsString;
    }

    public String getResizableColumnsAsString() {
        return resizableColumnsAsString;
    }

    public void setResizableColumnsAsString(String resizableColumnsAsString) {
        this.resizableColumnsAsString = resizableColumnsAsString;
    }
}
