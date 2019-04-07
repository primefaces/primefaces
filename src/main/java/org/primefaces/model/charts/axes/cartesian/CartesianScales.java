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
package org.primefaces.model.charts.axes.cartesian;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.charts.axes.AxesScale;

/**
 * Used to provide scales option has cartesian type
 */
public class CartesianScales extends AxesScale {

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
