/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.primefaces.org/elite/license.xhtml
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.primefaces.component.calendar.converter.PatternConverter;
import org.primefaces.component.calendar.converter.DatePatternConverter;
import org.primefaces.component.calendar.converter.TimePatternConverter;

/**
 * Utility class for calendar component
 */
public class CalendarUtils {

    private final static List<PatternConverter> PATTERN_CONVERTERS;
    
    static {
        PATTERN_CONVERTERS = new ArrayList<PatternConverter>();
        PATTERN_CONVERTERS.add(new TimePatternConverter());
        PATTERN_CONVERTERS.add(new DatePatternConverter());
    }
    
    public static String getValueAsString(FacesContext context, Calendar calendar) {
        Object submittedValue = calendar.getSubmittedValue();
        if (submittedValue != null) {
            return submittedValue.toString();
        }
        
        return getValueAsString(context, calendar, calendar.getValue());
    }
    
    public static String getValueAsString(FacesContext context, Calendar calendar, Object value) {        
        if (value == null) {
            return null;
        }
        
        //first ask the converter
        if (calendar.getConverter() != null)
        {
            return calendar.getConverter().getAsString(context, calendar, value);
        }
        else if (value instanceof String){
            return (String) value;
        }
        //Use built-in converter
        else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context)); 
            dateFormat.setTimeZone(calendar.calculateTimeZone());
            
            return dateFormat.format((Date) value);
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
    
    public static String getTimeOnlyValueAsString(FacesContext context, Calendar calendar) {
        Object value = calendar.getValue();
        if (value == null) {
            return null;
        }
        
        //first ask the converter
        if (calendar.getConverter() != null)
        {
            return calendar.getConverter().getAsString(context, calendar, value);
        }
        else if (value instanceof String){
            return (String) value;
        }
        //Use built-in converter
        else if (value instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(calendar.calculateTimeOnlyPattern(), calendar.calculateLocale(context));
            format.setTimeZone(calendar.calculateTimeZone());

            return format.format(calendar.getValue());
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
    public static String convertPattern(String pattern) {
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
    
}