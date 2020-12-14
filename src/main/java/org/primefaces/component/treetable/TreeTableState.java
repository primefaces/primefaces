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
package org.primefaces.component.treetable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

public class TreeTableState implements Serializable {

    private static final long serialVersionUID = 1L;

    private int first;

    private int rows;

    private Map<String, SortMeta> sortBy;

    private List<Object> selectedRowKeys;

    private Map<String, FilterMeta> filterBy;

    private Map<String, Boolean> visibleColumns;

    private Map<String, String> resizableColumns;

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

    public List<Object> getSelectedRowKeys() {
        return selectedRowKeys;
    }

    public void setSelectedRowKeys(List<Object> selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys;
    }

    public Map<String, FilterMeta> getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(Map<String, FilterMeta> filterBy) {
        this.filterBy = filterBy;
    }

    public Map<String, Boolean> getVisibleColumns() {
        return visibleColumns;
    }

    public void setVisibleColumns(Map<String, Boolean> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    public Map<String, String> getResizableColumns() {
        return resizableColumns;
    }

    public void setResizableColumns(Map<String, String> resizableColumns) {
        this.resizableColumns = resizableColumns;
    }
}
