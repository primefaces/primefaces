/*
 * Copyright 2009-2014 PrimeTek.
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

    public int getRowCount() {
        if (wrappedArray == null) {
            return -1;
        }
        
        return wrappedArray.length;
    }

    public E getRowData() {
        if (wrappedArray == null) {
            return null;
        }
        else if (!isRowAvailable()) {
            throw new IllegalArgumentException("No next row available!");
        }

        return wrappedArray[index];
    }

    public int getRowIndex() {
        return index;
    }

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

    public Object getWrappedData() {
        return wrapped;
    }

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
