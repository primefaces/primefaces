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

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.convert.DatePatternConverter;
import org.primefaces.convert.PatternConverter;
import org.primefaces.convert.TimePatternConverter;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class for calendar component
 */
public class CalendarUtils {

    private static final PatternConverter[] PATTERN_CONVERTERS =
            new PatternConverter[]{new TimePatternConverter(), new DatePatternConverter()};

    private CalendarUtils() {
    }

    public static final String getValueAsString(FacesContext context, UICalendar calendar) {
        Object submittedValue = calendar.getSubmittedValue();
        if (submittedValue != null) {
            return submittedValue.toString();
        }

        return getValueAsString(context, calendar, calendar.getValue());
    }

    /**
     * Try to convert the given value to {@link Date} or return <code>null</code> if there is no appropriate converter for doing so.
     * @param context the faces context
     * @param calendar the calendar component
     * @param value the value to convert
     * @return the {@link Date} object or <code>null</code>
     * @deprecated Use eg {@link CalendarUtils#getObjectAsLocalDate(FacesContext, UICalendar, Object)} instead.
     */
    @Deprecated
    public static Date getObjectAsDate(FacesContext context, UICalendar calendar, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            return (Date) value;
        }

        String pattern = calendar.calculatePattern();
        if (pattern != null) {
            Locale locale = calendar.calculateLocale(context);
            if (locale != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
                try {
                    return dateFormat.parse(value.toString());
                }
                catch (ParseException ex) {
                    // NO-OP
                }
            }
        }

        if (calendar.getConverter() != null) {
            try {
                Object obj = calendar.getConverter().getAsObject(context, calendar, value.toString());
                if (obj instanceof Date) {
                    return (Date) obj;
                }
            }
            catch (ConverterException ex) {
                // NO-OP
            }
        }

        Converter converter = context.getApplication().createConverter(value.getClass());
        if (converter != null) {
            Object obj = converter.getAsObject(context, calendar, value.toString());
            if (obj instanceof Date) {
                return (Date) obj;
            }
        }

        // TODO Currently we do not support conversion of jquery datepicker's special dates like 'today' or '+1m +7d'
        // See http://api.jqueryui.com/datepicker/#option-maxDate, https://github.com/primefaces/primefaces/issues/4621

        return null;
    }

    /**
     * Try to convert the given value to {@link Date} or return <code>null</code> if there is no appropriate converter for doing so.
     * @param context the faces context
     * @param calendar the calendar component
     * @param value the value to convert
     * @return the {@link Date} object or <code>null</code>
     */
    public static LocalDate getObjectAsLocalDate(FacesContext context, UICalendar calendar, Object value) {
        //TODO: do we need getObjectAsLocalDateTime additional or instead?

        if (value == null) {
            return null;
        }

        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }

        String pattern = calendar.calculatePattern();
        if (pattern != null) {
            Locale locale = calendar.calculateLocale(context);
            if (locale != null) {
                DateTimeFormatter formatter =  DateTimeFormatter.ofPattern(pattern, locale);
                try {
                    return LocalDate.parse(value.toString(), formatter);
                }
                catch (DateTimeParseException ex) {
                    // NO-OP
                }
            }
        }

        if (calendar.getConverter() != null) {
            try {
                Object obj = calendar.getConverter().getAsObject(context, calendar, value.toString());
                if (obj instanceof LocalDate) {
                    return (LocalDate) obj;
                }
            }
            catch (ConverterException ex) {
                // NO-OP
            }
        }

        Converter converter = context.getApplication().createConverter(value.getClass());
        if (converter != null) {
            Object obj = converter.getAsObject(context, calendar, value.toString());
            if (obj instanceof LocalDate) {
                return (LocalDate) obj;
            }
        }

        // TODO Currently we do not support conversion of jquery datepicker's special dates like 'today' or '+1m +7d'
        // See http://api.jqueryui.com/datepicker/#option-maxDate, https://github.com/primefaces/primefaces/issues/4621

        return null;
    }

    public static final String getValueAsString(FacesContext context, UICalendar calendar, Object value) {
        if (value == null) {
            return null;
        }

        return getValueAsString(context, calendar, value, calendar.calculatePattern());
    }

    public static final String getTimeOnlyValueAsString(FacesContext context, UICalendar calendar) {
        Object value = calendar.getValue();
        if (value == null) {
            return null;
        }

        return getValueAsString(context, calendar, value, calendar.calculateTimeOnlyPattern());
    }

    public static final String getValueAsString(FacesContext context, UICalendar calendar, Object value, String pattern) {
        if (value instanceof List) {
            StringBuilder valuesAsString = new StringBuilder();
            String separator = "multiple".equals(calendar.getSelectionMode()) ? "," : " - ";
            List values = ((List) value);

            for (int i = 0; i < values.size(); i++) {
                if (i != 0) {
                    valuesAsString.append(separator);
                }

                valuesAsString.append(getValue(context, calendar, values.get(i), pattern));
            }

            return valuesAsString.toString();
        }
        else {
            return getValue(context, calendar, value, pattern);
        }
    }

    public static final String getValue(FacesContext context, UICalendar calendar, Object value, String pattern) {
        //first ask the converter
        if (calendar.getConverter() != null) {
            return calendar.getConverter().getAsString(context, calendar, value);
        }
        else if (value instanceof String) {
            return (String) value;
        }
        //Use built-in converter
        else if (value instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(pattern, calendar.calculateLocale(context));
            format.setTimeZone(calculateTimeZone(calendar.getTimeZone()));

            return format.format((Date) value);
        }
        else if (value instanceof LocalDate || value instanceof LocalDateTime || value instanceof LocalTime || value instanceof YearMonth) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, calendar.calculateLocale(context));
            if (value instanceof LocalDate) {
                return ((LocalDate) value).format(dateTimeFormatter);
            }
            else if (value instanceof LocalDateTime) {
                //known issue: https://github.com/primefaces/primefaces/issues/4625
                //known issue: https://github.com/primefaces/primefaces/issues/4626

                //TODO: remove temporary (ugly) work-around for adding fixed time-pattern
                if (calendar instanceof DatePicker) {
                    pattern += " HH:mm";
                }
                dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, calendar.calculateLocale(context));

                return ((LocalDateTime) value).format(dateTimeFormatter);
            }
            else if (value instanceof LocalTime) {
                return ((LocalTime) value).format(dateTimeFormatter);
            }
            else { //if (value instanceof YearMonth)
                return ((YearMonth) value).format(dateTimeFormatter);
            }
        }
        else {
            //Delegate to global defined converter (e.g. joda)
            ValueExpression ve = calendar.getValueExpression("value");
            if (ve != null) {
                Class type = ve.getType(context.getELContext());
                if (type != null && type != Object.class && type != Date.class &&
                        type != LocalDate.class && type != LocalDateTime.class && type != LocalTime.class && type != Year.class) {
                    Converter converter = context.getApplication().createConverter(type);
                    if (converter != null) {
                        return converter.getAsString(context, calendar, value);
                    }
                }
            }

            throw new FacesException("Value could be either String or java.util.Date");
        }
    }

    /**
     * Converts a java date pattern to a jquery date pattern
     *
     * @param pattern Pattern to be converted
     * @return converted pattern
     */
    public static final String convertPattern(String pattern) {
        if (pattern == null) {
            return null;
        }
        else {
            String convertedPattern = pattern;
            for (PatternConverter converter : PATTERN_CONVERTERS) {
                convertedPattern = converter.convert(convertedPattern);
            }

            return convertedPattern;
        }
    }

    /**
     * Write the value of Calendar options
     *
     * @param context
     * @param uicalendar component
     * @param optionName the name of an option
     * @param values the List values of an option
     * @throws IOException if writer is null
     */
    public static void encodeListValue(FacesContext context, UICalendar uicalendar, String optionName, List<Object> values) throws IOException {
        if (values == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        writer.write("," + optionName + ":[");
        for (int i = 0; i < values.size(); i++) {
            Object item = values.get(i);
            Object preText = (i == 0) ? "" : ",";

            if (item instanceof Date) {
                writer.write(preText + "\"" + EscapeUtils.forJavaScript(getValueAsString(context, uicalendar, item)) + "\"");
            }
            else if (item instanceof LocalDate || item instanceof LocalDateTime || item instanceof LocalTime) {
                writer.write(preText + "\"" + EscapeUtils.forJavaScript(getValueAsString(context, uicalendar, item)) + "\"");
            }
            else {
                writer.write(preText + "" + item);
            }
        }

        writer.write("]");
    }

    public static ZoneId calculateZoneId(Object usertimeZone) {
        return calculateZoneId(usertimeZone, ZoneId.systemDefault());
    }

    public static ZoneId calculateZoneId(Object usertimeZone, ZoneId defaultZoneId) {
        if (usertimeZone != null) {
            if (usertimeZone instanceof String) {
                return ZoneId.of((String) usertimeZone);
            }
            else if (usertimeZone instanceof ZoneId) {
                return (ZoneId) usertimeZone;
            }
            else if (usertimeZone instanceof TimeZone) {
                return ((TimeZone) usertimeZone).toZoneId();
            }
            else {
                throw new IllegalArgumentException("TimeZone could be either String or java.time.ZoneId or java.util.TimeZone");
            }
        }
        else {
            return defaultZoneId;
        }
    }

    /**
     * @deprecated  use {@link #calculateZoneId(Object)} instead.
     * @param usertimeZone
     * @return
     */
    @Deprecated
    public static TimeZone calculateTimeZone(Object usertimeZone) {
        return calculateTimeZone(usertimeZone, TimeZone.getDefault());
    }

    /**
     * @deprecated  use {@link #calculateZoneId(Object, ZoneId)} instead.
     * @param usertimeZone
     * @param defaultTimeZone
     * @return
     */
    @Deprecated
    public static TimeZone calculateTimeZone(Object usertimeZone, TimeZone defaultTimeZone) {
        if (usertimeZone != null) {
            if (usertimeZone instanceof String) {
                return TimeZone.getTimeZone((String) usertimeZone);
            }
            else if (usertimeZone instanceof TimeZone) {
                return (TimeZone) usertimeZone;
            }
            else {
                throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
            }
        }
        else {
            return defaultTimeZone;
        }
    }

    private static ZonedDateTime convertDate2ZonedDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        else {
            return date.toInstant().atZone(zoneId);
        }
    }

    public static LocalDate convertDate2LocalDate(Date date) {
        return convertDate2LocalDate(date, ZoneId.systemDefault());
    }

    public static LocalDate convertDate2LocalDate(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        else {
            return convertDate2ZonedDateTime(date, zoneId).toLocalDate();
        }
    }

    public static LocalDateTime convertDate2LocalDateTime(Date date) {
        return convertDate2LocalDateTime(date, ZoneId.systemDefault());
    }

    public static LocalDateTime convertDate2LocalDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        else {
            return convertDate2ZonedDateTime(date, zoneId).toLocalDateTime();
        }
    }

    public static LocalTime convertDate2LocalTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        else {
            return convertDate2ZonedDateTime(date, zoneId).toLocalTime();
        }
    }

    public static Date convertLocalDate2Date(LocalDate localDate, ZoneId zoneId) {
        if (localDate == null) {
            return null;
        }
        else {
            return Date.from(localDate.atStartOfDay(zoneId).toInstant());
        }
    }

    public static Date convertLocalDateTime2Date(LocalDateTime localDateTime) {
        return convertLocalDateTime2Date(localDateTime, ZoneId.systemDefault());
    }

    public static Date convertLocalDateTime2Date(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null)  {
            return null;
        }
        else {
            return Date.from(localDateTime.atZone(zoneId).toInstant());
        }
    }

    public static Date convertLocalTime2Date(LocalTime localTime, ZoneId zoneId) {
        if (localTime == null) {
            return null;
        }
        else {
            return Date.from(localTime.atDate(LocalDate.now()).atZone(zoneId).toInstant());
        }
    }
}
