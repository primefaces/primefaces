/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.convert.Converter;

/**
 * DataModel to deal with huge datasets with by lazy loading, page by page.
 *
 * @param <T> The model class.
 */
public abstract class LazyDataModel<T> extends ListDataModel<T> implements SelectableDataModel<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private Converter<T> converter;

    private int rowCount;

    private int pageSize;

    /**
     * For serialization only
     */
    public LazyDataModel() {

    }

    /**
     * This constructor allows to skip the implementation of {@link #getRowData(java.lang.String)} and
     * {@link #getRowKey(java.lang.Object)}, when selection is used.
     *
     * @param converter The converter used to convert rowKey to rowData and vice versa.
     */
    public LazyDataModel(Converter<T> converter) {
        this.converter = converter;
    }

    /**
     * Counts the all available data for the given filters.
     * In case of SQL, this would execute a "SELECT COUNT ... WHERE ...".
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
        if (converter != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            return converter.getAsObject(context, UIComponent.getCurrentComponent(context), rowKey);
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a Converter via constructor or implement getRowData(String rowKey) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    @Override
    public String getRowKey(T object) {
        if (converter != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            return converter.getAsString(context, UIComponent.getCurrentComponent(context), object);
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a Converter via constructor or implement getRowKey(T object) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    protected String getMessage(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = facesContext.getViewRoot().getViewId();
        UIComponent component = UIComponent.getCurrentComponent(facesContext);
        String clientId = component == null ? "<unknown>" : component.getClientId(facesContext);
        return String.format(msg, getClass().getName(), clientId, viewId);
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

    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex != -1) {
            if (pageSize == 0) {
                rowIndex = -1;
            }
            else {
                rowIndex = rowIndex % pageSize;
            }
        }

        super.setRowIndex(rowIndex);
    }

    @Override
    public List<T> getWrappedData() {
        return (List<T>) super.getWrappedData();
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

    public Converter<T> getConverter() {
        return converter;
    }

    public void setConverter(Converter<T> converter) {
        this.converter = converter;
    }
}
