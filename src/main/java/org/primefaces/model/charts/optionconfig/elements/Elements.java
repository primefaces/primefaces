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
import org.primefaces.util.FastStringWriter;

/**
 * Used to configure element option under chart options
 * While chart types provide settings to configure the styling of each dataset,
 * you sometimes want to style all datasets the same way.
 */
public class Elements implements Serializable {

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
