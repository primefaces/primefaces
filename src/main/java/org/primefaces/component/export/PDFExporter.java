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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
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
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
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
	        
	        if (preProcessor != null) {
	    		preProcessor.invoke(context.getELContext(), new Object[]{document});
	    	}

            if (!document.isOpen()) {
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
        
        if (pageOnly) {
            exportPageOnly(context, table, pdfTable);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, pdfTable);
        }
        else {
            exportAll(context, table, pdfTable);
        }
        
        if (table.hasFooterColumn()) {
            addColumnFacets(table, pdfTable, ColumnType.FOOTER);
        }
    	
    	table.setRowIndex(-1);
    	
    	return pdfTable;
	}
    
    @Override
    protected void exportCells(DataTable table, Object document) {
        PdfPTable pdfTable = (PdfPTable) document;
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
            
            if (col.isRendered() && col.isExportable()) {
                addColumnValue(pdfTable, col.getChildren(), this.cellFont);
            }
        }
    }
	
	protected void addColumnFacets(DataTable table, PdfPTable pdfTable, ColumnType columnType) {
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
            
            if (col.isRendered() && col.isExportable()) {
                UIComponent facet = col.getFacet(columnType.facet());
                if(facet != null) {
                    addColumnValue(pdfTable, facet, this.facetFont);
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
                    
                    if(textValue != null) {
                        pdfTable.addCell(new Paragraph(textValue, this.facetFont));
                    }
                }
            }
        }
	}
	
    protected void addColumnValue(PdfPTable pdfTable, UIComponent component, Font font) {
    	String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
            
        pdfTable.addCell(new Paragraph(value, font));
    }
    
    protected void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, Font font) {
        StringBuilder builder = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();
        
        for (UIComponent component : components) {
        	if(component.isRendered() ) {
        		String value = exportValue(context, component);
                
                if (value != null)
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
    	externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
    	externalContext.setResponseContentLength(baos.size());
    	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
    	OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }
        
    protected int getColumnsCount(DataTable table) {
        int count = 0;
        
        for(UIColumn col : table.getColumns()) {
            if(col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
                        
            if(!col.isRendered()||!col.isExportable()) {
                continue;
            }
            
            count++;
        }
        
        return count;
    }
}