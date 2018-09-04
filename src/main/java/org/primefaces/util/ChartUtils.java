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
package org.primefaces.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.primefaces.model.charts.bubble.BubblePoint;

/**
 * Utilities for Chart components that use chartJs
 */
public class ChartUtils {

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
                    writeText = (i == 0) ? "" : ",";
                    writeText += "{\"x\":" + point.getX() + ",\"y\":" + point.getY() + ",\"r\":" + point.getR() + "}";
                }
                else if (item instanceof String) {
                    String escapedText = EscapeUtils.forJavaScript((String) item);
                    writeText = (i == 0) ? "\"" + escapedText + "\"" : ",\"" + escapedText + "\"";
                }
                else {
                    writeText = (i == 0) ? item : "," + item;
                }

                fsw.write("" + writeText);
            }
            fsw.write("]");
        }
        else {
            if (value instanceof String) {
                fsw.write("\"" + EscapeUtils.forJavaScript((String) value) + "\"");
            }
            else {
                fsw.write("" + value);
            }
        }
    }
}
