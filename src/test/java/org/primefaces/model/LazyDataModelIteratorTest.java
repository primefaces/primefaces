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
        List<Integer> items = new ArrayList<Integer>();
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
        List<Integer> items = new ArrayList<Integer>();
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
        List<Integer> items = new ArrayList<Integer>();
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
        List<Integer> items = new ArrayList<Integer>();
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
        List<Integer> items = new ArrayList<Integer>();
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
            List<Integer> page = new ArrayList<Integer>();
            for(int i = first; i < first + pageSize && i < totalItems; i++) {
                page.add(i);
            }
            return page;
        }

        @Override
        public List<Integer> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            System.out.println(String.format("Loading %d items from offset %d", pageSize, first));
            singleSortingLoadCounter++;
            List<Integer> page = new ArrayList<Integer>();
            for(int i = first; i < first + pageSize && i < totalItems; i++) {
                page.add(i);
            }
            return page;
        }
    }

}