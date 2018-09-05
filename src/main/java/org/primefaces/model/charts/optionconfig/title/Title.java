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
package org.primefaces.model.charts.optionconfig.title;

import java.io.IOException;
import java.io.Serializable;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The title configuration is passed into the options.title namespace.
 */
public class Title implements Serializable {

    private boolean display = false;
    private String position;
    private Number fontSize;
    private String fontFamily;
    private String fontColor;
    private String fontStyle;
    private Number padding;
    private Object lineHeight;
    private Object text;

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
     * @param display is the title shown
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Gets the position
     *
     * @return position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the position
     *
     * @param position Position of title.
     */
    public void setPosition(String position) {
        this.position = position;
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
     * @param fontSize Font size
     */
    public void setFontSize(Number fontSize) {
        this.fontSize = fontSize;
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
     * @param fontFamily Font family for the title text.
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
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
     * @param fontColor Font color
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
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
     * @param fontStyle Font style
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * Gets the padding
     *
     * @return padding
     */
    public Number getPadding() {
        return padding;
    }

    /**
     * Sets the padding
     *
     * @param padding Number of pixels to add above and below the title text.
     */
    public void setPadding(Number padding) {
        this.padding = padding;
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
     * Gets the text
     *
     * @return text
     */
    public Object getText() {
        return text;
    }

    /**
     * Sets the text
     *
     * @param text Title text to display.
     * If specified as an array, text is rendered on multiple lines.
     */
    public void setText(Object text) {
        this.text = text;
    }

    /**
     * Write the options of Title
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "display", this.display, false);
            ChartUtils.writeDataValue(fsw, "position", this.position, true);
            ChartUtils.writeDataValue(fsw, "fontSize", this.fontSize, true);
            ChartUtils.writeDataValue(fsw, "fontFamily", this.fontFamily, true);
            ChartUtils.writeDataValue(fsw, "fontColor", this.fontColor, true);
            ChartUtils.writeDataValue(fsw, "fontStyle", this.fontStyle, true);
            ChartUtils.writeDataValue(fsw, "padding", this.padding, true);
            ChartUtils.writeDataValue(fsw, "lineHeight", this.lineHeight, true);
            ChartUtils.writeDataValue(fsw, "text", this.text, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
