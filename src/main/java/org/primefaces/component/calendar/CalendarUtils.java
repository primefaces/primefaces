/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

/**
 * Utility class for calendar component
 */
public class CalendarUtils {

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
			//year
			pattern = pattern.replaceAll("yy", "y");
			
			//month
			if(pattern.indexOf("MMM") != -1)
				pattern = pattern.replaceAll("MMM", "M");
			else
				pattern = pattern.replaceAll("M", "m");
			
			//day of week
			pattern = pattern.replaceAll("EEE", "D");

            //time
            if(pattern.indexOf("H") != -1 || pattern.indexOf("h") != -1) {
                pattern = pattern.replaceAll("H", "h").replaceAll("a", "TT");
            }
			
			return pattern;
		}
	}
}