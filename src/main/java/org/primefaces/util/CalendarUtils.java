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
package org.primefaces.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.api.UICalendar;
import org.primefaces.convert.DateTimePatternConverter;
import org.primefaces.convert.PatternConverter;

public class CalendarUtils {

    private static final String[] TIME_CHARS = {"H", "K", "h", "k", "m", "s"};

    private static final PatternConverter PATTERN_CONVERTER = new DateTimePatternConverter();

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
     * Try to convert the given value to {@link LocalDate} or return <code>null</code> if there is no appropriate converter for doing so.
     * @param context the faces context
     * @param calendar the calendar component
     * @param value the value to convert
     * @return the {@link LocalDate} object or <code>null</code>
     */
    public static LocalDate getObjectAsLocalDate(FacesContext context, UICalendar calendar, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }

        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        }

        if (value instanceof Date) {
            return convertDate2LocalDate((Date) value);
        }

        String pattern = calendar.calculatePattern();
        if (pattern != null) {
            Locale locale = calendar.calculateLocale(context);
            if (locale != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
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

    /**
     * Try to convert the given value to {@link LocalTime} or return <code>null</code> if there is no appropriate converter for doing so.
     * @param context the faces context
     * @param calendar the calendar component
     * @param value the value to convert
     * @return the {@link LocalTime} object or <code>null</code>
     */
    public static LocalTime getObjectAsLocalTime(FacesContext context, UICalendar calendar, Object value) {
        if (value == null || value instanceof LocalDate) {
            return null;
        }

        if (value instanceof LocalTime) {
            return (LocalTime) value;
        }

        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalTime();
        }

        if (value instanceof Date) {
            return convertDate2LocalTime((Date) value);
        }

        String pattern = calendar.calculatePattern();
        if (pattern != null) {
            Locale locale = calendar.calculateLocale(context);
            if (locale != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
                try {
                    return LocalTime.parse(value.toString(), formatter);
                }
                catch (DateTimeParseException ex) {
                    // NO-OP
                }
            }
        }

        if (calendar.getConverter() != null) {
            try {
                Object obj = calendar.getConverter().getAsObject(context, calendar, value.toString());
                if (obj instanceof LocalTime) {
                    return (LocalTime) obj;
                }
            }
            catch (ConverterException ex) {
                // NO-OP
            }
        }

        Converter converter = context.getApplication().createConverter(value.getClass());
        if (converter != null) {
            Object obj = converter.getAsObject(context, calendar, value.toString());
            if (obj instanceof LocalTime) {
                return (LocalTime) obj;
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
        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            StringBuilder valuesAsString = new StringBuilder();
            String separator = "multiple".equals(calendar.getSelectionMode()) ? "," : " " + calendar.getRangeSeparator() + " ";
            List<?> values = ((List) value);

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
        Converter converter = calendar.getConverter();
        if (converter != null) {
            if (converter instanceof javax.faces.convert.DateTimeConverter) {
                javax.faces.convert.DateTimeConverter nativeConverter = (javax.faces.convert.DateTimeConverter) converter;
                String dateType = nativeConverter.getType();
                // only run converter if type for dates match
                if (value.getClass().getSimpleName().equalsIgnoreCase(dateType)) {
                    return converter.getAsString(context, calendar, value);
                }
            }
            else {
                return converter.getAsString(context, calendar, value);
            }
        }

        if (value instanceof String) {
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
                return ((LocalDateTime) value).format(dateTimeFormatter);
            }
            else if (value instanceof LocalTime) {
                if (calendar instanceof UICalendar) {
                    return LocalDateTime.of(LocalDate.now(), (LocalTime) value).format(dateTimeFormatter);
                }
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
                Class<?> type = ve.getType(context.getELContext());
                if (type != null && type != Object.class && type != Date.class &&
                        type != LocalDate.class && type != LocalDateTime.class && type != LocalTime.class && type != YearMonth.class) {
                    converter = context.getApplication().createConverter(type);
                    if (converter != null) {
                        return converter.getAsString(context, calendar, value);
                    }
                }
            }

            throw new FacesException("Value could be either String, LocalDate, LocalDateTime, LocalTime, YearMonth or java.util.Date (deprecated)");
        }
    }

    /**
     * Converts a java date pattern to a jquery UI date picker pattern.
     *
     * @param pattern Pattern to be converted
     * @return converted pattern
     */
    public static final String convertPattern(String pattern) {
        if (pattern == null) {
            return null;
        }
        else {
            return PATTERN_CONVERTER.convert(pattern);
        }
    }

    /**
     * Write the value of Calendar options
     *
     * @param context
     * @param uicalendar component
     * @param optionName the name of an option
     * @param values the List values of an option
     * @param pattern the pattern for formatting
     * @throws IOException if writer is null
     */
    public static void encodeListValue(FacesContext context, UICalendar uicalendar, String optionName, List<?> values, String pattern) throws IOException {
        if (values == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        writer.write("," + optionName + ":[");
        for (int i = 0; i < values.size(); i++) {
            Object item = values.get(i);
            Object preText = (i == 0) ? "" : ",";

            if (item instanceof Date) {
                writer.write(preText + "\"" + EscapeUtils.forJavaScript(getValueAsString(context, uicalendar, item, pattern)) + "\"");
            }
            else if (item instanceof LocalDate || item instanceof LocalDateTime || item instanceof LocalTime) {
                writer.write(preText + "\"" + EscapeUtils.forJavaScript(getValueAsString(context, uicalendar, item, pattern)) + "\"");
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
                String usertimeZoneStr = (String) usertimeZone;
                if (usertimeZoneStr.length() > 0) {
                    return ZoneId.of(usertimeZoneStr);
                }
                else {
                    return defaultZoneId;
                }
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

    public static ZoneOffset calculateZoneOffset(Object usertimeZone) {
        return calculateZoneOffset(usertimeZone, ZoneId.systemDefault());
    }

    public static ZoneOffset calculateZoneOffset(Object usertimeZone, ZoneId defaultZoneId) {
        ZoneId zoneId = calculateZoneId(usertimeZone, defaultZoneId);
        LocalDateTime now = LocalDateTime.now();
        return zoneId.getRules().getOffset(now);
    }

    private static ZonedDateTime convertDate2ZonedDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        else {
            if (date instanceof java.sql.Date) {
                java.sql.Date sqlDate = (java.sql.Date) date;
                return sqlDate.toLocalDate().atStartOfDay(zoneId);
            }
            else {
                return date.toInstant().atZone(zoneId);
            }
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

    public static LocalTime convertDate2LocalTime(Date date) {
        return convertDate2LocalTime(date, ZoneId.systemDefault());
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

    public static final String removeTime(String pattern) {
        for (String timeChar : TIME_CHARS) {
            if (pattern.contains(timeChar)) {
                pattern = pattern.substring(0, pattern.indexOf(timeChar));
            }
        }
        return pattern.trim();
    }

    public static final boolean hasTime(String pattern) {
        for (String timeChar : TIME_CHARS) {
            if (pattern.contains(timeChar)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert ISO-String (@see <a href="https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Date/toISOString">https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Date/toISOString</a>)
     * to LocalDateTime.
     * @param zoneId Target-ZoneId of the LocalDateTime, the isoDateString is converted into.
     * @param isoDateString
     * @return
     */
    public static LocalDateTime toLocalDateTime(ZoneId zoneId, String isoDateString) {
        if (isoDateString == null) {
            return null;
        }

        Instant instant = Instant.parse(isoDateString);
        ZonedDateTime utc = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return utc.withZoneSameLocal(zoneId).toLocalDateTime();
    }

    /**
     * Calculates NOW based on the calendar's current timezone or will default to system timezone if none set.
     * It will return a LocalDateTime if time units needed else just a LocalDate if no time is needed.
     *
     * @param uicalendar the base calendar to calculate NOW for
     * @return a Temporal representing either a Date or DateTime
     */
    public static Temporal now(UICalendar uicalendar) {
        boolean timeOnly = uicalendar.isTimeOnly();
        ZoneId zone = calculateZoneId(uicalendar.getTimeZone());
        if (timeOnly) {
            return LocalTime.now(zone);
        }
        boolean hasTime = uicalendar.hasTime();
        return hasTime ? LocalDateTime.now(zone) : LocalDate.now(zone);
    }

    /**
     * Calculates NOW based on the calendar's current timezone or will default to system timezone if none set.
     *
     * @param uicalendar the base calendar to calculate NOW for
     * @return an Object representing either a Date or Temporal
     */
    public static Object now(UICalendar uicalendar, Class<?> dateType) {
        Temporal now = now(uicalendar);
        if (dateType.isAssignableFrom(java.util.Date.class)) {
            ZoneId zone = calculateZoneId(uicalendar.getTimeZone());
            java.util.Date date;
            if (now instanceof LocalDate) {
                date = java.util.Date.from(((LocalDate) now).atStartOfDay(zone).toInstant());
            }
            else if (now instanceof LocalTime) {
                date = java.util.Date.from(((LocalTime) now).atDate(LocalDate.now(zone)).atZone(zone).toInstant());
            }
            else {
                date = java.util.Date.from(((LocalDateTime) now).atZone(zone).toInstant());
            }
            return date;
        }
        return now;
    }
}
