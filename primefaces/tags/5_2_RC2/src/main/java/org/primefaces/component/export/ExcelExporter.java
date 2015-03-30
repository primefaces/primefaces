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
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class ExcelExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {    	
    	Workbook wb = createWorkBook();
    	Sheet sheet = wb.createSheet();
        
    	if(preProcessor != null) {
    		preProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}

        addColumnFacets(table, sheet, Exporter.ColumnType.HEADER);
        
        if (pageOnly) {
            exportPageOnly(context, table, sheet);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, sheet);
        }
        else {
            exportAll(context, table, sheet);
        }
        
        if (table.hasFooterColumn()) {
            addColumnFacets(table, sheet, Exporter.ColumnType.FOOTER);
        }
    	
    	table.setRowIndex(-1);
            	
    	if(postProcessor != null) {
    		postProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
    	
    	writeExcelToResponse(context.getExternalContext(), wb, filename);
	}
	
    @Override
    protected void exportCells(DataTable table, Object document) {
        Sheet sheet = (Sheet) document;
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(sheetRowIndex);
        
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
                        
            if (col.isRendered() && col.isExportable()) {
                addColumnValue(row, col.getChildren());
            }
        }
    }
    
	protected void addColumnFacets(DataTable table, Sheet sheet, Exporter.ColumnType columnType) {
        int sheetRowIndex = columnType.equals(Exporter.ColumnType.HEADER) ? 0 : (sheet.getLastRowNum() + 1);
        Row rowHeader = sheet.createRow(sheetRowIndex);
        
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }
                        
            if (col.isRendered() && col.isExportable()) {                
                UIComponent facet = col.getFacet(columnType.facet());
                if(facet != null) {
                    addColumnValue(rowHeader, facet);
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
                    
                    addColumnValue(rowHeader, textValue);
                }
            }
        }
    }
	
    protected void addColumnValue(Row row, UIComponent component) {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        addColumnValue(row, value);
    }
    
    protected void addColumnValue(Row row, String value) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);

        cell.setCellValue(createRichTextString(value));
    }
    
    protected void addColumnValue(Row row, List<UIComponent> components) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        StringBuilder builder = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();
        
        for (UIComponent component : components) {
        	if(component.isRendered()) {
                String value = exportValue(context, component);
                
                if (value != null)
                	builder.append(value);
            }
		}  
        
        cell.setCellValue(createRichTextString(builder.toString()));
    }
    
    protected RichTextString createRichTextString(String value) {
        return new HSSFRichTextString(value);
    }
    
    protected Workbook createWorkBook() {
        return new HSSFWorkbook();
    }
    
    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
    	externalContext.setResponseContentType("application/vnd.ms-excel");
    	externalContext.setResponseHeader("Expires", "0");
    	externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
    	externalContext.setResponseHeader("Pragma", "public");
    	externalContext.setResponseHeader("Content-disposition", getContentDisposition(filename));
    	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());

        OutputStream out = externalContext.getResponseOutputStream();
        generatedExcel.write(out);
        externalContext.responseFlushBuffer();        
    }

    protected String getContentDisposition(String filename) {
        return "attachment;filename="+ filename + ".xls";
    }
}