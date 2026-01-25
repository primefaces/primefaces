/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class LazyDataModelIterator<T> implements Iterator<T> {

    protected LazyDataModel<T> model;
    protected int index;
    protected Map<Integer, List<T>> pages;
    protected List<T> rows;

    protected Map<String, SortMeta> sortBy;
    protected Map<String, FilterMeta> filterBy;

    public LazyDataModelIterator(LazyDataModel<T> model) {
        this.model = model;
        this.index = -1;
        this.pages = new HashMap<>();
        this.sortBy = Collections.emptyMap();
        this.filterBy = Collections.emptyMap();
    }

    public LazyDataModelIterator(LazyDataModel<T> model, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        this(model);
        if (sortBy != null) {
            this.sortBy = sortBy;
        }
        if (filterBy != null) {
            this.filterBy = filterBy;
        }
    }

    @Override
    public boolean hasNext() {
        int nextIndex = index + 1;
        int pageSize = model.getPageSize();

        if (pageSize > 0) {
            int pageNo = nextIndex / pageSize;

            if (!pages.containsKey(pageNo)) {
                List<T> page = model.load(nextIndex, pageSize, sortBy, filterBy);

                if (page == null || page.isEmpty()) {
                    return false;
                }
                pages.remove(pageNo - 1);
                pages.put(pageNo, page);
            }

            int pageIndex = nextIndex % pageSize;
            return pageIndex < pages.get(pageNo).size();
        }

        // there are cases when LazyDataModel is used without paging
        if (rows == null) {
            rows = model.load(0, 0, sortBy, filterBy);
        }
        return nextIndex < rows.size();
    }

    @Override
    public T next() {
        index++;

        int pageSize = model.getPageSize();
        if (pageSize > 0) {
            int pageNo = index / pageSize;
            int pageIndex = index % pageSize;
            List<T> page = pages.get(pageNo);
            if (page == null || pageIndex >= page.size()) {
                throw new NoSuchElementException();
            }
            return page.get(pageIndex);
        }

        // there are cases when LazyDataModel is used without paging
        if (rows == null) {
            rows = model.load(0, 0, sortBy, filterBy);
        }
        return rows.get(index);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}