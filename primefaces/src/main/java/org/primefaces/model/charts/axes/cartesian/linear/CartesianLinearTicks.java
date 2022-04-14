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
package org.primefaces.model.charts.axes.cartesian.linear;

import java.io.IOException;

import org.primefaces.model.charts.axes.cartesian.CartesianTicks;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide cartesian linear ticks. CartesianLinearTicks extends {@link CartesianTicks}
 */
public class CartesianLinearTicks extends CartesianTicks {

    private static final long serialVersionUID = 1L;

    private Number maxTicksLimit;
    private Number precision;
    private Number stepSize;


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
     * If defined and stepSize is not specified, the step size will be rounded to this many decimal places.
     *
     * @return the current precision
     */
    public Number getPrecision() {
        return precision;
    }

    /**
     * If defined and stepSize is not specified, the step size will be rounded to this many decimal places.
     *
     * @param precision User defined precision for the scale.
     */
    public void setPrecision(Number precision) {
        this.precision = precision;
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
     * Write the options of cartesian linear ticks
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            fsw.write(super.encode());
            ChartUtils.writeDataValue(fsw, "maxTicksLimit", this.maxTicksLimit, true);
            ChartUtils.writeDataValue(fsw, "precision", this.precision, true);
            ChartUtils.writeDataValue(fsw, "stepSize", this.stepSize, true);

            return fsw.toString();
        }
    }
}
