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
package org.primefaces.model.charts.axes.radial.linear;

import java.io.IOException;

import org.primefaces.model.charts.axes.AxesTicks;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide radial linear ticks. RadialLinearTicks extends {@link AxesTicks}
 */
public class RadialLinearTicks extends AxesTicks {

    private static final long serialVersionUID = 1L;

    private String backdropColor;
    private Number backdropPaddingX;
    private Number backdropPaddingY;
    private boolean beginAtZero = false;
    private Number min;
    private Number max;
    private Number maxTicksLimit;
    private Number stepSize;
    private Number suggestedMax;
    private Number suggestedMin;
    private boolean showLabelBackdrop = true;

    /**
     * Gets the backdropColor
     *
     * @return backdropColor
     */
    public String getBackdropColor() {
        return backdropColor;
    }

    /**
     * Sets the backdropColor
     *
     * @param backdropColor Color of label backdrops
     */
    public void setBackdropColor(String backdropColor) {
        this.backdropColor = backdropColor;
    }

    /**
     * Gets the backdropPaddingX
     *
     * @return backdropPaddingX
     */
    public Number getBackdropPaddingX() {
        return backdropPaddingX;
    }

    /**
     * Sets the backdropPaddingX
     *
     * @param backdropPaddingX Horizontal padding of label backdrop.
     */
    public void setBackdropPaddingX(Number backdropPaddingX) {
        this.backdropPaddingX = backdropPaddingX;
    }

    /**
     * Gets the backdropPaddingY
     *
     * @return backdropPaddingY
     */
    public Number getBackdropPaddingY() {
        return backdropPaddingY;
    }

    /**
     * Sets the backdropPaddingY
     *
     * @param backdropPaddingY Vertical padding of label backdrop.
     */
    public void setBackdropPaddingY(Number backdropPaddingY) {
        this.backdropPaddingY = backdropPaddingY;
    }

    /**
     * Gets the beginAtZero
     *
     * @return beginAtZero
     */
    public boolean isBeginAtZero() {
        return beginAtZero;
    }

    /**
     * Sets the beginAtZero
     *
     * @param beginAtZero if true, scale will include 0 if it is not already included.
     */
    public void setBeginAtZero(boolean beginAtZero) {
        this.beginAtZero = beginAtZero;
    }

    /**
     * Gets the min
     *
     * @return min
     */
    public Number getMin() {
        return min;
    }

    /**
     * Sets the min
     *
     * @param min User defined minimum number for the scale, overrides minimum value from data.
     */
    public void setMin(Number min) {
        this.min = min;
    }

    /**
     * Gets the max
     *
     * @return max
     */
    public Number getMax() {
        return max;
    }

    /**
     * Sets the max
     *
     * @param max User defined maximum number for the scale, overrides maximum value from data.
     */
    public void setMax(Number max) {
        this.max = max;
    }

    /**
     * Gets the maxTicksLimit
     *
     * @return maxTicksLimit
     */
    public Number getMaxTicksLimit() {
        return maxTicksLimit;
    }

    /**
     * Sets the maxTicksLimit
     *
     * @param maxTicksLimit Maximum number of ticks and gridlines to show.
     */
    public void setMaxTicksLimit(Number maxTicksLimit) {
        this.maxTicksLimit = maxTicksLimit;
    }

    /**
     * Gets the stepSize
     *
     * @return stepSize
     */
    public Number getStepSize() {
        return stepSize;
    }

    /**
     * Sets the stepSize
     *
     * @param stepSize User defined fixed step size for the scale.
     */
    public void setStepSize(Number stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * Gets the suggestedMax
     *
     * @return suggestedMax
     */
    public Number getSuggestedMax() {
        return suggestedMax;
    }

    /**
     * Sets the suggestedMax
     *
     * @param suggestedMax Adjustment used when calculating the maximum data value.
     */
    public void setSuggestedMax(Number suggestedMax) {
        this.suggestedMax = suggestedMax;
    }

    /**
     * Gets the suggestedMin
     *
     * @return suggestedMin
     */
    public Number getSuggestedMin() {
        return suggestedMin;
    }

    /**
     * Sets the suggestedMin
     *
     * @param suggestedMin Adjustment used when calculating the minimum data value.
     */
    public void setSuggestedMin(Number suggestedMin) {
        this.suggestedMin = suggestedMin;
    }

    /**
     * Gets the showLabelBackdrop
     *
     * @return showLabelBackdrop
     */
    public boolean isShowLabelBackdrop() {
        return showLabelBackdrop;
    }

    /**
     * Sets the showLabelBackdrop
     *
     * @param showLabelBackdrop If true, draw a background behind the tick labels
     */
    public void setShowLabelBackdrop(boolean showLabelBackdrop) {
        this.showLabelBackdrop = showLabelBackdrop;
    }

    /**
     * Write the radial linear ticks
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write("{");
            fsw.write(super.encode());

            ChartUtils.writeDataValue(fsw, "beginAtZero", this.beginAtZero, true);
            ChartUtils.writeDataValue(fsw, "backdropColor", this.backdropColor, true);
            ChartUtils.writeDataValue(fsw, "backdropPaddingX", this.backdropPaddingX, true);
            ChartUtils.writeDataValue(fsw, "backdropPaddingY", this.backdropPaddingY, true);
            ChartUtils.writeDataValue(fsw, "min", this.min, true);
            ChartUtils.writeDataValue(fsw, "max", this.max, true);
            ChartUtils.writeDataValue(fsw, "maxTicksLimit", this.maxTicksLimit, true);
            ChartUtils.writeDataValue(fsw, "stepSize", this.stepSize, true);
            ChartUtils.writeDataValue(fsw, "suggestedMax", this.suggestedMax, true);
            ChartUtils.writeDataValue(fsw, "suggestedMin", this.suggestedMin, true);
            ChartUtils.writeDataValue(fsw, "showLabelBackdrop", this.showLabelBackdrop, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
