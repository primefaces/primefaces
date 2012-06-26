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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.util.ComponentUtils;

public class SelectionFeature implements DataTableFeature {

    public void decode(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
		String selection = params.get(clientId + "_selection");
        
		if(table.isSingleSelectionMode())
			decodeSingleSelection(table, selection);
		else
			decodeMultipleSelection(table, selection);
    }
    
    void decodeSingleSelection(DataTable table, String selection) {
		if(ComponentUtils.isValueBlank(selection))
			table.setSelection(null);
        else
            table.setSelection(table.getRowData(selection));
	}

	void decodeMultipleSelection(DataTable table, String selection) {
		Class<?> clazz = table.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());

		if(ComponentUtils.isValueBlank(selection)) {
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
    
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("SelectFeature should not encode.");
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return table.isSelectionEnabled();
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }
    
}
