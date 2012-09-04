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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.lang.reflect.Array;
import java.util.Map;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.util.Constants;

public class PDFExporter extends Exporter {
    
    private Font cellFont;
    private Font facetFont;
       
	@Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException { 
		try {
	        Document document = new Document();
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        
	        if(preProcessor != null) {
	    		preProcessor.invoke(context.getELContext(), new Object[]{document});
	    	}

            if(!document.isOpen()) {
                document.open();
            }
	        
	    	document.add(exportPDFTable(context, table, pageOnly, selectionOnly, encodingType));
	    	
	    	if(postProcessor != null) {
	    		postProcessor.invoke(context.getELContext(), new Object[]{document});
	    	}
	    	
	        document.close();
	    	
	        writePDFToResponse(context.getExternalContext(), baos, filename);
	        
		} catch(DocumentException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	protected PdfPTable exportPDFTable(FacesContext context, DataTable table, boolean pageOnly, boolean selectionOnly, String encoding) {
    	int columnsCount = getColumnsCount(table);
    	PdfPTable pdfTable = new PdfPTable(columnsCount);
    	this.cellFont = FontFactory.getFont(FontFactory.TIMES, encoding);
    	this.facetFont = FontFactory.getFont(FontFactory.TIMES, encoding, Font.DEFAULTSIZE, Font.BOLD);
    	
    	addColumnFacets(table, pdfTable, ColumnType.HEADER);
        
        if(pageOnly) {
            exportPageOnly(context, table, pdfTable);
        }
        else if(selectionOnly) {
            exportSelectionOnly(context, table, pdfTable);
        }
        else {
            exportAll(context, table, pdfTable);
        }
        
        if(table.hasFooterColumn()) {
            addColumnFacets(table, pdfTable, ColumnType.FOOTER);
        }
    	
    	table.setRowIndex(-1);
    	
    	return pdfTable;
	}
    
    protected void exportPageOnly(FacesContext context, DataTable table, PdfPTable pdfTable) {
        int first = table.getFirst();
    	int rowsToExport = first + table.getRows();
        
        for(int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {                
            exportRow(table, pdfTable, rowIndex);
        }
    }
    
    protected void exportSelectionOnly(FacesContext context, DataTable table, PdfPTable pdfTable) {
        Object selection = table.getSelection();
        String var = table.getVar();
        
        if(selection != null) {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            
            if(selection.getClass().isArray()) {
                int size = Array.getLength(selection);
                
                for(int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    
                    exportCells(table, pdfTable);
                }
            }
            else {
                requestMap.put(var, selection);
                
                exportCells(table, pdfTable);
            }
        }
    }
    
    protected void exportAll(FacesContext context, DataTable table, PdfPTable pdfTable) {
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

                exportRow(table, pdfTable, rowIndex);
            }
     
            //restore
            table.setFirst(first);
            table.loadLazyData();
        } 
        else {
            for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {                
                exportRow(table, pdfTable, rowIndex);
            }
            
            //restore
            table.setFirst(first);
        }
    }
    
    protected void exportRow(DataTable table, PdfPTable pdfTable, int rowIndex) {
        table.setRowIndex(rowIndex);
        
        if(!table.isRowAvailable()) {
            return;
        }
       
        exportCells(table, pdfTable);
    }
    
    protected void exportCells(DataTable table, PdfPTable pdfTable) {
        for(UIColumn col : table.getColumns()) {
            if(!col.isRendered()) {
                continue;
            }
            
            if(col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyModel();
            }
            
            if(col.isExportable()) {
                addColumnValue(pdfTable, col.getChildren(), this.cellFont);
            }
        }
    }
	
	protected void addColumnFacets(DataTable table, PdfPTable pdfTable, ColumnType columnType) {
        for(UIColumn col : table.getColumns()) {
            if(!col.isRendered()) {
                continue;
            }
            
            if(col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyModel();
            }
            
            if(col.isExportable()) {
                addColumnValue(pdfTable, col.getFacet(columnType.facet()), this.facetFont);
            }
        }
	}
	
    protected void addColumnValue(PdfPTable pdfTable, UIComponent component, Font font) {
    	String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
            
        pdfTable.addCell(new Paragraph(value, font));
    }
    
    protected void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, Font font) {
        StringBuilder builder = new StringBuilder();
        
        for(UIComponent component : components) {
        	if(component.isRendered() ) {
        		String value = exportValue(FacesContext.getCurrentInstance(), component);
                
                if(value != null)
                	builder.append(value);
            }
		}  
        
        pdfTable.addCell(new Paragraph(builder.toString(), font));
    }
    
    protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException, DocumentException {     
    	externalContext.setResponseContentType("application/pdf");
    	externalContext.setResponseHeader("Expires", "0");
    	externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
    	externalContext.setResponseHeader("Pragma", "public");
    	externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ fileName + ".pdf");
    	externalContext.setResponseContentLength(baos.size());
    	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());
    	OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }
    
    protected int getColumnsCount(DataTable table) {
        int count = 0;
        
        for(UIComponent child : table.getChildren()) {
            if(!child.isRendered()) {
                continue;
            }

            if(child instanceof Column) {
                Column column = (Column) child;
                
                if(column.isExportable()) {
                    count++;
                }
            }
            else if(child instanceof Columns) {
                Columns columns = (Columns) child;
                
                if(columns.isExportable()) {
                    count += columns.getRowCount();
                }
            }
        }
        
        return count;
    }
}