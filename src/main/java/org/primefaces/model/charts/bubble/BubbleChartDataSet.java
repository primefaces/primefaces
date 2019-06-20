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
package org.primefaces.model.charts.bubble;

import java.io.IOException;
import java.util.List;

import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.model.charts.data.BubblePoint;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide DataSet objects to Bubble chart component.
 */
public class BubbleChartDataSet extends ChartDataSet {

    private static final long serialVersionUID = 1L;

    private List<BubblePoint> data;
    private String label;
    private String backgroundColor;
    private String borderColor;
    private Number borderWidth;
    private String hoverBackgroundColor;
    private String hoverBorderColor;
    private Number hoverBorderWidth;
    private Number hoverRadius;
    private Number hitRadius;
    private String pointStyle;
    private Number radius;

    /**
     * Gets the list of data in this dataSet
     *
     * @return List&#60;{@link BubblePoint}&#62; list of data
     */
    public List<BubblePoint> getData() {
        return data;
    }

    /**
     * Sets the list of data in this dataSet
     *
     * @param data List&#60;{@link BubblePoint}&#62; list of data
     */
    public void setData(List<BubblePoint> data) {
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
     * @param backgroundColor bubble background color
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
     * @param borderColor bubble border color
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
     * @param borderWidth bubble border width (in pixels)
     */
    public void setBorderWidth(Number borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Gets the hoverBackgroundColor
     *
     * @return hoverBackgroundColor
     */
    public String getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    /**
     * Sets the hoverBackgroundColor
     *
     * @param hoverBackgroundColor bubble background color when hovered
     */
    public void setHoverBackgroundColor(String hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    /**
     * Gets the hoverBorderColor
     *
     * @return hoverBorderColor
     */
    public String getHoverBorderColor() {
        return hoverBorderColor;
    }

    /**
     * Sets the hoverBorderColor
     *
     * @param hoverBorderColor bubble border color hovered
     */
    public void setHoverBorderColor(String hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
    }

    /**
     * Gets the hoverBorderWidth
     *
     * @return hoverBorderWidth
     */
    public Number getHoverBorderWidth() {
        return hoverBorderWidth;
    }

    /**
     * Sets the hoverBorderWidth
     *
     * @param hoverBorderWidth bubble border width when hovered (in pixels)
     */
    public void setHoverBorderWidth(Number hoverBorderWidth) {
        this.hoverBorderWidth = hoverBorderWidth;
    }

    /**
     * Gets the hoverRadius
     *
     * @return hoverRadius
     */
    public Number getHoverRadius() {
        return hoverRadius;
    }

    /**
     * Sets the hoverRadius
     *
     * @param hoverRadius bubble additional radius when hovered (in pixels)
     */
    public void setHoverRadius(Number hoverRadius) {
        this.hoverRadius = hoverRadius;
    }

    /**
     * Gets the hitRadius
     *
     * @return hitRadius
     */
    public Number getHitRadius() {
        return hitRadius;
    }

    /**
     * Sets the hitRadius
     *
     * @param hitRadius bubble additional radius for hit detection (in pixels)
     */
    public void setHitRadius(Number hitRadius) {
        this.hitRadius = hitRadius;
    }

    /**
     * Gets the pointStyle
     *
     * @return pointStyle
     */
    public String getPointStyle() {
        return pointStyle;
    }

    /**
     * Sets the pointStyle
     *
     * @param pointStyle bubble shape style
     */
    public void setPointStyle(String pointStyle) {
        this.pointStyle = pointStyle;
    }

    /**
     * Gets the radius
     *
     * @return radius
     */
    public Number getRadius() {
        return radius;
    }

    /**
     * Sets the radius
     *
     * @param radius bubble radius (in pixels)
     */
    public void setRadius(Number radius) {
        this.radius = radius;
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

            ChartUtils.writeDataValue(fsw, "type", "bubble", false);
            ChartUtils.writeDataValue(fsw, "data", this.data, true);
            ChartUtils.writeDataValue(fsw, "label", this.label, true);
            ChartUtils.writeDataValue(fsw, "hidden", this.isHidden(), true);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
            ChartUtils.writeDataValue(fsw, "hoverBackgroundColor", this.hoverBackgroundColor, true);
            ChartUtils.writeDataValue(fsw, "hoverBorderColor", this.hoverBorderColor, true);
            ChartUtils.writeDataValue(fsw, "hoverBorderWidth", this.hoverBorderWidth, true);
            ChartUtils.writeDataValue(fsw, "hitRadius", this.hitRadius, true);
            ChartUtils.writeDataValue(fsw, "pointStyle", this.pointStyle, true);
            ChartUtils.writeDataValue(fsw, "radius", this.radius, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
