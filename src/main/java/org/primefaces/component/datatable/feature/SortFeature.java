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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.row.Row;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.SortOrder;

public class SortFeature implements DataTableFeature {

    private boolean isSortRquest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_sorting");
    }

    public void decode(FacesContext context, DataTable table) {
        table.setRowIndex(-1);
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String sortKey = params.get(clientId + "_sortKey");
		String sortDir  = params.get(clientId + "_sortDir");
        boolean isDynamicColumn = params.containsKey(clientId + "_dynamic_column");
        UIColumn sortColumn = null;

        if(isDynamicColumn) {
            String[] idTokens = sortKey.split(String.valueOf(UINamingContainer.getSeparatorChar(context)));
            int colIndex = Integer.parseInt(idTokens[idTokens.length - 1]);
                   
            for(UIColumn column : table.getColumns()) {
                if(column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    
                    if(dynamicColumn.getIndex() == colIndex) {
                        dynamicColumn.applyModel();
                        sortColumn = dynamicColumn;
                        
                        break;
                    }
                }
            }
        }
        else {
            ColumnGroup group = table.getColumnGroup("header");
            if(group != null) {
                outer:
                for(UIComponent child : group.getChildren()) {
                    Row headerRow = (Row) child;
                    
                    for(UIComponent headerRowChild : headerRow.getChildren()) {
                        Column column = (Column) headerRowChild;
                        
                        if(column.getClientId(context).equals(sortKey)) {
                            sortColumn = column;
                            break outer;
                        }
                    }
                }
            } 
            else {
                //single header row
                for(UIComponent child : table.getChildren()) {
                    if(child.getClientId(context).equals(sortKey)) {
                        sortColumn = (Column) child;
                        break;
                    }
                }
            }
        }
        
        //Reset state
		table.setFirst(0);
        
        //update table sort state
        ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
        table.setValueExpression("sortBy", sortByVE);
        table.setSortColumn(sortColumn);
        table.setSortOrder(sortDir);

        if(!table.isLazy()) {
            sort(context, table, sortByVE, table.getVar(), SortOrder.valueOf(sortDir), sortColumn.getSortFunction());
        }
    }
    
    public void sort(FacesContext context, DataTable table, ValueExpression sortByVE, String var, SortOrder sortOrder, MethodExpression sortFunction) {
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

        Collections.sort(list, new BeanPropertyComparator(sortByVE, var, sortOrder, sortFunction));
    }

    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        if(table.isLazy()) {
            table.loadLazyData();
        }
                
        renderer.encodeTbody(context, table, true);
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return isSortRquest(context, table);
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isSortRquest(context, table);
    }
    
}