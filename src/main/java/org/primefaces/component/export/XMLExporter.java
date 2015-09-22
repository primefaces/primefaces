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

import java.io.*;
import java.util.Collections;
import java.util.List;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;
import org.primefaces.util.XMLUtils;

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
    	
        if (pageOnly) {
            exportPageOnly(context, table, writer);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, writer);
        }
        else {
            exportAll(context, table, writer);
        }
    	
    	writer.write("</" + table.getId() + ">");
    	
    	table.setRowIndex(-1);
            	
        writer.flush();
        writer.close();
        
        externalContext.responseFlushBuffer();
	}
    
    @Override
    protected void preRowExport(DataTable table, Object document) {
        ((PrintWriter) document).write("\t<" + table.getVar() + ">\n");
    }
    
    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((PrintWriter) document).write("\t</" + table.getVar() + ">\n");
    }
    
    @Override
    protected void exportCells(DataTable table, Object document) {
        PrintWriter writer = (PrintWriter) document;
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
                        
            if (col.isRendered() && col.isExportable()) {
                String columnTag = getColumnTag(col);
                try {
                    addColumnValue(writer, col.getChildren(), columnTag);
                } 
                catch (IOException ex) {
                    throw new FacesException(ex);
                };
            }
        }
    }
    
    protected String getColumnTag(UIColumn column) {
        String headerText = column.getHeaderText();
        UIComponent facet = column.getFacet("header");
        String columnTag;
        
        if (headerText != null) {
            columnTag = headerText.toLowerCase();
        }
        else if (facet != null) {
            columnTag = exportValue(FacesContext.getCurrentInstance(), facet).toLowerCase();            
        }
        else {
            throw new FacesException("No suitable xml tag found for " + column);
        }
        
        // #459 return columnTag.replaceAll(" ", "_");
        return XMLUtils.escapeTag(columnTag);
    }
    		
	protected void addColumnValue(Writer writer, List<UIComponent> components, String tag) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

		writer.write("\t\t<" + tag + ">");

		for (UIComponent component : components) {
			if(component.isRendered()) {
				String value = exportValue(context, component);

				writer.write(value);
			}
		}

		writer.write("</" + tag + ">\n");
	}
	    
    protected void configureResponse(ExternalContext externalContext, String filename) {
        externalContext.setResponseContentType("text/xml");
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".xml");
		externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
    }
	
}
