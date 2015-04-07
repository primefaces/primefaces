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
package org.primefaces.component.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class CSVExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		ExternalContext externalContext = context.getExternalContext();
        configureResponse(externalContext, filename, encodingType);
        Writer writer = externalContext.getResponseOutputWriter();
    	
    	addColumnFacets(writer, table, ColumnType.HEADER);
    	
        if (pageOnly) {
            exportPageOnly(context, table, writer);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, writer);
        }
        else {
            exportAll(context, table, writer);
        }
        
        if (table.hasFooterColumn()) {
            addColumnFacets(writer, table, ColumnType.FOOTER);
        }
            	        
        writer.flush();
        writer.close();
        
        externalContext.responseFlushBuffer();
	}
    
    protected void addColumnFacets(Writer writer, DataTable table, ColumnType columnType) throws IOException {
        boolean firstCellWritten = false;
        
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
            
            if (col.isRendered() && col.isExportable()) {
                if (firstCellWritten) {
                    writer.write(",");
                }
                
                UIComponent facet = col.getFacet(columnType.facet());
                if(facet != null) {
                    addColumnValue(writer, facet);
                }
                else {
                    String textValue;
                    switch(columnType) {
                        case HEADER:
                            textValue = col.getHeaderText();
                        break;
                            
                        case FOOTER:
                            textValue = col.getFooterText();
                        break;
                            
                        default:
                            textValue = "";
                        break;
                    }
                    
                    addColumnValue(writer, textValue);

                }
                
                firstCellWritten = true;
            }
        }
	
		writer.write("\n");
    }
    
    @Override
    protected void exportCells(DataTable table, Object document) {
        PrintWriter writer = (PrintWriter) document;
        boolean firstCellWritten = false;
        
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
            
            if (col.isRendered() && col.isExportable()) {
                if (firstCellWritten) {
                    writer.write(",");
                }
                
                try {
                    addColumnValue(writer, col.getChildren());
                } catch (IOException ex) {
                    throw new FacesException(ex);
                }
                
                firstCellWritten = true;
            }
        }
    }
    
    protected void configureResponse(ExternalContext externalContext, String filename, String encodingType) {
        externalContext.setResponseContentType("text/csv; charset=" + encodingType);
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".csv");
		externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
    }
    
	protected void addColumnValues(Writer writer, List<UIColumn> columns) throws IOException {
		for(Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
            addColumnValue(writer, iterator.next().getChildren());

            if (iterator.hasNext())
                writer.write(",");
		}
	}

	protected void addColumnValue(Writer writer, UIComponent component) throws IOException {
		String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        
        addColumnValue(writer, value);
	}
    
    protected void addColumnValue(Writer writer, String value) throws IOException {        
         value = (value == null) ? "" : value.replaceAll("\"", "\"\"");
        
        writer.write("\"" + value + "\"");
	}
	
	protected void addColumnValue(Writer writer, List<UIComponent> components) throws IOException {
        writer.write("\"");
        
		for (UIComponent component : components) {
			if (component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);

                //escape double quotes
                value = value == null ? "" : value.replaceAll("\"", "\"\"");
                
				writer.write(value);
			}
		}

		writer.write("\"");
	}

    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((PrintWriter) document).write("\n");
    }
}
