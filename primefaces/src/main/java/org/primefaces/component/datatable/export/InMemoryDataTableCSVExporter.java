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
import org.primefaces.component.export.CSVOptions;
import org.primefaces.component.export.ColumnValue;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;

/**
 * InMemoryDataTableCSVExporter exists to support post-processors adjusting text inside the document.
 * The existing DataTableCSVExporter uses a PrintWriter as the backing document, which is stream-based,
 * making it resistant to editing the text of the document after it has been written.
 * InMemoryDataTableCSVExporter uses a StringBuilder as the backing document,
 * allowing a post-processor to make any necessary changes to the entire document without restriction.
 * Though be aware there may be a higher memory requirement.
 */
public class InMemoryDataTableCSVExporter extends DataTableExporter<StringBuilder, CSVOptions> {

    public InMemoryDataTableCSVExporter() {
        super(CSVOptions.EXCEL, EnumSet.of(FacetType.COLUMN), false);
    }

    @Override
    protected void postExport(FacesContext context) throws IOException {
        super.postExport(context);

        if (document != null) {
            try {
                OutputStreamWriter osw = new OutputStreamWriter(os(), exportConfiguration.getEncodingType());
                osw.write(document.toString());
                osw.flush();
            }
            catch (UnsupportedEncodingException e) {
                throw new FacesException(e);
            }
        }
    }

    @Override
    protected StringBuilder createDocument(FacesContext context) throws IOException {
        String encoding = exportConfiguration.getEncodingType();
        StringBuilder builder = new StringBuilder();
        if (StandardCharsets.UTF_8.name().equals(encoding)) {
            builder.append("\ufeff"); // byte order mark for UTF-8
        }
        return builder;
    }

    @Override
    protected void postRowExport(FacesContext context, DataTable table) {
        document.append(options().getEndOfLineSymbols());

        super.postRowExport(context, table);
    }

    @Override
    protected void exportColumnFacetValue(FacesContext context, DataTable table, ColumnValue columnValue, int index) {
        if (index != 0) { // not first, for column separator
            document.append(options().getDelimiterChar());
        }

        String text = columnValue.toString();
        String exportValue = Constants.EMPTY_STRING;
        if (LangUtils.isNotBlank(text)) {
            exportValue = text.replace(options().getQuoteString(), options().getDoubleQuoteString());
        }

        document.append(options().getQuoteChar()).append(exportValue).append(options().getQuoteChar());
    }

    @Override
    protected void exportCellValue(FacesContext context, DataTable table, UIColumn col, ColumnValue columnValue, int index) {
        if (index != 0) {
            document.append(options().getDelimiterChar());
        }

        document.append(options().getQuoteChar())
                .append(escapeQuotes(columnValue.toString()))
                .append(options().getQuoteChar());
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public String getFileExtension() {
        return ".csv";
    }

    protected String escapeQuotes(String value) {
        return value == null ? Constants.EMPTY_STRING : value.replace(options().getQuoteString(), options().getDoubleQuoteString());
    }
}
