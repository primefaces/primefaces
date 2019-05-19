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
package org.primefaces.model.charts.optionconfig.elements;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Point elements are used to represent the points in a line chart or a bubble chart.
 */
public class ElementsPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private Number radius = 3;
    private String pointStyle;
    private String backgroundColor;
    private Number borderWidth;
    private String borderColor;
    private Number hitRadius;
    private Number hoverRadius;
    private Number hoverBorderWidth;

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
     * @param radius Point radius.
     */
    public void setRadius(Number radius) {
        this.radius = radius;
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
     * @param pointStyle Point style.
     */
    public void setPointStyle(String pointStyle) {
        this.pointStyle = pointStyle;
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
     * @param backgroundColor Point fill color.
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
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
     * @param borderWidth Point stroke width.
     */
    public void setBorderWidth(Number borderWidth) {
        this.borderWidth = borderWidth;
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
     * @param borderColor Point stroke color.
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
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
     * @param hitRadius Extra radius added to point radius for hit detection.
     */
    public void setHitRadius(Number hitRadius) {
        this.hitRadius = hitRadius;
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
     * @param hoverRadius Point radius when hovered.
     */
    public void setHoverRadius(Number hoverRadius) {
        this.hoverRadius = hoverRadius;
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
     * @param hoverBorderWidth Stroke width when hovered.
     */
    public void setHoverBorderWidth(Number hoverBorderWidth) {
        this.hoverBorderWidth = hoverBorderWidth;
    }

    /**
     * Write the point options of Elements
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "radius", this.radius, false);
            ChartUtils.writeDataValue(fsw, "pointStyle", this.pointStyle, true);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "hitRadius", this.hitRadius, true);
            ChartUtils.writeDataValue(fsw, "hoverRadius", this.hoverRadius, true);
            ChartUtils.writeDataValue(fsw, "hoverBorderWidth", this.hoverBorderWidth, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
