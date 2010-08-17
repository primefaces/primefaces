/*
 * Copyright 2010 Prime Technology.
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class ScheduleRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Schedule schedule = (Schedule) component;
		ScheduleModel model = (ScheduleModel) schedule.getValue();
		String clientId = schedule.getClientId(facesContext);
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();

		if(params.containsKey(clientId + "_ajaxEvent")) {
            
			String selectedEventParam = clientId + "_selectedEventId";
			String selectedDateParam = clientId + "_selectedDate";
			String changedEventParam = clientId + "_changedEventId";
			
			//Event select
			if(params.containsKey(selectedEventParam)) {
				String eventId = params.get(selectedEventParam);
				ScheduleEvent selectedEvent = model.getEvent(eventId);
				schedule.queueEvent(new ScheduleEntrySelectEvent(schedule, selectedEvent));
			}
			//Date Select
			else if(params.containsKey(selectedDateParam)) {
				String dateAsString = params.get(selectedDateParam);
				schedule.queueEvent(new DateSelectEvent(component, new Date(Long.valueOf(dateAsString))));
			}
			//Event dragdrop or resize
			else if(params.containsKey(changedEventParam)) {
				String eventId = params.get(changedEventParam);
				ScheduleEvent changedEvent = model.getEvent(eventId);
				int dayDelta = Integer.valueOf(params.get(clientId + "_dayDelta"));
				int minuteDelta = Integer.valueOf(params.get(clientId + "_minuteDelta"));
				Calendar calendar = Calendar.getInstance();
				boolean isResize = params.containsKey(clientId + "_resized");
				
				if(!isResize) {
					calendar.setTime(changedEvent.getStartDate());
					calendar.roll(Calendar.DATE, dayDelta);
					changedEvent.getStartDate().setTime(calendar.getTimeInMillis());
				}
				
				calendar = Calendar.getInstance();
				calendar.setTime(changedEvent.getEndDate());
				calendar.roll(Calendar.DATE, dayDelta);
				changedEvent.getEndDate().setTime(calendar.getTimeInMillis());
				
				if(isResize)
					schedule.queueEvent(new ScheduleEntryResizeEvent(schedule, changedEvent, dayDelta, minuteDelta));
				else
					schedule.queueEvent(new ScheduleEntryMoveEvent(schedule, changedEvent, dayDelta, minuteDelta));
			}
		}
	}

    @Override
	public void encodeEnd(FacesContext fc, UIComponent component) throws IOException {
		Schedule schedule = (Schedule) component;

        if(fc.getExternalContext().getRequestParameterMap().containsKey(schedule.getClientId(fc))) {
            encodeEvents(fc, schedule);
        } else {
            encodeMarkup(fc, schedule);
            encodeScript(fc, schedule);
        }
	}
	
	protected void encodeEvents(FacesContext facesContext, Schedule schedule) throws IOException {
		String clientId = schedule.getClientId(facesContext);
		ScheduleModel model = (ScheduleModel) schedule.getValue();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		String startDateParam = params.get(clientId + "_start");
		String endDateParam = params.get(clientId + "_end");

		if(model instanceof LazyScheduleModel) {
			Date startDate = new Date(Long.valueOf(startDateParam));
			Date endDate = new Date(Long.valueOf(endDateParam));
			
			LazyScheduleModel lazyModel = ((LazyScheduleModel) model);
			lazyModel.clear();							//Clear old events
			lazyModel.loadEvents(startDate, endDate);	//Lazy load events
		}
		
		encodeEventsAsJSON(facesContext, model);			
	}
	
	protected void encodeEventsAsJSON(FacesContext facesContext, ScheduleModel model) throws IOException {
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
			if(event.getStyleClass() != null) 
				writer.write(",\"className\":\"" + event.getStyleClass() + "\"");
			
			writer.write("}");

			if(iterator.hasNext())
				writer.write(",");
		}
		
		writer.write("]}");	
	}

	protected void encodeScript(FacesContext facesContext, Schedule schedule) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = schedule.getClientId(facesContext);
		UIComponent form = ComponentUtils.findParentForm(facesContext, schedule);
		if(form == null) {
			throw new FacesException("Schedule: '" + clientId + "' must be inside a form");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function() {");

		writer.write(schedule.resolveWidgetVar() + " = new PrimeFaces.widget.Schedule('" + clientId +"'");
		writer.write(",{");
		
		writer.write("defaultView:'"+ schedule.getView() + "'");
		writer.write(",language:'"+ schedule.calculateLocale(facesContext).getLanguage() + "'");
		writer.write(",formId:'" + form.getClientId(facesContext) + "'");
		writer.write(",url:'" + getActionURL(facesContext) + "'");
		writer.write(",theme:true");
		
		if(schedule.isEditable()) {
			writer.write(",editable:true");

			if(schedule.getOnEventSelectUpdate() != null) writer.write(",onEventSelectUpdate:'" + ComponentUtils.findClientIds(facesContext, schedule, schedule.getOnEventSelectUpdate()) + "'");
			if(schedule.getOnDateSelectUpdate() != null) writer.write(",onDateSelectUpdate:'" + ComponentUtils.findClientIds(facesContext, schedule, schedule.getOnDateSelectUpdate()) + "'");
			if(schedule.getOnEventMoveUpdate() != null) writer.write(",onEventMoveUpdate:'" + ComponentUtils.findClientIds(facesContext, schedule, schedule.getOnEventMoveUpdate()) + "'");
			if(schedule.getOnEventResizeUpdate() != null) writer.write(",onEventResizeUpdate:'" + ComponentUtils.findClientIds(facesContext, schedule, schedule.getOnEventResizeUpdate()) + "'");

            //client side callbacks
            if(schedule.getOnEventSelectStart() != null) writer.write(",onEventSelectStart: function(xhr) {" + schedule.getOnEventSelectStart() + "}");
            if(schedule.getOnEventSelectComplete() != null) writer.write(",onEventSelectComplete: function(xhr, status, args) {" + schedule.getOnEventSelectComplete() + "}");
            if(schedule.getOnDateSelectStart() != null) writer.write(",onDateSelectStart: function(xhr) {" + schedule.getOnDateSelectStart() + "}");
            if(schedule.getOnDateSelectComplete() != null) writer.write(",onDateSelectComplete: function(xhr, status, args) {" + schedule.getOnDateSelectComplete() + "}");
		}
	
		if(schedule.getInitialDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) schedule.getInitialDate());
			writer.write(",year: " + c.get(Calendar.YEAR));
	 		writer.write(",month: " + c.get(Calendar.MONTH));
			writer.write(",date: " + c.get(Calendar.DATE));
		}

		if(schedule.isShowHeader()) {
			writer.write(",header:{");
			writer.write("left:'" + schedule.getLeftHeaderTemplate() + "'");
			writer.write(",center:'" + schedule.getCenterHeaderTemplate() + "'");
			writer.write(",right:'" + schedule.getRightHeaderTemplate() + "'}");
		} else {
			writer.write(",header:false");
		}
		
		if(!schedule.isAllDaySlot()) writer.write(",allDaySlot:false");
		if(schedule.getSlotMinutes() != 30) writer.write(",slotMinutes:" + schedule.getSlotMinutes());
		if(schedule.getFirstHour() != 6) writer.write(",firstHour:" + schedule.getFirstHour());
		if(schedule.getMinTime() != null) writer.write(",minTime:'" + schedule.getMinTime() + "'");
		if(schedule.getMaxTime() != null) writer.write(",maxTime:'" + schedule.getMaxTime() + "'");
		if(schedule.getAspectRatio() != null) writer.write(",aspectRatio: '"+ schedule.getAspectRatio() + "'");
		if(!schedule.isShowWeekends()) writer.write(",weekends:false");
		if(!schedule.isDraggable()) writer.write(",disableDragging:true");
		if(!schedule.isResizable()) writer.write(",disableResizing:true");
		if(schedule.getStartWeekday() != 0) writer.write(",firstDay:" + schedule.getStartWeekday());
		
		writer.write("});});");
		
		writer.endElement("script");		
	}

	protected void encodeMarkup(FacesContext facesContext, Schedule schedule) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = schedule.getClientId(facesContext);

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		if(schedule.getStyle() != null) writer.writeAttribute("style", schedule.getStyle(), "style");
		if(schedule.getStyleClass() != null) writer.writeAttribute("class", schedule.getStyleClass(), "style");
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
    @Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}