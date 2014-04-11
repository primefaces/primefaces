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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.context.RequestContext;
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
		String sortDir = params.get(clientId + "_sortDir");
         
        if(table.isMultiSort()) {
            List<SortMeta> multiSortMeta = new ArrayList<SortMeta>();
            String[] sortKeys = sortKey.split(",");
            String[] sortOrders = sortDir.split(",");
            
            for(int i = 0; i < sortKeys.length; i++) {
                UIColumn sortColumn = table.findColumn(sortKeys[i]);
                ValueExpression columnSortByVE = sortColumn.getValueExpression("sortBy");
                String sortField;
            
                if(sortColumn.isDynamic()) {
                    ((DynamicColumn) sortColumn).applyStatelessModel();
                    Object sortByProperty = sortColumn.getSortBy();
                    String field = sortColumn.getField();
                    if(field == null)
                        sortField = (sortByProperty == null) ? table.resolveDynamicField(columnSortByVE) : sortByProperty.toString();
                    else
                        sortField = field;
                }
                else {
                    String field = sortColumn.getField();
                    if(field == null)
                        sortField = (columnSortByVE == null) ? (String) sortColumn.getSortBy() : table.resolveStaticField(columnSortByVE);
                    else
                        sortField = field;
                }
                
                multiSortMeta.add(new SortMeta(sortColumn, sortField, SortOrder.valueOf(convertSortOrderParam(sortOrders[i])), sortColumn.getSortFunction()));
            }
            
            table.setMultiSortMeta(multiSortMeta);
        }
        else {
            UIColumn sortColumn = table.findColumn(sortKey);
            ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
            
            if(sortColumn.isDynamic()) {
                ((DynamicColumn) sortColumn).applyStatelessModel();
                Object sortBy = sortColumn.getSortBy();
                
                if(sortBy == null)
                    table.setValueExpression("sortBy", sortByVE);
                else
                    table.setSortBy(sortBy);
            }
            else {
                if(sortByVE != null)
                    table.setValueExpression("sortBy", sortByVE);
                else
                    table.setSortBy(sortColumn.getSortBy());
            }
            
            table.setSortColumn(sortColumn);
            table.setSortFunction(sortColumn.getSortFunction());
            table.setSortOrder(convertSortOrderParam(sortDir)); 
        }
    }
    
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
		table.setFirst(0);
        
        if(table.isLazy()) {
            table.loadLazyData();
        }
        else {
            if(table.isMultiSort())
                multiSort(context, table);
            else
                singleSort(context, table);
            
            if(table.isPaginator()) {
                RequestContext requestContext = RequestContext.getCurrentInstance();

                if(requestContext != null) {
                    requestContext.addCallbackParam("totalRecords", table.getRowCount());
                }
            }
        }
   
        renderer.encodeTbody(context, table, true);
    }
        
    private ValueExpression createValueExpression(FacesContext context, String var, Object sortBy) {
        ELContext elContext = context.getELContext();
        return context.getApplication().getExpressionFactory().createValueExpression(elContext, "#{" + var + "." + sortBy + "}", Object.class);
    }
    
    public void singleSort(FacesContext context, DataTable table) {
        Object value = table.getValue();
        if(value == null)
            return;
        
        ValueExpression sortByVE;
        ValueExpression tableSortByVE = table.getValueExpression("sortBy");
        if(tableSortByVE != null) {
            sortByVE = tableSortByVE;
            
            UIColumn sortColumn = table.getSortColumn();
            if(sortColumn != null && sortColumn.isDynamic()) {
                ((DynamicColumn) sortColumn).applyStatelessModel();
            }
        }
        else {
            sortByVE = createValueExpression(context, table.getVar(), table.getSortBy());
        }
        
        SortOrder sortOrder = SortOrder.valueOf(table.getSortOrder().toUpperCase(Locale.ENGLISH));
        MethodExpression sortFunction = table.getSortFunction();
        List list = null;
        
        if(value instanceof List)
            list = (List) value;
        else if(value instanceof ListDataModel)
            list = (List) ((ListDataModel) value).getWrappedData();
        else
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        
        Collections.sort(list, new BeanPropertyComparator(sortByVE, table.getVar(), sortOrder, sortFunction, table.isCaseSensitiveSort(), table.resolveDataLocale()));
    }
    
    public void multiSort(FacesContext context, DataTable table) {
        Object value = table.getValue();
        List<SortMeta> sortMeta = table.getMultiSortMeta();
        List list = null;
        boolean caseSensitiveSort = table.isCaseSensitiveSort();
        Locale locale = table.resolveDataLocale();
        
        if(value == null) {
            return;
        }

        if(value instanceof List)
            list = (List) value;
        else if(value instanceof ListDataModel)
            list = (List) ((ListDataModel) value).getWrappedData();
        else
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");

        ChainedBeanPropertyComparator chainedComparator = new ChainedBeanPropertyComparator();
        for(SortMeta meta : sortMeta) { 
            BeanPropertyComparator comparator;
            UIColumn sortColumn = meta.getColumn();
            ValueExpression sortByVE;
            ValueExpression columnSortByVE = sortColumn.getValueExpression("sortBy");
            
            if(sortColumn.isDynamic()) {
                ((DynamicColumn) sortColumn).applyStatelessModel();
                Object sortByProperty = sortColumn.getSortBy();
                
                if(sortByProperty == null) {
                    sortByVE = columnSortByVE;
                    comparator = new DynamicChainedPropertyComparator((DynamicColumn) sortColumn, sortByVE, table.getVar(), meta.getSortOrder(), sortColumn.getSortFunction(), caseSensitiveSort, locale);
                }
                else {
                    sortByVE = createValueExpression(context, table.getVar(), sortByProperty);
                    comparator = new BeanPropertyComparator(sortByVE, table.getVar(), meta.getSortOrder(), sortColumn.getSortFunction(), caseSensitiveSort, locale);
                }
            }
            else {
                sortByVE = (columnSortByVE != null) ? columnSortByVE : createValueExpression(context, table.getVar(), sortColumn.getSortBy());
                comparator = new BeanPropertyComparator(sortByVE, table.getVar(), meta.getSortOrder(), sortColumn.getSortFunction(), caseSensitiveSort, locale);
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
    
    private String convertSortOrderParam(String order) {
        String sortOrder = null;
        int orderNumber = Integer.parseInt(order);
        
        switch (orderNumber) {
            case 0:
                sortOrder = "UNSORTED";
            break;
                
            case 1:
                sortOrder = "ASCENDING";
            break;
                    
            case -1:
                sortOrder = "DESCENDING";
            break;
        }
        
        return sortOrder;
    } 
}