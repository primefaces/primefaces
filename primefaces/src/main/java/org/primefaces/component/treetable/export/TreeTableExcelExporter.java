/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.treetable.export;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.util.*;
import org.primefaces.util.ExcelStylesManager;


public class TreeTableExcelExporter extends TreeTableExporter {

    protected Workbook wb;
    private ExcelStylesManager stylesManager;

    @Override
    protected void preExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        wb = createWorkBook();

        if (exportConfiguration.getPreProcessor() != null) {
            exportConfiguration.getPreProcessor().invoke(context.getELContext(), new Object[]{wb});
        }
    }

    @Override
    public void doExport(FacesContext context, TreeTable table, ExportConfiguration exportConfiguration, int index) throws IOException {
        String sheetName = getSheetName(context, table);
        if (sheetName == null) {
            sheetName = table.getId() + (index + 1);
        }

        sheetName = WorkbookUtil.createSafeSheetName(sheetName);
        if ("empty".equals(sheetName) || "null".equals(sheetName)) {
            sheetName = "Sheet (" + (index + 1) + ")";
        }

        ExcelOptions options = (ExcelOptions) exportConfiguration.getOptions();
        stylesManager = new ExcelStylesManager(wb, LocaleUtils.getCurrentLocale(context), options);
        Sheet sheet = createSheet(wb, sheetName, options);
        applyOptions(wb, table, sheet, options);
        exportTable(context, table, sheet, exportConfiguration);

        if (options == null || options.isAutoSizeColumn()) {
            for (int i = 0; i < getExportableColumns(table).size(); i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    @Override
    protected void postExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        if (exportConfiguration.getPostProcessor() != null) {
            exportConfiguration.getPostProcessor().invoke(context.getELContext(), new Object[]{wb});
        }

        wb.write(getOutputStream());

        wb.close();
        wb = null;
    }

    @Override
    protected void exportCells(TreeTable table, Object document) {
        Sheet sheet = (Sheet) document;
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(sheetRowIndex);

        for (UIColumn col : getExportableColumns(table)) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            addColumnValue(table, row, col.getChildren(), col);
        }
    }

    protected void addColumnFacets(TreeTable table, Sheet sheet, TreeTableExporter.ColumnType columnType) {
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row rowHeader = sheet.createRow(sheetRowIndex);

        for (UIColumn col : getExportableColumns(table)) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            UIComponent facet = col.getFacet(columnType.facet());
            String textValue;
            switch (columnType) {
                case HEADER:
                    textValue = (col.getExportHeaderValue() != null) ? col.getExportHeaderValue() : col.getHeaderText();
                    break;

                case FOOTER:
                    textValue = (col.getExportFooterValue() != null) ? col.getExportFooterValue() : col.getFooterText();
                    break;

                default:
                    textValue = null;
                    break;
            }

            if (textValue != null) {
                addColumnValue(rowHeader, textValue);
            }
            else if (ComponentUtils.shouldRenderFacet(facet)) {
                addColumnValue(rowHeader, facet);
            }
            else {
                addColumnValue(rowHeader, Constants.EMPTY_STRING);
            }
        }
    }

    protected void addTableFacets(FacesContext context, TreeTable table, Sheet sheet, TreeTableExporter.ColumnType columnType) {
        String facetText = null;
        UIComponent facet = table.getFacet(columnType.facet());
        if (ComponentUtils.shouldRenderFacet(facet)) {
            if (facet instanceof UIPanel) {
                for (UIComponent child : facet.getChildren()) {
                    if (child.isRendered()) {
                        String value = ComponentUtils.getValueToRender(context, child);

                        if (value != null) {
                            facetText = value;
                            break;
                        }
                    }
                }
            }
            else {
                facetText = ComponentUtils.getValueToRender(context, facet);
            }
        }

        if (facetText != null) {
            int colspan = getExportableColumns(table).size();
            int rowIndex = sheet.getLastRowNum() + 1;
            Row rowHeader = sheet.createRow(rowIndex);

            if (colspan > 1) {
                sheet.addMergedRegion(new CellRangeAddress(
                            rowIndex, // first row (0-based)
                            rowIndex, // last row (0-based)
                            0, // first column (0-based)
                            colspan - 1 // last column (0-based)
                ));
            }
            addColumnValue(rowHeader, 0, facetText);
        }
    }

    protected void addColumnValue(Row row, UIComponent component) {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        addColumnValue(row, value);
    }

    protected void addColumnValue(Row row, String value) {
        int col = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        addColumnValue(row, col, value);
    }

    protected void addColumnValue(Row row, int col, String text) {
        Cell cell = row.createCell(col);
        cell.setCellValue(stylesManager.createRichTextString(text));
        cell.setCellStyle(stylesManager.getFacetStyle());
    }

    protected void addColumnValue(TreeTable table, Row row, List<UIComponent> components, UIColumn column) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        FacesContext context = FacesContext.getCurrentInstance();

        exportColumn(context, table, column, components, true,
                (value) -> stylesManager.updateCell(column, cell, Objects.toString(value, Constants.EMPTY_STRING)));
    }

    protected boolean addColumnGroup(TreeTable table, Sheet sheet, TreeTableExporter.ColumnType columnType) {
        ColumnGroup cg = table.getColumnGroup(columnType.facet());
        if (cg == null || cg.getChildCount() == 0) {
            return false;
        }
        for (UIComponent component : cg.getChildren()) {
            if (!(component instanceof org.primefaces.component.row.Row)) {
                continue;
            }
            org.primefaces.component.row.Row row = (org.primefaces.component.row.Row) component;
            int rowIndex = sheet.getLastRowNum() + 1;
            Row xlRow = sheet.createRow(rowIndex);
            int colIndex = 0;
            for (UIComponent rowComponent : row.getChildren()) {
                if (!(rowComponent instanceof UIColumn)) {
                    // most likely a ui:repeat which won't work here
                    continue;
                }
                UIColumn column = (UIColumn) rowComponent;
                if (!column.isRendered() || !column.isExportable()) {
                    continue;
                }

                String text = null;
                switch (columnType) {
                    case HEADER:
                        text = (column.getExportHeaderValue() != null) ? column.getExportHeaderValue() : column.getHeaderText();
                        break;

                    case FOOTER:
                        text = (column.getExportFooterValue() != null) ? column.getExportFooterValue() : column.getFooterText();
                        break;

                    default:
                        text = null;
                        break;
                }

                // by default column has 1 rowspan && colspan
                int rowSpan = column.getRowspan() - 1;
                int colSpan = column.getColspan() - 1;

                if (rowSpan > 0 && colSpan > 0) {
                    colIndex = calculateColumnOffset(sheet, rowIndex, colIndex);
                    sheet.addMergedRegion(new CellRangeAddress(
                                rowIndex, // first row (0-based)
                                rowIndex + rowSpan, // last row (0-based)
                                colIndex, // first column (0-based)
                                colIndex + colSpan // last column (0-based)
                    ));
                    addColumnValue(xlRow, (short) colIndex, text);
                    colIndex = colIndex + colSpan;
                }
                else if (rowSpan > 0) {
                    sheet.addMergedRegion(new CellRangeAddress(
                                rowIndex, // first row (0-based)
                                rowIndex + rowSpan, // last row (0-based)
                                colIndex, // first column (0-based)
                                colIndex // last column (0-based)
                    ));
                    addColumnValue(xlRow, (short) colIndex, text);
                }
                else if (colSpan > 0) {
                    colIndex = calculateColumnOffset(sheet, rowIndex, colIndex);
                    sheet.addMergedRegion(new CellRangeAddress(
                                rowIndex, // first row (0-based)
                                rowIndex, // last row (0-based)
                                colIndex, // first column (0-based)
                                colIndex + colSpan // last column (0-based)
                    ));
                    addColumnValue(xlRow, (short) colIndex, text);
                    colIndex = colIndex + colSpan;
                }
                else {
                    colIndex = calculateColumnOffset(sheet, rowIndex, colIndex);
                    addColumnValue(xlRow, (short) colIndex, text);
                }
                colIndex++;
            }
        }
        return true;
    }

    protected int calculateColumnOffset(Sheet sheet, int row, int col) {
        for (int j = 0; j < sheet.getNumMergedRegions(); j++) {
            CellRangeAddress merged = sheet.getMergedRegion(j);
            if (merged.isInRange(row, col)) {
                col = merged.getLastColumn() + 1;
            }
        }
        return col;
    }

    protected Workbook createWorkBook() {
        return new HSSFWorkbook();
    }

    protected Workbook getWorkBook() {
        return wb;
    }

    protected Sheet createSheet(Workbook wb, String sheetName, ExcelOptions options) {
        return wb.createSheet(sheetName);
    }

    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    @Override
    public String getFileExtension() {
        return ".xls";
    }

    public void exportTable(FacesContext context, UIComponent component, Sheet sheet, ExportConfiguration exportConfiguration) {
        TreeTable table = (TreeTable) component;

        if (exportConfiguration.isExportHeader()) {
            addTableFacets(context, table, sheet, TreeTableExporter.ColumnType.HEADER);
            boolean headerGroup = addColumnGroup(table, sheet, TreeTableExporter.ColumnType.HEADER);
            if (!headerGroup) {
                addColumnFacets(table, sheet, TreeTableExporter.ColumnType.HEADER);
            }
        }

        if (exportConfiguration.isPageOnly()) {
            exportPageOnly(context, table, sheet);
        }
        else if (exportConfiguration.isSelectionOnly()) {
            exportSelectionOnly(context, table, sheet);
        }
        else {
            exportAll(context, table, sheet);
        }

        if (exportConfiguration.isExportFooter()) {
            addColumnGroup(table, sheet, TreeTableExporter.ColumnType.FOOTER);
            if (table.hasFooterColumn()) {
                addColumnFacets(table, sheet, TreeTableExporter.ColumnType.FOOTER);
            }
            addTableFacets(context, table, sheet, TreeTableExporter.ColumnType.FOOTER);
        }
    }

    protected void applyOptions(Workbook wb, TreeTable table, Sheet sheet, ExporterOptions options) {
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setPrintGridlines(true);
    }

    public String getSheetName(FacesContext context, UIComponent table) {
        UIComponent header = table.getFacet("header");
        if (header != null) {
            if (header instanceof UIPanel) {
                for (UIComponent child : header.getChildren()) {
                    if (child.isRendered()) {
                        String value = ComponentUtils.getValueToRender(context, child);

                        if (value != null) {
                            return value;
                        }
                    }
                }
            }
            else {
                return ComponentUtils.getValueToRender(context, header);
            }
        }

        return null;
    }
}
