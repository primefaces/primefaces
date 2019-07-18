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
package org.primefaces.model.charts.axes;

import java.io.Serializable;

/**
 * Base Scale configuration for common scale options.
 */
public abstract class AxesScale implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean display = true;
    private Number weight;

    /**
     * Controls the axis global visibility (visible when true, hidden when false).
     *
     * @return display flag
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * Controls the axis global visibility (visible when true, hidden when false).
     *
     * @param display true to display false to hide.
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * The weight used to sort the axis. Higher weights are further away from the chart area.
     *
     * @return the weight
     */
    public Number getWeight() {
        return weight;
    }

    /**
     * The weight used to sort the axis. Higher weights are further away from the chart area.
     *
     * @param weight number to set to the weight to
     */
    public void setWeight(Number weight) {
        this.weight = weight;
    }

}
