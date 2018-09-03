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

public class CSVOptions implements ExporterOptions {

    public static final CSVOptions STANDARD = new CSVOptions('"', ',', "\r\n");

    public static final CSVOptions EXCEL = new CSVOptions('"', ',', "\n");

    public static final CSVOptions EXCEL_NORTHERN_EUROPE = new CSVOptions('"', ';', "\n");

    public static final CSVOptions TABS = new CSVOptions('"', '\t', "\n");

    private static final String STYLING_NOT_SUPPORTED = "CSV does not support styling.";

    private final char quoteChar;

    private final char delimiterChar;

    private final String endOfLineSymbols;

    private final String quoteString;

    private final String doubleQuoteString;

    public CSVOptions(char quoteChar, char delimiterChar, String endOfLineSymbols) {
        this.quoteChar = quoteChar;
        this.delimiterChar = delimiterChar;
        this.endOfLineSymbols = endOfLineSymbols;
        quoteString = Character.toString(quoteChar);
        doubleQuoteString = quoteString + quoteString;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public char getDelimiterChar() {
        return delimiterChar;
    }

    public String getEndOfLineSymbols() {
        return endOfLineSymbols;
    }

    public String getQuoteString() {
        return quoteString;
    }

    public String getDoubleQuoteString() {
        return doubleQuoteString;
    }

    @Override
    public String getFacetFontStyle() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

    @Override
    public String getFacetFontColor() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

    @Override
    public String getFacetBgColor() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

    @Override
    public String getFacetFontSize() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

    @Override
    public String getCellFontStyle() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

    @Override
    public String getCellFontColor() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

    @Override
    public String getCellFontSize() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

}
