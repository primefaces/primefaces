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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.ChainedBeanPropertyComparator;
import org.primefaces.model.DynamicChainedPropertyComparator;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class SortFeature implements DataTableFeature {

    private boolean isSortRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_sorting");
    }
    
    public void decode(FacesContext context, DataTable table) {
        table.setRowIndex(-1);
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		String sortKey = params.get(clientId + "_sortKey");
		String sortDir  = params.get(clientId + "_sortDir");
         
        if(table.isMultiSort()) {
            List<SortMeta> multiSortMeta = new ArrayList<SortMeta>();
            String[] sortKeys = sortKey.split(",");
            String[] sortOrders = sortDir.split(",");
            
            for(int i = 0; i < sortKeys.length; i++) {
                UIColumn sortColumn = findSortColumn(table, sortKeys[i]);
                ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
                String sortField = null;
                
                if(sortColumn.isDynamic()) {
                    ((DynamicColumn) sortColumn).applyStatelessModel();
                    sortField = table.resolveDynamicField(sortByVE);
                } 
                else {
                    sortField = table.resolveStaticField(sortByVE);
                }
                
                multiSortMeta.add(new SortMeta(sortColumn, sortField, SortOrder.valueOf(sortOrders[i]), sortColumn.getSortFunction()));
            }
            
            table.setMultiSortMeta(multiSortMeta);
        }
        else {
            UIColumn sortColumn = findSortColumn(table, sortKey);
            ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
            table.setValueExpression("sortBy", sortByVE);
            table.setSortColumn(sortColumn);
            table.setSortOrder(sortDir);
        }
    }
    
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
		table.setFirst(0);
        
        if(table.isLazy()) {
            table.loadLazyData();
        }
        else {
            if(table.isMultiSort()) {
                multiSort(context, table);
            } 
            else {
                sortColumn(context, table);
            }
        }
   
        renderer.encodeTbody(context, table, true);
    }
    
    public void sort(FacesContext context, DataTable table, ValueExpression sortBy, SortOrder sortOrder, MethodExpression sortFunction) {
        Object value = table.getValue();
        List list = null;
        
        if(value == null) {
            return;
        }

        if(value instanceof List) {
            list = (List) value;
        } else if(value instanceof ListDataModel) {
            list = (List) ((ListDataModel) value).getWrappedData();
        } else {
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        }
        
        Collections.sort(list, new BeanPropertyComparator(sortBy, table.getVar(), sortOrder, sortFunction));
    }
      
    private void sortColumn(FacesContext context, DataTable table) {
        UIColumn sortColumn = table.getSortColumn();        
        SortOrder sortOrder = SortOrder.valueOf(table.getSortOrder().toUpperCase(Locale.ENGLISH));
        
        if(sortColumn.isDynamic()) {
            ((DynamicColumn) sortColumn).applyStatelessModel();
        }
        
        sort(context, table, sortColumn.getValueExpression("sortBy"), sortOrder, sortColumn.getSortFunction());
    }
    
    public void multiSort(FacesContext context, DataTable table) {
        Object value = table.getValue();
        List<SortMeta> sortMeta = table.getMultiSortMeta();
        List list = null;
        
        if(value == null) {
            return;
        }

        if(value instanceof List) {
            list = (List) value;
        } else if(value instanceof ListDataModel) {
            list = (List) ((ListDataModel) value).getWrappedData();
        } else {
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        }

        ChainedBeanPropertyComparator chainedComparator = new ChainedBeanPropertyComparator();
        for(SortMeta meta : sortMeta) { 
            BeanPropertyComparator comparator = null;
            UIColumn sortColumn = meta.getColumn();
            
            if(sortColumn.isDynamic()) {
                comparator = new DynamicChainedPropertyComparator((DynamicColumn) sortColumn, sortColumn.getValueExpression("sortBy"), 
                                            table.getVar(), meta.getSortOrder(), sortColumn.getSortFunction());
            } 
            else {
                comparator = new BeanPropertyComparator(sortColumn.getValueExpression("sortBy"), table.getVar(), meta.getSortOrder(), sortColumn.getSortFunction());
            }
            
            chainedComparator.addComparator(comparator);
        }
        
        Collections.sort(list, chainedComparator);
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return isSortRequest(context, table);
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isSortRequest(context, table);
    }

    private UIColumn findSortColumn(DataTable table, String sortKey) {
        for(UIColumn column : table.getColumns()) {
            if(column.getColumnKey().equals(sortKey)) {
                return column;
            }
        }
        
        throw new FacesException("Cannot find column with key " + sortKey);
    }
    
}