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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.util.ComponentUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFExporter extends Exporter {

	public void export(FacesContext facesContext, UIData table, String filename, int[] excludeColumns, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException { 
		try {
	        Document document = new Document(PageSize.A4.rotate());
	        document.setPageSize(PageSize.A4);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        document.open();
	        
	        if(preProcessor != null) {
	    		preProcessor.invoke(facesContext.getELContext(), new Object[]{document});
	    	}
	        
			PdfPTable pdfTable = exportPDFTable(table, excludeColumns);
	    	document.add(pdfTable);
	    	
	    	if(postProcessor != null) {
	    		postProcessor.invoke(facesContext.getELContext(), new Object[]{document});
	    	}
	    	
	        document.close();
	    	
	        writePDFToResponse(((HttpServletResponse) facesContext.getExternalContext().getResponse()), baos, filename);
	        
		} catch (DocumentException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	private PdfPTable exportPDFTable(UIData table, int[] excludeColumns) {
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int numberOfColumns = columns.size();
    	int tableSize = table.getRowCount();
    	PdfPTable pdfTable = new PdfPTable(numberOfColumns);
    	Font font = FontFactory.getFont(FontFactory.TIMES, "UTF-8");
    	Font headerFont = FontFactory.getFont(FontFactory.TIMES, "UTF-8", Font.DEFAULTSIZE, Font.BOLD);
    	
    	addColumnHeaders(pdfTable, columns, headerFont);
    	
    	for (int i = 0; i < tableSize; i++) {
    		table.setRowIndex(i);
			for (int j = 0; j < numberOfColumns; j++) {
				UIColumn column = columns.get(j);
				
				if(column.isRendered())
					addColumnValue(pdfTable, column.getChildren(), j, font);
			}
		}
    	
    	table.setRowIndex(-1);
    	
    	return pdfTable;
	}
	
	private void addColumnHeaders(PdfPTable pdfTable, List<UIColumn> columns, Font font) {
        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = (UIColumn) columns.get(i);
            
            if(column.isRendered())
            	addColumnValue(pdfTable, column.getHeader(), i, font);
        }
	}

    private void addColumnValue(PdfPTable pdfTable, UIComponent component, int index, Font font) {
    	String value = component == null ? "" : ComponentUtils.getStringValueToRender(FacesContext.getCurrentInstance(), component);
            
        pdfTable.addCell(new Paragraph(value, font));
    }
    
    private void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, int index, Font font) {
        StringBuffer buffer = new StringBuffer();
        
        for(UIComponent component : components) {
        	if(component.isRendered() ) {
        		String value = ComponentUtils.getStringValueToRender(FacesContext.getCurrentInstance(), component);
                
                if(value != null)
                	buffer.append(value);
            }
		}  
        
        pdfTable.addCell(new Paragraph(buffer.toString(), font));
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
  
    public UIComponent findComponentById(FacesContext context, UIComponent root, String id) {
		UIComponent component = null;
		
		for (int i = 0; i < root.getChildCount() && component == null; i++) {
			UIComponent child = (UIComponent) root.getChildren().get(i);
			component = findComponentById(context, child, id);
		}

		if (root.getId() != null) {
			if (component == null && root.getId().equals(id)) {
				component = root;
			}
		}
		return component;
	}
}