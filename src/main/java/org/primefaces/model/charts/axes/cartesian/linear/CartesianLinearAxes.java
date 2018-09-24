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