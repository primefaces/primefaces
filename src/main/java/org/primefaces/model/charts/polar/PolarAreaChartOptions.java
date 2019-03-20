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
package org.primefaces.model.charts.polar;

import org.primefaces.model.charts.ChartOptions;
import org.primefaces.model.charts.axes.radial.RadialScales;

/**
 * Used to set options to PolarArea chart component.
 */
public class PolarAreaChartOptions extends ChartOptions {

    private static final long serialVersionUID = 1L;

    private Number startAngle;
    private boolean animateRotate = true;
    private boolean animateScale = true;
    private RadialScales scales;

    /**
     * Gets the startAngle
     *
     * @return startAngle
     */
    public Number getStartAngle() {
        return startAngle;
    }

    /**
     * Sets the startAngle
     *
     * @param startAngle Starting angle to draw arcs for the first item in a dataset.
     */
    public void setStartAngle(Number startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * Gets the animateRotate
     *
     * @return animateRotate
     */
    public boolean isAnimateRotate() {
        return animateRotate;
    }

    /**
     * Sets the animateRotate
     *
     * @param animateRotate If true, the chart will animate in with a rotation animation.
     */
    public void setAnimateRotate(boolean animateRotate) {
        this.animateRotate = animateRotate;
    }

    /**
     * Gets the animateScale
     *
     * @return animateScale
     */
    public boolean isAnimateScale() {
        return animateScale;
    }

    /**
     * Sets the animateScale
     *
     * @param animateScale If true, will animate scaling the chart from the center outwards.
     */
    public void setAnimateScale(boolean animateScale) {
        this.animateScale = animateScale;
    }

    /**
     * Gets the options of radial scales
     *
     * @return scales
     */
    public RadialScales getScales() {
        return scales;
    }

    /**
     * Sets the radial scales
     *
     * @param scales The {@link RadialScales} object
     */
    public void setScales(RadialScales scales) {
        this.scales = scales;
    }
}
