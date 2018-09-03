/**
 * Copyright 2009-2018 PrimeTek.
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

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class PDFExporter extends Exporter {

    private Font cellFont;
    private Font facetFont;
    private Color facetBgColor;
    private ExporterOptions expOptions;
    private MethodExpression onTableRender;

    @Override
    public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType,
                       MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            this.onTableRender = onTableRender;

            if (preProcessor != null) {
                preProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            if (!document.isOpen()) {
                document.open();
            }

            if (options != null) {
                expOptions = options;
            }

            document.add(exportPDFTable(context, table, pageOnly, selectionOnly, encodingType));

            if (postProcessor != null) {
                postProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            document.close();

            writePDFToResponse(context.getExternalContext(), baos, filename);

        }
        catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void export(FacesContext context, List<String> clientIds, String outputFileName, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            this.onTableRender = onTableRender;

            if (preProcessor != null) {
                preProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            if (!document.isOpen()) {
                document.open();
            }

            if (options != null) {
                expOptions = options;
            }

            VisitContext visitContext = VisitContext.createVisitContext(context, clientIds, null);
            VisitCallback visitCallback = new PDFExportVisitCallback(this, document, pageOnly, selectionOnly, encodingType);
            context.getViewRoot().visitTree(visitContext, visitCallback);

            if (postProcessor != null) {
                postProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            document.close();

            writePDFToResponse(context.getExternalContext(), baos, outputFileName);

        }
        catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void export(FacesContext context, String outputFileName, List<DataTable> tables, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            this.onTableRender = onTableRender;

            if (preProcessor != null) {
                preProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            if (!document.isOpen()) {
                document.open();
            }

            if (options != null) {
                expOptions = options;
            }

            for (DataTable table : tables) {
                document.add(exportPDFTable(context, table, pageOnly, selectionOnly, encodingType));

                Paragraph preface = new Paragraph();
                addEmptyLine(preface, 3);
                document.add(preface);
            }

            if (postProcessor != null) {
                postProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            document.close();

            writePDFToResponse(context.getExternalContext(), baos, outputFileName);

        }
        catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    protected PdfPTable exportPDFTable(FacesContext context, DataTable table, boolean pageOnly, boolean selectionOnly, String encoding) {
        int columnsCount = getColumnsCount(table);
        PdfPTable pdfTable = new PdfPTable(columnsCount);
        cellFont = FontFactory.getFont(FontFactory.TIMES, encoding);
        facetFont = FontFactory.getFont(FontFactory.TIMES, encoding, Font.DEFAULTSIZE, Font.BOLD);

        if (onTableRender != null) {
            onTableRender.invoke(context.getELContext(), new Object[]{pdfTable, table});
        }

        if (expOptions != null) {
            applyFacetOptions(expOptions);
            applyCellOptions(expOptions);
        }

        addTableFacets(context, table, pdfTable, "header");

        addColumnFacets(table, pdfTable, ColumnType.HEADER);

        if (pageOnly) {
            exportPageOnly(context, table, pdfTable);
        }
        else if (selectionOnly) {
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

    protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName)
            throws IOException, DocumentException {

        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", ComponentUtils.createContentDisposition("attachment", fileName + ".pdf"));
        externalContext.setResponseContentLength(baos.size());
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
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
        if (facetFontStyle != null) {
            if (facetFontStyle.equalsIgnoreCase("NORMAL")) {
                facetFontStyle = "" + Font.NORMAL;
            }
            if (facetFontStyle.equalsIgnoreCase("BOLD")) {
                facetFontStyle = "" + Font.BOLD;
            }
            if (facetFontStyle.equalsIgnoreCase("ITALIC")) {
                facetFontStyle = "" + Font.ITALIC;
            }

            facetFont.setStyle(facetFontStyle);
        }
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
}
