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
import java.util.List;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Line elements are used to represent the line in a line chart.
 */
public class ElementsLine implements Serializable {

    private static final long serialVersionUID = 1L;

    private Number tension = 0.4;
    private String backgroundColor;
    private Number borderWidth;
    private String borderColor;
    private String borderCapStyle;
    private List<Number> borderDash;
    private Number borderDashOffset;
    private String borderJoinStyle;
    private boolean capBezierPoints;
    private Object fill;
    private boolean stepped;

    /**
     * Gets the tension
     *
     * @return tension
     */
    public Number getTension() {
        return tension;
    }

    /**
     * Sets the tension
     *
     * @param tension Bézier curve tension (0 for no Bézier curves).
     */
    public void setTension(Number tension) {
        this.tension = tension;
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
     * @param backgroundColor Line fill color.
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
     * @param borderWidth Line stroke width.
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
     * @param borderColor Line stroke color.
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
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
     * @param borderCapStyle Line cap style.
     */
    public void setBorderCapStyle(String borderCapStyle) {
        this.borderCapStyle = borderCapStyle;
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
     * @param borderDash Line dash
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
     * @param borderDashOffset Line dash offset
     */
    public void setBorderDashOffset(Number borderDashOffset) {
        this.borderDashOffset = borderDashOffset;
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
     * @param borderJoinStyle Line join style.
     */
    public void setBorderJoinStyle(String borderJoinStyle) {
        this.borderJoinStyle = borderJoinStyle;
    }

    /**
     * Gets the capBezierPoints
     *
     * @return capBezierPoints
     */
    public boolean isCapBezierPoints() {
        return capBezierPoints;
    }

    /**
     * Sets the capBezierPoints
     *
     * @param capBezierPoints true to keep Bézier control inside the chart, false for no restriction.
     */
    public void setCapBezierPoints(boolean capBezierPoints) {
        this.capBezierPoints = capBezierPoints;
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
     * @param fill Fill location: 'zero', 'top', 'bottom', true (eq. 'zero') or false (no fill).
     */
    public void setFill(Object fill) {
        this.fill = fill;
    }

    /**
     * Gets the stepped
     *
     * @return stepped
     */
    public boolean isStepped() {
        return stepped;
    }

    /**
     * Sets the stepped
     *
     * @param stepped true to show the line as a stepped line (tension will be ignored).
     */
    public void setStepped(boolean stepped) {
        this.stepped = stepped;
    }

    /**
     * Write the line options of Elements
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "tension", this.tension, false);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "borderCapStyle", this.borderCapStyle, true);
            ChartUtils.writeDataValue(fsw, "borderDash", this.borderDash, true);
            ChartUtils.writeDataValue(fsw, "borderDashOffset", this.borderDashOffset, true);
            ChartUtils.writeDataValue(fsw, "borderJoinStyle", this.borderJoinStyle, true);
            ChartUtils.writeDataValue(fsw, "capBezierPoints", this.capBezierPoints, true);
            ChartUtils.writeDataValue(fsw, "fill", this.fill, true);
            ChartUtils.writeDataValue(fsw, "stepped", this.stepped, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
