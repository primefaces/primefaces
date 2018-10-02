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
package org.primefaces.model.charts.optionconfig.elements;

import java.io.IOException;
import java.io.Serializable;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Arcs are used in the polar area, doughnut and pie charts.
 */
public class ElementsArc implements Serializable {

    private String backgroundColor;
    private String borderColor;
    private Number borderWidth = 2;

    /**
     * Gets the backgroundColor
     *
     * @return backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the backgroundColor
     *
     * @param backgroundColor Arc fill color.
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets the borderColor
     *
     * @return borderColor
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the borderColor
     *
     * @param borderColor Arc stroke color.
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Gets the borderWidth
     *
     * @return borderWidth
     */
    public Number getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the borderWidth
     *
     * @param borderWidth Arc stroke width.
     */
    public void setBorderWidth(Number borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Write the arc options of Elements
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, false);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
