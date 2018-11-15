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
