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

import java.io.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class XMLExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		ExternalContext externalContext = context.getExternalContext();
        configureResponse(externalContext, filename);
		OutputStream os = externalContext.getResponseOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, encodingType);
		PrintWriter writer = new PrintWriter(osw);	
		
    	writer.write("<?xml version=\"1.0\"?>\n");
    	writer.write("<" + table.getId() + ">\n");
    	
        if(pageOnly) {
            exportPageOnly(table, writer);
        }
        else if(selectionOnly) {
            exportSelectionOnly(context, table, writer);
        }
        else {
            exportAll(table, writer);
        }
    	
    	writer.write("</" + table.getId() + ">");
    	
    	table.setRowIndex(-1);
            	
        writer.flush();
        writer.close();
        
        externalContext.responseFlushBuffer();
	}
	
    public void exportPageOnly(DataTable table, Writer writer) throws IOException{
        int first = table.getFirst();
        int rowsToExport = first + table.getRows();

        for(int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {                
            exportRow(table, writer, rowIndex);
        }
    }
    
    public void exportSelectionOnly(FacesContext context, DataTable table, Writer writer) throws IOException{
        Object selection = table.getSelection();
        String var = table.getVar();
        
        if(selection != null) {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            
            if(selection.getClass().isArray()) {
                int size = Array.getLength(selection);
                
                for(int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    
                    exportCells(table, writer);
                }
            }
            else {
                requestMap.put(var, selection);
                
                exportCells(table, writer);
            }
        }
    }
    
    public void exportAll(DataTable table, Writer writer) throws IOException {
        int first = table.getFirst();
    	int rowCount = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();
        
        if(lazy) {
            for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                if(rowIndex % rows == 0) {
                    table.setFirst(rowIndex);
                    table.loadLazyData();
                }

                exportRow(table, writer, rowIndex);
            }
     
            //restore
            table.setFirst(first);
            table.loadLazyData();
        } 
        else {
            for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {                
                exportRow(table, writer, rowIndex);
            }
            
            //restore
            table.setFirst(first);
        }
    }
    
    protected void exportRow(DataTable table, Writer writer, int rowIndex) throws IOException {
        String var = table.getVar().toLowerCase();
        table.setRowIndex(rowIndex);
        
        if(!table.isRowAvailable()) {
            return;
        }
        
        writer.write("\t<" + var + ">\n");
        exportCells(table, writer);
        writer.write("\t</" + var + ">\n");
    }
    
    protected void exportCells(DataTable table, Writer writer) throws IOException {
        for(UIColumn col : table.getColumns()) {
            if(!col.isRendered()) {
                continue;
            }
            
            if(col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyModel();
            }
            
            if(col.isExportable()) {
                String columnTag = getColumnTag(col);
                
                addColumnValue(writer, col.getChildren(), columnTag);
            }
        }
    }
    
    protected String getColumnTag(UIColumn column) {
        String headerText = column.getHeaderText();
        UIComponent facet = column.getFacet("header");
        
        if(headerText != null) {
            return headerText.toLowerCase();
        }
        else if(facet != null) {
            return exportValue(FacesContext.getCurrentInstance(), facet).toLowerCase();            
        }
        else {
            throw new FacesException("No suitable xml tag found for " + column);
        }
    }
    		
	protected void addColumnValue(Writer writer, List<UIComponent> components, String tag) throws IOException {
		StringBuilder builder = new StringBuilder();
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
	    
    protected void configureResponse(ExternalContext externalContext, String filename) {
        externalContext.setResponseContentType("text/xml");
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".xml");
		externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());
    }
	
}
