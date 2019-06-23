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
package org.primefaces.model.charts.line;

import java.io.IOException;
import java.util.List;

import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide DataSet objects to Line chart component.
 */
public class LineChartDataSet extends ChartDataSet {

    private static final long serialVersionUID = 1L;

    private List<Object> data;
    private String label;
    private String xaxisID;
    private String yaxisID;
    private String backgroundColor;
    private String borderColor;
    private Number borderWidth;
    private List<Number> borderDash;
    private Number borderDashOffset;
    private String borderCapStyle;
    private String borderJoinStyle;
    private String cubicInterpolationMode;
    private Object fill;
    private Number lineTension;
    private Object pointBackgroundColor;
    private Object pointBorderColor;
    private Object pointBorderWidth;
    private Object pointRadius;
    private Object pointStyle;
    private Object pointHitRadius;
    private Object pointHoverBackgroundColor;
    private Object pointHoverBorderColor;
    private Object pointHoverBorderWidth;
    private Object pointHoverRadius;
    private boolean showLine = true;
    private boolean spanGaps;
    private Object steppedLine;

    /**
     * Gets the list of data in this dataSet. Can either be a Number or a type of ChartJs Point.
     *
     * @return List&#60;Object&#62; list of data
     */
    public List<Object> getData() {
        return data;
    }

    /**
     * Sets the list of data in this dataSet. Can either be a Number or a type of ChartJs Point.
     *
     * @param data List&#60;Object&#62; list of data
     */
    public void setData(List<Object> data) {
        this.data = data;
    }

    /**
     * Gets the label
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label
     *
     * @param label The label for the dataset which appears in the legend and tooltips
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the xAxisID
     *
     * @return xAxisID
     */
    public String getXaxisID() {
        return xaxisID;
    }

    /**
     * Sets the xAxisID
     *
     * @param xaxisID The ID of the x axis to plot this dataset on.
     * If not specified, this defaults to the ID of the first found x axis
     */
    public void setXaxisID(String xaxisID) {
        this.xaxisID = xaxisID;
    }

    /**
     * Gets the yAxisID
     *
     * @return yAxisID
     */
    public String getYaxisID() {
        return yaxisID;
    }

    /**
     * Sets the yAxisID
     *
     * @param yaxisID The ID of the y axis to plot this dataset on.
     * If not specified, this defaults to the ID of the first found y axis.
     */
    public void setYaxisID(String yaxisID) {
        this.yaxisID = yaxisID;
    }

    /**
     * Gets the backgroundColor
     *
     * @return backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the backgroundColor
     *
     * @param backgroundColor The fill color under the line.
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets the borderColor
     *
     * @return borderColor
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the borderColor
     *
     * @param borderColor The color of the line.
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Gets the borderWidth
     *
     * @return borderWidth
     */
    public Number getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the borderWidth
     *
     * @param borderWidth The width of the line in pixels.
     */
    public void setBorderWidth(Number borderWidth) {
        this.borderWidth = borderWidth;
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
     * @param borderDash Length and spacing of dashes.
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
     * Gets the borderCapStyle
     *
     * @return borderCapStyle
     */
    public String getBorderCapStyle() {
        return borderCapStyle;
    }

    /**
     * Sets the borderCapStyle
     *
     * @param borderCapStyle Cap style of the line.
     */
    public void setBorderCapStyle(String borderCapStyle) {
        this.borderCapStyle = borderCapStyle;
    }

    /**
     * Gets the borderJoinStyle
     *
     * @return borderJoinStyle
     */
    public String getBorderJoinStyle() {
        return borderJoinStyle;
    }

    /**
     * Sets the borderJoinStyle
     *
     * @param borderJoinStyle Line joint style.
     */
    public void setBorderJoinStyle(String borderJoinStyle) {
        this.borderJoinStyle = borderJoinStyle;
    }

    /**
     * Gets the cubicInterpolationMode
     *
     * @return cubicInterpolationMode
     */
    public String getCubicInterpolationMode() {
        return cubicInterpolationMode;
    }

    /**
     * Sets the cubicInterpolationMode
     *
     * @param cubicInterpolationMode Algorithm used to interpolate a smooth curve from the discrete data points.
     */
    public void setCubicInterpolationMode(String cubicInterpolationMode) {
        this.cubicInterpolationMode = cubicInterpolationMode;
    }

    /**
     * Gets the fill
     *
     * @return fill
     */
    public Object getFill() {
        return fill;
    }

    /**
     * Sets the fill
     *
     * @param fill How to fill the area under the line.
     */
    public void setFill(Object fill) {
        this.fill = fill;
    }

    /**
     * Gets the lineTension
     *
     * @return lineTension
     */
    public Number getLineTension() {
        return lineTension;
    }

    /**
     * Sets the lineTension
     *
     * @param lineTension Bezier curve tension of the line. Set to 0 to draw straightlines.
     * This option is ignored if monotone cubic interpolation is used.
     */
    public void setLineTension(Number lineTension) {
        this.lineTension = lineTension;
    }

    /**
     * Gets the pointBackgroundColor
     *
     * @return pointBackgroundColor
     */
    public Object getPointBackgroundColor() {
        return pointBackgroundColor;
    }

    /**
     * Sets the pointBackgroundColor
     *
     * @param pointBackgroundColor The fill color for points.
     */
    public void setPointBackgroundColor(Object pointBackgroundColor) {
        this.pointBackgroundColor = pointBackgroundColor;
    }

    /**
     * Gets the pointBorderColor
     *
     * @return pointBorderColor
     */
    public Object getPointBorderColor() {
        return pointBorderColor;
    }

    /**
     * Sets the pointBorderColor
     *
     * @param pointBorderColor The border color for points.
     */
    public void setPointBorderColor(Object pointBorderColor) {
        this.pointBorderColor = pointBorderColor;
    }

    /**
     * Gets the pointBorderWidth
     *
     * @return pointBorderWidth
     */
    public Object getPointBorderWidth() {
        return pointBorderWidth;
    }

    /**
     * Sets the pointBorderWidth
     *
     * @param pointBorderWidth The width of the point border in pixels.
     */
    public void setPointBorderWidth(Object pointBorderWidth) {
        this.pointBorderWidth = pointBorderWidth;
    }

    /**
     * Gets the pointRadius
     *
     * @return pointRadius
     */
    public Object getPointRadius() {
        return pointRadius;
    }

    /**
     * Sets the pointRadius
     *
     * @param pointRadius The radius of the point shape. If set to 0, the point is not rendered.
     */
    public void setPointRadius(Object pointRadius) {
        this.pointRadius = pointRadius;
    }

    /**
     * Gets the pointStyle
     *
     * @return pointStyle
     */
    public Object getPointStyle() {
        return pointStyle;
    }

    /**
     * Sets the pointStyle
     *
     * @param pointStyle Style of the point.
     */
    public void setPointStyle(Object pointStyle) {
        this.pointStyle = pointStyle;
    }

    /**
     * Gets the pointHitRadius
     *
     * @return pointHitRadius
     */
    public Object getPointHitRadius() {
        return pointHitRadius;
    }

    /**
     * Sets the pointHitRadius
     *
     * @param pointHitRadius The pixel size of the non-displayed point that reacts to mouse events.
     */
    public void setPointHitRadius(Object pointHitRadius) {
        this.pointHitRadius = pointHitRadius;
    }

    /**
     * Gets the pointHoverBackgroundColor
     *
     * @return pointHoverBackgroundColor
     */
    public Object getPointHoverBackgroundColor() {
        return pointHoverBackgroundColor;
    }

    /**
     * Sets the pointHoverBackgroundColor
     *
     * @param pointHoverBackgroundColor Point background color when hovered.
     */
    public void setPointHoverBackgroundColor(Object pointHoverBackgroundColor) {
        this.pointHoverBackgroundColor = pointHoverBackgroundColor;
    }

    /**
     * Gets the pointHoverBorderColor
     *
     * @return pointHoverBorderColor
     */
    public Object getPointHoverBorderColor() {
        return pointHoverBorderColor;
    }

    /**
     * Sets the pointHoverBorderColor
     *
     * @param pointHoverBorderColor Point border color when hovered.
     */
    public void setPointHoverBorderColor(Object pointHoverBorderColor) {
        this.pointHoverBorderColor = pointHoverBorderColor;
    }

    /**
     * Gets the pointHoverBorderWidth
     *
     * @return pointHoverBorderWidth
     */
    public Object getPointHoverBorderWidth() {
        return pointHoverBorderWidth;
    }

    /**
     * Sets the pointHoverBorderWidth
     *
     * @param pointHoverBorderWidth Border width of point when hovered.
     */
    public void setPointHoverBorderWidth(Object pointHoverBorderWidth) {
        this.pointHoverBorderWidth = pointHoverBorderWidth;
    }

    /**
     * Gets the pointHoverRadius
     *
     * @return pointHoverRadius
     */
    public Object getPointHoverRadius() {
        return pointHoverRadius;
    }

    /**
     * Sets the pointHoverRadius
     *
     * @param pointHoverRadius The radius of the point when hovered.
     */
    public void setPointHoverRadius(Object pointHoverRadius) {
        this.pointHoverRadius = pointHoverRadius;
    }

    /**
     * Gets the showLine
     *
     * @return showLine
     */
    public boolean isShowLine() {
        return showLine;
    }

    /**
     * Sets the showLine
     *
     * @param showLine If false, the line is not drawn for this dataset.
     */
    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    /**
     * Gets the spanGaps
     *
     * @return spanGaps
     */
    public boolean isSpanGaps() {
        return spanGaps;
    }

    /**
     * Sets the spanGaps
     *
     * @param spanGaps If true, lines will be drawn between points with no or null data.
     * If false, points with NaN data will create a break in the line
     */
    public void setSpanGaps(boolean spanGaps) {
        this.spanGaps = spanGaps;
    }

    /**
     * Gets the steppedLine
     *
     * @return steppedLine
     */
    public Object getSteppedLine() {
        return steppedLine;
    }

    /**
     * Sets the steppedLine
     *
     * @param steppedLine If the line is shown as a stepped line.
     */
    public void setSteppedLine(Object steppedLine) {
        this.steppedLine = steppedLine;
    }

    /**
     * Write the options of this dataSet
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write("{");

            ChartUtils.writeDataValue(fsw, "type", "line", false);
            ChartUtils.writeDataValue(fsw, "data", this.data, true);
            ChartUtils.writeDataValue(fsw, "label", this.label, true);
            ChartUtils.writeDataValue(fsw, "hidden", this.isHidden(), true);
            ChartUtils.writeDataValue(fsw, "xAxisID", this.xaxisID, true);
            ChartUtils.writeDataValue(fsw, "yAxisID", this.yaxisID, true);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
            ChartUtils.writeDataValue(fsw, "borderDash", this.borderDash, true);
            ChartUtils.writeDataValue(fsw, "borderDashOffset", this.borderDashOffset, true);
            ChartUtils.writeDataValue(fsw, "borderCapStyle", this.borderCapStyle, true);
            ChartUtils.writeDataValue(fsw, "borderJoinStyle", this.borderJoinStyle, true);
            ChartUtils.writeDataValue(fsw, "cubicInterpolationMode", this.cubicInterpolationMode, true);
            ChartUtils.writeDataValue(fsw, "fill", this.fill, true);
            ChartUtils.writeDataValue(fsw, "lineTension", this.lineTension, true);
            ChartUtils.writeDataValue(fsw, "pointBackgroundColor", this.pointBackgroundColor, true);
            ChartUtils.writeDataValue(fsw, "pointBorderColor", this.pointBorderColor, true);
            ChartUtils.writeDataValue(fsw, "pointBorderWidth", this.pointBorderWidth, true);
            ChartUtils.writeDataValue(fsw, "pointRadius", this.pointRadius, true);
            ChartUtils.writeDataValue(fsw, "pointStyle", this.pointStyle, true);
            ChartUtils.writeDataValue(fsw, "pointHitRadius", this.pointHitRadius, true);
            ChartUtils.writeDataValue(fsw, "pointHoverBackgroundColor", this.pointHoverBackgroundColor, true);
            ChartUtils.writeDataValue(fsw, "pointHoverBorderColor", this.pointHoverBorderColor, true);
            ChartUtils.writeDataValue(fsw, "pointHoverBorderWidth", this.pointHoverBorderWidth, true);
            ChartUtils.writeDataValue(fsw, "pointHoverRadius", this.pointHoverRadius, true);
            ChartUtils.writeDataValue(fsw, "showLine", this.showLine, true);
            ChartUtils.writeDataValue(fsw, "spanGaps", this.spanGaps, true);
            ChartUtils.writeDataValue(fsw, "steppedLine", this.steppedLine, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
