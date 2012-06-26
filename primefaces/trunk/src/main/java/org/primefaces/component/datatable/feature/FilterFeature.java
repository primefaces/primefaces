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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.context.RequestContext;
import org.primefaces.util.ComponentUtils;

public class FilterFeature implements DataTableFeature {

    public boolean isEnabled(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

    public void decode(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String globalFilterParam = clientId + UINamingContainer.getSeparatorChar(context) + "globalFilter";
        boolean hasGlobalFilter = params.containsKey(globalFilterParam);

        //Reset state
        table.setFirst(0);
        
        //populate filters
        Map<String,String> filters = new HashMap<String, String>();
        Map<String,Column> filterMap = table.getFilterMap();

        for(String filterName : filterMap.keySet()) {
            Column column = filterMap.get(filterName);
            String filterValue = params.get(filterName);

            if(!ComponentUtils.isValueBlank(filterValue)) {
                String filterField = resolveField(column.getValueExpression("filterBy"));

                filters.put(filterField, filterValue);
            }
        }

        if(hasGlobalFilter) {
            filters.put("globalFilter", params.get(globalFilterParam));
        }

        table.setFilters(filters);

        //process with filtering for non-lazy data
        if(!table.isLazy()) {
            List filteredData = new ArrayList();
            String globalFilter = hasGlobalFilter ? params.get(globalFilterParam).toLowerCase() : null;

            for(int i = 0; i < table.getRowCount(); i++) {
                table.setRowIndex(i);
                boolean localMatch = true;
                boolean globalMatch = false;

                for(String filterName : filterMap.keySet()) {
                    Column column = filterMap.get(filterName);
                    String columnFilter = params.containsKey(filterName) ? params.get(filterName).toLowerCase() : null; 
                    
                    if(column instanceof Columns) {
                        Columns columns = (Columns) column;
                        //parse a filter key like id_colIndex_2_filter to get colIndex like 2
                        int colIndex = Integer.parseInt(filterName.split("_colIndex_")[1].split("_filter")[0]);
                        columns.setColIndex(colIndex);
                    }
                    
                    String columnValue = String.valueOf(column.getValueExpression("filterBy").getValue(context.getELContext()));

                    if(hasGlobalFilter && !globalMatch) {
                        if(columnValue != null && columnValue.toLowerCase().contains(globalFilter))
                            globalMatch = true;
                    }

                    if(ComponentUtils.isValueBlank(columnFilter)) {
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
    
    private String resolveField(ValueExpression expression) {
        Object newValue = expression.getValue(FacesContext.getCurrentInstance().getELContext());

        if(newValue == null || !(newValue instanceof String)) {
            String expressionString = expression.getExpressionString();
            expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}
        
            return expressionString.substring(expressionString.indexOf(".") + 1);                //Remove var
        }
        else {
            String val = (String) newValue;
            
            return val.substring(val.indexOf(".") + 1);
        }
    }
  
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        renderer.encodeTbody(context, table, true);
    }
}
