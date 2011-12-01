/*
 * Copyright 2009-2011 Prime Technology.
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.row.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.SortOrder;

class DataHelper {

    void decodePageRequest(FacesContext context, DataTable table) {
        table.setRowIndex(-1);
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String firstParam = params.get(clientId + "_first");
		String rowsParam = params.get(clientId + "_rows");
		String pageParam = params.get(clientId + "_page");

		table.setFirst(Integer.valueOf(firstParam));
		table.setRows(Integer.valueOf(rowsParam));
	}

    void decodeSortRequest(FacesContext context, DataTable table) {
        table.setRowIndex(-1);
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String sortKey = params.get(clientId + "_sortKey");
		String sortDir  = params.get(clientId + "_sortDir");
        Column sortColumn = null;

        ColumnGroup group = table.getColumnGroup("header");
        if(group != null) {
            outer:
            for (UIComponent child : group.getChildren()) {
                Row headerRow = (Row) child;
                for (UIComponent headerRowChild : headerRow.getChildren()) {
                    Column column = (Column) headerRowChild;
                    if (column.getClientId(context).equals(sortKey)) {
                        sortColumn = column;
                        break outer;
                    }
                }
            }
        } else {
            //single header row
            for (Column column : table.getColumns()) {
                if (column.getClientId(context).equals(sortKey)) {
                    sortColumn = column;
                    break;
                }
            }
        }

        //Reset state
		table.setFirst(0);
        
        ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
        table.setValueExpression("sortBy", sortByVE);
        table.setSortOrder(sortDir);

        if(!table.isLazy()) {
            sort(context, table, sortByVE, table.getVar(), SortOrder.valueOf(sortDir), sortColumn.getSortFunction());
        }  
	}
    
    void sort(FacesContext context, DataTable table, ValueExpression sortByVE, String var, SortOrder sortOrder, MethodExpression sortFunction) {
        Object value = table.getValue();
        List list = null;

        if(value instanceof List) {
            list = (List) value;
        } else if(value instanceof ListDataModel) {
            list = (List) ((ListDataModel) value).getWrappedData();
        } else {
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        }

        Collections.sort(list, new BeanPropertyComparator(sortByVE, var, sortOrder, sortFunction));
    }

    void decodeFilters(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String globalFilterParam = clientId + UINamingContainer.getSeparatorChar(context) + "globalFilter";
        boolean hasGlobalFilter = params.containsKey(globalFilterParam);

        if(table.isFilterRequest(context)) {
            //Reset state
            table.setFirst(0);
        }

        if(table.isLazy()) {
            Map<String,String> filters = new HashMap<String, String>();
            Map<String,Column> filterMap = table.getFilterMap();

            for(String filterName : filterMap.keySet()) {
                Column column = filterMap.get(filterName);
                String filterValue = params.get(filterName);

                if(!isValueBlank(filterValue)) {
                    String filterField = resolveField(column.getValueExpression("filterBy"));
                    
                    filters.put(filterField, filterValue);
                }
            }
            
            if(hasGlobalFilter) {
                filters.put("globalFilter", params.get(globalFilterParam));
            }

            table.setFilters(filters);
        }
        else {
            Map<String,Column> filterMap = table.getFilterMap();
            List filteredData = new ArrayList();
            String globalFilter = hasGlobalFilter ? params.get(globalFilterParam).toLowerCase() : null;

            for(int i = 0; i < table.getRowCount(); i++) {
                table.setRowIndex(i);
                boolean localMatch = true;
                boolean globalMatch = false;

                for(String filterName : filterMap.keySet()) {
                    Column column = filterMap.get(filterName);
                    String columnFilter = params.containsKey(filterName) ? params.get(filterName).toLowerCase() : null; 
                    String columnValue = String.valueOf(column.getValueExpression("filterBy").getValue(context.getELContext()));

                    if(hasGlobalFilter && !globalMatch) {
                        if(columnValue != null && columnValue.toLowerCase().contains(globalFilter))
                            globalMatch = true;
                    }

                    if(isValueBlank(columnFilter)) {
                        localMatch = true;
                    }
                    else if(columnValue == null || !column.getFilterConstraint().applies(columnValue.toLowerCase(), columnFilter)) {
                        localMatch = false;
                        break;
                    }

                }

                boolean matches = localMatch;
                if(hasGlobalFilter) {
                    matches = localMatch && globalMatch;
                }

                if(matches) {
                    filteredData.add(table.getRowData());
                }
            }

            boolean isAllFiltered = filteredData.size() == table.getRowCount();            

            //Metadata for callback
            if(table.isPaginator()) {
                RequestContext requestContext = RequestContext.getCurrentInstance();
                
                if(requestContext != null) {
                  int totalRecords = isAllFiltered ? table.getRowCount() : filteredData.size();
                  requestContext.addCallbackParam("totalRecords", totalRecords);
                }
            }

            //No need to define filtered data if it is same as actual data
            if(!isAllFiltered) {
                table.setFilteredData(filteredData);
            }

            table.setRowIndex(-1);  //reset datamodel
        }
	}

    public boolean isValueBlank(String value) {
		if(value == null)
			return true;

		return value.trim().equals("");
	}

    void decodeSelection(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String selection = params.get(clientId + "_selection");
        
		if(table.isSingleSelectionMode())
			decodeSingleSelection(table, selection);
		else
			decodeMultipleSelection(table, selection);
	}

    void decodeSingleSelection(DataTable table, String selection) {
		if(isValueBlank(selection))
			table.setSelection(null);
        else
            table.setSelection(table.getRowData(selection));
	}

	void decodeMultipleSelection(DataTable table, String selection) {
		Class<?> clazz = table.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());

		if(isValueBlank(selection)) {
			Object data = Array.newInstance(clazz.getComponentType(), 0);
			table.setSelection(data);   
		}
        else {
            String[] rowKeys = selection.split(",");
            List selectionList = new ArrayList();
            
            for(int i = 0; i < rowKeys.length; i++) {
                Object rowData = table.getRowData(rowKeys[i]);
                
                if(rowData != null)
                    selectionList.add(rowData);
            }

            Object selectinArray = Array.newInstance(clazz.getComponentType(), selectionList.size());
            table.setSelection(selectionList.toArray((Object[])selectinArray));
		}
	}

    String resolveField(ValueExpression expression) {
        String expressionString = expression.getExpressionString();
        expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}
        
        return expressionString.substring(expressionString.indexOf(".") + 1);                //Remove var
    }
    
        
    /**
     * Finds if row to render is in same group of previous row
     */
    boolean isInSameGroup(FacesContext context, DataTable table, int currentRowIndex) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String var = table.getVar();
        
        table.setRowIndex(currentRowIndex);
        Object currentGroupByData = table.getSortBy();

        table.setRowIndex(currentRowIndex + 1);
        if(!table.isRowAvailable())
            return false;
        
        Object nextGroupByData = table.getSortBy();
        if(currentGroupByData != null && nextGroupByData.equals(currentGroupByData)) {
            return true;
        }
        
        return false;
    }
}
