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
package org.primefaces.model.charts.bar;

import org.primefaces.model.charts.ChartOptions;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;

/**
 * Used to set options to Bar chart component.
 */
public class BarChartOptions extends ChartOptions {

    private static final long serialVersionUID = 1L;

    private Number barPercentage = 0.9;
    private Number categoryPercentage;
    private Number barThickness;
    private Number maxBarThickness;
    private boolean offsetGridLines = true;
    private CartesianScales scales;

    /**
     * Gets the barPercentage
     *
     * @return barPercentage
     */
    public Number getBarPercentage() {
        return barPercentage;
    }

    /**
     * Sets the barPercentage
     *
     * @param barPercentage Percent (0-1) of the available width each bar should be within the category width.
     * 1.0 will take the whole category width and put the bars right next to each other.
     */
    public void setBarPercentage(Number barPercentage) {
        this.barPercentage = barPercentage;
    }

    /**
     * Gets the categoryPercentage
     *
     * @return categoryPercentage
     */
    public Number getCategoryPercentage() {
        return categoryPercentage;
    }

    /**
     * Sets the categoryPercentage
     *
     * @param categoryPercentage Percent (0-1) of the available width each category should be within the sample width.
     */
    public void setCategoryPercentage(Number categoryPercentage) {
        this.categoryPercentage = categoryPercentage;
    }

    /**
     * Gets the barThickness
     *
     * @return barThickness
     */
    public Number getBarThickness() {
        return barThickness;
    }

    /**
     * Sets the barThickness
     *
     * @param barThickness Manually set width of each bar in pixels.
     * If not set, the base sample widths are calculated automatically so that they take the full available widths without overlap.
     * Then, the bars are sized using barPercentage and categoryPercentage.
     */
    public void setBarThickness(Number barThickness) {
        this.barThickness = barThickness;
    }

    /**
     * Gets the maxBarThickness
     *
     * @return maxBarThickness
     */
    public Number getMaxBarThickness() {
        return maxBarThickness;
    }

    /**
     * Sets the maxBarThickness
     *
     * @param maxBarThickness Set this to ensure that bars are not sized thicker than this.
     */
    public void setMaxBarThickness(Number maxBarThickness) {
        this.maxBarThickness = maxBarThickness;
    }

    /**
     * Gets the offsetGridLines
     *
     * @return offsetGridLines
     */
    public boolean isOffsetGridLines() {
        return offsetGridLines;
    }

    /**
     * Sets the offsetGridLines
     *
     * @param offsetGridLines If true, the bars for a particular data point fall between the grid lines.
     * The grid line will move to the left by one half of the tick interval.
     * If false, the grid line will go right down the middle of the bars.
     */
    public void setOffsetGridLines(boolean offsetGridLines) {
        this.offsetGridLines = offsetGridLines;
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
