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
