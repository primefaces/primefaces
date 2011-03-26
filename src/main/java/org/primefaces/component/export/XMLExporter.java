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
import java.util.ArrayList;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.datatable.DataTable;

public class XMLExporter extends Exporter {

    @Override
	public void export(FacesContext facesContext, DataTable table, String filename, boolean pageOnly, int[] excludeColumns, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    	
		OutputStream os = response.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, encodingType);
		PrintWriter writer = new PrintWriter(osw);	
		
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	List<String> headers = getFacetTexts(table, ColumnType.HEADER);
    	List<String> footers = getFacetTexts(table, ColumnType.FOOTER);
    	String var = table.getVar().toLowerCase();
    	
    	writer.write("<?xml version=\"1.0\"?>\n");
    	writer.write("<" + table.getId() + ">\n");
    	
    	int first = pageOnly ? table.getFirst() : 0;
    	int size = pageOnly ? (first + table.getRows()) : table.getRowCount();
    	
    	for (int i = first; i < size; i++) {
    		table.setRowIndex(i);
    		
    		writer.write("\t<" + var + ">\n");
    		addColumnValues(writer, columns, headers);
    		writer.write("\t</" + var + ">\n");
		}
    	
   		writer.write("\t<footers>\n");
   		addFooterValues(writer, footers, headers);
   		writer.write("\t</footers>\n");
    	
    	writer.write("</" + table.getId() + ">");
    	
    	table.setRowIndex(-1);
    	
    	response.setContentType("text/xml");
    	response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".xml");
        
        writer.flush();
        writer.close();
        
        response.getOutputStream().flush();
	}
	
	private void addColumnValues(PrintWriter writer, List<UIColumn> columns, List<String> headers) throws IOException {
		for (int i = 0; i < columns.size(); i++) {
			UIColumn column = columns.get(i);
			
			if(column.isRendered())
				addColumnValue(writer, column.getChildren(), headers.get(i));
		}
	}
	
	private void addFooterValues(PrintWriter writer, List<String> footers, List<String> headers) throws IOException {
		for (int i = 0; i < footers.size(); i++) {
			String footer = footers.get(i);
			
			if(!footer.isEmpty())
				addColumnValue(writer, footer, headers.get(i));
		}
	}	
	
	private List<String> getFacetTexts(UIData data, ColumnType columnType) {
		List<String> facets = new ArrayList<String>();
		 
        for (int i = 0; i < data.getChildCount(); i++) {
            UIComponent child = (UIComponent) data.getChildren().get(i);
            
            if (child instanceof UIColumn && child.isRendered()) {
            	UIColumn column = (UIColumn) child;
				UIComponent facet = columnType == ColumnType.HEADER ? column.getHeader() : column.getFooter();
            	
            	if(facet != null && facet.isRendered()) {
            		String value = exportValue(FacesContext.getCurrentInstance(), facet);
            		
            		facets.add(value);
            	} else {
            		facets.add("");
            	}
            }
        }
        return facets;
	}

	private void addColumnValue(PrintWriter writer, List<UIComponent> components, String header) throws IOException {
		StringBuilder builder = new StringBuilder();
		String tag = header.toLowerCase();
		writer.write("\t\t<" + tag + ">");

		for(UIComponent component : components) {
			if(component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);

				builder.append(value);
			}
		}

		writer.write(builder.toString());
		
		writer.write("</" + tag + ">\n");
	}
	
	private void addColumnValue(PrintWriter writer, String footer, String header) throws IOException {
		String tag = header.toLowerCase();
		writer.write("\t\t<" + tag + ">");

		writer.write(footer.toLowerCase());
		
		writer.write("</" + tag + ">\n");
	}
	
}