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

public class PDFOptions implements ExporterOptions {

    private String facetFontStyle;

    private String facetFontColor;

    private String facetBgColor;

    private String facetFontSize;

    private String cellFontStyle;

    private String cellFontColor;

    private String cellFontSize;

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

}
