package org.primefaces.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LazyDataModelTest {
	
	@Test
	public void testIterator() {
		LazyDataModelImpl dataModel = new LazyDataModelImpl();
		dataModel.setPageSize(3);
		dataModel.testSize = 10;
		List<Integer> items = new ArrayList<Integer>();
		for (Integer item : dataModel) {
			System.out.println(item);
			items.add(item);
		}
		Assert.assertEquals(10, items.size());
	}
	
	private static class LazyDataModelImpl extends LazyDataModel<Integer> {
		int testSize;
		@Override
		public List<Integer> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
			System.out.println(String.format("Loading %d items from offset %d", pageSize, first));
			List<Integer> page = new ArrayList<Integer>();
			for(int i = first; i < first + pageSize && i < testSize; i++) {
				page.add(i);
			}
			return page; 
		}
	}
	
}