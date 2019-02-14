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
package org.primefaces.model.charts.optionconfig.elements;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.FastStringWriter;

/**
 * Used to configure element option under chart options
 * While chart types provide settings to configure the styling of each dataset,
 * you sometimes want to style all datasets the same way.
 */
public class Elements implements Serializable {

    private static final long serialVersionUID = 1L;

    private ElementsPoint point;
    private ElementsLine line;
    private ElementsRectangle rectangle;
    private ElementsArc arc;

    /**
     * Gets the point
     *
     * @return point
     */
    public ElementsPoint getPoint() {
        return point;
    }

    /**
     * Sets the point
     *
     * @param point the {@link ElementsPoint} object
     */
    public void setPoint(ElementsPoint point) {
        this.point = point;
    }

    /**
     * Gets the line
     *
     * @return line
     */
    public ElementsLine getLine() {
        return line;
    }

    /**
     * Sets the line
     *
     * @param line the {@link ElementsLine} object
     */
    public void setLine(ElementsLine line) {
        this.line = line;
    }

    /**
     * Gets the rectangle
     *
     * @return rectangle
     */
    public ElementsRectangle getRectangle() {
        return rectangle;
    }

    /**
     * Sets the rectangle
     *
     * @param rectangle the {@link ElementsRectangle} object
     */
    public void setRectangle(ElementsRectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * Gets the arc
     *
     * @return arc
     */
    public ElementsArc getArc() {
        return arc;
    }

    /**
     * Sets the arc
     *
     * @param arc the {@link ElementsArc} object
     */
    public void setArc(ElementsArc arc) {
        this.arc = arc;
    }

    /**
     * Write the options of Elements
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();
        boolean hasComma = false;
        String preString;

        try {
            if (this.point != null) {
                fsw.write("\"point\":{");
                fsw.write(this.point.encode());
                fsw.write("}");

                hasComma = true;
            }

            if (this.line != null) {
                preString = hasComma ? "," : "";
                fsw.write(preString + "\"line\":{");
                fsw.write(this.line.encode());
                fsw.write("}");

                hasComma = true;
            }

            if (this.rectangle != null) {
                preString = hasComma ? "," : "";
                fsw.write(preString + "\"rectangle\":{");
                fsw.write(this.rectangle.encode());
                fsw.write("}");

                hasComma = true;
            }

            if (this.arc != null) {
                preString = hasComma ? "," : "";
                fsw.write(preString + "\"arc\":{");
                fsw.write(this.arc.encode());
                fsw.write("}");
            }
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
