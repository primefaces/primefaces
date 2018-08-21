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