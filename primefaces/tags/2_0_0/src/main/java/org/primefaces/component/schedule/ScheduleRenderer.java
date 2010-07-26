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
package org.primefaces.component.schedule;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.event.ScheduleDateSelectEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class ScheduleRenderer extends CoreRenderer implements PartialRenderer {
	
	@SuppressWarnings("unchecked")
	public void decode(FacesContext facesContext, UIComponent component) {
		Schedule schedule = (Schedule) component;
		String clientId = schedule.getClientId(facesContext);
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		String selectedEventParam = clientId + "_selectedEventId";
		String selectedDateParam = clientId + "_selectedDate";
		String movedEventParam = clientId + "_movedEventId";
		
		if(params.containsKey(schedule.getClientId(facesContext))) {
			
			if(params.containsKey(selectedEventParam)) {
				String eventId = params.get(selectedEventParam);
				ScheduleModel<ScheduleEvent> model = (ScheduleModel<ScheduleEvent>) schedule.getValue();
				ScheduleEvent selectedEvent = model.getEvent(eventId);
				schedule.queueEvent(new ScheduleEntrySelectEvent(schedule, selectedEvent));
				
			} else if(params.containsKey(selectedDateParam)) {
				try {
					String dateAsString = params.get(selectedDateParam);
					SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
					schedule.queueEvent(new ScheduleDateSelectEvent(component, format.parse(dateAsString)));
				} catch (ParseException e) {
					throw new FacesException("Cannot parse selected date", e);
				}
				
			} else if(params.containsKey(movedEventParam)) {
				String eventId = params.get(movedEventParam);
				int dayDelta = Integer.valueOf(params.get("dayDelta"));
				//int minuteDelta = Integer.valueOf(params.get("minuteDelta"));
				
				ScheduleModel<ScheduleEvent> model = (ScheduleModel<ScheduleEvent>) schedule.getValue();
				ScheduleEvent movedEvent = model.getEvent(eventId);
				Calendar calendar = Calendar.getInstance();
				
				if(!params.containsKey("resized")) {
					calendar.setTime(movedEvent.getStartDate());
					calendar.roll(Calendar.DATE, dayDelta);
					movedEvent.getStartDate().setTime(calendar.getTimeInMillis());
				}
				
				calendar = Calendar.getInstance();
				calendar.setTime(movedEvent.getEndDate());
				calendar.roll(Calendar.DATE, dayDelta);
				movedEvent.getEndDate().setTime(calendar.getTimeInMillis());
				
				facesContext.renderResponse();
			}
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Schedule schedule = (Schedule) component;
		
		encodeScript(facesContext, schedule);
		encodeMarkup(facesContext, schedule);
	}
	
	@SuppressWarnings("unchecked")
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		Schedule schedule = (Schedule) component;
		String clientId = schedule.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		String selectedEventParam = clientId + "_selectedEventId";
		String selectedDateParam = clientId + "_selectedDate";
		String startDateParam = clientId + "_start";
		String endDateParam = clientId + "_end";
		
		if(params.containsKey(selectedEventParam) || params.containsKey(selectedDateParam)) {
			renderChildren(facesContext, schedule.getEventDialog());
		} 
		else {	
			ScheduleModel<ScheduleEvent> model = (ScheduleModel<ScheduleEvent>) schedule.getValue();
			
			if(model.isLazy()) {
				try {
					SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
					Date startDate = format.parse(params.get(startDateParam));
					Date endDate = format.parse(params.get(endDateParam));
					
					model.fetchEvents(startDate, endDate);	//Lazy load events
				} catch (ParseException e) {
					throw new FacesException("Cannot parse date", e);
				}
			}
			
			encodeEventsAsJSON(facesContext, model);			
		}
	}
	
	private void encodeEventsAsJSON(FacesContext facesContext, ScheduleModel<ScheduleEvent> model) throws IOException {
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("application/json");
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("{");
		writer.write("\"events\" : [");
		
		for(Iterator<ScheduleEvent> iterator = model.getEvents().iterator(); iterator.hasNext();) {
			ScheduleEvent event = iterator.next();
			
			writer.write("{");
			writer.write("\"id\": \"" + event.getId() + "\"");	
			writer.write(",\"title\": \"" + event.getTitle() + "\"");
			writer.write(",\"start\": " + event.getStartDate().getTime());	
			writer.write(",\"end\": " + event.getEndDate().getTime());	
			writer.write(",\"allDay\":" + event.isAllDay());
			if(event.getStyleClass() != null) writer.write(",\"className\":\"" + event.getStyleClass() + "\"");
			writer.write("}");

			if(iterator.hasNext())
				writer.write(",");
		}
		
		writer.write("]}");	
	}

	private void encodeScript(FacesContext facesContext, Schedule schedule) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = schedule.getClientId(facesContext);
		String scheduleVar = createUniqueWidgetVar(facesContext, schedule);
		UIComponent form = ComponentUtils.findParentForm(facesContext, schedule);
		if(form == null) {
			throw new FacesException("Schedule : \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(scheduleVar + " = new PrimeFaces.widget.Schedule('" + clientId +"'");
		writer.write(",{");
		
		writer.write("defaultView:'"+ schedule.getView() + "'");
		writer.write(",language:'"+ schedule.calculateLocale(facesContext).getLanguage() + "'");
		writer.write(",formId:'" + form.getClientId(facesContext) + "'");
		writer.write(",url:'" + getActionURL(facesContext) + "'");
		
		if(schedule.getInitialDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) schedule.getInitialDate());
			writer.write(",year: " + c.get(Calendar.YEAR));
	 		writer.write(",month: " + c.get(Calendar.MONTH));
			writer.write(",date: " + c.get(Calendar.DATE));
		}

		if(schedule.getAspectRatio() != null) writer.write(",aspectRatio: '"+ schedule.getAspectRatio() + "'");
		if(schedule.isTheme()) writer.write(",theme:true");
		if(!schedule.isShowWeekends()) writer.write(",weekends:false");
		if(schedule.isEditable()) writer.write(",editable:true");
		if(schedule.getEventDialog() != null) writer.write(",hasEventDialog:true");
		if(!schedule.isDraggable()) writer.write(",disableDragging:true");
		if(!schedule.isResizable()) writer.write(",disableResizing:true");
		
		writer.write(",header: {left: 'prev,next today', center: 'title', right: 'month,agendaWeek,agendaDay'}");
		
		writer.write("});});\n");
		
		writer.endElement("script");		
	}

	private void encodeMarkup(FacesContext facesContext, Schedule schedule) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = schedule.getClientId(facesContext);
		ScheduleEventDialog dialog = schedule.getEventDialog();

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		if(schedule.getStyle() != null) writer.writeAttribute("style", schedule.getStyle(), "style");
		if(schedule.getStyleClass() != null) writer.writeAttribute("class", schedule.getStyleClass(), "style");
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		writer.endElement("div");
		
		if(dialog != null) {
			encodeDialogMarkup(facesContext, schedule, dialog);
		}
		
		writer.endElement("div");
	}
	
	private void encodeDialogMarkup(FacesContext facesContext, Schedule schedule, ScheduleEventDialog dialog) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = schedule.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_dialogContainer", null);
		
		if(dialog.getHeader() != null) {
			writer.startElement("div", null);
			writer.writeAttribute("class", "hd", null);
			writer.write(dialog.getHeader());
			writer.endElement("div");
		}
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_dialogContainer_bd", null);
		writer.writeAttribute("class", "bd", null);
		
		renderChildren(facesContext, dialog);
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}