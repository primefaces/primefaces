/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.DataModelEvent;
import jakarta.faces.model.DataModelListener;

/**
 * DataModel to deal with huge datasets with by lazy loading, page by page.
 *
 * As long {@link DataModel} is not serializable,
 * see <a href="https://github.com/jakartaee/faces/issues/1585">...</a>,
 * do no extend from {@link jakarta.faces.model.ListDataModel}
 * see #7699
 *
 * @param <T> The model class.
 */
public abstract class LazyDataModel<T> extends DataModel<T> implements SelectableDataModel<T>, Serializable {

    private static final long serialVersionUID = 1L;

    protected Converter<T> rowKeyConverter;
    private int rowCount;
    private int pageSize;

    // overwrite to restore serialization support; see #7699
    private int rowIndex = -1;
    private List<T> data;

    /**
     * For serialization only
     */
    public LazyDataModel() {
        super();
    }

    /**
     * This constructor allows to skip the implementation of {@link #getRowData(java.lang.String)} and
     * {@link #getRowKey(java.lang.Object)}, when selection is used.
     *
     * @param rowKeyConverter The rowKeyConverter used to convert rowKey to rowData and vice versa.
     */
    public LazyDataModel(Converter<T> rowKeyConverter) {
        super();
        this.rowKeyConverter = rowKeyConverter;
    }

    /**
     * Counts the all available data for the given filters.
     *
     * In case of SQL, this would execute a "SELECT COUNT ... WHERE ...".
     *
     * In case you dont use SQL and receive both <code>rowCount</code>
     * and <code>data</code> within a single call, this method should just return <code>0</code>.
     * You must call {@link #recalculateFirst(int, int, int)} and {@link #setRowCount(int)}
     * in your {@link #load(int, int, java.util.Map, java.util.Map)} method.
     *
     * @param filterBy a map with all filter information (only relevant for DataTable, not for eg DataView)
     * @return the data count
     */
    public abstract int count(Map<String, FilterMeta> filterBy);

    /**
     * Loads the data for the given parameters.
     *
     * @param first the first entry
     * @param pageSize the page size
     * @param sortBy a map with all sort information (only relevant for DataTable, not for eg DataView)
     * @param filterBy a map with all filter information (only relevant for DataTable, not for eg DataView)
     * @return the data
     */
    public abstract List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy);

    @Override
    public T getRowData(String rowKey) {
        if (rowKeyConverter != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            return rowKeyConverter.getAsObject(context, UIComponent.getCurrentComponent(context), rowKey);
        }

        // Reusing getRowKey to retrieve rowData
        return Optional.ofNullable(getWrappedData()).orElse(List.of()).stream()
                .filter(item -> rowKey.equals(getRowKey(item)))
                .findFirst()
                .orElse(null);
    }

    /**
     * Loads a single row for the rowIndex provided.
     *
     * @param rowIndex the row index to load
     * @param sortBy a map with all sort information
     * @param filterBy a map with all filter information
     * @return the data
     */
    public T loadOne(int rowIndex, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<T> loaded = load(rowIndex, 1, sortBy, filterBy);
        if (loaded == null || loaded.isEmpty()) {
            return null;
        }
        return loaded.get(0);
    }

    /**
     * Recalculates <code>first</code>, see #1921.
     * Also see: {@link org.primefaces.component.api.UIPageableData#calculateFirst()}
     *
     * @param first the <code>first</code> param from the {@link #load(int, int, java.util.Map, java.util.Map)} method.
     * @param pageSize the <code>pageSize</code> param from the {@link #load(int, int, java.util.Map, java.util.Map)} method.
     * @param rowCount the new <code>rowCount</code>.
     * @return the recalculated <code>first</code>.
     */
    protected int recalculateFirst(int first, int pageSize, int rowCount) {
        if (rowCount > 0 && first >= rowCount) {
            int numberOfPages = (int) Math.ceil(rowCount * 1d / pageSize);
            first = Math.max((numberOfPages - 1) * pageSize, 0);
        }
        return first;
    }

    @Override
    public String getRowKey(T object) {
        if (rowKeyConverter != null) {
            return getRowKeyFromConverter(object);
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a Converter via constructor or implement getRowKey(T object) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    protected String getRowKeyFromConverter(T object) {
        FacesContext context = FacesContext.getCurrentInstance();
        return rowKeyConverter.getAsString(context, UIComponent.getCurrentComponent(context), object);
    }

    protected String getMessage(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = facesContext.getViewRoot().getViewId();
        UIComponent component = UIComponent.getCurrentComponent(facesContext);
        String clientId = component == null ? "<unknown>" : component.getClientId(facesContext);
        return String.format(msg, getClass().getName(), clientId, viewId);
    }

    @Override
    public boolean isRowAvailable() {
        if (data == null) {
            return false;
        }

        return rowIndex >= 0 && rowIndex < data.size();
    }

    @Override
    public T getRowData() {
        return data.get(rowIndex);
    }

    @Override
    public List<T> getWrappedData() {
        return data;
    }

    @Override
    public void setWrappedData(Object list) {
        this.data = (List) list;
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        int oldIndex = this.rowIndex;

        if (rowIndex == -1) {
            this.rowIndex = -1;
        }
        else if (pageSize > 0) {
            this.rowIndex = (rowIndex % pageSize);
        }
        else {
            this.rowIndex = rowIndex;
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
    public Iterator<T> iterator() {
        return new LazyDataModelIterator<>(this);
    }

    public Iterator<T> iterator(Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return new LazyDataModelIterator<>(this, sortBy, filterBy);
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
