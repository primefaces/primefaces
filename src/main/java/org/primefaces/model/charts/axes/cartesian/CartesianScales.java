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
package org.primefaces.model.charts.axes.cartesian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to provide scales option has cartesian type
 */
public class CartesianScales implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CartesianAxes> xAxes;
    private List<CartesianAxes> yAxes;

    public CartesianScales() {
        xAxes = new ArrayList<>();
        yAxes = new ArrayList<>();
    }

    /**
     * Gets xAxes
     *
     * @return xAxes List&#60;{@link CartesianAxes}&#62; list of x axes
     */
    public List<CartesianAxes> getXAxes() {
        return xAxes;
    }

    /**
     * Gets yAxes
     *
     * @return yAxes List&#60;{@link CartesianAxes}&#62; list of y axes
     */
    public List<CartesianAxes> getYAxes() {
        return yAxes;
    }

    /**
     * Adds a new xAxes as {@link CartesianAxes} data to scales
     *
     * @param newXAxesData
     */
    public void addXAxesData(CartesianAxes newXAxesData) {
        xAxes.add(newXAxesData);
    }

    /**
     * Adds a new yAxes as {@link CartesianAxes} data to scales
     *
     * @param newYAxesData
     */
    public void addYAxesData(CartesianAxes newYAxesData) {
        yAxes.add(newYAxesData);
    }
}
