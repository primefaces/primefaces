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
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class CSVExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		ExternalContext externalContext = context.getExternalContext();
        
		externalContext.setResponseContentType("text/csv");
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".csv");
		externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());
        Writer writer = externalContext.getResponseOutputWriter();
		List<UIColumn> columns = getColumnsToExport(table);
        String rowIndexVar = table.getRowIndexVar();
    	
    	addFacetColumns(writer, columns, ColumnType.HEADER);
    	
        if(pageOnly)
            exportPageOnly(context, table, columns, writer);
        else if(selectionOnly)
            exportSelectionOnly(context, table, columns, writer);
        else
            exportAll(context, table, columns, writer);
        
        if(hasColumnFooter(columns)) {
            addFacetColumns(writer, columns, ColumnType.FOOTER);
        }
    	
    	table.setRowIndex(-1);
        
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().remove(rowIndexVar);
        }
    	        
        writer.flush();
        writer.close();
        
        externalContext.responseFlushBuffer();
	}
    
    public void exportPageOnly(FacesContext context, DataTable table, List<UIColumn> columns, Writer writer) throws IOException{
        int first = table.getFirst();
    	int size = first + table.getRows();
        String rowIndexVar = table.getRowIndexVar();
    	
    	for(int i = first; i < size; i++) {
    		table.setRowIndex(i);
            if(!table.isRowAvailable())
                break;
            
            if(rowIndexVar != null) {
                context.getExternalContext().getRequestMap().put(rowIndexVar, i);
            }
            
    		addColumnValues(writer, columns);
			writer.write("\n");
		}
    }
    
    public void exportSelectionOnly(FacesContext context, DataTable table, List<UIColumn> columns, Writer writer) throws IOException{
        Object selection = table.getSelection();
        boolean single = table.isSingleSelectionMode();
        int size = selection == null  ? 0 : single ? 1 : Array.getLength(selection);
    	
    	for (int i = 0; i < size; i++) {
    		context.getExternalContext().getRequestMap().put(table.getVar(), single ? selection : Array.get(selection, i) );
            
    		addColumnValues(writer, columns);
			writer.write("\n");
		}
    }
    
    public void exportAll(FacesContext context, DataTable table, List<UIColumn> columns, Writer writer) throws IOException{
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

                addColumnValues(writer, columns);
                writer.write("\n");
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

                addColumnValues(writer, columns);
                writer.write("\n");
            }
            
            //restore
            table.setFirst(first);
        }
    }
	
	private void addColumnValues(Writer writer, List<UIColumn> columns) throws IOException {
		for(Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
            addColumnValue(writer, iterator.next().getChildren());

            if(iterator.hasNext())
                writer.write(",");
		}
	}

	private void addFacetColumns(Writer writer, List<UIColumn> columns, ColumnType columnType) throws IOException {
		for(Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
            addColumnValue(writer, iterator.next().getFacet(columnType.facet()));

            if(iterator.hasNext())
                writer.write(",");
		}
		
		writer.write("\n");
    }
	
	private void addColumnValue(Writer writer, UIComponent component) throws IOException {
		String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        
        //escape double quotes
        value = value.replaceAll("\"", "\"\"");
        
        writer.write("\"" + value + "\"");
	}
	
	private void addColumnValue(Writer writer, List<UIComponent> components) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		for(UIComponent component : components) {
			if(component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);

                //escape double quotes
                value = value.replaceAll("\"", "\"\"");
                
				builder.append(value);
			}
		}
		
		writer.write("\"" + builder.toString() + "\"");
	}
}
