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
package org.primefaces.component.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class XMLExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, int[] excludeColumns, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		ExternalContext externalContext = context.getExternalContext();
        
		externalContext.setResponseContentType("text/xml");
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".xml");
		externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());
    	
		OutputStream os = externalContext.getResponseOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, encodingType);
		PrintWriter writer = new PrintWriter(osw);	
		
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	List<String> headers = getFacetTexts(table, columns, ColumnType.HEADER);
    	List<String> footers = getFacetTexts(table, columns, ColumnType.FOOTER);
        String rowIndexVar = table.getRowIndexVar();
    	
    	writer.write("<?xml version=\"1.0\"?>\n");
    	writer.write("<" + table.getId() + ">\n");
    	
        if(pageOnly)
            exportPageOnly(context, table, columns, headers, writer);
        else if(selectionOnly)
            exportSelectionOnly(context, table, columns, headers, writer);
        else
            exportAll(context, table, columns, headers, writer);
        
        if(hasColumnFooter(columns)) {
            writer.write("\t<footers>\n");
            addFooterValues(writer, footers, headers);
            writer.write("\t</footers>\n");
        }
    	
    	writer.write("</" + table.getId() + ">");
    	
    	table.setRowIndex(-1);
        
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().remove(rowIndexVar);
        }
    	
        writer.flush();
        writer.close();
        
        externalContext.responseFlushBuffer();
	}
	
    public void exportPageOnly(FacesContext context, DataTable table, List<UIColumn> columns, List<String> headers, PrintWriter writer) throws IOException{
        int first = table.getFirst();
    	int size = first + table.getRows();
        String rowIndexVar = table.getRowIndexVar();
        String var = table.getVar().toLowerCase();
    	
    	for (int i = first; i < size; i++) {
    		table.setRowIndex(i);
            if(!table.isRowAvailable())
                break;
            
            if(rowIndexVar != null) {
                context.getExternalContext().getRequestMap().put(rowIndexVar, i);
            }
    		
    		writer.write("\t<" + var + ">\n");
    		addColumnValues(writer, columns, headers);
    		writer.write("\t</" + var + ">\n");
		}
    }
    
    public void exportSelectionOnly(FacesContext context, DataTable table, List<UIColumn> columns, List<String> headers, PrintWriter writer) throws IOException{
        Object selection = table.getSelection();
        boolean single = table.isSingleSelectionMode();
        int size = selection == null  ? 0 : single ?  1 : Array.getLength(selection);
        String var = table.getVar().toLowerCase();
    	
    	for (int i = 0; i < size; i++) {
    		context.getExternalContext().getRequestMap().put(table.getVar(), single ? selection : Array.get(selection, i));

    		writer.write("\t<" + var + ">\n");
    		addColumnValues(writer, columns, headers);
    		writer.write("\t</" + var + ">\n");
		}
    }
    
    public void exportAll(FacesContext context, DataTable table, List<UIColumn> columns, List<String> headers, PrintWriter writer) throws IOException{
        String var = table.getVar().toLowerCase();
        String rowIndexVar = table.getRowIndexVar();
        
        int first = table.getFirst();
    	int rowCount = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();
        
        if(lazy) {
            for(int i = 0; i < rowCount; i++) {
                if(i % rows == 0) {
                    table.setFirst(i);
                    table.loadLazyData();
                }

                table.setRowIndex(i);
                if(!table.isRowAvailable())
                    break;

                if(rowIndexVar != null) {
                    context.getExternalContext().getRequestMap().put(rowIndexVar, i);
                }

                writer.write("\t<" + var + ">\n");
                addColumnValues(writer, columns, headers);
                writer.write("\t</" + var + ">\n");
            }
     
            //restore
            table.setFirst(first);
            table.loadLazyData();
        }
        else {
            for(int i = 0; i < rowCount; i++) {
                table.setRowIndex(i);
                if(!table.isRowAvailable())
                    break;

                if(rowIndexVar != null) {
                    context.getExternalContext().getRequestMap().put(rowIndexVar, i);
                }

                writer.write("\t<" + var + ">\n");
                addColumnValues(writer, columns, headers);
                writer.write("\t</" + var + ">\n");
            }
            
            //restore
            table.setFirst(first);
        }
    }
    
	private void addColumnValues(PrintWriter writer, List<UIColumn> columns, List<String> headers) throws IOException {
		for(int i = 0; i < columns.size(); i++) {
            addColumnValue(writer, columns.get(i).getChildren(), headers.get(i));
		}
	}
	
	private void addFooterValues(PrintWriter writer, List<String> footers, List<String> headers) throws IOException {
		for (int i = 0; i < footers.size(); i++) {
			String footer = footers.get(i);
			
			if(!footer.isEmpty())
				addColumnValue(writer, footer, headers.get(i));
		}
	}	
	
	private List<String> getFacetTexts(UIData data, List<UIColumn> columns, ColumnType columnType) {
		List<String> facets = new ArrayList<String>();
		 
        for(UIColumn column : columns) {
            UIComponent facet = column.getFacet(columnType.facet());
            	
            if(facet != null && facet.isRendered()) {
                facets.add(exportValue(FacesContext.getCurrentInstance(), facet));
            } 
            else {
                facets.add("");
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
