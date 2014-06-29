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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import org.primefaces.component.calendar.converter.PatternConverter;
import org.primefaces.component.calendar.converter.DateConverter;
import org.primefaces.component.calendar.converter.TimeConverter;

/**
 * Utility class for calendar component
 */
public class CalendarUtils {

    static Map<String,PatternConverter> CONVERTER;
    
    static {
        CONVERTER = new HashMap<String,PatternConverter>();
        CONVERTER.put("TIME", new TimeConverter());
        CONVERTER.put("DATE", new DateConverter());
    }
    
	public static String getValueAsString(FacesContext context, Calendar calendar) {
		Object submittedValue = calendar.getSubmittedValue();
		if(submittedValue != null) {
			return submittedValue.toString();
		}
		
		Object value = calendar.getValue();
		if(value == null) {
			return null;
		} else {
			//first ask the converter
			if(calendar.getConverter() != null) {
				return calendar.getConverter().getAsString(context, calendar, value);
			}
			//Use built-in converter
			else {
				SimpleDateFormat dateFormat = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context));
				dateFormat.setTimeZone(calendar.calculateTimeZone());
				
				return dateFormat.format(value);
			}
		}
	}
	
	public static String getValueAsString(FacesContext context, Calendar calendar, Object value) {		
		if(value == null) {
			return null;
		}
		
		if(value instanceof String){
			return (String) value;
		} 
        else if(value instanceof Date) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context)); 
			dateFormat.setTimeZone(calendar.calculateTimeZone());
			
			return dateFormat.format((Date) value);
		} 
        else {
			throw new FacesException("Value could be either String or java.util.Date");
		}
	}
    
    public static String getTimeOnlyValueAsString(FacesContext context, Calendar calendar) {
        Object value = calendar.getValue();
        if(value == null) {
            return null;
        }
        
        if(value instanceof String){
			return (String) value;
		} else if(value instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(calendar.calculateTimeOnlyPattern(), calendar.calculateLocale(context));
            format.setTimeZone(calendar.calculateTimeZone());

            return format.format(calendar.getValue());
        }
		else {
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
		if(pattern == null)
			return null;
		else {
            //time            
            if(pattern.indexOf("h") != -1 || pattern.indexOf("H") != -1 || pattern.indexOf("m") != -1 || pattern.indexOf("s") != -1 
                 || pattern.indexOf("S") != -1  || pattern.indexOf("a") != -1)
                pattern = CONVERTER.get("TIME").Convert(pattern);
            
			//year || month || day 
            if(pattern.indexOf("y") != -1 || pattern.indexOf("E") != -1 || pattern.indexOf("D") != -1 || pattern.indexOf("M") != -1)
                pattern = CONVERTER.get("DATE").Convert(pattern);
            
			return pattern;
		}
	}
    
}