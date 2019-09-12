/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class DataTablePDFExporter extends DataTableExporter {

    private Font cellFont;
    private Font facetFont;
    private Color facetBgColor;
    private ExportConfiguration config;
    private Document document;
    private ByteArrayOutputStream baos;

    @Override
    protected void preExport(FacesContext context, ExportConfiguration config) throws IOException {
        document = new Document();
        baos = new ByteArrayOutputStream();
        this.config = config;

        try {
            PdfWriter.getInstance(document, baos);
        }
        catch (DocumentException e) {
            throw new IOException(e);
        }

        if (config.getPreProcessor() != null) {
            config.getPreProcessor().invoke(context.getELContext(), new Object[]{document});
        }

        if (!document.isOpen()) {
            document.open();
        }
    }

    @Override
    protected void doExport(FacesContext context, DataTable table, ExportConfiguration config) throws IOException {
        try {
            document.add(exportTable(context, table, config));
            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 3);
            document.add(preface);
        }
        catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected void postExport(FacesContext context, ExportConfiguration config) throws IOException {
        if (config.getPostProcessor() != null) {
            config.getPostProcessor().invoke(context.getELContext(), new Object[]{document});
        }

        writePDFToResponse(context.getExternalContext(), baos, config.getOutputFileName());

        reset();
    }

    protected void reset() throws IOException {
        document.close();
        document = null;
        baos.close();
        baos = null;
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

        addTableFacets(context, table, pdfTable, "header");

        addColumnFacets(table, pdfTable, ColumnType.HEADER);

        if (config.isPageOnly()) {
            exportPageOnly(context, table, pdfTable);
        }
        else if (config.isSelectionOnly()) {
            exportSelectionOnly(context, table, pdfTable);
        }
        else {
            exportAll(context, table, pdfTable);
        }

        if (table.hasFooterColumn()) {
            addColumnFacets(table, pdfTable, ColumnType.FOOTER);
        }

        addTableFacets(context, table, pdfTable, "footer");

        table.setRowIndex(-1);

        return pdfTable;
    }

    protected void addTableFacets(FacesContext context, DataTable table, PdfPTable pdfTable, String facetType) {
        String facetText = null;
        UIComponent facet = table.getFacet(facetType);
        if (facet != null) {
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
            int colspan = 0;

            for (UIColumn col : table.getColumns()) {
                if (col.isRendered() && col.isExportable()) {
                    colspan++;
                }
            }

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
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                addColumnValue(pdfTable, col.getChildren(), cellFont, col);
            }
        }
    }

    protected void addColumnFacets(DataTable table, PdfPTable pdfTable, ColumnType columnType) {
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
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
                    addColumnValue(pdfTable, textValue);
                }
                else if (facet != null) {
                    addColumnValue(pdfTable, facet);
                }
                else {
                    addColumnValue(pdfTable, "");
                }
            }
        }
    }

    protected void addColumnValue(PdfPTable pdfTable, UIComponent component) {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        addColumnValue(pdfTable, value);
    }

    protected void addColumnValue(PdfPTable pdfTable, String value) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, facetFont));
        if (facetBgColor != null) {
            cell.setBackgroundColor(facetBgColor);
        }

        pdfTable.addCell(cell);
    }

    protected void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, Font font, UIColumn column) {
        FacesContext context = FacesContext.getCurrentInstance();

        if (column.getExportFunction() != null) {
            pdfTable.addCell(new Paragraph(exportColumnByFunction(context, column), font));
        }
        else {
            StringBuilder builder = new StringBuilder();
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);

                    if (value != null) {
                        builder.append(value);
                    }
                }
            }

            pdfTable.addCell(new Paragraph(builder.toString(), font));
        }
    }

    protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException {
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", ComponentUtils.createContentDisposition("attachment", fileName + ".pdf"));
        externalContext.setResponseContentLength(baos.size());
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
        document.close();
        OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }

    protected int getColumnsCount(DataTable table) {
        int count = 0;

        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (!col.isRendered() || !col.isExportable()) {
                continue;
            }

            count++;
        }

        return count;
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
            if (cellFontStyle.equalsIgnoreCase("NORMAL")) {
                cellFontStyle = "" + Font.NORMAL;
            }
            if (cellFontStyle.equalsIgnoreCase("BOLD")) {
                cellFontStyle = "" + Font.BOLD;
            }
            if (cellFontStyle.equalsIgnoreCase("ITALIC")) {
                cellFontStyle = "" + Font.ITALIC;
            }

            cellFont.setStyle(cellFontStyle);
        }
    }

    protected void applyFont(String fontName, String encoding) {
        String newFont = fontName;
        if (LangUtils.isValueBlank(newFont)) {
            newFont = FontFactory.TIMES;
        }
        cellFont = FontFactory.getFont(newFont, encoding);
        facetFont = FontFactory.getFont(newFont, encoding, Font.DEFAULTSIZE, Font.BOLD);
    }
}
