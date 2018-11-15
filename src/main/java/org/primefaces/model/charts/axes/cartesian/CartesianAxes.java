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
