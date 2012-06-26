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
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.row.Row;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.SortOrder;

public class SortFeature implements DataTableFeature {

    public boolean isEnabled(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_sorting");
    }

    public void decode(FacesContext context, DataTable table) {
        table.setRowIndex(-1);
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String sortKey = params.get(clientId + "_sortKey");
		String sortDir  = params.get(clientId + "_sortDir");
        Column sortColumn = null;

        //find the sortColumn if the column is static
        if(sortKey.indexOf("_colIndex") == -1) {
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
        }
        //sort is a dynamic column
        else {
            int colIndex = Integer.parseInt(sortKey.split("_colIndex_")[1]);
            Columns columns = null;
            
            for(UIComponent child : table.getChildren()) {
                if(child instanceof Columns) {
                    columns = (Columns) child;
                    break;
                }
            }
            
            columns.setColIndex(colIndex);
            sortColumn = columns;
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
        renderer.encodeTbody(context, table, true);
    }
    
}
