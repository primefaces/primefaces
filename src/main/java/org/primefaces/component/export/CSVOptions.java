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

    @Override
    public String getFontName() {
        throw new UnsupportedOperationException(STYLING_NOT_SUPPORTED);
    }

}
