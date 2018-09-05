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
package org.primefaces.model.charts.axes;

import java.io.IOException;
import java.io.Serializable;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The tick configuration is nested under the scale configuration.
 */
public class AxesTicks implements Serializable {

    private boolean display = true;
    private String fontColor;
    private String fontFamily;
    private Number fontSize;
    private String fontStyle;
    private boolean reverse;

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
     * @param display If true, show tick marks.
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Gets the fontColor
     *
     * @return fontColor
     */
    public String getFontColor() {
        return fontColor;
    }

    /**
     * Sets the fontColor
     *
     * @param fontColor Font color for tick labels.
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Gets the fontFamily
     *
     * @return fontFamily
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Sets the fontFamily
     *
     * @param fontFamily Font family for the tick labels.
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * Gets the fontSize
     *
     * @return fontSize
     */
    public Number getFontSize() {
        return fontSize;
    }

    /**
     * Sets the fontSize
     *
     * @param fontSize Font size for the tick labels.
     */
    public void setFontSize(Number fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Gets the fontStyle
     *
     * @return fontStyle
     */
    public String getFontStyle() {
        return fontStyle;
    }

    /**
     * Sets the fontStyle
     *
     * @param fontStyle Font style for the tick labels.
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * Gets the reverse
     *
     * @return reverse
     */
    public boolean isReverse() {
        return reverse;
    }

    /**
     * Sets the reverse
     *
     * @param reverse Reverses order of tick labels.
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Write the common ticks options of axes
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "display", this.display, false);
            ChartUtils.writeDataValue(fsw, "fontColor", this.fontColor, true);
            ChartUtils.writeDataValue(fsw, "fontFamily", this.fontFamily, true);
            ChartUtils.writeDataValue(fsw, "fontSize", this.fontSize, true);
            ChartUtils.writeDataValue(fsw, "fontStyle", this.fontStyle, true);
            ChartUtils.writeDataValue(fsw, "reverse", this.reverse, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
