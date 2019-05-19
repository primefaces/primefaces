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
    private boolean offset = false;
    private String id;
    private AxesGridLines gridLines;
    private CartesianScaleLabel scaleLabel;
    private boolean stacked;

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
     * Gets the gridLines
     *
     * @return gridLines
     */
    public AxesGridLines getGridLines() {
        return gridLines;
    }

    /**
     * Sets the gridLines
     *
     * @param gridLines the {@link AxesGridLines} object
     */
    public void setGridLines(AxesGridLines gridLines) {
        this.gridLines = gridLines;
    }

    /**
     * Gets the scaleLabel
     *
     * @return scaleLabel
     */
    public CartesianScaleLabel getScaleLabel() {
        return scaleLabel;
    }

    /**
     * Sets the scaleLabel
     *
     * @param scaleLabel the {@link CartesianScaleLabel} object
     */
    public void setScaleLabel(CartesianScaleLabel scaleLabel) {
        this.scaleLabel = scaleLabel;
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
     * Write the common options of cartesian axes
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "offset", this.offset, false);
            ChartUtils.writeDataValue(fsw, "position", this.position, true);
            ChartUtils.writeDataValue(fsw, "id", this.id, true);
            ChartUtils.writeDataValue(fsw, "stacked", this.stacked, true);

            if (this.gridLines != null) {
                fsw.write(",\"gridLines\":" + this.gridLines.encode());
            }

            if (this.scaleLabel != null) {
                fsw.write(",\"scaleLabel\":" + this.scaleLabel.encode());
            }
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
