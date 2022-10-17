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
package org.primefaces.component.datatable.export;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.ExcelStylesManager;
import org.primefaces.util.LocaleUtils;

public class DataTableExcelExporter extends DataTableExporter {

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
    public void doExport(FacesContext context, DataTable table, ExportConfiguration exportConfiguration, int index) throws IOException {
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
    protected void exportCells(DataTable table, Object document) {
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

    protected void addColumnFacets(DataTable table, Sheet sheet, DataTableExporter.ColumnType columnType) {
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

    protected void addTableFacets(FacesContext context, DataTable table, Sheet sheet, DataTableExporter.ColumnType columnType) {
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

    protected void addColumnValue(DataTable table, Row row, List<UIComponent> components, UIColumn column) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        FacesContext context = FacesContext.getCurrentInstance();

        exportColumn(context, table, column, components, true,
                (value) -> stylesManager.updateCell(column, cell, Objects.toString(value, Constants.EMPTY_STRING)));
    }

    protected boolean addColumnGroup(DataTable table, Sheet sheet, DataTableExporter.ColumnType columnType) {
        ColumnGroup cg = table.getColumnGroup(columnType.facet());
        if (cg == null || cg.getChildCount() == 0) {
            return false;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        table.forEachColumnGroupRow(context, cg, true, row -> {
            final AtomicInteger colIndex = new AtomicInteger(0);
            int rowIndex = sheet.getLastRowNum() + 1;
            Row xlRow = sheet.createRow(rowIndex);

            table.forEachColumn(context, row, true, true, false, column -> {
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
                    colIndex.set(calculateColumnOffset(sheet, rowIndex, colIndex.get()));
                    sheet.addMergedRegion(new CellRangeAddress(
                                rowIndex, // first row (0-based)
                                rowIndex + rowSpan, // last row (0-based)
                                colIndex.get(), // first column (0-based)
                                colIndex.get() + colSpan // last column (0-based)
                    ));
                    addColumnValue(xlRow, (short) colIndex.get(), text);
                    colIndex.set(colIndex.get() + colSpan);
                }
                else if (rowSpan > 0) {
                    sheet.addMergedRegion(new CellRangeAddress(
                                rowIndex, // first row (0-based)
                                rowIndex + rowSpan, // last row (0-based)
                                colIndex.get(), // first column (0-based)
                                colIndex.get() // last column (0-based)
                    ));
                    addColumnValue(xlRow, (short) colIndex.get(), text);
                }
                else if (colSpan > 0) {
                    colIndex.set(calculateColumnOffset(sheet, rowIndex, colIndex.get()));
                    sheet.addMergedRegion(new CellRangeAddress(
                                rowIndex, // first row (0-based)
                                rowIndex, // last row (0-based)
                                colIndex.get(), // first column (0-based)
                                colIndex.get() + colSpan // last column (0-based)
                    ));
                    addColumnValue(xlRow, (short) colIndex.get(), text);
                    colIndex.set(colIndex.get() + colSpan);
                }
                else {
                    colIndex.set(calculateColumnOffset(sheet, rowIndex, colIndex.get()));
                    addColumnValue(xlRow, (short) colIndex.get(), text);
                }
                colIndex.incrementAndGet();
                return true;
            });
            return true;
        });
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
        DataTable table = (DataTable) component;

        if (exportConfiguration.isExportHeader()) {
            addTableFacets(context, table, sheet, DataTableExporter.ColumnType.HEADER);
            boolean headerGroup = addColumnGroup(table, sheet, DataTableExporter.ColumnType.HEADER);
            if (!headerGroup) {
                addColumnFacets(table, sheet, DataTableExporter.ColumnType.HEADER);
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
            addColumnGroup(table, sheet, DataTableExporter.ColumnType.FOOTER);
            if (table.hasFooterColumn()) {
                addColumnFacets(table, sheet, DataTableExporter.ColumnType.FOOTER);
            }
            addTableFacets(context, table, sheet, DataTableExporter.ColumnType.FOOTER);
        }

        table.setRowIndex(-1);
    }

    protected void applyOptions(Workbook wb, DataTable table, Sheet sheet, ExporterOptions options) {
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
