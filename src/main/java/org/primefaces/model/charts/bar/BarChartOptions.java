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
