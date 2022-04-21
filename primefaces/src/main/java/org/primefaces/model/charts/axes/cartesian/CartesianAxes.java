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
package org.primefaces.model.charts.axes.cartesian;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.model.charts.axes.AxesGridLines;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Axes that follow a cartesian grid are known as 'Cartesian Axes'.
 * Cartesian axes are used for line, bar, and bubble charts.
 */
public abstract class CartesianAxes implements Serializable {

    private static final long serialVersionUID = 1L;

    private String position;
    private boolean offset;
    private String id;
    private AxesGridLines grid;
    private CartesianScaleTitle title;
    private boolean stacked;
    private boolean reverse;
    private Number min;
    private Number max;
    private Number suggestedMax;
    private Number suggestedMin;

    /**
     * Gets the position
     *
     * @return position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the position
     *
     * @param position Position of the axis in the chart. Possible values are: 'top', 'left', 'bottom', 'right'
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Gets the offset
     *
     * @return offset
     */
    public boolean isOffset() {
        return offset;
    }

    /**
     * Sets the offset
     *
     * @param offset If true, extra space is added to the both edges and the axis is scaled to fit into the chart area.
     * This is set to true in the bar chart by default.
     */
    public void setOffset(boolean offset) {
        this.offset = offset;
    }

    /**
     * Gets the id
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id The ID is used to link datasets and scale axes together.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the grid
     *
     * @return grid
     */
    public AxesGridLines getGrid() {
        return grid;
    }

    /**
     * Sets the grid
     *
     * @param grid the {@link AxesGridLines} object
     */
    public void setGrid(AxesGridLines grid) {
        this.grid = grid;
    }

    /**
     * Gets the title
     *
     * @return title
     */
    public CartesianScaleTitle getScaleTitle() {
        return title;
    }

    /**
     * Sets the title
     *
     * @param title the {@link CartesianScaleTitle} object
     */
    public void setScaleTitle(CartesianScaleTitle title) {
        this.title = title;
    }

    /**
     * Gets the stacked
     *
     * @return stacked
     */
    public boolean isStacked() {
        return stacked;
    }

    /**
     * Sets the stacked
     *
     * @param stacked Stacked charts can be used to show how one data series is made up of a number of smaller pieces.
     */
    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    /**
     * Reverse the scale.
     *
     * @return reverse
     */
    public boolean isReverse() {
        return reverse;
    }

    /**
     * Reverse the scale.
     *
     * @param reverse Reverse the scale.
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
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
     * Write the common options of cartesian axes
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            ChartUtils.writeDataValue(fsw, "offset", this.offset, false);
            ChartUtils.writeDataValue(fsw, "position", this.position, true);
            ChartUtils.writeDataValue(fsw, "id", this.id, true);
            ChartUtils.writeDataValue(fsw, "stacked", this.stacked, true);
            ChartUtils.writeDataValue(fsw, "reverse", this.reverse, true);
            ChartUtils.writeDataValue(fsw, "min", this.min, true);
            ChartUtils.writeDataValue(fsw, "max", this.max, true);
            ChartUtils.writeDataValue(fsw, "suggestedMax", this.suggestedMax, true);
            ChartUtils.writeDataValue(fsw, "suggestedMin", this.suggestedMin, true);


            if (this.grid != null) {
                fsw.write(",\"grid\":" + this.grid.encode());
            }

            if (this.title != null) {
                fsw.write(",\"title\":" + this.title.encode());
            }

            return fsw.toString();
        }
    }
}
