/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import org.primefaces.component.api.RowKeyMapper;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class RowKeyMapperDataModel<T> extends DataModel<T> implements RowKeyMapper<T> {

    private static final Function<Object, String> DEFAULT_ROWKEY_TRANSFORMER = o -> Objects.toString(System.identityHashCode(o));

    private DataModel<T> wrapped;

    private Function<Object, String> rowKeyTransformer;

    private Map<String, T> cache;

    private Iterator<T> last;

    // for serialization
    public RowKeyMapperDataModel() {
        // NOOP
    }

    public RowKeyMapperDataModel(DataModel<T> wrapped, Function<Object, String> rowKeyTransformer) {
        this.wrapped = wrapped;
        this.rowKeyTransformer = rowKeyTransformer;
        this.cache = new HashMap<>();
        this.last = wrapped.iterator();
        setWrappedData(wrapped.getWrappedData());
    }

    public RowKeyMapperDataModel(DataModel<T> wrapped) {
        this(wrapped, DEFAULT_ROWKEY_TRANSFORMER);
    }

    @Override
    public T getRowData(String rowKey) {
        T rowData = cache.get(rowKey);
        while (rowData == null && last.hasNext()) {
            T o = last.next();
            String oRowKey = getRowKey(o);
            cache.put(oRowKey, o);
            if (Objects.equals(oRowKey, rowKey)) {
                rowData = o;
                break;
            }
        }
        return rowData;
    }

    @Override
    public String getRowKey(T object) {
        String rowKey = rowKeyTransformer.apply(object);
        //caching might not be necessary at this point since there is no use case that
        //requires to call DataTable#getRowKey and DataTable#getRowData in the same request
        //cache.computeIfAbsent(rowKey, k -> object);
        return rowKey;
    }

    @Override
    public void setWrappedData(Object data) {
        wrapped.setWrappedData(data);
    }

    @Override
    public boolean isRowAvailable() {
        return wrapped.isRowAvailable();
    }

    @Override
    public int getRowCount() {
        return wrapped.getRowCount();
    }

    @Override
    public T getRowData() {
        return wrapped.getRowData();
    }

    @Override
    public int getRowIndex() {
        return wrapped.getRowIndex();
    }

    @Override
    public void setRowIndex(int i) {
        wrapped.setRowIndex(i);
    }

    @Override
    public Object getWrappedData() {
        return wrapped.getWrappedData();
    }

    @Override
    public void addDataModelListener(DataModelListener listener) {
        wrapped.addDataModelListener(listener);
    }

    @Override
    public DataModelListener[] getDataModelListeners() {
        return wrapped.getDataModelListeners();
    }

    @Override
    public void removeDataModelListener(DataModelListener listener) {
        wrapped.removeDataModelListener(listener);
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        wrapped.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return wrapped.spliterator();
    }
}
