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

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.component.export.PDFOrientationType;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class DataTablePDFExporter extends DataTableExporter {

    private Font cellFont;
    private Font facetFont;
    private Color facetBgColor;
    private Document document;

    protected Document createDocument() {
        return new Document();
    }

    protected Document getDocument() {
        return document;
    }

    @Override
    protected void preExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        document = createDocument();

        try {
            PDFOptions options = (PDFOptions) exportConfiguration.getOptions();
            if (options != null) {
                if (PDFOrientationType.LANDSCAPE == options.getOrientation()) {
                    document.setPageSize(PageSize.A4.rotate());
                }
            }

            PdfWriter.getInstance(document, getOutputStream());
        }
        catch (DocumentException e) {
            throw new IOException(e);
        }

        if (exportConfiguration.getPreProcessor() != null) {
            exportConfiguration.getPreProcessor().invoke(context.getELContext(), new Object[]{document});
        }

        if (!document.isOpen()) {
            document.open();
        }
    }

    @Override
    protected void doExport(FacesContext context, DataTable table, ExportConfiguration exportConfiguration, int index) throws IOException {
        try {
            // Add empty paragraph between each exported tables
            if (index > 0) {
                Paragraph preface = new Paragraph();
                addEmptyLine(preface, 3);
                getDocument().add(preface);
            }

            getDocument().add(exportTable(context, table, exportConfiguration));
        }
        catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected void postExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        if (exportConfiguration.getPostProcessor() != null) {
            exportConfiguration.getPostProcessor().invoke(context.getELContext(), new Object[]{document});
        }

        getDocument().close();

        document = null;
    }


    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public String getFileExtension() {
        return ".pdf";
    }

    protected PdfPTable exportTable(FacesContext context, DataTable table, ExportConfiguration config) {
        int columnsCount = getColumnsCount(table);
        PdfPTable pdfTable = new PdfPTable(columnsCount);

        ExporterOptions options = config.getOptions();
        if (options != null) {
            applyFont(options.getFontName(), config.getEncodingType());
            applyFacetOptions(options);
            applyCellOptions(options);
        }
        else {
            applyFont(FontFactory.TIMES, config.getEncodingType());
        }

        if (config.getOnTableRender() != null) {
            config.getOnTableRender().invoke(context.getELContext(), new Object[]{pdfTable, table});
        }

        if (config.isExportHeader()) {
            addTableFacets(context, table, pdfTable, ColumnType.HEADER);
            boolean headerGroup = addColumnGroup(table, pdfTable, ColumnType.HEADER);
            if (!headerGroup) {
                addColumnFacets(table, pdfTable, ColumnType.HEADER);
            }
        }

        if (config.isPageOnly()) {
            exportPageOnly(context, table, pdfTable);
        }
        else if (config.isSelectionOnly()) {
            exportSelectionOnly(context, table, pdfTable);
        }
        else {
            exportAll(context, table, pdfTable);
        }

        if (config.isExportFooter()) {
            addColumnGroup(table, pdfTable, ColumnType.FOOTER);
            if (table.hasFooterColumn()) {
                addColumnFacets(table, pdfTable, ColumnType.FOOTER);
            }
            addTableFacets(context, table, pdfTable, ColumnType.FOOTER);
        }

        table.setRowIndex(-1);

        return pdfTable;
    }

    protected void addTableFacets(FacesContext context, DataTable table, PdfPTable pdfTable, ColumnType columnType) {
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

            PdfPCell cell = new PdfPCell(new Paragraph(facetText, facetFont));
            if (facetBgColor != null) {
                cell.setBackgroundColor(facetBgColor);
            }

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(colspan);
            pdfTable.addCell(cell);
        }
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        PdfPTable pdfTable = (PdfPTable) document;
        for (UIColumn col : getExportableColumns(table)) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            addColumnValue(table, pdfTable, col.getChildren(), cellFont, col);
        }
    }

    protected void addColumnFacets(DataTable table, PdfPTable pdfTable, ColumnType columnType) {
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
                addColumnValue(pdfTable, textValue, 1, 1);
            }
            else if (ComponentUtils.shouldRenderFacet(facet)) {
                addColumnValue(pdfTable, facet);
            }
            else {
                addColumnValue(pdfTable, Constants.EMPTY_STRING, 1, 1);
            }
        }
    }

    protected boolean addColumnGroup(DataTable table, PdfPTable pdfTable,  ColumnType columnType) {
        ColumnGroup cg = table.getColumnGroup(columnType.facet());
        if (cg == null || cg.getChildCount() == 0) {
            return false;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        table.forEachColumnGroupRow(context, cg, true, row -> {
            table.forEachColumn(context, row, true, true, false, column -> {
                if (column.isRendered() && column.isExportable()) {
                    String textValue;
                    switch (columnType) {
                        case HEADER:
                            textValue = (column.getExportHeaderValue() != null) ? column.getExportHeaderValue() : column.getHeaderText();
                            break;

                        case FOOTER:
                            textValue = (column.getExportFooterValue() != null) ? column.getExportFooterValue() : column.getFooterText();
                            break;

                        default:
                            textValue = Constants.EMPTY_STRING;
                            break;
                    }

                    int rowSpan = column.getRowspan();
                    int colSpan = column.getColspan();
                    addColumnValue(pdfTable, textValue, rowSpan, colSpan);
                }
                return true;
            });

            pdfTable.completeRow();
            return true;
        });
        return true;
    }

    protected void addColumnValue(PdfPTable pdfTable, UIComponent component) {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        addColumnValue(pdfTable, value, 1, 1);
    }

    protected PdfPCell addColumnValue(PdfPTable pdfTable, String value, int rowSpan, int colSpan) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, facetFont));
        if (facetBgColor != null) {
            cell.setBackgroundColor(facetBgColor);
        }
        if (rowSpan > 1) {
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setRowspan(rowSpan);

        }
        if (colSpan > 1) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(colSpan);
        }

        pdfTable.addCell(cell);
        return cell;
    }

    protected void addColumnValue(DataTable table, PdfPTable pdfTable, List<UIComponent> components, Font font, UIColumn column) {
        FacesContext context = FacesContext.getCurrentInstance();
        exportColumn(context, table, column, components, true, (s) -> {
            PdfPCell cell = createCell(column, new Paragraph(s, font));
            pdfTable.addCell(cell);
        });
    }

    protected int getColumnsCount(DataTable table) {
        return getExportableColumns(table).size();
    }

    protected void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    protected void applyFacetOptions(ExporterOptions options) {
        String facetBackground = options.getFacetBgColor();
        if (facetBackground != null) {
            facetBgColor = Color.decode(facetBackground);
        }

        String facetFontColor = options.getFacetFontColor();
        if (facetFontColor != null) {
            facetFont.setColor(Color.decode(facetFontColor));
        }

        String facetFontSize = options.getFacetFontSize();
        if (facetFontSize != null) {
            facetFont.setSize(Integer.valueOf(facetFontSize));
        }

        String facetFontStyle = options.getFacetFontStyle();
        setFontStyle(facetFont, facetFontStyle);
    }

    protected void applyCellOptions(ExporterOptions options) {
        String cellFontColor = options.getCellFontColor();
        if (cellFontColor != null) {
            cellFont.setColor(Color.decode(cellFontColor));
        }

        String cellFontSize = options.getCellFontSize();
        if (cellFontSize != null) {
            cellFont.setSize(Integer.valueOf(cellFontSize));
        }

        String cellFontStyle = options.getCellFontStyle();
        setFontStyle(cellFont, cellFontStyle);
    }

    protected void setFontStyle(Font cellFont, String cellFontStyle) {
        if (cellFontStyle != null) {
            if ("NORMAL".equalsIgnoreCase(cellFontStyle)) {
                cellFontStyle = "" + Font.NORMAL;
            }
            if ("BOLD".equalsIgnoreCase(cellFontStyle)) {
                cellFontStyle = "" + Font.BOLD;
            }
            if ("ITALIC".equalsIgnoreCase(cellFontStyle)) {
                cellFontStyle = "" + Font.ITALIC;
            }

            cellFont.setStyle(cellFontStyle);
        }
    }

    protected void applyFont(String fontName, String encoding) {
        String newFont = fontName;
        if (LangUtils.isBlank(newFont)) {
            newFont = FontFactory.TIMES;
        }
        cellFont = FontFactory.getFont(newFont, encoding);
        facetFont = FontFactory.getFont(newFont, encoding, Font.DEFAULTSIZE, Font.BOLD);
    }

    protected PdfPCell createCell(UIColumn column, Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        return applyColumnAlignments(column, cell);
    }

    protected PdfPCell applyColumnAlignments(UIColumn column, PdfPCell cell) {
        String[] styles = new String[] {column.getStyle(), column.getStyleClass()};
        if (LangUtils.containsIgnoreCase(styles, "right")) {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
        else  if (LangUtils.containsIgnoreCase(styles, "center")) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        else {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }
        return cell;
    }
}
