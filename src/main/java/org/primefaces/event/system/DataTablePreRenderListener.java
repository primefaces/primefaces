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
package org.primefaces.event.system;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.feature.DataTableFeatureKey;
import org.primefaces.component.datatable.feature.SortFeature;

public class DataTablePreRenderListener implements SystemEventListener {

    public void processEvent(SystemEvent event) throws AbortProcessingException {
        DataTable table = (DataTable) event.getSource();
        FacesContext context = FacesContext.getCurrentInstance();
        
        if(!table.shouldEncodeFeature(context)) {
            if(table.isLazy()) {
                if(table.isLiveScroll())
                    table.loadLazyScrollData(0, table.getScrollRows());
                else
                    table.loadLazyData();
            }

            boolean defaultSorted = (table.getValueExpression("sortBy") != null || table.getSortBy() != null);
            if(defaultSorted && !table.isLazy()) {
                SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);

                if(table.isMultiSort())
                    sortFeature.multiSort(context, table);
                else
                    sortFeature.singleSort(context, table);            
            }

            if(table.isPaginator()) {
                table.calculateFirst();
            }

            Columns dynamicCols = table.getDynamicColumns();
            if(dynamicCols != null) {
                dynamicCols.setRowIndex(-1);
            }
        }
    }

    public boolean isListenerForSource(Object source) {
        return true;
    }
    
}
