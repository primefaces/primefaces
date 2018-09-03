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
package org.primefaces.model.charts.bubble;

import org.primefaces.model.charts.ChartOptions;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;

/**
 * Used to set options to Bubble chart component.
 */
public class BubbleChartOptions extends ChartOptions {

    private CartesianScales scales;

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
