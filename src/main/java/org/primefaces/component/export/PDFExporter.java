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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.datatable.DataTable;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.lang.reflect.Array;

public class PDFExporter extends Exporter {

	@Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, int[] excludeColumns, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException { 
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
	        
			PdfPTable pdfTable = exportPDFTable(context, table, pageOnly, selectionOnly, excludeColumns, encodingType);
	    	document.add(pdfTable);
	    	
	    	if(postProcessor != null) {
	    		postProcessor.invoke(context.getELContext(), new Object[]{document});
	    	}
	    	
	        document.close();
	    	
	        writePDFToResponse(((HttpServletResponse) context.getExternalContext().getResponse()), baos, filename);
	        
		} catch (DocumentException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	private PdfPTable exportPDFTable(FacesContext context, DataTable table, boolean pageOnly, boolean selectionOnly, int[] excludeColumns, String encoding) {
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int numberOfColumns = columns.size();
    	PdfPTable pdfTable = new PdfPTable(numberOfColumns);
    	Font font = FontFactory.getFont(FontFactory.TIMES, encoding);
    	Font headerFont = FontFactory.getFont(FontFactory.TIMES, encoding, Font.DEFAULTSIZE, Font.BOLD);
        String rowIndexVar = table.getRowIndexVar();
    	
    	addFacetColumns(pdfTable, columns, headerFont, ColumnType.HEADER);
        
        if(pageOnly)
            exportPageOnly(context, table, columns, pdfTable, font);
        else if(selectionOnly)
            exportSelectionOnly(context, table, columns, pdfTable, font);
        else
            exportAll(context, table, columns, pdfTable, font);
        
        if(hasColumnFooter(columns)) {
            addFacetColumns(pdfTable, columns, headerFont, ColumnType.FOOTER);
        }
    	
    	table.setRowIndex(-1);
        
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().remove(rowIndexVar);
        }
    	
    	return pdfTable;
	}
    
    private void exportPageOnly( FacesContext context, DataTable table, List<UIColumn> columns, PdfPTable pdfTable, Font font){
        String rowIndexVar = table.getRowIndexVar();
        int numberOfColumns = columns.size();
        
        int first = table.getFirst();
    	int size = first + table.getRows();
        
        for(int i = first; i < size; i++) {
    		table.setRowIndex(i);
            
            if(rowIndexVar != null) {
                context.getExternalContext().getRequestMap().put(rowIndexVar, i);
            }

			for(int j = 0; j < numberOfColumns; j++) {				
                addColumnValue(pdfTable, columns.get(j).getChildren(), j, font);
			}
		}
    }
    
    private void exportSelectionOnly( FacesContext context, DataTable table, List<UIColumn> columns, PdfPTable pdfTable, Font font){
        int numberOfColumns = columns.size();
        
        Object selection = table.getSelection();
        boolean selectionMode = table.getSelectionMode().equalsIgnoreCase("multiple");
        
    	int size = selection == null  ? 0 : selectionMode ? Array.getLength(selection) : 1;
        
        for(int i = 0; i < size; i++) {
            context.getExternalContext().getRequestMap().put(table.getVar(), selectionMode ? Array.get(selection, i) : selection );

			for(int j = 0; j < numberOfColumns; j++) {				
                addColumnValue(pdfTable, columns.get(j).getChildren(), j, font);
			}
		}
    }
    
    private void exportAll( FacesContext context, DataTable table, List<UIColumn> columns, PdfPTable pdfTable, Font font ){
        String rowIndexVar = table.getRowIndexVar();
        int numberOfColumns = columns.size();
        
        int first = 0;
    	int size = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();
        
        //first align
        table.setFirst(first);
        if(lazy){
            table.clearLazyCache();
            table.loadLazyData();
        }
        
    	for(int i = 0; (first + i) < size; i++) {
            
            if(lazy && i == rows){
                first += i ;
                i = 0;
                table.setFirst(first);
                table.clearLazyCache();
                table.loadLazyData();
            }
            
    		table.setRowIndex(first + i);
            
            if(rowIndexVar != null) {
                context.getExternalContext().getRequestMap().put(rowIndexVar, first + i);
            }

			for(int j = 0; j < numberOfColumns; j++) {				
                addColumnValue(pdfTable, columns.get(j).getChildren(), j, font);
			}
		}
    }
	
	private void addFacetColumns(PdfPTable pdfTable, List<UIColumn> columns, Font font, ColumnType columnType) {
        for(int i = 0; i < columns.size(); i++) {
            UIColumn column = (UIColumn) columns.get(i);

            addColumnValue(pdfTable, column.getFacet(columnType.facet()), font);
        }
	}
	
    private void addColumnValue(PdfPTable pdfTable, UIComponent component, Font font) {
    	String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
            
        pdfTable.addCell(new Paragraph(value, font));
    }
    
    private void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, int index, Font font) {
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
    
    private void writePDFToResponse(HttpServletResponse response, ByteArrayOutputStream baos, String fileName) throws IOException, DocumentException {     
    	response.setContentType("application/pdf");
    	response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".pdf");
        response.setContentLength(baos.size());
        
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }
}