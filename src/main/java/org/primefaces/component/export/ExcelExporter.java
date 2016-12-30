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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

public class ExcelExporter extends Exporter {

    private CellStyle cellStyle;
    private CellStyle facetStyle;

    @Override
    public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options) throws IOException {
        Workbook wb = createWorkBook();
        String sheetName = getSheetName(context, table);
        if(sheetName == null) {
            sheetName = table.getId();
        }
        
        sheetName = WorkbookUtil.createSafeSheetName(sheetName);
        if(sheetName.equals("empty") || sheetName.equals("null")) {
            sheetName = "Sheet";
        }
        
    	Sheet sheet = wb.createSheet(sheetName);
        
    	if(preProcessor != null) {
    		preProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
        
        applyOptions(wb, table, sheet, options);
        exportTable(context, table, sheet, pageOnly, selectionOnly);
        
        for (int i = 0; i < table.getColumnsCount(); i++) {
            sheet.autoSizeColumn((short) i);
        }
            	
    	if(postProcessor != null) {
    		postProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
        
    	writeExcelToResponse(context.getExternalContext(), wb, filename);
    }
    
    @Override
    public void export(FacesContext context, String filename, List<DataTable> tables, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options) throws IOException {
        Workbook wb = createWorkBook();

    	if(preProcessor != null) {
    		preProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
        
        for(int i = 0; i < tables.size(); i++) {
            DataTable table = tables.get(i);
            String sheetName = getSheetName(context, table);
            if(sheetName == null) {
                sheetName = table.getId();
            }
            
            sheetName = WorkbookUtil.createSafeSheetName(sheetName);
            if(sheetName.equals("empty") || sheetName.equals("null")) {
                sheetName = "Sheet" + String.valueOf(i + 1);
            }
            
            Sheet sheet = wb.createSheet(sheetName);
            applyOptions(wb, table, sheet, options);
            exportTable(context, table, sheet, pageOnly, selectionOnly);
            
            for (int j = 0; j < table.getColumnsCount(); j++) {
                sheet.autoSizeColumn((short) j);
            }
        }
            	
    	if(postProcessor != null) {
    		postProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
    	
    	writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
	public void export(FacesContext context, List<String> clientIds, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options) throws IOException {    	
    	Workbook wb = createWorkBook();
        
        if(preProcessor != null) {
    		preProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
        
        VisitContext visitContext = VisitContext.createVisitContext(context, clientIds, null);
        VisitCallback visitCallback = new ExcelExportVisitCallback(this, wb, pageOnly, selectionOnly);
        context.getViewRoot().visitTree(visitContext, visitCallback);
        
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
                addColumnValue(row, col.getChildren(), col);
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
        
        if(facetStyle != null) {
            cell.setCellStyle(facetStyle);
        }
    }
    
    protected void addColumnValue(Row row, List<UIComponent> components, UIColumn column) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        FacesContext context = FacesContext.getCurrentInstance();
        
        if(column.getExportFunction() != null) {
            cell.setCellValue(createRichTextString(exportColumnByFunction(context, column)));
        }
        else {
            StringBuilder builder = new StringBuilder();
            for (UIComponent component : components) {
                if(component.isRendered()) {
                    String value = exportValue(context, component);
                
                    if (value != null)
                        builder.append(value);
                }
            }  
        
            cell.setCellValue(createRichTextString(builder.toString()));
        }
        
        if(cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
    }
    
    protected RichTextString createRichTextString(String value) {
        return new HSSFRichTextString(value);
    }
    
    protected Workbook createWorkBook() {
        return new HSSFWorkbook();
    }
    
    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
    	externalContext.setResponseContentType(getContentType());
    	externalContext.setResponseHeader("Expires", "0");
    	externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
    	externalContext.setResponseHeader("Pragma", "public");
    	externalContext.setResponseHeader("Content-disposition", getContentDisposition(filename));
    	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());

        OutputStream out = externalContext.getResponseOutputStream();
        generatedExcel.write(out);  
    }

    protected String getContentType() {
        return "application/vnd.ms-excel";
    }
    
    protected String getContentDisposition(String filename) {
        return "attachment;filename=\""+ filename + ".xls\"";
    }
    
    public void exportTable(FacesContext context, DataTable table, Sheet sheet, boolean pageOnly, boolean selectionOnly) {
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
    }

    protected void applyOptions(Workbook wb, DataTable table, Sheet sheet, ExporterOptions options) {
        facetStyle = wb.createCellStyle();
        facetStyle.setAlignment((short)CellStyle.ALIGN_CENTER);
        facetStyle.setVerticalAlignment((short)CellStyle.VERTICAL_CENTER);
        facetStyle.setWrapText(true);
        applyFacetOptions(wb, options, facetStyle);
        
        cellStyle = wb.createCellStyle();
        cellStyle.setAlignment((short)CellStyle.ALIGN_LEFT);
        applyCellOptions(wb, options, cellStyle);

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setPrintGridlines(true);
    }

    protected void applyFacetOptions(Workbook wb, ExporterOptions options, CellStyle facetStyle) {
        Font facetFont = wb.createFont();
        
        if(options != null) {
            String facetFontStyle = options.getFacetFontStyle();
            if(facetFontStyle != null) {
                if(facetFontStyle.equalsIgnoreCase("BOLD")) {
                    facetFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                }
                if(facetFontStyle.equalsIgnoreCase("ITALIC")) {
                    facetFont.setItalic(true);
                }
            }

            HSSFPalette palette = ((HSSFWorkbook)wb).getCustomPalette();
            Color color = null;

            String facetBackground = options.getFacetBgColor();
            if (facetBackground != null) {
                color = Color.decode(facetBackground);
                HSSFColor backgroundColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                ((HSSFCellStyle) facetStyle).setFillForegroundColor(backgroundColor.getIndex());
                facetStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            }

            String facetFontColor = options.getFacetFontColor();
            if (facetFontColor != null) {
                color = Color.decode(facetFontColor);
                HSSFColor facetColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                ((HSSFFont) facetFont).setColor(facetColor.getIndex());
            }

            String facetFontSize = options.getFacetFontSize();
            if (facetFontSize != null) {
                facetFont.setFontHeightInPoints(Short.valueOf(facetFontSize));
            }
        }
        
        facetStyle.setFont(facetFont);
    }
 
    protected void applyCellOptions(Workbook wb, ExporterOptions options, CellStyle cellStyle) { 
        Font cellFont = wb.createFont();
        
        if(options != null) {
            String cellFontColor = options.getCellFontColor();
            if (cellFontColor != null) {
                HSSFPalette palette = ((HSSFWorkbook)wb).getCustomPalette();
                Color color = Color.decode(cellFontColor);
                HSSFColor cellColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                ((HSSFFont) cellFont).setColor(cellColor.getIndex());
            }

            String cellFontSize = options.getCellFontSize();
            if (cellFontSize != null) {
                cellFont.setFontHeightInPoints(Short.valueOf(cellFontSize));
            }

            String cellFontStyle = options.getCellFontStyle();
            if(cellFontStyle != null) {
                if(cellFontStyle.equalsIgnoreCase("BOLD")) {
                    cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                }
                if(cellFontStyle.equalsIgnoreCase("ITALIC")) {
                    cellFont.setItalic(true);
                }
            }
        }
        
        cellStyle.setFont(cellFont);
    }
}