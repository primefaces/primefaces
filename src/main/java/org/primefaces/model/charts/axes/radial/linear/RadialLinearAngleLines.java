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
package org.primefaces.model.charts.axes.radial.linear;

import java.io.IOException;
import java.io.Serializable;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to configure angled lines that radiate from the center of the chart to the point labels.
 */
public class RadialLinearAngleLines implements Serializable {

    private boolean display = true;
    private String color;
    private Number lineWidth;

    /**
     * Gets the display
     *
     * @return display
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * Sets the display
     *
     * @param display if true, angle lines are shown
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Gets the color
     *
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color
     *
     * @param color Color of angled lines
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the lineWidth
     *
     * @return lineWidth
     */
    public Number getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the lineWidth
     *
     * @param lineWidth Width of angled lines
     */
    public void setLineWidth(Number lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Write the options of angled lines on radial linear type
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write("{");

            ChartUtils.writeDataValue(fsw, "display", this.display, false);
            ChartUtils.writeDataValue(fsw, "color", this.color, true);
            ChartUtils.writeDataValue(fsw, "lineWidth", this.lineWidth, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
