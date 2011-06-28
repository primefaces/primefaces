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

import java.io.IOException;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.primefaces.component.datatable.DataTable;

public class ExcelExporter extends Exporter {

    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, int[] excludeColumns, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {    	
    	Workbook wb = new HSSFWorkbook();
    	Sheet sheet = wb.createSheet();
    	List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int numberOfColumns = columns.size();
        String rowIndexVar = table.getRowIndexVar();
    	if(preProcessor != null) {
    		preProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}

    	int first = pageOnly ? table.getFirst() : 0;
    	int rowsToExport = pageOnly ? (first + table.getRows()) : table.getRowCount();
    	int sheetRowIndex = 1;

        addFacetColumns(sheet, columns, ColumnType.HEADER, 0);
    	
    	for(int i = first; i < rowsToExport; i++) {
    		table.setRowIndex(i);
            
            if(rowIndexVar != null) {
                context.getExternalContext().getRequestMap().put(rowIndexVar, i);
            }
            
			Row row = sheet.createRow(sheetRowIndex++);
			
			for(int j = 0; j < numberOfColumns; j++) {
                addColumnValue(row, columns.get(j).getChildren(), j);
			}
		}

        if(hasColumnFooter(columns)) {
            addFacetColumns(sheet, columns, ColumnType.FOOTER, sheetRowIndex++);
        }
    	
    	table.setRowIndex(-1);
        
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().remove(rowIndexVar);
        }
    	
    	if(postProcessor != null) {
    		postProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
    	
    	writeExcelToResponse(((HttpServletResponse)context.getExternalContext().getResponse()), wb, filename);
	}
	
	private void addFacetColumns(Sheet sheet, List<UIColumn> columns, ColumnType columnType, int rowIndex) {
        Row rowHeader = sheet.createRow(rowIndex);

        for(int i = 0; i < columns.size(); i++) {            
            addColumnValue(rowHeader, columns.get(i).getFacet(columnType.facet()), i);
        }
    }
	
    private void addColumnValue(Row rowHeader, UIComponent component, int index) {
        Cell cell = rowHeader.createCell(index);
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

        cell.setCellValue(new HSSFRichTextString(value));
    }
    
    private void addColumnValue(Row rowHeader, List<UIComponent> components, int index) {
        Cell cell = rowHeader.createCell(index);
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
    
    private void writeExcelToResponse(HttpServletResponse response, Workbook generatedExcel, String filename) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".xls");

        generatedExcel.write(response.getOutputStream());
    }
}