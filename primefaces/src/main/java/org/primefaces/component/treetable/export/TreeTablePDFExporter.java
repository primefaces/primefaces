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
package org.primefaces.component.treetable.export;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.export.ColumnValue;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.component.export.PDFOrientationType;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.util.Constants;
import org.primefaces.util.IOUtils;
import org.primefaces.util.LangUtils;

import java.awt.Color;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import jakarta.faces.context.FacesContext;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TreeTablePDFExporter extends TreeTableExporter<Document, PDFOptions> {

    private static final Logger LOGGER = Logger.getLogger(TreeTablePDFExporter.class.getName());

    private Font cellFont;
    private Font facetFont;
    private Color facetBgColor;
    private PdfPTable pdfTable;

    public TreeTablePDFExporter() {
        super(new PDFOptions());
    }

    @Override
    protected Document createDocument(FacesContext context) throws IOException {
        Document document = new Document();
        PDFOptions options = (PDFOptions) exportConfiguration.getOptions();

        if (options != null && PDFOrientationType.LANDSCAPE == options.getOrientation()) {
            document.setPageSize(PageSize.A4.rotate());
        }

        try {
            PdfWriter.getInstance(document, os());
        }
        catch (DocumentException e) {
            IOUtils.closeQuietly(document, e1 -> LOGGER.warning(e1.getMessage()));
            throw new IOException(e);
        }

        if (!document.isOpen()) {
            document.open();
        }

        String encoding = Objects.toString(exportConfiguration.getEncodingType(), BaseFont.IDENTITY_H);
        if (options != null) {
            applyFont(options.getFontName(), encoding);
            applyFacetOptions(options);
            applyCellOptions(options);
        }
        else {
            applyFont(FontFactory.TIMES, encoding);
        }

        return document;
    }

    @Override
    protected void exportTable(FacesContext context, TreeTable table, int index) throws IOException {
        try {
            // Add empty paragraph between each exported tables
            if (index > 0) {
                Paragraph preface = new Paragraph();
                addEmptyLine(preface, 3);
                document.add(preface);
            }

            int columnsCount = getExportableColumns(table).size();
            pdfTable = new PdfPTable(columnsCount);
            super.exportTable(context, table, index);
            document.add(pdfTable);
        }
        catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected void exportTabletFacetValue(FacesContext context, TreeTable table, String textValue) {
        int colspan = getExportableColumns(table).size();
        addFacetValue(1, colspan, ColumnValue.of(textValue));
    }

    @Override
    protected void exportColumnFacetValue(FacesContext context, TreeTable table, ColumnValue columnValue, int index) {
        addFacetValue(1, 1, columnValue);
    }

    @Override
    protected void exportColumnGroupFacetValue(FacesContext context, TreeTable table, UIColumn column,
                                               AtomicInteger colIndex, ColumnValue columnValue) {
        int rowSpan = column.getExportRowspan() != 0 ? column.getExportRowspan() : column.getRowspan();
        int colSpan = column.getExportColspan() != 0 ? column.getExportColspan() : column.getColspan();
        addFacetValue(rowSpan, colSpan, columnValue);
    }

    @Override
    protected void exportCellValue(FacesContext context, TreeTable table, UIColumn col, ColumnValue columnValue, int index) {
        PdfPCell cell = createCell(col, new Paragraph(columnValue.toString(), cellFont));
        addCell(pdfTable, cell);
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public String getFileExtension() {
        return ".pdf";
    }

    @Override
    protected Object[] getOnTableRenderArgs() {
        return new Object[]{document, pdfTable};
    }

    // -- UTILS --

    protected void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(Constants.SPACE));
        }
    }

    protected void addCell(PdfPTable table, PdfPCell cell) {
        table.addCell(cell);
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
            facetFont.setSize(Integer.parseInt(facetFontSize));
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
            cellFont.setSize(Integer.parseInt(cellFontSize));
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
        cellFont = FontFactory.getFont(newFont, encoding, BaseFont.EMBEDDED);
        facetFont = FontFactory.getFont(newFont, encoding, BaseFont.EMBEDDED, Font.DEFAULTSIZE, Font.BOLD);
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

    protected void addFacetValue(int rowSpan, int colSpan, ColumnValue columnValue) {
        PdfPCell cell = new PdfPCell(new Paragraph(columnValue.toString(), facetFont));
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

        addCell(pdfTable, cell);
    }
}
