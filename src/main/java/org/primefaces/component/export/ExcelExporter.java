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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class ExcelExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {    	
    	Workbook wb = new HSSFWorkbook();
    	Sheet sheet = wb.createSheet();
        
    	if(preProcessor != null) {
    		preProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}

        addColumnFacets(table, sheet, ColumnType.HEADER);
        
        if(pageOnly) {
            exportPageOnly(context, table, sheet);
        }
        else if(selectionOnly) {
            exportSelectionOnly(context, table, sheet);
        }
        else {
            exportAll(context, table, sheet);
        }
        
        if(table.hasFooterColumn()) {
            addColumnFacets(table, sheet, ColumnType.FOOTER);
        }
    	
    	table.setRowIndex(-1);
            	
    	if(postProcessor != null) {
    		postProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
    	
    	writeExcelToResponse(context.getExternalContext(), wb, filename);
	}
	
    protected void exportPageOnly(FacesContext context, DataTable table, Sheet sheet) {        
        int first = table.getFirst();
    	int rowsToExport = first + table.getRows();
        
        for(int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {                
            exportRow(table, sheet, rowIndex);
        }
    }
    
    protected void exportSelectionOnly(FacesContext context, DataTable table, Sheet sheet) {        
        Object selection = table.getSelection();
        String var = table.getVar();
        
        if(selection != null) {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            
            if(selection.getClass().isArray()) {
                int size = Array.getLength(selection);
                
                for(int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    
                    exportCells(table, sheet);
                }
            }
            else {
                requestMap.put(var, selection);
                
                exportCells(table, sheet);
            }
        }
    }
    
    protected void exportAll(FacesContext context, DataTable table, Sheet sheet) {
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

                exportRow(table, sheet, rowIndex);
            }
     
            //restore
            table.setFirst(first);
            table.loadLazyData();
        } 
        else {
            for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {                
                exportRow(table, sheet, rowIndex);
            }
            
            //restore
            table.setFirst(first);
        }
    }

    protected void exportRow(DataTable table, Sheet sheet, int rowIndex) {
        table.setRowIndex(rowIndex);
        
        if(!table.isRowAvailable()) {
            return;
        }
       
        exportCells(table, sheet);
    }
    
    protected void exportCells(DataTable table, Sheet sheet) {
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(sheetRowIndex);
        
        for(UIComponent child : table.getChildren()) {
            if(!child.isRendered()) {
                continue;
            }

            if(child instanceof Column) {
                Column column = (Column) child;

                if(column.isExportable()) {
                    addColumnValue(row, column.getChildren());
                }
            }
            else if(child instanceof Columns) {
                Columns columns = (Columns) child;
                Object value = columns.getValue();

                if(value != null) {
                    int columnsCount = ((Collection<?>) columns.getValue()).size();

                    for(int cc = 0; cc < columnsCount; cc++) {
                        columns.setRowModel(cc);

                        if(columns.isExportable()) {;
                            addColumnValue(row, columns.getChildren());
                        }
                    }
                }
            }
        }
    }
    
	protected void addColumnFacets(DataTable table, Sheet sheet, ColumnType columnType) {
        int sheetRowIndex = columnType.equals(ColumnType.HEADER) ? 0 : (sheet.getLastRowNum() + 1);
        Row rowHeader = sheet.createRow(sheetRowIndex);
        
        for(UIComponent child : table.getChildren()) {
            if(!child.isRendered()) {
                continue;
            }
                        
            if(child instanceof Column) {
                Column column = (Column) child;
                
                if(column.isExportable()) {
                    addColumnValue(rowHeader, column.getFacet(columnType.facet()));
                } 
            }
            else if(child instanceof Columns) {
                Columns columns = (Columns) child;
                Object value = columns.getValue();
                
                if(value != null) {
                    int columnsCount = ((Collection<?>) columns.getValue()).size();

                    for(int i = 0; i < columnsCount; i++) {
                        columns.setRowModel(i);

                        if(columns.isExportable()) {
                            addColumnValue(rowHeader, columns.getFacet(columnType.facet()));
                        }
                    }
                }
            }
        }
    }
	
    protected void addColumnValue(Row row, UIComponent component) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

        cell.setCellValue(new HSSFRichTextString(value));
    }
    
    protected void addColumnValue(Row row, List<UIComponent> components) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        StringBuilder builder = new StringBuilder();
        
        for(UIComponent component : components) {
        	if(component.isRendered()) {
                String value = exportValue(FacesContext.getCurrentInstance(), component);
                
                if(value != null)
                	builder.append(value);
            }
		}  
        
        cell.setCellValue(new HSSFRichTextString(builder.toString()));
    }
    
    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
    	externalContext.setResponseContentType("application/vnd.ms-excel");
    	externalContext.setResponseHeader("Expires", "0");
    	externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
    	externalContext.setResponseHeader("Pragma", "public");
    	externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".xls");
    	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());

        OutputStream out = externalContext.getResponseOutputStream();
        generatedExcel.write(out);
        externalContext.responseFlushBuffer();        
    }
}