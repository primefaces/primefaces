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

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Common ticks options for cartesian types
 */
public abstract class CartesianTicks implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean autoSkip = true;
    private Number autoSkipPadding;
    private Number labelOffset;
    private Number minRotation;
    private Number maxRotation;
    private boolean mirror;
    private Number padding;

    /**
     * Gets the autoSkip
     *
     * @return autoSkip
     */
    public boolean isAutoSkip() {
        return autoSkip;
    }

    /**
     * Sets the autoSkip
     *
     * @param autoSkip If true, automatically calculates how many labels that can be shown and hides labels accordingly.
     * Turn it off to show all labels no matter what
     */
    public void setAutoSkip(boolean autoSkip) {
        this.autoSkip = autoSkip;
    }

    /**
     * Gets the autoSkipPadding
     *
     * @return autoSkipPadding
     */
    public Number getAutoSkipPadding() {
        return autoSkipPadding;
    }

    /**
     * Sets the autoSkipPadding
     *
     * @param autoSkipPadding Padding between the ticks on the horizontal axis when autoSkip is enabled.
     * Note: Only applicable to horizontal scales.
     */
    public void setAutoSkipPadding(Number autoSkipPadding) {
        this.autoSkipPadding = autoSkipPadding;
    }

    /**
     * Gets the labelOffset
     *
     * @return labelOffset
     */
    public Number getLabelOffset() {
        return labelOffset;
    }

    /**
     * Sets the labelOffset
     *
     * @param labelOffset
     * Distance in pixels to offset the label from the centre point of the tick (in the y direction for the x axis, and the x direction for the y axis).
     * Note: this can cause labels at the edges to be cropped by the edge of the canvas
     */
    public void setLabelOffset(Number labelOffset) {
        this.labelOffset = labelOffset;
    }

    /**
     * Gets the maxRotation
     *
     * @return maxRotation
     */
    public Number getMaxRotation() {
        return maxRotation;
    }

    /**
     * Sets the maxRotation
     *
     * @param maxRotation Maximum rotation for tick labels when rotating to condense labels.
     * Note: Rotation doesn't occur until necessary. Note: Only applicable to horizontal scales.
     */
    public void setMaxRotation(Number maxRotation) {
        this.maxRotation = maxRotation;
    }

    /**
     * Gets the minRotation
     *
     * @return minRotation
     */
    public Number getMinRotation() {
        return minRotation;
    }

    /**
     * Sets the minRotation
     *
     * @param minRotation Minimum rotation for tick labels.
     * Note: Only applicable to horizontal scales.
     */
    public void setMinRotation(Number minRotation) {
        this.minRotation = minRotation;
    }

    /**
     * Gets the mirror
     *
     * @return mirror
     */
    public boolean isMirror() {
        return mirror;
    }

    /**
     * Sets the mirror
     *
     * @param mirror Flips tick labels around axis, displaying the labels inside the chart instead of outside.
     * Note: Only applicable to vertical scales
     */
    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    /**
     * Gets the padding
     *
     * @return padding
     */
    public Number getPadding() {
        return padding;
    }

    /**
     * Sets the padding
     *
     * @param padding Padding between the tick label and the axis. When set on a vertical axis, this applies in the horizontal (X) direction.
     * When set on a horizontal axis, this applies in the vertical (Y) direction.
     */
    public void setPadding(Number padding) {
        this.padding = padding;
    }

    /**
     * Write the common ticks options
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "autoSkip", this.autoSkip, false);
            ChartUtils.writeDataValue(fsw, "autoSkipPadding", this.autoSkipPadding, true);
            ChartUtils.writeDataValue(fsw, "labelOffset", this.labelOffset, true);
            ChartUtils.writeDataValue(fsw, "maxRotation", this.maxRotation, true);
            ChartUtils.writeDataValue(fsw, "minRotation", this.minRotation, true);
            ChartUtils.writeDataValue(fsw, "mirror", this.mirror, true);
            ChartUtils.writeDataValue(fsw, "padding", this.padding, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
