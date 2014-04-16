/*
 * Copyright 2009-2014 PrimeTek.
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
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class DraggableRowsFeature implements DataTableFeature {
    
    private static final Logger LOGGER = Logger.getLogger(DraggableRowsFeature.class.getName());

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_rowreorder");
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }

    public void decode(FacesContext context, DataTable table) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));
        int toIndex = Integer.parseInt(params.get(clientId + "_toIndex"));
        table.setRowIndex(fromIndex);
        Object rowData = table.getRowData();
        Object value = table.getValue();
        
        if(value instanceof List) {
            List list = (List) value;
            
            if(toIndex >= fromIndex) {
                Collections.rotate(list.subList(fromIndex, toIndex + 1), -1);
            }
            else {
                Collections.rotate(list.subList(toIndex, fromIndex + 1), 1);
            }            
        } 
        else {
            LOGGER.info("Row reordering is only available for list backed datatables, use rowReorder ajax behavior with listener for manual handling of model update.");
        }     
    }

    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("DraggableRows Feature should not encode.");
    }
    
}
