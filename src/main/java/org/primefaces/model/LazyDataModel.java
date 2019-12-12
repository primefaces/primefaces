/**
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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

/**
 * Custom lazy loading DataModel to deal with huge datasets
 */
public abstract class LazyDataModel<T> extends DataModel<T> implements SelectableDataModel<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private int rowIndex = -1;

    private int rowCount;

    private int pageSize;

    private List<T> data;

    public LazyDataModel() {
        super();
    }

    @Override
    public boolean isRowAvailable() {
        if (data == null) {
            return false;
        }

        return rowIndex >= 0 && rowIndex < data.size();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public T getRowData() {
        return data.get(rowIndex);
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
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

    @Override
    public List<T> getWrappedData() {
        return data;
    }

    @Override
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

    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, FilterMeta> filterBy) {

        Map<String, SortMeta> sortBy;
        if (sortField == null) {
            sortBy = Collections.emptyMap();
        }
        else {
            sortBy = new HashMap<>(1);
            sortBy.put(sortField, new SortMeta(null, sortField, sortOrder == null ? SortOrder.UNSORTED : sortOrder, null));
        }

        return load(first, pageSize, sortBy, filterBy);
    }

    /**
     * Loads the data for the given parameters.
     *
     * @param first the first entry
     * @param pageSize the page size
     * @param sortBy a list with all sort informations
     * @param filterBy a map with all filter informations
     * @return the data
     */
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        throw new UnsupportedOperationException(
                getMessage("Either the LazyDataModel#load for single or multi-sort must be implemented [component=%s,view=%s]."));
    }

    @Override
    public T getRowData(String rowKey) {
        throw new UnsupportedOperationException(
                getMessage("getRowData(String rowKey) must be implemented by %s when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    @Override
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
        return new LazyDataModelIterator<>(this);
    }

    @Deprecated
    public Iterator<T> iterator(String sortField, SortOrder sortOrder, Map<String, FilterMeta> filterBy) {
        Map<String, SortMeta> sortBy;
        if (sortField == null) {
            sortBy = Collections.emptyMap();
        }
        else {
            sortBy = new HashMap<>(1);
            sortBy.put(sortField, new SortMeta(null, sortField, sortOrder == null ? SortOrder.UNSORTED : sortOrder, null));
        }

        return iterator(sortBy, filterBy);
    }

    public Iterator<T> iterator(Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return new LazyDataModelIterator<>(this, sortBy, filterBy);
    }
}
