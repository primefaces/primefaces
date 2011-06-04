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
package org.primefaces.component.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.datatable.DataTable;

public class CSVExporter extends Exporter {

    @Override
	public void export(FacesContext facesContext, DataTable table, String filename, boolean pageOnly, int[] excludeColumns, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		OutputStream os = response.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os , encodingType);
		PrintWriter writer = new PrintWriter(osw);	
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	
    	addFacetColumns(writer, columns, ColumnType.HEADER);
    	
    	int first = pageOnly ? table.getFirst() : 0;
    	int size = pageOnly ? (first + table.getRows()) : table.getRowCount();
    	
    	for(int i = first; i < size; i++) {
    		table.setRowIndex(i);
    		addColumnValues(writer, columns);
			writer.write("\n");
		}

        if(hasColumnFooter(columns)) {
            addFacetColumns(writer, columns, ColumnType.FOOTER);
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
		for(Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
            addColumnValue(writer, iterator.next().getChildren());

            if(iterator.hasNext())
                writer.write(",");
		}
	}

	private void addFacetColumns(PrintWriter writer, List<UIColumn> columns, ColumnType columnType) throws IOException {
		for(Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
            addColumnValue(writer, iterator.next().getFacet(columnType.facet()));

            if(iterator.hasNext())
                writer.write(",");
		}
		
		writer.write("\n");
    }
	
	private void addColumnValue(PrintWriter writer, UIComponent component) throws IOException {
		String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
            
        writer.write("\"" + value + "\"");
	}
	
	private void addColumnValue(PrintWriter writer, List<UIComponent> components) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		for(UIComponent component : components) {
			if(component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);

				builder.append(value);
			}
		}
		
		writer.write("\"" + builder.toString() + "\"");
	}
}
