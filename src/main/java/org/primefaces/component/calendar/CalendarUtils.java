/*
 * Copyright 2009 Prime Technology.
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

	public static String getValueAsString(FacesContext facesContext, Calendar calendar) {
		Object value = calendar.getValue();
		
		//Delegate to user supplied converter if defined
		if(calendar.getConverter() != null)
			return calendar.getConverter().getAsString(facesContext, calendar, value);
			
		if(value == null)
			return null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(calendar.getPattern(), calendar.calculateLocale(facesContext));
		dateFormat.setTimeZone(calendar.calculateTimeZone());
		
		if(calendar.getSelection().equalsIgnoreCase("single")) {
			return dateFormat.format(value);
		} else if(calendar.getSelection().equalsIgnoreCase("multiple")) {
			Date[] dates = (Date[]) calendar.getValue();
			StringBuffer datesAsString = new StringBuffer();
			
			for (int i = 0; i < dates.length; i++) {
				if(i == 0)
					datesAsString.append(dateFormat.format(dates[i]));
				else {
					datesAsString.append(",");
					datesAsString.append(dateFormat.format(dates[i]));
				}
			}
			
			return datesAsString.toString();
		} else {
			throw new IllegalArgumentException("Calendar '" + calendar.getClientId(facesContext) + "' has an invalid selection type. Must be either 'single' or 'multiple'");
		}
	}
		
	public static String getPageDate(Calendar calendar) {
		Object pagedate = calendar.getPagedate();
		Object value = calendar.getValue();
		boolean multiple = calendar.getSelection().equalsIgnoreCase("multiple");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", calendar.calculateLocale(FacesContext.getCurrentInstance())); 
		dateFormat.setTimeZone(calendar.calculateTimeZone());
		
		if(pagedate != null) {
			if(pagedate instanceof String) {
				return (String) pagedate;
			} else if(pagedate instanceof Date) {
				return dateFormat.format((Date) pagedate);
			} else {
				throw new FacesException("PageDate could be either String or java.util.Date");
			}
		} else if(value != null) {
			Date dateToDisplay;
			
			if(multiple)
				dateToDisplay = ((Date[]) value)[0];
			else 
				dateToDisplay = (Date) value;
				
			return dateFormat.format(dateToDisplay);
		} else {
			return null;
		}
	}
	
	public static String getDateAsString(Calendar calendar, Object date) {		
		if(date == null) {
			return null;
		}
		
		if(date instanceof String){
			return (String) date;
		} else if(date instanceof Date) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(calendar.getPattern(), calendar.calculateLocale(FacesContext.getCurrentInstance())); 
			dateFormat.setTimeZone(calendar.calculateTimeZone());
			
			return dateFormat.format((Date) date);
		} else {
			throw new FacesException("Date could be either String or java.util.Date");
		}
	}
	
	public static String getPatternDelimeter(Calendar calendar) {
		String pattern = calendar.getPattern();
		
		return pattern.split("[A-Za-z]+")[1];
	}
	
	public static int getDateFieldPosition(Calendar calendar, String delimiter, String fieldPrefix) {
		String pattern = calendar.getPattern();
		
		//Special character
		if(delimiter.equals("."))
			delimiter = "\\.";
		
		String[] dateFields = pattern.split(delimiter);
			
		for (int i = 0; i < dateFields.length; i++) {
			if(dateFields[i].startsWith(fieldPrefix))
				return i + 1;							//widget uses 1 instead of 0 as first position
		}
			
		return -1;
	}
}
