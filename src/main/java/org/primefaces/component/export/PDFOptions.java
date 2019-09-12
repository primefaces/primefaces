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

public class PDFOptions implements ExporterOptions {

    private String facetFontStyle;

    private String facetFontColor;

    private String facetBgColor;

    private String facetFontSize;

    private String cellFontStyle;

    private String cellFontColor;

    private String cellFontSize;

    private String fontName;

    public PDFOptions() {
    }

    public PDFOptions(String facetFontStyle, String facetFontColor, String facetBgColor, String facetFontSize) {
        this.facetFontStyle = facetFontStyle;
        this.facetFontColor = facetFontColor;
        this.facetBgColor = facetBgColor;
        this.facetFontSize = facetFontSize;
    }

    public PDFOptions(String cellFontStyle, String cellFontColor, String cellFontSize) {
        this.cellFontStyle = cellFontStyle;
        this.cellFontColor = cellFontColor;
        this.cellFontSize = cellFontSize;
    }

    public PDFOptions(String facetFontStyle, String facetFontColor, String facetBgColor, String facetFontSize, String cellFontStyle,
                      String cellFontColor, String cellFontSize) {
        this(facetFontStyle, facetFontColor, facetBgColor, facetFontSize);
        this.cellFontStyle = cellFontStyle;
        this.cellFontColor = cellFontColor;
        this.cellFontSize = cellFontSize;
    }

    @Override
    public String getFacetFontStyle() {
        return facetFontStyle;
    }

    public void setFacetFontStyle(String facetFontStyle) {
        this.facetFontStyle = facetFontStyle;
    }

    @Override
    public String getFacetFontColor() {
        return facetFontColor;
    }

    public void setFacetFontColor(String facetFontColor) {
        this.facetFontColor = facetFontColor;
    }

    @Override
    public String getFacetBgColor() {
        return facetBgColor;
    }

    public void setFacetBgColor(String facetBgColor) {
        this.facetBgColor = facetBgColor;
    }

    @Override
    public String getFacetFontSize() {
        return facetFontSize;
    }

    public void setFacetFontSize(String facetFontSize) {
        this.facetFontSize = facetFontSize;
    }

    @Override
    public String getCellFontStyle() {
        return cellFontStyle;
    }

    public void setCellFontStyle(String cellFontStyle) {
        this.cellFontStyle = cellFontStyle;
    }

    @Override
    public String getCellFontColor() {
        return cellFontColor;
    }

    public void setCellFontColor(String cellFontColor) {
        this.cellFontColor = cellFontColor;
    }

    @Override
    public String getCellFontSize() {
        return cellFontSize;
    }

    public void setCellFontSize(String cellFontSize) {
        this.cellFontSize = cellFontSize;
    }

    @Override
    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

}
