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
package org.primefaces.component.export;

public class XMLOptions implements ExporterOptions {

    private static final String STYLING_NOT_SUPPORTED = "XML does not support styling.";

    private boolean exportHeader = false;

    private boolean exportFooter = false;

    public XMLOptions() {
        super();
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

    @Override
    public boolean isExportHeader() {
        return exportHeader;
    }

    @Override
    public boolean isExportFooter() {
        return exportFooter;
    }

    public void setExportHeader(boolean exportHeader) {
        this.exportHeader = exportHeader;
    }

    public void setExportFooter(boolean exportFooter) {
        this.exportFooter = exportFooter;
    }

}
