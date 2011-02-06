/*
 * Copyright 2009 Prime Technology.
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

public class ExporterFactory {

	public static Exporter getExporterForType(String type) {
		if(type.equalsIgnoreCase("xls"))
			return new ExcelExporter();
		else if(type.equalsIgnoreCase("pdf"))
			return new PDFExporter();
		else if(type.equalsIgnoreCase("csv"))
			return new CSVExporter();
		else if(type.equalsIgnoreCase("xml"))
			return new XMLExporter();
		
		throw new IllegalArgumentException("Invalid file type to export:" + type + ", export type can only be xls, pdf, csv or xml");
	}
}
