/**
 * Copyright 2009-2018 PrimeTek.
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

import javax.validation.constraints.Null;
import java.util.*;

public class LazyDataModelIterator<T> implements Iterator<T> {

    private LazyDataModel<T> model;
    private int index;
    private Map<Integer, List<T>> pages;

    @Null
    private String sortField;
    @Null
    private SortOrder sortOrder;

    @Null
    private List<SortMeta> multiSortMeta;

    @Null
    private Map<String, Object> filters;

    LazyDataModelIterator(LazyDataModel<T> model) {
        this.model = model;
        this.index = -1;
        this.pages = new HashMap<Integer, List<T>>();
    }

    LazyDataModelIterator(LazyDataModel<T> model, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        this(model);
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.filters = filters;
    }

    LazyDataModelIterator(LazyDataModel<T> model, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        this(model);
        this.multiSortMeta = multiSortMeta;
        this.filters = filters;
    }

    @Override
    public boolean hasNext() {
        int nextIndex = index + 1;
        int pageNo = nextIndex / model.getPageSize();

        if (!pages.containsKey(pageNo)) {
            List<T> page;

            if (sortField != null || sortOrder != null) {
                page = model.load(nextIndex, model.getPageSize(), sortField, sortOrder, filters);
            }
            else {
                page = model.load(nextIndex, model.getPageSize(), multiSortMeta, filters);
            }

            if (page == null || page.isEmpty()) {
                return false;
            }
            pages.remove(pageNo - 1);
            pages.put(pageNo, page);
        }

        int pageIndex = nextIndex % model.getPageSize();
        if (pageIndex < pages.get(pageNo).size()) {
            return true;
        }

        return false;
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