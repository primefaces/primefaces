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
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class DraggableColumnsFeature implements DataTableFeature {

    public void decode(FacesContext context, DataTable table) {
        List<Column> actualColumns = table.getColumns();
        String clientId = table.getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String[] order = params.get(clientId + "_columnOrder").split(",");
        UIComponent firstChild = actualColumns.get(0);
        
        if(firstChild instanceof Columns) {
            Columns uicolumns = (Columns) firstChild;
            List<?> model = (List<?>) uicolumns.getValue();
            List orderedModel = new ArrayList();
            
            for(String columnId : order) {
                int colIndex = Integer.parseInt(columnId.split("_colIndex_")[1]);
                
                orderedModel.add(model.get(colIndex));
            }
            
            uicolumns.getValueExpression("value").setValue(context.getELContext(), orderedModel);
        }
        else {
            List<Column> orderedColumns = new ArrayList<Column>();
            for(String columnId : order) {
                for(Column column : actualColumns) {
                    if(columnId.equals(column.getClientId(context))) {
                        orderedColumns.add(column);
                        break;                    
                    }

                }
            }
            
            table.setColumns(orderedColumns);
        }
    }

    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("DraggableColumns Feature should not encode.");
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return table.isDraggableColumns();
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }
}
