/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
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


public class TreeTableExcelExporter extends TreeTableExporter {

    protected static final String DEFAULT_FONT = HSSFFont.FONT_ARIAL;
    protected Workbook wb;
    private CellStyle cellStyleRightAlign;
    private CellStyle cellStyleCenterAlign;
    private CellStyle cellStyleLeftAlign;
    private CellStyle facetStyle;
    private CellStyle currencyStyle;
    private boolean stronglyTypedCells;
    private Locale locale;

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
        if (options == null) {
            stronglyTypedCells = true;
        }
        else {
            stronglyTypedCells = options.isStronglyTypedCells();
        }
        if (stronglyTypedCells) {
            locale = LocaleUtils.getCurrentLocale(context);
        }
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

            sheet.addMergedRegion(new CellRangeAddress(
                        rowIndex, // first row (0-based)
                        rowIndex, // last row (0-based)
                        0, // first column (0-based)
                        colspan - 1 // last column (0-based)
            ));
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
        cell.setCellValue(createRichTextString(text));
        cell.setCellStyle(facetStyle);
    }

    protected void addColumnValue(TreeTable table, Row row, List<UIComponent> components, UIColumn column) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        FacesContext context = FacesContext.getCurrentInstance();

        applyColumnAlignments(column, cell);

        exportColumn(context, table, column, components, true, (s) -> {
            updateCell(cell, Objects.toString(s, Constants.EMPTY_STRING));
        });
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

    /**
     * If ExcelOptions.isStronglyTypedCells = true then for cells check:
     * <pre>
     * Numeric - String that are all numbers make them a numeric cell
     * Currency - Convert to currency cell so math can be done in Excel
     * String - fallback to just a normal string cell
     * </pre>
     * Possible future enhancement of Date cells as well.
     *
     * @param cell the cell to operate on
     * @param value the String value to put in the cell
     */
    protected void updateCell(Cell cell, String value) {
        boolean printed = false;
        if (stronglyTypedCells) {
            if (LangUtils.isNumeric(value)) {
                cell.setCellValue(Double.parseDouble(value));
                printed = true;
            }

            if (!printed) {
                Number currency = CurrencyValidator.getInstance().validate(value, locale);
                if (currency != null) {
                    cell.setCellValue(currency.doubleValue());
                    cell.setCellStyle(currencyStyle);
                    printed = true;
                }
            }
        }

        // fall back to just printing the string value
        if (!printed) {
            cell.setCellValue(createRichTextString(value));
        }
    }

    protected RichTextString createRichTextString(String value) {
        return new HSSFRichTextString(value);
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
        addTableFacets(context, table, sheet, TreeTableExporter.ColumnType.HEADER);
        boolean headerGroup = addColumnGroup(table, sheet, TreeTableExporter.ColumnType.HEADER);
        if (!headerGroup) {
            addColumnFacets(table, sheet, TreeTableExporter.ColumnType.HEADER);
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

        addColumnGroup(table, sheet, TreeTableExporter.ColumnType.FOOTER);
        if (table.hasFooterColumn()) {
            addColumnFacets(table, sheet, TreeTableExporter.ColumnType.FOOTER);
        }
        addTableFacets(context, table, sheet, TreeTableExporter.ColumnType.FOOTER);
    }

    protected void applyOptions(Workbook wb, TreeTable table, Sheet sheet, ExporterOptions options) {
        Font font = getFont(wb, options);

        facetStyle = wb.createCellStyle();
        facetStyle.setFont(font);
        facetStyle.setAlignment(HorizontalAlignment.CENTER);
        facetStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        facetStyle.setWrapText(true);
        applyFacetOptions(wb, options, facetStyle);

        cellStyleLeftAlign = wb.createCellStyle();
        cellStyleLeftAlign.setFont(font);
        cellStyleLeftAlign.setAlignment(HorizontalAlignment.LEFT);
        applyCellOptions(wb, options, cellStyleLeftAlign);

        cellStyleCenterAlign = wb.createCellStyle();
        cellStyleCenterAlign.setFont(font);
        cellStyleCenterAlign.setAlignment(HorizontalAlignment.CENTER);
        applyCellOptions(wb, options, cellStyleCenterAlign);

        cellStyleRightAlign = wb.createCellStyle();
        cellStyleRightAlign.setFont(font);
        cellStyleRightAlign.setAlignment(HorizontalAlignment.RIGHT);
        applyCellOptions(wb, options, cellStyleRightAlign);

        if (stronglyTypedCells) {
            currencyStyle = wb.createCellStyle();
            currencyStyle.setFont(font);
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            String pattern = CurrencyValidator.getInstance().getPattern(locale);
            short currencyPattern = wb.getCreationHelper().createDataFormat().getFormat(pattern);
            currencyStyle.setDataFormat(currencyPattern);
            applyCellOptions(wb, options, currencyStyle);
        }

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setPrintGridlines(true);
    }

    protected void applyFacetOptions(Workbook wb, ExporterOptions options, CellStyle facetStyle) {
        Font facetFont = getFont(wb, options);

        if (options != null) {
            String facetFontStyle = options.getFacetFontStyle();
            if (facetFontStyle != null) {
                if ("BOLD".equalsIgnoreCase(facetFontStyle)) {
                    facetFont.setBold(true);
                }
                if ("ITALIC".equalsIgnoreCase(facetFontStyle)) {
                    facetFont.setItalic(true);
                }
            }

            HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
            Color color = null;

            String facetBackground = options.getFacetBgColor();
            if (facetBackground != null) {
                color = Color.decode(facetBackground);
                HSSFColor backgroundColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                facetStyle.setFillForegroundColor(backgroundColor.getIndex());
                facetStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }

            String facetFontColor = options.getFacetFontColor();
            if (facetFontColor != null) {
                color = Color.decode(facetFontColor);
                HSSFColor facetColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                facetFont.setColor(facetColor.getIndex());
            }

            String facetFontSize = options.getFacetFontSize();
            if (facetFontSize != null) {
                facetFont.setFontHeightInPoints(Short.valueOf(facetFontSize));
            }
        }

        facetStyle.setFont(facetFont);
    }

    protected void applyCellOptions(Workbook wb, ExporterOptions options, CellStyle cellStyle) {
        Font cellFont = getFont(wb, options);

        if (options != null) {
            String cellFontColor = options.getCellFontColor();
            if (cellFontColor != null) {
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                Color color = Color.decode(cellFontColor);
                HSSFColor cellColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                cellFont.setColor(cellColor.getIndex());
            }

            String cellFontSize = options.getCellFontSize();
            if (cellFontSize != null) {
                cellFont.setFontHeightInPoints(Short.valueOf(cellFontSize));
            }

            String cellFontStyle = options.getCellFontStyle();
            if (cellFontStyle != null) {
                if ("BOLD".equalsIgnoreCase(cellFontStyle)) {
                    cellFont.setBold(true);
                }
                if ("ITALIC".equalsIgnoreCase(cellFontStyle)) {
                    cellFont.setItalic(true);
                }
            }
        }

        cellStyle.setFont(cellFont);
    }

    protected Cell applyColumnAlignments(UIColumn column, Cell cell) {
        if (cell.getCellStyle() != null) {
            // don't apply style if cell already has one
            return cell;
        }
        String[] styles = new String[] {column.getStyle(), column.getStyleClass()};
        if (LangUtils.containsIgnoreCase(styles, "right")) {
            cell.setCellStyle(cellStyleRightAlign);
        }
        else  if (LangUtils.containsIgnoreCase(styles, "center")) {
            cell.setCellStyle(cellStyleCenterAlign);
        }
        else {
            cell.setCellStyle(cellStyleLeftAlign);
        }
        return cell;
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

    public Font getFont(Workbook wb, ExporterOptions options) {
        Font font = wb.createFont();

        if (options != null) {
            String fontName = LangUtils.isValueBlank(options.getFontName()) ? DEFAULT_FONT : options.getFontName();
            font.setFontName(fontName);
        }
        else {
            font.setFontName(DEFAULT_FONT);
        }
        return font;
    }
}
