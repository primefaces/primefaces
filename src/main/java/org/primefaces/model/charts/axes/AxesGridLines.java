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
package org.primefaces.model.charts.axes;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The grid line configuration is nested under the scale configuration.
 */
public class AxesGridLines implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean display = true;
    private Object color;
    private List<Number> borderDash;
    private Number borderDashOffset;
    private Object lineWidth;
    private boolean drawBorder = true;
    private boolean drawOnChartArea = true;
    private boolean drawTicks = true;
    private Number tickMarkLength;
    private Number zeroLineWidth;
    private String zeroLineColor;
    private List<Number> zeroLineBorderDash;
    private Number zeroLineBorderDashOffset;
    private boolean offsetGridLines;

    /**
     * Gets the display
     *
     * @return display
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * Sets the display
     *
     * @param display If false, do not display grid lines for this axis.
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Gets the color
     *
     * @return color
     */
    public Object getColor() {
        return color;
    }

    /**
     * Sets the color
     *
     * @param color The color of the grid lines.
     * If specified as an array, the first color applies to the first grid line, the second to the second grid line and so on.
     */
    public void setColor(Object color) {
        this.color = color;
    }

    /**
     * Gets the borderDash
     *
     * @return borderDash
     */
    public List<Number> getBorderDash() {
        return borderDash;
    }

    /**
     * Sets the borderDash
     *
     * @param borderDash Length and spacing of dashes on grid lines.
     */
    public void setBorderDash(List<Number> borderDash) {
        this.borderDash = borderDash;
    }

    /**
     * Gets the borderDashOffset
     *
     * @return borderDashOffset
     */
    public Number getBorderDashOffset() {
        return borderDashOffset;
    }

    /**
     * Sets the borderDashOffset
     *
     * @param borderDashOffset Offset for line dashes.
     */
    public void setBorderDashOffset(Number borderDashOffset) {
        this.borderDashOffset = borderDashOffset;
    }

    /**
     * Gets the lineWidth
     *
     * @return lineWidth
     */
    public Object getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the lineWidth
     *
     * @param lineWidth Stroke width of grid lines.
     */
    public void setLineWidth(Object lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Gets the drawBorder
     *
     * @return drawBorder
     */
    public boolean isDrawBorder() {
        return drawBorder;
    }

    /**
     * Sets the drawBorder
     *
     * @param drawBorder If true, draw border at the edge between the axis and the chart area.
     */
    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    /**
     * Gets the drawOnChartArea
     *
     * @return drawOnChartArea
     */
    public boolean isDrawOnChartArea() {
        return drawOnChartArea;
    }

    /**
     * Sets the drawOnChartArea
     *
     * @param drawOnChartArea If true, draw lines on the chart area inside the axis lines.
     * This is useful when there are multiple axes and you need to control which grid lines are drawn.
     */
    public void setDrawOnChartArea(boolean drawOnChartArea) {
        this.drawOnChartArea = drawOnChartArea;
    }

    /**
     * Gets the drawTicks
     *
     * @return drawTicks
     */
    public boolean isDrawTicks() {
        return drawTicks;
    }

    /**
     * Sets the drawTicks
     *
     * @param drawTicks If true, draw lines beside the ticks in the axis area beside the chart.
     */
    public void setDrawTicks(boolean drawTicks) {
        this.drawTicks = drawTicks;
    }

    /**
     * Gets the tickMarkLength
     *
     * @return tickMarkLength
     */
    public Number getTickMarkLength() {
        return tickMarkLength;
    }

    /**
     * Sets the tickMarkLength
     *
     * @param tickMarkLength Length in pixels that the grid lines will draw into the axis area.
     */
    public void setTickMarkLength(Number tickMarkLength) {
        this.tickMarkLength = tickMarkLength;
    }

    /**
     * Gets the zeroLineWidth
     *
     * @return zeroLineWidth
     */
    public Number getZeroLineWidth() {
        return zeroLineWidth;
    }

    /**
     * Sets the zeroLineWidth
     *
     * @param zeroLineWidth Stroke width of the grid line for the first index (index 0).
     */
    public void setZeroLineWidth(Number zeroLineWidth) {
        this.zeroLineWidth = zeroLineWidth;
    }

    /**
     * Gets the zeroLineColor
     *
     * @return zeroLineColor
     */
    public String getZeroLineColor() {
        return zeroLineColor;
    }

    /**
     * Sets the zeroLineColor
     *
     * @param zeroLineColor Stroke color of the grid line for the first index (index 0).
     */
    public void setZeroLineColor(String zeroLineColor) {
        this.zeroLineColor = zeroLineColor;
    }

    /**
     * Gets the zeroLineBorderDash
     *
     * @return zeroLineBorderDash
     */
    public List<Number> getZeroLineBorderDash() {
        return zeroLineBorderDash;
    }

    /**
     * Sets the zeroLineBorderDash
     *
     * @param zeroLineBorderDash Length and spacing of dashes of the grid line for the first index (index 0).
     */
    public void setZeroLineBorderDash(List<Number> zeroLineBorderDash) {
        this.zeroLineBorderDash = zeroLineBorderDash;
    }

    /**
     * Gets the zeroLineBorderDashOffset
     *
     * @return zeroLineBorderDashOffset
     */
    public Number getZeroLineBorderDashOffset() {
        return zeroLineBorderDashOffset;
    }

    /**
     * Sets the zeroLineBorderDashOffset
     *
     * @param zeroLineBorderDashOffset Offset for line dashes of the grid line for the first index (index 0).
     */
    public void setZeroLineBorderDashOffset(Number zeroLineBorderDashOffset) {
        this.zeroLineBorderDashOffset = zeroLineBorderDashOffset;
    }

    /**
     * Gets the offsetGridLines
     *
     * @return offsetGridLines
     */
    public boolean isOffsetGridLines() {
        return offsetGridLines;
    }

    /**
     * Sets the offsetGridLines
     *
     * @param offsetGridLines If true, grid lines will be shifted to be between labels.
     * This is set to true in the bar chart by default.
     */
    public void setOffsetGridLines(boolean offsetGridLines) {
        this.offsetGridLines = offsetGridLines;
    }

    /**
     * Write the common ticks options of axes
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write("{");

            ChartUtils.writeDataValue(fsw, "display", this.display, false);
            ChartUtils.writeDataValue(fsw, "color", this.color, true);
            ChartUtils.writeDataValue(fsw, "borderDash", this.borderDash, true);
            ChartUtils.writeDataValue(fsw, "borderDashOffset", this.borderDashOffset, true);
            ChartUtils.writeDataValue(fsw, "lineWidth", this.lineWidth, true);
            ChartUtils.writeDataValue(fsw, "drawBorder", this.drawBorder, true);
            ChartUtils.writeDataValue(fsw, "drawOnChartArea", this.drawOnChartArea, true);
            ChartUtils.writeDataValue(fsw, "drawTicks", this.drawTicks, true);
            ChartUtils.writeDataValue(fsw, "tickMarkLength", this.tickMarkLength, true);
            ChartUtils.writeDataValue(fsw, "zeroLineWidth", this.zeroLineWidth, true);
            ChartUtils.writeDataValue(fsw, "zeroLineColor", this.zeroLineColor, true);
            ChartUtils.writeDataValue(fsw, "zeroLineBorderDash", this.zeroLineBorderDash, true);
            ChartUtils.writeDataValue(fsw, "zeroLineBorderDashOffset", this.zeroLineBorderDashOffset, true);
            ChartUtils.writeDataValue(fsw, "offsetGridLines", this.offsetGridLines, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
