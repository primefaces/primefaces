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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LazyDataModelIteratorTest {

    @Test
    void iteratorPageSizeLessThanTotalItems() {
        System.out.println("\ntestIteratorPageSizeLessThanTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(3);
        dataModel.totalItems = 10;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        assertEquals(10, items.size());
        assertEquals(4, dataModel.getLoadCount());
    }

    @Test
    void iteratorPageSizeEqualToTotalItems() {
        System.out.println("\ntestIteratorPageSizeEqualToTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(20);
        dataModel.totalItems = 20;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        assertEquals(20, items.size());
        assertEquals(2, dataModel.getLoadCount());
    }

    @Test
    void iteratorPageSizeGreaterThanTotalItems() {
        System.out.println("\ntestIteratorPageSizeGreaterThanTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(10);
        dataModel.totalItems = 9;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        assertEquals(9, items.size());
        assertEquals(1, dataModel.getLoadCount());
    }

    @Test
    void iteratorZeroTotalItems() {
        System.out.println("\ntestIteratorZeroTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(10);
        dataModel.totalItems = 0;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        assertEquals(0, items.size());
        assertEquals(1, dataModel.getLoadCount());
    }

    @Test
    void iteratorWhileHasNext() {
        System.out.println("\ntestIteratorWhileHasNext");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(10);
        dataModel.totalItems = 5;
        List<Integer> items = new ArrayList<>();
        Iterator<Integer> it = dataModel.iterator();
        while (it.hasNext()) {
            Integer item = it.next();
            System.out.println(item);
            items.add(item);
        }
        assertEquals(5, items.size());
        assertEquals(1, dataModel.getLoadCount());
    }

    @Test
    void iteratorWhileNoSuchElementException() {
        System.out.println("\ntestIteratorWhileNoSuchElementException");

        // Act
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            LazyDataModelImpl dataModel = new LazyDataModelImpl();
            dataModel.setPageSize(2);
            dataModel.totalItems = 2;
            dataModel.iterator().next();
        });

        // Assert (expected exception)
        assertNull(thrown.getMessage());
    }

    @Test
    void iteratorWhileRemoveUnsupportedOperationException() {
        System.out.println("\ntestIteratorWhileRemoveUnsupportedOperationException");

        UnsupportedOperationException thrown = assertThrows(UnsupportedOperationException.class, () -> {
            LazyDataModelImpl dataModel = new LazyDataModelImpl();
            dataModel.setPageSize(2);
            dataModel.totalItems = 2;
            Iterator<Integer> it = dataModel.iterator();
            while (it.hasNext()) {
                it.remove();
            }
        });

        // Assert (expected exception)
        assertNull(thrown.getMessage());
    }

    @Test
    void overloadedIteratorSingleSorting() {
        System.out.println("\ntestOverloadedIteratorSingleSorting");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(2);
        dataModel.totalItems = 1;
        Iterator<Integer> it = dataModel.iterator(Collections.singletonMap("foo", SortMeta.builder().field("foo").order(SortOrder.ASCENDING).build()),
                Collections.singletonMap("foo", FilterMeta.builder().field("foo").filterValue("bar").build()));
        while (it.hasNext()) {
            Integer item = it.next();
            assertNotNull(item);
        }
        assertEquals(1, dataModel.loadCounter);
    }

    @Test
    void overloadedIteratorMultiSorting() {
        System.out.println("\ntestOverloadedIteratorMultiSorting");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(2);
        dataModel.totalItems = 1;
        Iterator<Integer> it = dataModel.iterator(
                Collections.emptyMap(),
                Collections.singletonMap("foo", FilterMeta.builder().field("foo").filterValue("bar").build()));
        while (it.hasNext()) {
            Integer item = it.next();
            assertNotNull(item);
        }
        assertEquals(1, dataModel.loadCounter);
    }

    private static class LazyDataModelImpl extends LazyDataModel<Integer> {

        private static final long serialVersionUID = 1L;

        private int totalItems;
        private int loadCounter;

        public int getLoadCount() {
            return loadCounter;
        }

        @Override
        public List<Integer> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
            System.out.println(String.format("Loading %d items from offset %d", pageSize, first));
            loadCounter++;
            List<Integer> page = new ArrayList<>();
            for (int i = first; i < first + pageSize && i < totalItems; i++) {
                page.add(i);
            }
            return page;
        }

        @Override
        public int count(Map<String, FilterMeta> filterBy) {
            return totalItems;
        }
    }

}
