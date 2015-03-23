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

import javax.el.MethodExpression;
import org.primefaces.component.api.UIColumn;

public class SortMeta {
    
    private UIColumn column;
    
    private String sortField;
    
    private SortOrder sortOrder;
    
    private MethodExpression sortFunction;

    public SortMeta() {}
    
    public SortMeta(UIColumn column, String sortField, SortOrder sortOrder, MethodExpression sortFunction) {
        this.column = column;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public UIColumn getColumn() {
        return column;
    }

    public void setSortBy(UIColumn column) {
        this.column = column;
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