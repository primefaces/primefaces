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
package org.primefaces.model.charts.optionconfig.legend;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The chart legend displays data about the datasets that are appearing on the chart.
 * The legend configuration is passed into the options.legend namespace. The
 * global options for the chart legend is defined in Chart.defaults.global.legend.
 */
public class Legend implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean display = true;
    private String position;
    private boolean fullWidth = true;
    private boolean reverse;
    private LegendLabel labels;

    /**
     * Is the legend shown?
     *
     * @return the display
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * Set if the legend is shown.
     *
     * @param display the display to set
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Position of the legend. top, left, bottom, right.
     *
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Position of the legend. top, left, bottom, right.
     *
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Marks that this box should take the full width of the canvas (pushing down other boxes).
     * This is unlikely to need to be changed in day-to-day use.
     *
     * @return the fullWidth
     */
    public boolean isFullWidth() {
        return fullWidth;
    }

    /**
     * Marks that this box should take the full width of the canvas (pushing down other boxes).
     * This is unlikely to need to be changed in day-to-day use.
     *
     * @param fullWidth the fullWidth to set
     */
    public void setFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
    }

    /**
     * Legend will show datasets in reverse order.
     *
     * @return the reverse
     */
    public boolean isReverse() {
        return reverse;
    }

    /**
     * Legend will show datasets in reverse order.
     *
     * @param reverse the reverse to set
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * The legend label configuration is nested below the legend configuration using the labels key.
     *
     * @return the labels
     */
    public LegendLabel getLabels() {
        return labels;
    }

    /**
     * The legend label configuration is nested below the legend configuration using the labels key.
     *
     * @param labels the labels to set
     */
    public void setLabels(LegendLabel labels) {
        this.labels = labels;
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
            ChartUtils.writeDataValue(fsw, "display", this.display, false);
            ChartUtils.writeDataValue(fsw, "position", this.position, true);
            ChartUtils.writeDataValue(fsw, "fullWidth", this.fullWidth, true);
            ChartUtils.writeDataValue(fsw, "reverse", this.reverse, true);

            if (this.labels != null) {
                fsw.write(",\"labels\":" + this.labels.encode());
            }
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }

}
