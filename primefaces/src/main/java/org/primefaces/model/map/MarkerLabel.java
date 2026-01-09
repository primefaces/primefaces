/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.model.map;

import java.io.Serializable;
import java.util.Objects;

/**
 * These options specify the appearance of a marker label. A marker label is a string (often a single character)
 * which will appear inside the marker. If you are using it with a custom marker, you can reposition it with
 * the labelOrigin property in the Icon class.
 */
public class MarkerLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The text to be displayed in the label.
     */
    private String text;

    /**
     * The className property of the label's element (equivalent to the element's class attribute).
     * Multiple space-separated CSS classes can be added. Default is no CSS class (an empty string).
     * The font color, size, weight, and family can only be set via the other properties of MarkerLabel.
     * CSS classes should not be used to change the position nor orientation of the label
     * (e.g. using translations and rotations) if also using marker collision management.
     */
    private String className;

    /**
     * The color of the label text. Default color is black.
     */
    private String color;

    /**
     * The font family of the label text (equivalent to the CSS font-family property).
     */
    private String fontFamily;

    /**
     * The font size of the label text (equivalent to the CSS font-size property). Default size is 14px.
     */
    private String fontSize;

    /**
     * The font weight of the label text (equivalent to the CSS font-weight property).
     */
    private String fontWeight;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    @Override
    public String toString() {
        return "MarkerLabel [text=" + text + ", className=" + className + ", color=" + color + ", fontFamily=" + fontFamily + ", fontSize=" + fontSize
                    + ", fontWeight=" + fontWeight + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, color, fontFamily, fontSize, fontWeight, text);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MarkerLabel other = (MarkerLabel) obj;
        return Objects.equals(className, other.className) && Objects.equals(color, other.color) && Objects.equals(fontFamily, other.fontFamily)
                    && Objects.equals(fontSize, other.fontSize) && Objects.equals(fontWeight, other.fontWeight) && Objects.equals(text, other.text);
    }
}
