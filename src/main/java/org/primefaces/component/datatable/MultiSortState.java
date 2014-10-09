/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.component.datatable;

import javax.el.MethodExpression;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class MultiSortState implements java.io.Serializable {
        
    private String sortKey;

    private String sortField;

    private SortOrder sortOrder;

    private MethodExpression sortFunction;

    public MultiSortState() {}

    public MultiSortState(SortMeta sortMeta) {
        this.sortKey = sortMeta.getColumn().getColumnKey();
        this.sortField = sortMeta.getSortField();
        this.sortOrder = sortMeta.getSortOrder();
        this.sortFunction = sortMeta.getSortFunction();
    }

    public String getSortKey() {
        return this.sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public MethodExpression getSortFunction() {
        return sortFunction;
    }

    public void setSortFunction(MethodExpression sortFunction) {
        this.sortFunction = sortFunction;
    } 
}
