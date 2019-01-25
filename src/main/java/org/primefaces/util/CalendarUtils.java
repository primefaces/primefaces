/**
 * Copyright 2009-2019 PrimeTek.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.primefaces.convert.DatePatternConverter;
import org.primefaces.convert.PatternConverter;
import org.primefaces.convert.TimePatternConverter;

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

        throw new FacesException("Value could be either String or java.util.Date");
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
            String valuesAsString = "";
            String separator = calendar.getSelectionMode().equals("multiple") ? "," : "-";
            List values = ((List) value);

            for (int i = 0; i < values.size(); i++) {
                if (i != 0) {
                    valuesAsString += separator;
                }

                valuesAsString += getValue(context, calendar, values.get(i), pattern);
            }

            return valuesAsString;
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
        else {
            //Delegate to global defined converter (e.g. joda or java8)
            ValueExpression ve = calendar.getValueExpression("value");
            if (ve != null) {
                Class type = ve.getType(context.getELContext());
                if (type != null && type != Object.class && type != Date.class) {
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
     * @throws java.io.IOException if writer is null
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
            else {
                writer.write(preText + "" + item);
            }
        }

        writer.write("]");
    }

    public static TimeZone calculateTimeZone(Object usertimeZone) {
        return calculateTimeZone(usertimeZone, TimeZone.getDefault());
    }

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
}
