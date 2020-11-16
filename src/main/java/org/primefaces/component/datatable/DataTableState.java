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
package org.primefaces.component.datatable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

public class DataTableState implements Serializable {

    private static final long serialVersionUID = 1L;

    private int first;

    private int rows;

    private Map<String, SortMeta> sortBy;

    private List<Object> rowKeys;

    private Map<String, FilterMeta> filterBy;

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

    public Map<String, SortMeta> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Map<String, SortMeta> sortBy) {
        this.sortBy = sortBy;
    }

    public List<Object> getRowKeys() {
        return rowKeys;
    }

    public void setRowKeys(List<Object> rowKeys) {
        this.rowKeys = rowKeys;
    }

    public Map<String, FilterMeta> getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(Map<String, FilterMeta> filterBy) {
        this.filterBy = filterBy;
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
