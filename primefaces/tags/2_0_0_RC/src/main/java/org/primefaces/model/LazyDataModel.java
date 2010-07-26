/*
 * Copyright 2009 Prime Technology.
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

import java.io.Serializable;
import java.util.List;

import javax.faces.model.DataModel;

/**
 * Custom lazy loading DataModel to deal with huge datasets
 * 
 */
public abstract class LazyDataModel<T> extends DataModel implements Serializable {

	private int rowIndex = -1;

	private int totalNumRows;

	private int pageSize;

	private List<T> list;

	public LazyDataModel() {
		super();
	}

	public LazyDataModel(int totalNumRows) {
		super();
		
		this.totalNumRows = totalNumRows;
	}
	
	public abstract List<T> fetchLazyData(int first, int pageSize);

	public boolean isRowAvailable() {
		if (list == null)
			return false;

		int rowIndex = getRowIndex();
		if (rowIndex >= 0 && rowIndex < list.size())
			return true;
		else
			return false;
	}

	public int getRowCount() {
		return totalNumRows;
	}

	public Object getRowData() {
		if (list == null)
			return null;
		else if (!isRowAvailable())
			throw new IllegalArgumentException();
		else {
			int dataIndex = getRowIndex();
			
			return list.get(dataIndex);
		}
	}

	public int getRowIndex() {
		return (rowIndex % pageSize);
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Object getWrappedData() {
		return list;
	}
	public void setWrappedData(Object list) {
		this.list = (List) list;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
