/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.model.charts.pie;

import org.primefaces.model.charts.ChartOptions;

/**
 * Used to set options to Pie chart component.
 */
public class PieChartOptions extends ChartOptions {

    private static final long serialVersionUID = 1L;

    private Object cutout;
    private Number rotation;
    private Number circumference;
    private boolean animateRotate = true;
    private boolean animateScale;

    /**
     * Gets the cutout
     *
     * @return cutout  accepts pixels as number and percent as string ending with %.
     */
    public Object getCutout() {
        return cutout;
    }

    /**
     * Sets the cutout
     *
     * @param cutout accepts pixels as number and percent as string ending with %.
     */
    public void setCutout(Object cutout) {
        this.cutout = cutout;
    }

    /**
     * Gets the rotation
     *
     * @return rotation
     */
    public Number getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation
     *
     * @param rotation Starting angle to draw arcs from.
     */
    public void setRotation(Number rotation) {
        this.rotation = rotation;
    }

    /**
     * Gets the circumference
     *
     * @return circumference
     */
    public Number getCircumference() {
        return circumference;
    }

    /**
     * Sets the circumference
     *
     * @param circumference Sweep to allow arcs to cover
     */
    public void setCircumference(Number circumference) {
        this.circumference = circumference;
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
}
