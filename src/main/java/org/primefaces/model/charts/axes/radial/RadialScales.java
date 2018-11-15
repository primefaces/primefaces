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
package org.primefaces.model.charts.axes.radial;

import java.io.Serializable;

import org.primefaces.model.charts.axes.AxesGridLines;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearAngleLines;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearPointLabels;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearTicks;

/**
 * Used to provide scales option has radial type
 */
public class RadialScales implements Serializable {

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
