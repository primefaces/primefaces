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
package org.primefaces.convert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.faces.convert.ConverterException;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.HTML;

public class DateTimeConverter extends javax.faces.convert.DateTimeConverter implements ClientConverter {

    private Map<String, Object> metadata;

    @Override
    public Map<String, Object> getMetadata() {
        if (metadata == null) {
            String pattern = this.getPattern();
            String type = this.getType();
            String dateStyle = this.getDateStyle();
            String timeStyle = this.getTimeStyle();

            metadata = new HashMap<>();

            if (pattern != null) {
                metadata.put(HTML.ValidationMetadata.PATTERN, CalendarUtils.convertPattern(pattern));
            }

            if (type != null) {
                String typeCleared = type;
                if ("localDate".equalsIgnoreCase(typeCleared)) {
                    typeCleared = "date";
                }
                else if ("localTime".equalsIgnoreCase(typeCleared)) {
                    typeCleared = "time";
                }
                else if ("localDateTime".equalsIgnoreCase(typeCleared)) {
                    typeCleared = "both";
                }

                metadata.put(HTML.ValidationMetadata.DATETIME_TYPE, typeCleared);
                if (pattern == null) {
                    DateFormat df = null;
                    if ("both".equals(type)) {
                        df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                        metadata.put(HTML.ValidationMetadata.DATE_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                        df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                        metadata.put(HTML.ValidationMetadata.TIME_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    }
                    else if ("date".equals(type)) {
                        df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                        metadata.put(HTML.ValidationMetadata.DATE_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    }
                    else if ("time".equals(type)) {
                        df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                        metadata.put(HTML.ValidationMetadata.TIME_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    }
                }
            }
        }

        return metadata;
    }

    @Override
    public String getConverterId() {
        return DateTimeConverter.CONVERTER_ID;
    }

    private int getStyle(String style) {
        if ("default".equals(style)) {
            return (DateFormat.DEFAULT);
        }
        else if ("short".equals(style)) {
            return (DateFormat.SHORT);
        }
        else if ("medium".equals(style)) {
            return (DateFormat.MEDIUM);
        }
        else if ("long".equals(style)) {
            return (DateFormat.LONG);
        }
        else if ("full".equals(style)) {
            return (DateFormat.FULL);
        }
        else {
            throw new ConverterException("Invalid style '" + style + '\'');
        }
    }
}
