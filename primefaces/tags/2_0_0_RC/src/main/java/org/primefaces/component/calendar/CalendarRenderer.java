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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;

public class CalendarRenderer extends CoreRenderer{
	
	private static String DEFAULT_POPUP_ICON = "/primefaces/calendar/calendar_icon.png";
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Calendar calendar = (Calendar) component;
		String clientId = calendar.getClientId(facesContext);
		
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_input");
		
		calendar.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Calendar calendar = (Calendar) component;
		
		encodeScript(facesContext, calendar);
		encodeMarkup(facesContext, calendar);
	}
	
	private void encodeScript(FacesContext facesContext, Calendar calendar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String calendarVar = createUniqueWidgetVar(facesContext, calendar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		
		if(isPopup(calendar)) {
			writer.write(calendarVar + "_popupOverlay = new YAHOO.widget.Overlay('" + clientId + "_popupContainer', {visible: false});\n");
			writer.write(calendarVar + "_popupOverlay.render();\n");
		}

		if(calendar.getPages() > 1)
			writer.write(calendarVar + " = new YAHOO.widget.CalendarGroup('" + clientId + "','" + clientId + "_container', {PAGES:" + calendar.getPages() + "});\n");
		else
			writer.write(calendarVar + " = new YAHOO.widget.Calendar('" + clientId + "','" + clientId + "_container');\n");
		
		encodeSelectionEvent(facesContext, calendar, calendarVar);

		writer.write("PrimeFaces.widget.CalendarUtils.applyLocale(" + calendarVar + ",'" + calendar.calculateLocale(facesContext).getLanguage() + "');\n");
		
		String valueAsString = getValueAsString(facesContext, calendar, "MM/dd/yyyy");
		String pagedate = getPageDate(valueAsString, calendar);
		String maxdate = getDateAsString(calendar, calendar.getMaxdate());
		String mindate = getDateAsString(calendar, calendar.getMindate());
		
		if(valueAsString != null) writer.write(calendarVar + ".cfg.setProperty('selected','" + valueAsString + "');\n");
		if(pagedate != null) writer.write(calendarVar + ".cfg.setProperty('pagedate','" + pagedate + "');\n");
		if(maxdate != null) writer.write(calendarVar + ".cfg.setProperty('maxdate','"  + maxdate + "');\n");
		if(mindate != null) writer.write(calendarVar + ".cfg.setProperty('mindate','"  + mindate + "');\n");
		if(calendar.isNavigator()) writer.write(calendarVar + ".cfg.setProperty('navigator',true);\n");
		if(calendar.getSelection().equals("multiple")) writer.write(calendarVar + ".cfg.setProperty('multi_select',true);\n");
		if(calendar.isClose()) writer.write(calendarVar + ".cfg.setProperty('close', true);\n");
		if(calendar.getTitle() != null) writer.write(calendarVar + ".cfg.setProperty('title','" + calendar.getTitle() + "');\n");
		if(!calendar.isShowWeekdays()) writer.write(calendarVar + ".cfg.setProperty('show_weekdays', false);\n");
		if(calendar.getMonthFormat() != null) writer.write(calendarVar + ".cfg.setProperty('locale_months', '" + calendar.getMonthFormat() + "');\n");
		if(calendar.getWeekdayFormat() != null) writer.write(calendarVar + ".cfg.setProperty('locale_weekdays', '" + calendar.getWeekdayFormat() + "');\n");
		if(calendar.getStartWeekday() != 0) writer.write(calendarVar + ".cfg.setProperty('start_weekday', " + calendar.getStartWeekday() + ");\n");
		if(calendar.isShowWeekHeader()) writer.write(calendarVar + ".cfg.setProperty('show_week_header', true);\n");
		if(calendar.isShowWeekFooter()) writer.write(calendarVar + ".cfg.setProperty('show_week_footer', true);\n");
		
		if(!isPopup(calendar))
			writer.write(calendarVar + ".render();\n");
		else
			writer.write("YAHOO.util.Event.addListener('" + clientId + "_popupButtonImage', 'click', function(e){" + calendarVar + ".render();" 
					+ calendarVar + ".show();" + calendarVar +"_popupOverlay.show();});");
		
		writer.write("});\n");
		
		writer.endElement("script");
	}
	
	private void encodeSelectionEvent(FacesContext facesContext, Calendar calendar, String calendarVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		
		String selectionEvent =".subscribe(PrimeFaces.widget.CalendarUtils.selectEvent, {calendar:" + calendarVar + ",inputId:'" + clientId + "_input'";
		if(calendar.getSelection().equals("multiple")) 
			selectionEvent += ", selection:'multiple'";
		else
			selectionEvent += ", selection:'single'";
	
		String delimiter = getPatternDelimeter(calendar);
		selectionEvent +=", delimiter:'" + delimiter + "'";
		selectionEvent +=", dayFieldIndex:" + getDateFieldPosition(calendar, delimiter, "d");
		selectionEvent +=", monthFieldIndex:" + getDateFieldPosition(calendar, delimiter, "M");
		selectionEvent +=", yearFieldIndex:" + getDateFieldPosition(calendar, delimiter, "y");
		
		if(isPopup(calendar))
			selectionEvent +=", popupOverlay:" + calendarVar + "_popupOverlay}, true);\n";
		else
			selectionEvent +="}, true);\n";

		writer.write(calendarVar + ".selectEvent" + selectionEvent);
		writer.write(calendarVar + ".deselectEvent" + selectionEvent);
	}

	private void encodeMarkup(FacesContext facesContext, Calendar calendar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		
		writer.startElement("div", calendar);
		writer.writeAttribute("id", clientId, null);
		
		encodeInputField(facesContext, calendar, clientId);
		
		if(isPopup(calendar)) {
			encodePopupButtonMarkup(facesContext,calendar);
			encodePopupContainer(facesContext, clientId);
		}
		else {
			encodeCalendarContainer(facesContext, clientId);
		}
		
		writer.endElement("div");
	}
	
	private void encodePopupContainer(FacesContext facesContext, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_popupContainer", null);
		
		encodeCalendarContainer(facesContext, clientId);
		
		writer.endElement("div");
	}
	
	private void encodeCalendarContainer(FacesContext facesContext, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container" , null);
		writer.endElement("div");
	}
	
	private void encodeInputField(FacesContext facesContext, Calendar calendar, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String valueAsString = getValueAsString(facesContext, calendar, calendar.getPattern());
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_input", null);
		writer.writeAttribute("name", clientId + "_input", null);
		
		if(isPopup(calendar))
			writer.writeAttribute("type", "text", null);
		else
			writer.writeAttribute("type", "hidden", null);
			
		writer.writeAttribute("value", valueAsString, null);
		writer.writeAttribute("readonly", "true", null);
		writer.endElement("input");
	}

	private void encodePopupButtonMarkup(FacesContext facesContext, Calendar calendar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String icon = calendar.getPopupIcon() != null ? getResourceURL(facesContext, calendar.getPopupIcon()) : ResourceUtils.getResourceURL(facesContext, DEFAULT_POPUP_ICON);
		
		writer.startElement("img", null);
		writer.writeAttribute("id", clientId + "_popupButtonImage", null);
		writer.writeAttribute("name", clientId + "_popupButtonImage", null);
		writer.writeAttribute("src", icon , null);
		writer.writeAttribute("style", "cursor:pointer;margin:0;padding:0;", null);
		writer.endElement("img");
	}

	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object value) throws ConverterException {
		Calendar calendar = (Calendar) component;
		String submittedValue = (String) value;
		
		//Delegate to user supplied converter if defined
		if(calendar.getConverter() != null) {
			return calendar.getConverter().getAsObject(facesContext, calendar, submittedValue);
		}
			
		if(ComponentUtils.isValueBlank(submittedValue))
			return null;

		Locale locale = calendar.calculateLocale(facesContext);
		SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern(), locale);
		format.setTimeZone(calendar.calculateTimeZone());

		try {
			if(calendar.getSelection().equalsIgnoreCase("single")) {
				Date convertedValue;
				
				convertedValue = format.parse(submittedValue);
				calendar.setSubmittedValue(convertedValue);
				
				return convertedValue;
			}
			else if(calendar.getSelection().equalsIgnoreCase("multiple")) {
				String[] datesAsString = (submittedValue).split(",");
				
				Date[] dates = new Date[datesAsString.length];			
				
				for (int i = 0; i < datesAsString.length; i++) {
					dates[i] = format.parse(datesAsString[i]);
				}
				
				return dates;
			} else
				throw new IllegalArgumentException("Selection mode: " + calendar.getSelection() + " is not valid, use either 'single' or 'multiple'");
		
		} catch (ParseException e) {
			throw new ConverterException(e);
		}
		
	}
	
	String getValueAsString(FacesContext facesContext, Calendar calendar, String pattern) {
		Object value = calendar.getValue();
		
		//Delegate to user supplied converter if defined
		if(calendar.getConverter() != null)
			return calendar.getConverter().getAsString(facesContext, calendar, value);
			
		if(value == null)
			return null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, calendar.calculateLocale(facesContext));
		dateFormat.setTimeZone(calendar.calculateTimeZone());
		
		if(calendar.getSelection().equalsIgnoreCase("single")) {
			return dateFormat.format(value);
		} else if(calendar.getSelection().equalsIgnoreCase("multiple")) {
			Date[] dates = (Date[]) calendar.getValue();
			String datesAsString = "";
			
			for (int i = 0; i < dates.length; i++) {
				if(i == 0)
					datesAsString = dateFormat.format(dates[i]);
				else
					datesAsString = datesAsString + "," + dateFormat.format(dates[i]);
			}
			
			return datesAsString;
		} else {
			throw new IllegalArgumentException("Calendar '" + calendar.getClientId(facesContext) + "' has an invalid selection type. Must be either 'single' or 'multiple'");
		}
	}
	
	boolean isPopup(Calendar calendar) {
		return calendar.getMode().equalsIgnoreCase("popup");
	}
	
	String getPageDate(String date, Calendar calendar) {
		Object pagedate = calendar.getPagedate();
		boolean multiple = calendar.getSelection().equalsIgnoreCase("multiple");
		
		if(pagedate != null) {
			if(pagedate instanceof String) {
				return (String) pagedate;
			} else if(pagedate instanceof Date) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", calendar.calculateLocale(FacesContext.getCurrentInstance())); 
				dateFormat.setTimeZone(calendar.calculateTimeZone());
				
				return dateFormat.format((Date) pagedate);
			} else {
				throw new FacesException("PageDate could be either String or java.util.Date");
			}
		} else if(date != null) {
			String selectedDate;
			
			if(multiple)
				selectedDate = date.split(",")[0];
			else 
				selectedDate = date;
				
			String[] tokens = selectedDate.split("/");
			
			return tokens[0].trim() + "/" + tokens[2].trim();
		} else {
			return null;
		}
	}
	
	String getDateAsString(Calendar calendar, Object date) {		
		if(date == null) {
			return null;
		}
		if(date instanceof String){
			return (String) date;
		} else if(date instanceof Date) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", calendar.calculateLocale(FacesContext.getCurrentInstance())); 
			dateFormat.setTimeZone(calendar.calculateTimeZone());
			
			return dateFormat.format((Date) date);
		} else {
			throw new FacesException("Date could be either String or java.util.Date");
		}
	}
	
	String getPatternDelimeter(Calendar calendar) {
		String pattern = calendar.getPattern();
		
		return pattern.split("[A-Za-z]+")[1];
	}
	
	int getDateFieldPosition(Calendar calendar, String delimiter, String fieldPrefix) {
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