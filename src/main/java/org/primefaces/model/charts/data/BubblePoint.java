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
package org.primefaces.model.charts.data;

import java.io.Serializable;

/**
 *
 * Used to set data to Bubble chart component.
 * Bubble chart datasets need to contain a data array of points, each points represented by an object.
 */
public class BubblePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private Number x;
    private Number y;
    private Number r;

    public BubblePoint() { }

    public BubblePoint(Number x, Number y, Number r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    /**
     * Gets the x axis value
     *
     * @return x axis value
     */
    public Number getX() {
        return x;
    }

    /**
     * Sets the x axis value
     *
     * @param x the value of x axis
     */
    public void setX(Number x) {
        this.x = x;
    }

    /**
     * Gets the y axis value
     *
     * @return y axis value
     */
    public Number getY() {
        return y;
    }

    /**
     * Sets the y axis value
     *
     * @param y the value of y axis
     */
    public void setY(Number y) {
        this.y = y;
    }

    /**
     * Gets the radius
     *
     * @return radius value
     */
    public Number getR() {
        return r;
    }

    /**
     * Sets the radius
     *
     * @param r Bubble radius in pixels (not scaled).
     */
    public void setR(Number r) {
        this.r = r;
    }
}
