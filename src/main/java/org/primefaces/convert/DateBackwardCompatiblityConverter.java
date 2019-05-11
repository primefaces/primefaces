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
import org.primefaces.util.MessageFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Converter which provides Backward-Compatibility with {@link Date}
 * Only required for {@link org.primefaces.component.datepicker.DatePicker} with mode selectionMode = multiple and selectionMode = range.
 */
@FacesConverter("pf.DateBackwardCompatiblityConverter")
public class DateBackwardCompatiblityConverter implements Converter {

    //TODO: define shortcut for this converter

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object object = null;

        if (component instanceof UICalendar) {
            UICalendar uicalendar = (UICalendar) component;

            //migrated code from org.primefaces.component.calendar.BaseCalendarRenderer, method getConvertedValue
            SimpleDateFormat format = new SimpleDateFormat(uicalendar.calculatePattern(), uicalendar.calculateLocale(context));
            format.setLenient(false);
            format.setTimeZone(CalendarUtils.calculateTimeZone(uicalendar.getTimeZone()));
            try {
                return format.parse(value);
            }
            catch (ParseException e) {
                uicalendar.setConversionFailed(true);

                FacesMessage message = null;
                Object[] params = new Object[3];
                params[0] = value;
                params[1] = format.format(new Date());
                params[2] = MessageFactory.getLabel(context, uicalendar);

                if (uicalendar.isTimeOnly()) {
                    message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
                }
                else if (uicalendar.hasTime()) {
                    message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
                }
                else {
                    message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
                }

                throw new ConverterException(message);
            }
        }
        else {
            //TODO: write error, throw exception, ...
        }

        return object;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String string = null;
        DateTimeFormatter formatter = null;

        if (value instanceof String) {
            string = (String) value;
        }
        else if (component instanceof UICalendar) {
            //inverted code from org.primefaces.component.calendar::getConvertedValue - keep synchronized!

            UICalendar uicalendar = (UICalendar) component;

            if (value instanceof LocalDate) {
                formatter =  DateTimeFormatter.ofPattern(uicalendar.calculateTimeOnlyPattern(), uicalendar.calculateLocale(context));
                return ((LocalDate) value).format(formatter);
            }
            else if (value instanceof LocalDateTime) {
                //TODO: remove temporary (ugly) work-around for adding fixed time-pattern
                formatter =  DateTimeFormatter.ofPattern(uicalendar.calculatePattern() + " HH:mm", uicalendar.calculateLocale(context));
                return ((LocalDateTime) value).format(formatter);
            }
            else if (value instanceof LocalTime) {
                formatter =  DateTimeFormatter.ofPattern(uicalendar.calculateTimeOnlyPattern(), uicalendar.calculateLocale(context));
                return ((LocalTime) value).format(formatter);
            }
            else if (value instanceof Date) {
                SimpleDateFormat format = new SimpleDateFormat(uicalendar.calculatePattern(), uicalendar.calculateLocale(context));
                format.setTimeZone(CalendarUtils.calculateTimeZone(uicalendar.getTimeZone()));
                return format.format((Date) value);
            }
            else {
                //TODO: write error, throw exception, ...
            }
        }
        else {
            //TODO: write error, throw exception, ...
        }

        return string;
    }
}
