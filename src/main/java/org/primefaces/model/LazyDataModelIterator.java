/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.*;

public class LazyDataModelIterator<T> implements Iterator<T> {

    private LazyDataModel<T> model;
    private int index;
    private Map<Integer, List<T>> pages;

    private Map<String, SortMeta> sortBy;
    private Map<String, FilterMeta> filterBy;

    LazyDataModelIterator(LazyDataModel<T> model) {
        this.model = model;
        index = -1;
        pages = new HashMap<>();
        sortBy = Collections.emptyMap();
        filterBy = Collections.emptyMap();
    }

    LazyDataModelIterator(LazyDataModel<T> model, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
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
        int pageNo = nextIndex / model.getPageSize();

        if (!pages.containsKey(pageNo)) {
            List<T> page = model.load(nextIndex, model.getPageSize(), sortBy, filterBy);

            if (page == null || page.isEmpty()) {
                return false;
            }
            pages.remove(pageNo - 1);
            pages.put(pageNo, page);
        }

        int pageIndex = nextIndex % model.getPageSize();
        return pageIndex < pages.get(pageNo).size();
    }

    @Override
    public T next() {
        index++;
        int pageNo = index / model.getPageSize();
        int pageIndex = index % model.getPageSize();
        List<T> page = pages.get(pageNo);
        if (page == null || pageIndex >= page.size()) {
            throw new NoSuchElementException();
        }
        return page.get(pageIndex);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}