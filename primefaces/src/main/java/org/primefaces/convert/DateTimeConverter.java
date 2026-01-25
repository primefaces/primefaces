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
package org.primefaces.convert;

import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.HTML;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;

public class DateTimeConverter extends jakarta.faces.convert.DateTimeConverter implements ClientConverter {

    private Map<String, Object> metadata;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (component instanceof DatePicker) {
            return getAsObject(context, (DatePicker) component, value);
        }
        return super.getAsObject(context, component, value);
    }

    public Object getAsObject(FacesContext context, DatePicker datePicker, String value) {
        if (value == null) {
            return null;
        }
        String type = getType();
        boolean isDate = "date".equals(type);
        boolean isLocalDateTime = "localDateTime".equals(type);
        if (isDate || isLocalDateTime) {
            try {
                DateTimeFormatter formatter = getDateTimeFormatter(context, datePicker);
                LocalTime time = datePicker.isShowTime()
                        ? LocalTime.parse(value, formatter)
                        : LocalTime.MIDNIGHT;
                ZoneId zone = CalendarUtils.calculateZoneId(datePicker.getTimeZone());
                LocalDateTime localDateTime = LocalDate.parse(value, formatter)
                        .atTime(time)
                        .atZone(zone)
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
                if (isDate) {
                    return CalendarUtils.convertLocalDateTime2Date(localDateTime);
                }
                return localDateTime;
            }
            catch (Exception ex) {
                throw new ConverterException(ex.getMessage(), ex);
            }
        }
        return super.getAsObject(context, datePicker, value);
    }

    protected DateTimeFormatter getDateTimeFormatter(FacesContext context, DatePicker datePicker) {
        String pattern = datePicker.calculatePattern();
        Locale locale = datePicker.calculateLocale(context);
        return DateTimeFormatter.ofPattern(pattern, locale);
    }

    @Override
    public Map<String, Object> getMetadata() {
        if (metadata != null) {
            return metadata;
        }

        String pattern = this.getPattern();
        String type = this.getType();
        String dateStyle = this.getDateStyle();
        String timeStyle = this.getTimeStyle();

        metadata = new HashMap<>();

        if (pattern != null) {
            metadata.put(HTML.ValidationMetadata.PATTERN, pattern);
        }

        if (type != null) {
            String typeCleared = type.toLowerCase();

            switch (typeCleared) {
                case "localdate":
                    typeCleared = "date";
                    break;
                case "localtime":
                    typeCleared = "time";
                    break;
                case "localdatetime":
                    typeCleared = "both";
                    break;
                default:
                    //keep typeCleared as it is
                    break;
            }


            metadata.put(HTML.ValidationMetadata.DATETIME_TYPE, typeCleared);
            if (pattern == null) {
                DateFormat df = null;
                if ("both".equals(type) || "date".equals(type)) {
                    df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                    metadata.put(HTML.ValidationMetadata.DATE_STYLE_PATTERN, ((SimpleDateFormat) df).toPattern());
                }
                if ("both".equals(type) || "time".equals(type)) {
                    df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                    metadata.put(HTML.ValidationMetadata.TIME_STYLE_PATTERN, ((SimpleDateFormat) df).toPattern());
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
