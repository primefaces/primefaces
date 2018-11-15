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
package org.primefaces.model.charts.pie;

import org.primefaces.model.charts.ChartOptions;

/**
 * Used to set options to Pie chart component.
 */
public class PieChartOptions extends ChartOptions {

    private static final long serialVersionUID = 1L;

    private Number cutoutPercentage;
    private Number rotation;
    private Number circumference;
    private boolean animateRotate = true;
    private boolean animateScale = false;

    /**
     * Gets the cutoutPercentage
     *
     * @return cutoutPercentage
     */
    public Number getCutoutPercentage() {
        return cutoutPercentage;
    }

    /**
     * Sets the cutoutPercentage
     *
     * @param cutoutPercentage The percentage of the chart that is cut out of the middle.
     */
    public void setCutoutPercentage(Number cutoutPercentage) {
        this.cutoutPercentage = cutoutPercentage;
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
