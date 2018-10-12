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

import javax.faces.FacesException;

public class ExporterFactory {

    private ExporterFactory() {
    }

    public static Exporter getExporterForType(String type, ExporterOptions options) {
        Exporter exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            switch (exporterType) {
                case XLS:
                    exporter = new ExcelExporter();
                    break;

                case PDF:
                    exporter = new PDFExporter();
                    break;

                case CSV:
                    exporter = new CSVExporter(options);
                    break;

                case XML:
                    exporter = new XMLExporter();
                    break;

                case XLSX:
                    exporter = new ExcelXExporter();
                    break;
                case XLSXSTREAM:
                    exporter = new ExcelXStreamExporter();
                    break;

            }
        }
        catch (IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }
}
