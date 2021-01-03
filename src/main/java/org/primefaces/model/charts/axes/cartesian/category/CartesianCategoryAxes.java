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
package org.primefaces.model.charts.axes.cartesian.category;

import java.io.IOException;
import java.util.List;

import org.primefaces.model.charts.axes.cartesian.CartesianAxes;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * If global configuration is used, labels are drawn from one of the label arrays included in the chart data.
 * If only data.labels is defined, this will be used. If data.xLabels is defined and the axis is horizontal, this will be used.
 * Similarly, if data.yLabels is defined and the axis is vertical, this property will be used.
 * Using both xLabels and yLabels together can create a chart that uses strings for both the X and Y axes. CartesianCategoryAxes extends {@link CartesianAxes}.
 */
public class CartesianCategoryAxes extends CartesianAxes {

    private static final long serialVersionUID = 1L;

    private String type;
    private List<String> labels;
    private CartesianCategoryTicks ticks;

    /**
     * Gets the ticks
     *
     * @return ticks
     */
    public CartesianCategoryTicks getTicks() {
        return ticks;
    }

    /**
     * Sets the ticks
     *
     * @param ticks the {@link CartesianCategoryTicks} object
     */
    public void setTicks(CartesianCategoryTicks ticks) {
        this.ticks = ticks;
    }

    /**
     * Gets the type
     *
     * @return type of cartesian axes
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type
     *
     * @param type the type of cartesian axes
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the labels
     *
     * @return labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Sets the labels
     *
     * @param labels List&#60;String&#62; list of labels to display.
     */
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Write the options of cartesian category axes
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            fsw.write(super.encode());

            if (this.type != null) {
                fsw.write(",\"type\":\"" + this.type + "\"");
            }

            ChartUtils.writeDataValue(fsw, "labels", this.labels, true);

            if (this.ticks != null) {
                fsw.write(",\"ticks\":{");
                fsw.write(this.ticks.encode());
                fsw.write("}");
            }

            return fsw.toString();
        }
    }
}
