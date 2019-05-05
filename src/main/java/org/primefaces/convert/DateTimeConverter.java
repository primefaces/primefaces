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
package org.primefaces.convert;

import org.primefaces.component.api.UICalendar;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.HTML;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                metadata.put(HTML.VALIDATION_METADATA.PATTERN, CalendarUtils.convertPattern(pattern));
            }

            if (type != null) {
                metadata.put(HTML.VALIDATION_METADATA.DATETIME_TYPE, type);
                if (pattern == null) {
                    DateFormat df = null;
                    if (type.equals("both")) {
                        df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.DATE_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                        df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.TIME_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    }
                    else if (type.equals("date")) {
                        df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.DATE_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    }
                    else if (type.equals("time")) {
                        df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.TIME_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
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

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object object = super.getAsObject(context, component, value);

        /*
        PF Client-Side-Validation triggers this converter. (If no converter is set explicit.)
        So this converter needs to convert java.util.Date <--> LocalDate/LocalDateTime/LocalTime.
        */

        if (object != null) {
            if (component instanceof UICalendar) {
                UICalendar uiCalendar = (UICalendar) component;

                if (object instanceof Date) {
                    Date date = (Date) object;

                    if (uiCalendar.isTimeOnly()) {
                        object = CalendarUtils.convertDate2LocalTime(date);
                    }
                    else if (uiCalendar.hasTime()) {
                        object = CalendarUtils.convertDate2LocalDateTime(date);
                    }
                    else {
                        object = CalendarUtils.convertDate2LocalDate(date);
                    }
                }
            }
        }

        return object;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        /*
        PF Client-Side-Validation triggers this converter. (If no converter is set explicit.)
        So this converter needs to convert java.util.Date <--> LocalDate/LocalDateTime/LocalTime.
        */

        if (value != null) {
            if (component instanceof UICalendar) {
                if (value instanceof LocalDate) {
                    value = CalendarUtils.convertLocalDate2Date((LocalDate) value);
                }
                else if (value instanceof LocalDateTime) {
                    value = CalendarUtils.convertLocalDateTime2Date((LocalDateTime) value);
                }
                else if (value instanceof LocalTime) {
                    value = CalendarUtils.convertLocalTime2Date((LocalTime) value);
                }
            }
        }

        return super.getAsString(context, component, value);
    }
}
