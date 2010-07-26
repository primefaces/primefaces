/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

public abstract class Exporter {
	
	public List<UIColumn> getColumnsToExport(UIData table, int[] excludedColumns) {
        List<UIColumn> allColumns = new ArrayList<UIColumn>();
        List<UIColumn> columnsToExport = new ArrayList<UIColumn>();
        
        for(UIComponent component : table.getChildren()) {
        	if(component instanceof UIColumn)
        		allColumns.add((UIColumn)component);
		}
        
        if(excludedColumns == null) {
        	return allColumns;
        } else {
        	for(int i = 0; i < allColumns.size(); i++) {
				if(Arrays.binarySearch(excludedColumns, i) < 0)
					columnsToExport.add(allColumns.get(i));
        	}
        	
        	allColumns = null;
        	
        	return columnsToExport;
		}
    }

	public abstract void export(FacesContext facesContext, DataTable table,
			String outputFileName, boolean pageOnly, int[] excludedColumnIndexes,
			String encodingType, MethodExpression preProcessor,
			MethodExpression postProcessor) throws IOException;
}
