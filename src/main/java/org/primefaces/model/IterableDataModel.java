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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

public class IterableDataModel<E> extends DataModel<E> {

    private int index = -1;
    private Iterable<E> wrapped;
    private List<E> wrappedList;

    public IterableDataModel() {
        this(null);
    }

    public IterableDataModel(Iterable<E> iterable) {
        setWrappedData(iterable);
    }

    @Override
    public int getRowCount() {
        if (wrappedList == null) {
            return -1;
        }

        return wrappedList.size();
    }

    @Override
    public E getRowData() {
        if (wrappedList == null) {
            return null;
        }
        if (!isRowAvailable()) {
            throw new IllegalArgumentException();
        }

        return wrappedList.get(index);
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

        if (wrappedList == null) {
            return;
        }

        DataModelListener[] dataModelListeners = getDataModelListeners();
        if (oldIndex != index && dataModelListeners != null) {

            Object rowData = null;
            if (isRowAvailable()) {
                rowData = getRowData();
            }

            DataModelEvent event = new DataModelEvent(this, index, rowData);
            for (DataModelListener listener : dataModelListeners) {
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
            wrappedList = null;
            setRowIndex(-1);
        }
        else {
            wrapped = (Iterable<E>) data;
            if (wrapped instanceof List) {
                wrappedList = (List<E>) wrapped;
            }
            else if (wrapped instanceof Collection) {
                wrappedList = new ArrayList((Collection<E>) wrapped);
            }
            else {
                wrappedList = new ArrayList();
                Iterator<E> iterator = wrapped.iterator();
                while (iterator.hasNext()) {
                    wrappedList.add(iterator.next());
                }
            }
            setRowIndex(0);
        }
    }

    @Override
    public boolean isRowAvailable() {
        if (wrappedList == null) {
            return false;
        }
        else if (index >= 0 && index < wrappedList.size()) {
            return true;
        }

        return false;
    }
}