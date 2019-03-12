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
package org.primefaces.model.charts.axes.cartesian.category;

import java.io.IOException;
import java.util.List;

import org.primefaces.model.charts.axes.cartesian.CartesianTicks;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide cartesian category ticks. CartesianCategoryTicks extends {@link CartesianTicks}
 */
public class CartesianCategoryTicks extends CartesianTicks {

    private static final long serialVersionUID = 1L;

    private List<String> labels;
    private String min;
    private String max;

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
     * Gets the min
     *
     * @return min
     */
    public String getMin() {
        return min;
    }

    /**
     * Sets the min
     *
     * @param min The minimum item to display.
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * Gets the max
     *
     * @return max
     */
    public String getMax() {
        return max;
    }

    /**
     * Sets the max
     *
     * @param max The maximum item to display.
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * Write the options of cartesian category ticks
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write(super.encode());
            ChartUtils.writeDataValue(fsw, "labels", this.labels, true);
            ChartUtils.writeDataValue(fsw, "min", this.min, true);
            ChartUtils.writeDataValue(fsw, "max", this.max, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
