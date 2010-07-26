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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.util.ComponentUtils;

public class CSVExporter extends Exporter {

	public void export(FacesContext facesContext, UIData table, String filename, int[] excludeColumns, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		PrintWriter writer = new PrintWriter(response.getOutputStream());	
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int rows = table.getRowCount();
    
    	addColumnHeaders(writer, columns);
    	
    	for (int i = 0; i < rows; i++) {
    		table.setRowIndex(i);
    		addColumnValues(writer, columns);
			writer.write("\n");
		}
    	
    	table.setRowIndex(-1);
    	
    	response.setContentType("text/csv");
    	response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".csv");
        
        writer.flush();
        writer.close();
        
        response.getOutputStream().flush();
	}
	
	private void addColumnValues(PrintWriter writer, List<UIColumn> columns) throws IOException {
		for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
			UIColumn column = (UIColumn) iterator.next();
			
			if(column.isRendered()) {
				addColumnValue(writer, column.getChildren());
				
				if(iterator.hasNext())
					writer.write(",");
			}
		}
	}

	private void addColumnHeaders(PrintWriter writer, List<UIColumn> columns) throws IOException {
		for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
			UIColumn column = (UIColumn) iterator.next();
			
			if(column.isRendered()) {
				addColumnValue(writer, column.getHeader());
			
				if(iterator.hasNext())
					writer.write(",");
			}
		}
		
		writer.write("\n");
    }
	
	private void addColumnValue(PrintWriter writer, UIComponent component) throws IOException {
		String value = component == null ? "" : ComponentUtils.getStringValueToRender(FacesContext.getCurrentInstance(), component);
            
        writer.write("\"" + value + "\"");
	}
	
	private void addColumnValue(PrintWriter writer, List<UIComponent> components) throws IOException {
		StringBuffer buffer = new StringBuffer();
		
		for(UIComponent component : components) {
			if(component.isRendered()) {
				String value = ComponentUtils.getStringValueToRender(FacesContext.getCurrentInstance(), component);

				buffer.append(value);
			}
		}
		
		writer.write("\"" + buffer.toString() + "\"");
       
	}
}
