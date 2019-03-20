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
package org.primefaces.model.charts.line;

import org.primefaces.model.charts.ChartOptions;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;

/**
 * Used to set options to Line chart component.
 */
public class LineChartOptions extends ChartOptions {

    private static final long serialVersionUID = 1L;

    private boolean showLines = true;
    private boolean spanGaps;
    private CartesianScales scales;

    /**
     * Gets the showLines
     *
     * @return showLines
     */
    public boolean isShowLines() {
        return showLines;
    }

    /**
     * Sets the showLines
     *
     * @param showLines If false, the lines between points are not drawn.
     */
    public void setShowLines(boolean showLines) {
        this.showLines = showLines;
    }

    /**
     * Gets the spanGaps
     *
     * @return spanGaps
     */
    public boolean isSpanGaps() {
        return spanGaps;
    }

    /**
     * Sets the spanGaps
     *
     * @param spanGaps If false, NaN data causes a break in the line.
     */
    public void setSpanGaps(boolean spanGaps) {
        this.spanGaps = spanGaps;
    }


    /**
     * Gets the options of cartesian scales
     *
     * @return scales
     */
    public CartesianScales getScales() {
        return scales;
    }

    /**
     * Sets the cartesian scales
     *
     * @param scales The {@link CartesianScales} object
     */
    public void setScales(CartesianScales scales) {
        this.scales = scales;
    }
}
