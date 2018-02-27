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

import java.io.Serializable;
import java.util.*;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;
import javax.validation.constraints.Null;

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
        if (data == null) {
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
        int oldIndex = this.rowIndex;

        if (rowIndex == -1 || pageSize == 0) {
            this.rowIndex = -1;
        }
        else {
            this.rowIndex = (rowIndex % pageSize);
        }

        if (data == null) {
            return;
        }

        DataModelListener[] listeners = getDataModelListeners();
        if (listeners != null && oldIndex != this.rowIndex) {
            Object rowData = null;
            if (isRowAvailable()) {
                rowData = getRowData();
            }

            DataModelEvent dataModelEvent = new DataModelEvent(this, rowIndex, rowData);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].rowSelected(dataModelEvent);
            }
        }
    }

    public List<T> getWrappedData() {
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

    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        throw new UnsupportedOperationException("Lazy loading is not implemented.");
    }

    public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        throw new UnsupportedOperationException("Lazy loading is not implemented.");
    }

    public T getRowData(String rowKey) {
        throw new UnsupportedOperationException(
                getMessage("getRowData(String rowKey) must be implemented by %s when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    public Object getRowKey(T object) {
        throw new UnsupportedOperationException(
                getMessage("getRowKey(T object) must be implemented by %s when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    private String getMessage(String format) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = facesContext.getViewRoot().getViewId();
        UIComponent component = UIComponent.getCurrentComponent(facesContext);
        String clientId = component == null ? "<unknown>" : component.getClientId(facesContext);
        return String.format(format, getClass().getName(), clientId, viewId);
    }

    @Override
    public Iterator<T> iterator() {
        return new LazyDataModelIterator<T>(this);
    }

    public Iterator<T> iterator(String sortField, SortOrder sortOrder, Map<String, Object> filters) {
    	return new LazyDataModelIterator<T>(this, sortField, sortOrder, filters);
    }

	public Iterator<T> iterator(List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		return new LazyDataModelIterator<T>(this, multiSortMeta, filters);
	}
    
    private static final class LazyDataModelIterator<T> implements Iterator<T> {

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
}
