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
package org.primefaces.model.charts.optionconfig.legend;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The legend label configuration is nested below the legend configuration using the labels key.
 */
public class LegendLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Number boxWidth;
    private String fontColor;
    private String fontFamily;
    private Number fontSize;
    private String fontStyle;
    private Number padding;
    private boolean usePointStyle = false;
    /**
     * width of colored box
     *
     * @return the boxWidth
     */
    public Number getBoxWidth() {
        return boxWidth;
    }
    /**
     * width of colored box
     *
     * @param boxWidth the boxWidth to set
     */
    public void setBoxWidth(Number boxWidth) {
        this.boxWidth = boxWidth;
    }
    /**
     * Font color for the label.
     *
     * @return the fontColor
     */
    public String getFontColor() {
        return fontColor;
    }
    /**
     * Font color for the label.
     *
     * @param fontColor the fontColor to set
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
    /**
     * Font family for the label, follows CSS font-family options.
     *
     * @return the fontFamily
     */
    public String getFontFamily() {
        return fontFamily;
    }
    /**
     * Font family for the label, follows CSS font-family options.
     *
     * @param fontFamily the fontFamily to set
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
    /**
     * Font size for the label.
     *
     * @return the fontSize
     */
    public Number getFontSize() {
        return fontSize;
    }
    /**
     * Font size for the label.
     *
     * @param fontSize the fontSize to set
     */
    public void setFontSize(Number fontSize) {
        this.fontSize = fontSize;
    }
    /**
     * Font style for the label, follows CSS font-style options.
     *
     * @return the fontStyle
     */
    public String getFontStyle() {
        return fontStyle;
    }
    /**
     * Font style for the label, follows CSS font-style options.
     *
     * @param fontStyle the fontStyle to set
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }
    /**
     * Padding between rows of colored boxes.
     *
     * @return the padding
     */
    public Number getPadding() {
        return padding;
    }
    /**
     * Padding between rows of colored boxes.
     *
     * @param padding the padding to set
     */
    public void setPadding(Number padding) {
        this.padding = padding;
    }
    /**
     * Label style will match corresponding point style (size is based on fontSize, boxWidth is not used in this case).
     *
     * @return the usePointStyle
     *
     */
    public boolean isUsePointStyle() {
        return usePointStyle;
    }
    /**
     * Label style will match corresponding point style (size is based on fontSize, boxWidth is not used in this case).
     *
     * @param usePointStyle the usePointStyle to set
     */
    public void setUsePointStyle(boolean usePointStyle) {
        this.usePointStyle = usePointStyle;
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

            ChartUtils.writeDataValue(fsw, "usePointStyle", this.usePointStyle, false);
            ChartUtils.writeDataValue(fsw, "fontColor", this.fontColor, true);
            ChartUtils.writeDataValue(fsw, "fontFamily", this.fontFamily, true);
            ChartUtils.writeDataValue(fsw, "fontSize", this.fontSize, true);
            ChartUtils.writeDataValue(fsw, "fontStyle", this.fontStyle, true);
            ChartUtils.writeDataValue(fsw, "padding", this.padding, true);
            ChartUtils.writeDataValue(fsw, "boxWidth", this.boxWidth, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }

}
