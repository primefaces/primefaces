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
package org.primefaces.model.charts.axes.cartesian.linear;

import java.io.IOException;

import org.primefaces.model.charts.axes.cartesian.CartesianAxes;
import org.primefaces.util.FastStringWriter;

/**
 * The linear scale is use to chart numerical data.
 * It can be placed on either the x or y axis. The scatter chart type automatically configures a line chart to use one of these scales for the x axis.
 * As the name suggests, linear interpolation is used to determine where a value lies on the axis.
 */
public class CartesianLinearAxes extends CartesianAxes {

    private static final long serialVersionUID = 1L;

    private String type;
    private CartesianLinearTicks ticks;

    /**
     * Gets the ticks
     *
     * @return ticks
     */
    public CartesianLinearTicks getTicks() {
        return ticks;
    }

    /**
     * Sets the ticks
     *
     * @param ticks the {@link CartesianLinearTicks} object
     */
    public void setTicks(CartesianLinearTicks ticks) {
        this.ticks = ticks;
    }

    /**
     * Gets the type
     *
     * @return type of cartesian axes
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type
     *
     * @param type the type of cartesian axes
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Write the options of cartesian category axes
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write(super.encode());

            if (this.type != null) {
                fsw.write(",\"type\":\"" + this.type + "\"");
            }

            if (this.ticks != null) {
                fsw.write(",\"ticks\":{");
                fsw.write(this.ticks.encode());
                fsw.write("}");
            }
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}