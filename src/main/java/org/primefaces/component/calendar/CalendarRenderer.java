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

import org.primefaces.event.DateSelectEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;

public class CalendarRenderer extends CoreRenderer{
	
	public static String DEFAULT_POPUP_ICON = "/primefaces/calendar/calendar_icon.png";
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Calendar calendar = (Calendar) component;
		String clientId = calendar.getClientId(facesContext);
		
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_input");
		
		calendar.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Calendar calendar = (Calendar) component;
		
		encodeMarkup(facesContext, calendar);
		encodeScript(facesContext, calendar);
	}
	
	protected void encodeScript(FacesContext facesContext, Calendar calendar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String calendarVar = createUniqueWidgetVar(facesContext, calendar);
		String popupVar = null;
		boolean isPopup = calendar.isPopup();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
				
		if(isPopup) {
			popupVar = calendarVar + "_popup";
			writer.write(popupVar + " = new YAHOO.widget.Overlay('" + clientId + "_popupContainer', {visible: false, context:['" + clientId + "_popupButton','tl','bl']});");
			writer.write(popupVar + ".render();\n");
		}
		
		if(calendar.getPages() > 1)
			writer.write(calendarVar + " = new PrimeFaces.widget.CalendarGroup('" + clientId + "','" + clientId + "_container',");
		else
			writer.write(calendarVar + " = new PrimeFaces.widget.Calendar('" + clientId + "','" + clientId + "_container',");
		
		encodeConfig(facesContext, calendar, calendarVar);
		
		writer.endElement("script");
	}
	
	protected void encodeConfig(FacesContext facesContext, Calendar calendar, String calendarVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String valueAsString = CalendarUtils.getValueAsString(facesContext, calendar);
		String pagedate = CalendarUtils.getPageDate(calendar);
		String maxdate = CalendarUtils.getDateAsString(calendar, calendar.getMaxdate());
		String mindate = CalendarUtils.getDateAsString(calendar, calendar.getMindate());

		writer.write("{language:'" + calendar.calculateLocale(facesContext).getLanguage() + "'");
		
		if(calendar.getPages() != -1) writer.write(",pages:" + calendar.getPages());
		if(pagedate != null) writer.write(",pagedate:'" + pagedate + "'");
		if(maxdate != null) writer.write(",maxdate:'" + maxdate + "'");
		if(mindate != null) writer.write(",mindate:'" + mindate + "'");
		if(calendar.isNavigator()) writer.write(",navigator:true");
		if(calendar.getSelection().equals("multiple")) writer.write(",multi_select:true");
		if(calendar.isClose()) writer.write(",close:true");
		if(calendar.getTitle() != null) writer.write(",title:'" + escapeText(calendar.getTitle()) + "'");
		if(!calendar.isShowWeekdays()) writer.write(",show_weekdays:false");
		if(calendar.getMonthFormat() != null) writer.write(",locale_months:'" + calendar.getMonthFormat() + "'");
		if(calendar.getWeekdayFormat() != null) writer.write(",locale_weekdays:'" + calendar.getWeekdayFormat() + "'");
		if(calendar.getStartWeekday() != 0) writer.write(",start_weekday:" + calendar.getStartWeekday());
		if(calendar.isShowWeekHeader()) writer.write(",show_week_header:true");
		if(calendar.isShowWeekFooter()) writer.write(",show_week_footer:true");
		if(calendar.isPopup()) writer.write(",popup:" + calendarVar + "_popup");
		
		String delimiter = CalendarUtils.getPatternDelimeter(calendar);
		writer.write(",date_delim:'" + delimiter + "'");
		writer.write(",year_pos:" + CalendarUtils.getDateFieldPosition(calendar, delimiter, "y"));
		writer.write(",month_pos:" + CalendarUtils.getDateFieldPosition(calendar, delimiter, "M"));
		writer.write(",day_pos:" + CalendarUtils.getDateFieldPosition(calendar, delimiter, "d"));
		
		//Ajax selection
		if(calendar.getSelectListener() != null) {
			writer.write(",hasSelectListener:true");
			
			UIComponent form = ComponentUtils.findParentForm(facesContext, calendar);
			if(form == null)
				throw new FacesException("Calendar \"" + calendar.getClientId(facesContext) + "\" must be enclosed with a form when using ajax selection.");
			
			writer.write(",formId:'" + form.getClientId(facesContext) + "'");
			writer.write(",actionURL:'" + getActionURL(facesContext) + "'");
			
			if(calendar.getOnSelectUpdate() != null)
				writer.write(",onselectUpdate:'" + ComponentUtils.findClientIds(facesContext, calendar, calendar.getOnSelectUpdate()) + "'");	
		}
		
		writer.write("});\n");
		
		if(valueAsString != null) {
			writer.write(calendarVar + ".cfg.setProperty('selected', '" + valueAsString + "');\n");
		}
	}

	protected void encodeMarkup(FacesContext facesContext, Calendar calendar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		
		writer.startElement("span", calendar);
		writer.writeAttribute("id", clientId, null);
		if(calendar.getStyle() != null) writer.writeAttribute("style", calendar.getStyle(), null);
		if(calendar.getStyleClass() != null) writer.writeAttribute("class", calendar.getStyleClass(), null);
		
		encodeInputField(facesContext, calendar, clientId);
		
		if(calendar.isPopup()) {
			encodePopupButtonMarkup(facesContext,calendar);
			encodePopupContainer(facesContext, clientId);
		}
		else {
			encodeCalendarContainer(facesContext, clientId);
		}
		
		writer.endElement("span");
	}
	
	protected void encodePopupContainer(FacesContext facesContext, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_popupContainer", null);
		
		encodeCalendarContainer(facesContext, clientId);
		
		writer.endElement("div");
	}
	
	protected void encodeCalendarContainer(FacesContext facesContext, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container" , null);
		writer.endElement("div");
	}
	
	protected void encodeInputField(FacesContext facesContext, Calendar calendar, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String valueAsString = CalendarUtils.getValueAsString(facesContext, calendar);
		String inputId = clientId + "_input";
		
		writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		
		if(calendar.isPopup()) {
			writer.writeAttribute("type", "text", null);
			
			String style = "vertical-align:middle;";
			if(calendar.getInputStyle() != null)
				style = style + calendar.getInputStyle();
			
			writer.writeAttribute("style", style, null);
			
			if(calendar.getInputStyleClass() != null) writer.writeAttribute("class", calendar.getInputStyleClass(), null);
		}
		else
			writer.writeAttribute("type", "hidden", null);
			
		writer.writeAttribute("value", valueAsString, null);
		writer.writeAttribute("readonly", calendar.isReadOnlyInputText(), null);
		writer.endElement("input");
	}

	protected void encodePopupButtonMarkup(FacesContext facesContext, Calendar calendar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String iconId = clientId + "_popupButton";
		String iconSrc = calendar.getPopupIcon() != null ? getResourceURL(facesContext, calendar.getPopupIcon()) : ResourceUtils.getResourceURL(facesContext, DEFAULT_POPUP_ICON);
		
		writer.startElement("img", null);
		writer.writeAttribute("id", iconId, null);
		writer.writeAttribute("name", iconId, null);
		writer.writeAttribute("src", iconSrc , null);
		writer.writeAttribute("style", "vertical-align:middle;cursor:pointer;margin:0;padding:0;", null);
		writer.endElement("img");
	}

	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object value) throws ConverterException {
		Calendar calendar = (Calendar) component;
		String submittedValue = (String) value;
		
		//Delegate to user supplied converter if defined
		if(calendar.getConverter() != null) {
			return calendar.getConverter().getAsObject(facesContext, calendar, submittedValue);
		}

		if(isValueBlank(submittedValue))
			return null;

		Locale locale = calendar.calculateLocale(facesContext);
		SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern(), locale);
		format.setTimeZone(calendar.calculateTimeZone());

		try {
			if(calendar.getSelection().equalsIgnoreCase("single")) {
				Date convertedValue;
				
				convertedValue = format.parse(submittedValue);
				
				calendar.queueEvent(new DateSelectEvent(calendar, convertedValue));		//Queue a date select event for any listeners
				
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
}