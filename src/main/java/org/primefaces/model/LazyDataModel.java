/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.Map;

import javax.faces.model.DataModel;

/**
 * Custom lazy loading DataModel to deal with huge datasets
 */
public abstract class LazyDataModel<T> extends DataModel<T> implements SelectableDataModel<T>, Serializable {

	private int rowIndex = -1;

	private int rowCount;

	private int pageSize;

	private List<T> data;

	public LazyDataModel() {
		super();
	}
	
	public boolean isRowAvailable() {
		if(data == null) {
            return false;
        }

		return rowIndex >= 0 && rowIndex < data.size();
	}

	public int getRowCount() {
		return rowCount;
	}

	public T getRowData() {
        return data.get(rowIndex);
	}

    public int getRowIndex() {
        return this.rowIndex;
    }
    
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex == -1 ? rowIndex : (rowIndex % pageSize);
    }

	public Object getWrappedData() {
		return data;
	}
	public void setWrappedData(Object list) {
		this.data = (List) list;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public abstract List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters);

    public T getRowData(String rowKey) {
        throw new UnsupportedOperationException("getRowData(String rowKey) must be implemented when basic rowKey algorithm is not used.");
    }

    public Object getRowKey(T object) {
        throw new UnsupportedOperationException("getRowKey(T object) must be implemented when basic rowKey algorithm is not used.");
    }
}