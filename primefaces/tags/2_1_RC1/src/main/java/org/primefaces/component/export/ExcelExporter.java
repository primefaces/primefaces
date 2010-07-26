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

import java.io.IOException;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.ComponentUtils;

public class ExcelExporter extends Exporter {

	public void export(FacesContext facesContext, DataTable table, String filename, boolean pageOnly, int[] excludeColumns, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {    	
    	HSSFWorkbook wb = new HSSFWorkbook();
    	HSSFSheet sheet = wb.createSheet();
    	List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int numberOfColumns = columns.size();
    	if(preProcessor != null) {
    		preProcessor.invoke(facesContext.getELContext(), new Object[]{wb});
    	}
    	
    	addColumnHeaders(sheet, columns);
    	
    	int first = pageOnly ? table.getFirst() : 0;
    	int size = pageOnly ? (first + table.getRows()) : table.getRowCount();
    	int sheetRowIndex = 1;
    	
    	for(int i = first; i < size; i++) {
    		table.setRowIndex(i);
			HSSFRow row = sheet.createRow(sheetRowIndex++);
			
			for (int j = 0; j < numberOfColumns; j++) {
				UIColumn column = columns.get(j);
				
				if(column.isRendered())
					addColumnValue(row, column.getChildren(), j);
			}
		}
    	
    	table.setRowIndex(-1);
    	
    	if(postProcessor != null) {
    		postProcessor.invoke(facesContext.getELContext(), new Object[]{wb});
    	}
    	
    	writeExcelToResponse(((HttpServletResponse)facesContext.getExternalContext().getResponse()), wb, filename);
	}
	
	private void addColumnHeaders(HSSFSheet sheet, List<UIColumn> columns) {
        HSSFRow rowHeader = sheet.createRow(0);

        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = (UIColumn) columns.get(i);
            
            if(column.isRendered())
            	addColumnValue(rowHeader, column.getHeader(), i);
        }
    }

    private void addColumnValue(HSSFRow rowHeader, UIComponent component, int index) {
        HSSFCell cell = rowHeader.createCell(index);
        String value = component == null ? "" : ComponentUtils.getStringValueToRender(FacesContext.getCurrentInstance(), component);

        cell.setCellValue(new HSSFRichTextString(value));
    }
    
    private void addColumnValue(HSSFRow rowHeader, List<UIComponent> components, int index) {
        HSSFCell cell = rowHeader.createCell(index);
        StringBuffer buffer = new StringBuffer();
        
        for(UIComponent component : components) {
        	if(component.isRendered()) {
                String value = ComponentUtils.getStringValueToRender(FacesContext.getCurrentInstance(), component);
                
                if(value != null)
                	buffer.append(value);
            }
		}  
        
        cell.setCellValue(new HSSFRichTextString(buffer.toString()));
    }
    
    private void writeExcelToResponse(HttpServletResponse response, HSSFWorkbook generatedExcel, String filename) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".xls");

        generatedExcel.write(response.getOutputStream());
    }
}