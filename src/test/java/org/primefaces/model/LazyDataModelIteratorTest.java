/* 
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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

public class LazyDataModelIteratorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testIteratorPageSizeLessThanTotalItems() {
        System.out.println("\ntestIteratorPageSizeLessThanTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(3);
        dataModel.totalItems = 10;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        Assert.assertEquals(10, items.size());
        Assert.assertEquals(4, dataModel.getLoadCount());
    }

    @Test
    public void testIteratorPageSizeEqualToTotalItems() {
        System.out.println("\ntestIteratorPageSizeEqualToTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(20);
        dataModel.totalItems = 20;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        Assert.assertEquals(20, items.size());
        Assert.assertEquals(2, dataModel.getLoadCount());
    }

    @Test
    public void testIteratorPageSizeGreaterThanTotalItems() {
        System.out.println("\ntestIteratorPageSizeGreaterThanTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(10);
        dataModel.totalItems = 9;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        Assert.assertEquals(9, items.size());
        Assert.assertEquals(1, dataModel.getLoadCount());
    }

    @Test
    public void testIteratorZeroTotalItems() {
        System.out.println("\ntestIteratorZeroTotalItems");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(10);
        dataModel.totalItems = 0;
        List<Integer> items = new ArrayList<>();
        for (Integer item : dataModel) {
            System.out.println(item);
            items.add(item);
        }
        Assert.assertEquals(0, items.size());
        Assert.assertEquals(1, dataModel.getLoadCount());
    }

    @Test
    public void testIteratorWhileHasNext() {
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
        Assert.assertEquals(5, items.size());
        Assert.assertEquals(1, dataModel.getLoadCount());
    }

    @Test
    public void testIteratorWhileNoSuchElementException() {
        System.out.println("\ntestIteratorWhileNoSuchElementException");
        expectedException.expect(NoSuchElementException.class);
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(2);
        dataModel.totalItems = 2;
        dataModel.iterator().next();
    }

    @Test
    public void testIteratorWhileRemoveUnsupportedOperationException() {
        System.out.println("\ntestIteratorWhileRemoveUnsupportedOperationException");
        expectedException.expect(UnsupportedOperationException.class);
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(2);
        dataModel.totalItems = 2;
        Iterator<Integer> it = dataModel.iterator();
        while (it.hasNext()) {
            it.remove();
        }
    }

    @Test
    public void testOverloadedIteratorSingleSorting() {
        System.out.println("\ntestOverloadedIteratorSingleSorting");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(2);
        dataModel.totalItems = 1;
        Iterator<Integer> it = dataModel.iterator("foo", SortOrder.ASCENDING, Collections.singletonMap("foo", (Object) "bar"));
        while (it.hasNext()) {
            Integer item = it.next();
            Assert.assertNotNull(item);
        }
        Assert.assertEquals(1, dataModel.singleSortingLoadCounter);
    }

    @Test
    public void testOverloadedIteratorMultiSorting() {
        System.out.println("\ntestOverloadedIteratorMultiSorting");
        LazyDataModelImpl dataModel = new LazyDataModelImpl();
        dataModel.setPageSize(2);
        dataModel.totalItems = 1;
        Iterator<Integer> it = dataModel.iterator(Arrays.asList(new SortMeta()), Collections.singletonMap("foo", (Object) "bar"));
        while (it.hasNext()) {
            Integer item = it.next();
            Assert.assertNotNull(item);
        }
        Assert.assertEquals(1, dataModel.multiSortingLoadCounter);
    }

    private static class LazyDataModelImpl extends LazyDataModel<Integer> {

        private static final long serialVersionUID = 1L;

        int totalItems;
        int multiSortingLoadCounter;
        int singleSortingLoadCounter;

        int getLoadCount() {
            return multiSortingLoadCounter + singleSortingLoadCounter;
        }

        @Override
        public List<Integer> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
            System.out.println(String.format("Loading %d items from offset %d", pageSize, first));
            multiSortingLoadCounter++;
            List<Integer> page = new ArrayList<>();
            for(int i = first; i < first + pageSize && i < totalItems; i++) {
                page.add(i);
            }
            return page;
        }

        @Override
        public List<Integer> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            System.out.println(String.format("Loading %d items from offset %d", pageSize, first));
            singleSortingLoadCounter++;
            List<Integer> page = new ArrayList<>();
            for(int i = first; i < first + pageSize && i < totalItems; i++) {
                page.add(i);
            }
            return page;
        }
    }

}