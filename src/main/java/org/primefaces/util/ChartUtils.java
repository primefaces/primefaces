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
package org.primefaces.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.primefaces.model.charts.data.BubblePoint;
import org.primefaces.model.charts.data.NumericPoint;

/**
 * Utilities for Chart components that use chartJs
 */
public class ChartUtils {

    private ChartUtils() {
    }

    /**
     * Write the value of chartJs options
     *
     * @param fsw a writer object that use to write the value of an option
     * @param optionName the name of an option
     * @param value the value of an option
     * @param hasComma specifies whether to add a comma at the beginning of the option name
     * @throws java.io.IOException if writer named fsw is null
     */
    public static void writeDataValue(Writer fsw, String optionName, Object value, boolean hasComma) throws IOException {
        if (value == null) {
            return;
        }

        boolean isList = value instanceof List;

        if (hasComma) {
            fsw.write(",");
        }

        fsw.write("\"" + optionName + "\":");
        if (isList) {
            fsw.write("[");
            for (int i = 0; i < ((List<?>) value).size(); i++) {
                Object item = ((List<?>) value).get(i);
                Object writeText;

                if (item instanceof BubblePoint) {
                    BubblePoint point = (BubblePoint) item;
                    writeText = (i == 0) ? Constants.EMPTY_STRING : ",";
                    writeText += "{\"x\":" + point.getX() + ",\"y\":" + point.getY() + ",\"r\":" + point.getR() + "}";
                }
                else if (item instanceof NumericPoint) {
                    NumericPoint point = (NumericPoint) item;
                    writeText = (i == 0) ? Constants.EMPTY_STRING : ",";
                    writeText += "{\"x\":" + point.getX() + ",\"y\":" + point.getY() + "}";
                }
                else if (item instanceof String) {
                    String escapedText = EscapeUtils.forJavaScript((String) item);
                    writeText = (i == 0) ? "\"" + escapedText + "\"" : ",\"" + escapedText + "\"";
                }
                else {
                    writeText = (i == 0) ? item : "," + item;
                }

                fsw.write(Constants.EMPTY_STRING + writeText);
            }
            fsw.write("]");
        }
        else {
            if (value instanceof String) {
                fsw.write("\"" + EscapeUtils.forJavaScript((String) value) + "\"");
            }
            else {
                fsw.write(Constants.EMPTY_STRING + value);
            }
        }
    }
}
