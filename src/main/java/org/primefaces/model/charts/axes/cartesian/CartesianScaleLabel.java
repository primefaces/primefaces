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

import java.io.IOException;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The scale label configuration is nested under the scale configuration in the scaleLabel key.
 * It defines options for the scale title.
 */
public class CartesianScaleLabel {

    private boolean display = false;
    private String labelString;
    private Object lineHeight;
    private String fontColor;
    private String fontFamily;
    private Number fontSize;
    private String fontStyle;
    private Object padding;

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
     * @param display If true, display the axis title.
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Gets the labelString
     *
     * @return labelString
     */
    public String getLabelString() {
        return labelString;
    }

    /**
     * Sets the labelString
     *
     * @param labelString The text for the title.
     */
    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }

    /**
     * Gets the lineHeight
     *
     * @return lineHeight
     */
    public Object getLineHeight() {
        return lineHeight;
    }

    /**
     * Sets the lineHeight
     *
     * @param lineHeight Height of an individual line of text
     */
    public void setLineHeight(Object lineHeight) {
        this.lineHeight = lineHeight;
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
     * @param fontColor Font color for scale title.
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
     * @param fontFamily Font family for the scale title, follows CSS font-family options.
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
     * @param fontSize Font size for scale title.
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
     * @param fontStyle Font style for the scale title, follows CSS font-style options.
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * Gets the padding
     *
     * @return padding
     */
    public Object getPadding() {
        return padding;
    }

    /**
     * Sets the padding
     *
     * @param padding Padding to apply around scale labels. Only top and bottom are implemented.
     */
    public void setPadding(Object padding) {
        this.padding = padding;
    }

    /**
     * Write the options of scale label
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write("{");

            ChartUtils.writeDataValue(fsw, "display", this.display, false);
            ChartUtils.writeDataValue(fsw, "labelString", this.labelString, true);
            ChartUtils.writeDataValue(fsw, "lineHeight", this.lineHeight, true);
            ChartUtils.writeDataValue(fsw, "fontColor", this.fontColor, true);
            ChartUtils.writeDataValue(fsw, "fontFamily", this.fontFamily, true);
            ChartUtils.writeDataValue(fsw, "fontSize", this.fontSize, true);
            ChartUtils.writeDataValue(fsw, "fontStyle", this.fontStyle, true);
            ChartUtils.writeDataValue(fsw, "padding", this.padding, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
