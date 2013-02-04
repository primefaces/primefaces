/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import javax.el.ValueExpression;
import org.primefaces.component.api.DynamicColumn;

public class DynamicChainedPropertyComparator extends BeanPropertyComparator {
    
    private DynamicColumn column;
    
    public DynamicChainedPropertyComparator(DynamicColumn column, ValueExpression sortBy, String var, SortOrder sortOrder, MethodExpression sortFunction) {
        super(sortBy, var, sortOrder, sortFunction);
        this.column = column;
    } 
    
    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object obj1, Object obj2) {
        column.applyStatelessModel();
        
        return super.compare(obj1, obj2);
    }
}
