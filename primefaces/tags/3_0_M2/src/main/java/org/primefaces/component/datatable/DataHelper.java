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
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.row.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.Cell;
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
		table.setPage(Integer.valueOf(pageParam));
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
		table.setPage(1);
        
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

        if(table.isFilterRequest(context)) {
            //Reset state
            table.setFirst(0);
            table.setPage(1);
        }

        if(table.isLazy()) {
            Map<String,String> filters = new HashMap<String, String>();
            Map<String,Column> filterMap = table.getFilterMap();

            for(String filterName : filterMap.keySet()) {
                Column column = filterMap.get(filterName);
                String filterValue = params.get(filterName).toLowerCase();

                if(!isValueBlank(filterValue)) {
                    String filterField = resolveField(column.getValueExpression("filterBy"));
                    
                    filters.put(filterField, filterValue);
                }
            }

            table.setFilters(filters);

            //Metadata for callback
            if(table.isPaginator()) {
                RequestContext.getCurrentInstance().addCallbackParam("totalRecords", table.getRowCount());
            }
            
        }
        else {
            Map<String,Column> filterMap = table.getFilterMap();
            List filteredData = new ArrayList();

            String globalFilter = params.get(clientId + UINamingContainer.getSeparatorChar(context) + "globalFilter");
            boolean hasGlobalFilter = !isValueBlank(globalFilter);
            if(hasGlobalFilter) {
                globalFilter = globalFilter.toLowerCase();
            }

            for(int i = 0; i < table.getRowCount(); i++) {
                table.setRowIndex(i);
                boolean localMatch = true;
                boolean globalMatch = false;

                for(String filterName : filterMap.keySet()) {
                    Column column = filterMap.get(filterName);
                    String columnFilter = params.get(filterName).toLowerCase();
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
        
        if(!isValueBlank(selection) && !table.isCellSelection()) {
            String[] rowKeys = selection.split(",");
            
            for(String rowKey : rowKeys) {
                table.addSelectedRowIndex(Integer.parseInt(rowKey.trim()));
            }
        }

		if(table.isSingleSelectionMode())
			decodeSingleSelection(table, selection);
		else
			decodeMultipleSelection(table, selection);
        
        //clear
        table.setRowIndex(-1);
	}

    void decodeSingleSelection(DataTable table, String selection) {
		if(isValueBlank(selection)) {
			table.setSelection(null);
		} else {
            if(table.isCellSelection()) {
				table.setSelection(buildCell(table, selection));
			}
            else {
                int selectedRowIndex = Integer.parseInt(selection);

                table.setRowIndex(selectedRowIndex);
                table.setSelection(table.getRowData());
            }
		}
	}

	void decodeMultipleSelection(DataTable table, String selection) {
		Class<?> clazz = table.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());

		if(isValueBlank(selection)) {
			Object data = Array.newInstance(clazz.getComponentType(), 0);
			table.setSelection(data);   
		}
        else {
            if(table.isCellSelection()) {
				String[] cellInfos = selection.split(",");
				Cell[] cells = new Cell[cellInfos.length];

				for(int i = 0; i < cellInfos.length; i++) {
					cells[i] = buildCell(table, cellInfos[i]);
					table.setRowIndex(-1);	//clean
				}

				table.setSelection(cells);
			}
             else {
                String[] rowSelectValues = selection.split(",");
                Object data = Array.newInstance(clazz.getComponentType(), rowSelectValues.length);

                for(int i = 0; i < rowSelectValues.length; i++) {
                    table.setRowIndex(Integer.parseInt(rowSelectValues[i]));

                    Array.set(data, i, table.getRowData());
                }

                table.setSelection(data);
            }
		}
	}

    String resolveField(ValueExpression expression) {
        String expressionString = expression.getExpressionString();
        expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}
        
        return expressionString.substring(expressionString.indexOf(".") + 1);                //Remove var
    }

    Cell buildCell(DataTable dataTable, String value) {
		String[] cellInfo = value.split("#");

		//Column
        int rowIndex = Integer.parseInt(cellInfo[0]);
		UIColumn column = dataTable.getColumns().get(Integer.parseInt(cellInfo[1]));

		//RowData
		dataTable.setRowIndex(rowIndex);
		Object rowData = dataTable.getRowData();

		//Cell value
		Object cellValue = null;
		UIComponent columnChild = column.getChildren().get(0);
		if(columnChild instanceof ValueHolder) {
			cellValue = ((ValueHolder) columnChild).getValue();
		}

		return new Cell(rowData, column.getId(), cellValue);
	}
}
