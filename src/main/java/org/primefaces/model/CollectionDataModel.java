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

import java.util.Collection;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

public class CollectionDataModel<E> extends DataModel<E> {

    private int index = -1;
    private Collection<E> wrapped;
    private E[] wrappedArray;

    public CollectionDataModel() {
        this(null);
    }

    public CollectionDataModel(Collection<E> collection) {
        super();
        setWrappedData(collection);
    }

    @Override
    public int getRowCount() {
        if (wrappedArray == null) {
            return -1;
        }

        return wrappedArray.length;
    }

    @Override
    public E getRowData() {
        if (wrappedArray == null) {
            return null;
        }
        else if (!isRowAvailable()) {
            throw new IllegalArgumentException("No next row available!");
        }

        return wrappedArray[index];
    }

    @Override
    public int getRowIndex() {
        return index;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex < -1) {
            throw new IllegalArgumentException();
        }

        int oldIndex = index;
        index = rowIndex;

        if (wrappedArray == null) {
            return;
        }

        DataModelListener[] listeners = getDataModelListeners();
        if (oldIndex != index && listeners != null) {

            Object rowData = null;
            if (isRowAvailable()) {
                rowData = getRowData();
            }

            DataModelEvent event = new DataModelEvent(this, index, rowData);
            for (DataModelListener listener : listeners) {
                if (listener != null) {
                    listener.rowSelected(event);
                }
            }
        }
    }

    @Override
    public Object getWrappedData() {
        return wrapped;
    }

    @Override
    public void setWrappedData(Object data) {
        if (data == null) {
            wrapped = null;
            wrappedArray = null;
            setRowIndex(-1);
        }
        else {
            wrapped = (Collection<E>) data;
            wrappedArray = (E[]) new Object[wrapped.size()];
            wrapped.toArray(wrappedArray);
            setRowIndex(0);
        }
    }

    @Override
    public boolean isRowAvailable() {
        if (wrappedArray == null) {
            return false;
        }
        else if (index >= 0 && index < wrappedArray.length) {
            return true;
        }

        return false;
    }
}
