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
package org.primefaces.model.chart;

import java.io.Serializable;

public class ChartModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private boolean shadow = true;
    private String seriesColors;
    private String negativeSeriesColors;
    private String legendPosition;
    private int legendCols;
    private int legendRows;
    private LegendPlacement legendPlacement;
    private boolean legendEscapeHtml = false;
    private boolean mouseoverHighlight = true;
    private String extender;
    private boolean resetAxesOnResize = true;
    private String dataRenderMode = "value";
    private String legendLabel;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public String getSeriesColors() {
        return seriesColors;
    }

    public void setSeriesColors(String seriesColors) {
        this.seriesColors = seriesColors;
    }

    public String getNegativeSeriesColors() {
        return negativeSeriesColors;
    }

    public void setNegativeSeriesColors(String negativeSeriesColors) {
        this.negativeSeriesColors = negativeSeriesColors;
    }

    public String getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public int getLegendCols() {
        return legendCols;
    }

    public void setLegendCols(int legendCols) {
        this.legendCols = legendCols;
    }

    public int getLegendRows() {
        return legendRows;
    }

    public void setLegendRows(int legendRows) {
        this.legendRows = legendRows;
    }

    public LegendPlacement getLegendPlacement() {
        return legendPlacement;
    }

    public void setLegendPlacement(LegendPlacement legendPlacement) {
        this.legendPlacement = legendPlacement;
    }

    public boolean isMouseoverHighlight() {
        return mouseoverHighlight;
    }

    public void setMouseoverHighlight(boolean mouseoverHighlight) {
        this.mouseoverHighlight = mouseoverHighlight;
    }

    public boolean isResetAxesOnResize() {
        return resetAxesOnResize;
    }

    public void setResetAxesOnResize(boolean resetAxesOnResize) {
        this.resetAxesOnResize = resetAxesOnResize;
    }

    public String getExtender() {
        return extender;
    }

    public void setExtender(String extender) {
        this.extender = extender;
    }

    public boolean isLegendEscapeHtml() {
        return legendEscapeHtml;
    }

    public void setLegendEscapeHtml(boolean legendEscapeHtml) {
        this.legendEscapeHtml = legendEscapeHtml;
    }

    public String getDataRenderMode() {
        return dataRenderMode;
    }

    public void setDataRenderMode(String dataRenderMode) {
        this.dataRenderMode = dataRenderMode;
    }

    public String getLegendLabel() {
        return legendLabel;
    }

    public void setLegendLabel(String legendLabel) {
        this.legendLabel = legendLabel;
    }
}
