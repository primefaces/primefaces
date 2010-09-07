/*
 * Copyright 2010 Prime Technology.
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
import javax.el.ValueExpression;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.component.column.Column;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.BeanPropertyComparator;

class DataHelper {

    void decodePageRequest(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String firstParam = params.get(clientId + "_first");
		String rowsParam = params.get(clientId + "_rows");
		String pageParam = params.get(clientId + "_page");

		table.setFirst(Integer.valueOf(firstParam));
		table.setRows(Integer.valueOf(rowsParam));
		table.setPage(Integer.valueOf(pageParam));

        if(table.isLazy()) {
            table.loadLazyData();
        }
	}

    void decodeSortRequest(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String sortKey = params.get(clientId + "_sortKey");
		boolean asc = Boolean.valueOf(params.get(clientId + "_sortDir"));
        Column sortColumn = null;

        for(Column column : table.getColumns()) {
            if(column.getClientId(context).equals(sortKey)) {
                sortColumn = column;
                break;
            }
        }

        //Reset state
		table.setFirst(0);
		table.setPage(1);

        if(table.isLazy()) {
            table.setSortField(resolveField(sortColumn.getValueExpression("sortBy")));
            table.setSortOrder(asc);

            table.loadLazyData();
            
        } else {
            List list = (List) table.getValue();
            Collections.sort(list, new BeanPropertyComparator(sortColumn, table.getVar(), asc));
        }        
	}

    void decodeFilterRequest(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        //Reset state
        table.setFirst(0);
        table.setPage(1);

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

            table.loadLazyData();

            //Metadata for callback
            if(table.isPaginator()) {
                RequestContext.getCurrentInstance().addCallbackParam("totalRecords", table.getRowCount());
            }
            
        }
        else {
            Map<String,Column> filterMap = table.getFilterMap();
            List filteredData = new ArrayList();
            table.setValue(null);	//Always work with user data

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

            table.setRowIndex(-1);	//cleanup

            table.setValue(filteredData);

            //Metadata for callback
            if(table.isPaginator()) {
                RequestContext.getCurrentInstance().addCallbackParam("totalRecords", filteredData.size());
            }

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

        table.setRowIndex(-1);	//clean

        //Instant selection and unselection
        queueInstantSelectionEvent(context, table, clientId, params);

		table.setRowIndex(-1);	//clean
	}

    void queueInstantSelectionEvent(FacesContext context, DataTable table, String clientId, Map<String,String> params) {

		if(table.isInstantSelectionRequest(context)) {
            int selectedRowIndex = Integer.parseInt(params.get(clientId + "_instantSelectedRowIndex"));
            table.setRowIndex(selectedRowIndex);
            SelectEvent selectEvent = new SelectEvent(table, table.getRowData());
            selectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
            table.queueEvent(selectEvent);
        }
        else if(table.isInstantUnselectionRequest(context)) {
            int unselectedRowIndex = Integer.parseInt(params.get(clientId + "_instantUnselectedRowIndex"));
            table.setRowIndex(unselectedRowIndex);
            UnselectEvent unselectEvent = new UnselectEvent(table, table.getRowData());
            unselectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
            table.queueEvent(unselectEvent);
        }
	}

    void decodeSingleSelection(DataTable table, String selection) {
		if(isValueBlank(selection)) {
			table.setSelection(null);
            table.setEmptySelected(true);
		} else {
            table.setRowIndex(Integer.parseInt(selection));
            Object data = table.getRowData();

            table.setSelection(data);
		}
	}

	void decodeMultipleSelection(DataTable table, String selection) {
		Class<?> clazz = table.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());

		if(isValueBlank(selection)) {
			Object data = Array.newInstance(clazz.getComponentType(), 0);
			table.setSelection(data);
            
		} else {
            String[] rowSelectValues = selection.split(",");
            Object data = Array.newInstance(clazz.getComponentType(), rowSelectValues.length);

            for(int i = 0; i < rowSelectValues.length; i++) {
                table.setRowIndex(Integer.parseInt(rowSelectValues[i]));

                Array.set(data, i, table.getRowData());
            }

            table.setSelection(data);
		}
	}

    String resolveField(ValueExpression expression) {
        String expressionString = expression.getExpressionString();
        expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}
        
        return expressionString.substring(expressionString.indexOf(".") + 1);                //Remove var
    }
}
