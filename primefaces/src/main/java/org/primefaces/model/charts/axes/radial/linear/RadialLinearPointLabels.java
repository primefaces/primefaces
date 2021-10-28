/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.model.charts.axes.radial.linear;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.model.charts.ChartFont;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;
import org.primefaces.util.LangUtils;

/**
 * Used to configure the point labels that are shown on the perimeter of the scale.
 */
public class RadialLinearPointLabels implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fontColor;
    private ChartFont font;
    private String fontFamily;
    private Number fontSize = 10;
    private String fontStyle;

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
     * @param fontColor Font color for point labels.
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
     * @param fontFamily Font family to use when rendering labels.
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
     * @param fontSize font size in pixels
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
     * @param fontStyle Font style to use when rendering point labels.
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public ChartFont getFont() {
        if (font == null) {
            font = new ChartFont();
            font.setFamily(this.fontFamily);
            font.setSize(this.fontSize);
            font.setStyle(this.fontStyle);
        }
        return font;
    }

    public void setFont(ChartFont font) {
        this.font = font;
    }

    /**
     * Write the options of point labels on radial linear type
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            fsw.write("{");

            boolean hasComma = false;
            if (LangUtils.isNotBlank(this.fontColor)) {
                ChartUtils.writeDataValue(fsw, "color", this.fontColor, hasComma);
                hasComma = true;
            }

            ChartFont font = getFont();
            font.write(fsw, "font", hasComma);

            fsw.write("}");

            return fsw.toString();
        }
    }
}
