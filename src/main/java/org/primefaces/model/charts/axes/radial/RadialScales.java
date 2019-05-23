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
package org.primefaces.model.charts.axes.radial;

import org.primefaces.model.charts.axes.AxesGridLines;
import org.primefaces.model.charts.axes.AxesScale;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearAngleLines;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearPointLabels;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearTicks;

/**
 * Used to provide scales option has radial type
 */
public class RadialScales extends AxesScale {

    private static final long serialVersionUID = 1L;

    private RadialLinearAngleLines angelLines;
    private AxesGridLines gridLines;
    private RadialLinearPointLabels pointLabels;
    private RadialLinearTicks ticks;

    /**
     * Gets the angelLines
     *
     * @return angelLines
     */
    public RadialLinearAngleLines getAngelLines() {
        return angelLines;
    }

    /**
     * Sets the angelLines
     *
     * @param angelLines the {@link RadialLinearAngleLines} object
     */
    public void setAngelLines(RadialLinearAngleLines angelLines) {
        this.angelLines = angelLines;
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
     * Gets the pointLabels
     *
     * @return pointLabels
     */
    public RadialLinearPointLabels getPointLabels() {
        return pointLabels;
    }

    /**
     * Sets the pointLabels
     *
     * @param pointLabels the {@link RadialLinearPointLabels} object
     */
    public void setPointLabels(RadialLinearPointLabels pointLabels) {
        this.pointLabels = pointLabels;
    }

    /**
     * Gets the ticks
     *
     * @return ticks
     */
    public RadialLinearTicks getTicks() {
        return ticks;
    }

    /**
     * Sets the ticks
     *
     * @param ticks the {@link RadialLinearTicks} object
     */
    public void setTicks(RadialLinearTicks ticks) {
        this.ticks = ticks;
    }
}
