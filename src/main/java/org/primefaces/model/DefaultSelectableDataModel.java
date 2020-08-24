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

import javax.faces.component.StateHelper;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultSelectableDataModel<T> extends DataModel<T> implements SelectableDataModel<T> {

    private static final Function<Object, String> DEFAULT_ROWKEY_TRANSFORMER = o -> Objects.toString(System.identityHashCode(o));

    private static final String ROW_KEY_HASH_NAME = "DefaultSelectableDataModel#hash";

    private static final String ROW_KEY_CACHE_NAME = "DefaultSelectableDataModel#cache";

    private DataModel<T> wrapped;

    private Function<Object, String> rowKeyTransformer;

    private StateHelper stateHelper;

    // for serialization
    public DefaultSelectableDataModel() {
        // NOOP
    }

    public DefaultSelectableDataModel(StateHelper stateHelper, DataModel<T> wrapped, Function<Object, String> rowKeyTransformer) {
        this.wrapped = wrapped;
        this.rowKeyTransformer = rowKeyTransformer;
        this.stateHelper = stateHelper;
        if (isDirty(wrapped.getWrappedData())) {
            setWrappedData(wrapped.getWrappedData());
        }
    }

    public DefaultSelectableDataModel(StateHelper stateHelper, DataModel<T> wrapped) {
        this(stateHelper, wrapped, DEFAULT_ROWKEY_TRANSFORMER);
    }

    @Override
    public T getRowData(String rowKey) {
        return getCache().get(rowKey);
    }

    @Override
    public Object getRowKey(T object) {
        return rowKeyTransformer.apply(object);
    }

    @Override
    public void setWrappedData(Object data) {
        wrapped.setWrappedData(data);
        Map<String, Object> cache = data != null
                ? ((Collection<T>) data).stream().collect(Collectors.toMap(rowKeyTransformer, o -> o))
                : Collections.emptyMap();
        stateHelper.put(ROW_KEY_HASH_NAME, Objects.hash(data));
        stateHelper.put(ROW_KEY_CACHE_NAME, cache);
    }

    protected Map<String, T> getCache() {
        return (Map<String, T>) stateHelper.get(ROW_KEY_CACHE_NAME);
    }

    protected boolean isDirty(Object data) {
        return !Objects.equals(stateHelper.get(ROW_KEY_HASH_NAME), Objects.hash(data));
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
