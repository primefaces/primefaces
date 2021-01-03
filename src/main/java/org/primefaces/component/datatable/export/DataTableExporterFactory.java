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
package org.primefaces.component.datatable.export;

import javax.faces.FacesException;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.export.ExporterType;

public class DataTableExporterFactory {

    private DataTableExporterFactory() {
    }

    public static Exporter<DataTable> getExporter(String type, ExporterOptions options) {
        Exporter<DataTable> exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            switch (exporterType) {
                case XLS:
                    exporter = new DataTableExcelExporter();
                    break;

                case PDF:
                    exporter = new DataTablePDFExporter();
                    break;

                case CSV:
                    exporter = new DataTableCSVExporter();
                    break;

                case XML:
                    exporter = new DataTableXMLExporter();
                    break;

                case XLSX:
                    exporter = new DataTableExcelXExporter();
                    break;
                case XLSXSTREAM:
                    exporter = new DataTableExcelXStreamExporter();
                    break;

            }
        }
        catch (IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }
}
