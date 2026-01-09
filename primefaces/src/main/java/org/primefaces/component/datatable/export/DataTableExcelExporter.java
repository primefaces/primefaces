/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.datatable.export;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ColumnValue;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.util.ExcelStylesManager;
import org.primefaces.util.LocaleUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

public class DataTableExcelExporter extends DataTableExporter<Workbook, ExcelOptions> {

    private ExcelStylesManager stylesManager;

    public DataTableExcelExporter() {
        super(new ExcelOptions());
    }

    @Override
    protected void preExport(FacesContext context) throws IOException {
        super.preExport(context);
        stylesManager = ExcelStylesManager.createExcelStylesManager(document, LocaleUtils.getCurrentLocale(context), options());
    }

    @Override
    protected Workbook createDocument(FacesContext context) throws IOException {
        return new HSSFWorkbook();
    }

    @Override
    protected void exportTable(FacesContext context, DataTable table, int index) throws IOException {
        Sheet sheet = createSheet(context, table, index);

        super.exportTable(context, table, index);

        autoSizeColumn(table, sheet);
    }

    @Override
    protected void preRowExport(FacesContext context, DataTable table) {
        Sheet sheet = sheet();
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        sheet.createRow(sheetRowIndex);
    }

    @Override
    protected void postExport(FacesContext context) throws IOException {
        super.postExport(context);
        document.write(os());
    }

    @Override
    protected void exportTabletFacetValue(FacesContext context, DataTable table, String textValue) {
        Sheet sheet = sheet();
        int rowIndex = sheet().getLastRowNum();
        int colspan = getExportableColumns(table).size();

        if (colspan > 1) {
            sheet.addMergedRegion(new CellRangeAddress(
                    rowIndex, // first row (0-based)
                    rowIndex, // last row (0-based)
                    0, // first column (0-based)
                    colspan - 1 // last column (0-based)
            ));
        }

        exportColumnFacetValue(context, table, ColumnValue.of(textValue), 0);
    }

    @Override
    protected void exportColumnFacetValue(FacesContext context, DataTable table, ColumnValue columnValue, int index) {
        Cell cell = row().createCell(index);
        stylesManager.updateFacetCell(cell, columnValue);
    }

    @Override
    protected void exportCellValue(FacesContext context, DataTable table, UIColumn col, ColumnValue columnValue, int i) {
        Cell cell = row().createCell(i);
        stylesManager.updateCell(col, cell, columnValue);
    }

    @Override
    protected void exportColumnGroupFacetValue(FacesContext context, DataTable table, UIColumn column,
                                               AtomicInteger colIndex, ColumnValue columnValue) {
        Sheet sheet = sheet();
        int rowIndex = sheet.getLastRowNum();

        // by default column has 1 rowspan && colspan
        int rowSpan = (column.getExportRowspan() != 0 ? column.getExportRowspan() : column.getRowspan()) - 1;
        int colSpan = (column.getExportColspan() != 0 ? column.getExportColspan() : column.getColspan()) - 1;

        if (rowSpan > 0 && colSpan > 0) {
            colIndex.set(calculateColumnOffset(sheet, rowIndex, colIndex.get()));
            sheet.addMergedRegion(new CellRangeAddress(
                    rowIndex, // first row (0-based)
                    rowIndex + rowSpan, // last row (0-based)
                    colIndex.get(), // first column (0-based)
                    colIndex.get() + colSpan // last column (0-based)
            ));
            exportColumnFacetValue(context, table, columnValue, (short) colIndex.get());
            colIndex.set(colIndex.get() + colSpan);
        }
        else if (rowSpan > 0) {
            sheet.addMergedRegion(new CellRangeAddress(
                    rowIndex, // first row (0-based)
                    rowIndex + rowSpan, // last row (0-based)
                    colIndex.get(), // first column (0-based)
                    colIndex.get() // last column (0-based)
            ));
            exportColumnFacetValue(context, table, columnValue, (short) colIndex.get());
        }
        else if (colSpan > 0) {
            colIndex.set(calculateColumnOffset(sheet, rowIndex, colIndex.get()));
            sheet.addMergedRegion(new CellRangeAddress(
                    rowIndex, // first row (0-based)
                    rowIndex, // last row (0-based)
                    colIndex.get(), // first column (0-based)
                    colIndex.get() + colSpan // last column (0-based)
            ));
            exportColumnFacetValue(context, table, columnValue, (short) colIndex.get());
            colIndex.set(colIndex.get() + colSpan);
        }
        else {
            colIndex.set(calculateColumnOffset(sheet, rowIndex, colIndex.get()));
            exportColumnFacetValue(context, table, columnValue, (short) colIndex.get());
        }
    }

    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    @Override
    public String getFileExtension() {
        return ".xls";
    }

    protected void autoSizeColumn(DataTable table, Sheet sheet) {
        ExcelOptions options = (ExcelOptions) exportConfiguration.getOptions();
        if (options == null || options.isAutoSizeColumn()) {
            for (int i = 0; i < getExportableColumns(table).size(); i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    // -- UTILS --

    protected int calculateColumnOffset(Sheet sheet, int row, int col) {
        for (int j = 0; j < sheet.getNumMergedRegions(); j++) {
            CellRangeAddress merged = sheet.getMergedRegion(j);
            if (merged.isInRange(row, col)) {
                col = merged.getLastColumn() + 1;
                return calculateColumnOffset(sheet, row, col);
            }
        }
        return col;
    }

    protected Sheet createSheet(FacesContext context, DataTable table, int index) {
        String sheetName = getSheetName(context, table, index);
        Sheet sheet = document.createSheet(sheetName);
        applyOptions(sheet);
        return sheet;
    }

    protected void applyOptions(Sheet sheet) {
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setPrintGridlines(true);
    }

    public String getSheetName(FacesContext context, UIComponent table, int index) {
        String sheetName = getComponentFacetValue(context, table, "header");
        if (sheetName == null) {
            sheetName = table.getId() + (index + 1);
        }

        sheetName = WorkbookUtil.createSafeSheetName(sheetName);
        if ("empty".equals(sheetName) || "null".equals(sheetName)) {
            sheetName = "Sheet (" + (index + 1) + ")";
        }

        return sheetName;
    }

    protected Row row() {
        Sheet sheet = sheet();
        return sheet.getRow(sheet.getLastRowNum());
    }

    protected Sheet sheet() {
        if (document.getNumberOfSheets() == 0) {
            throw new IllegalStateException("No existing sheet");
        }

        return document.getSheetAt(document.getNumberOfSheets() - 1);
    }
}
