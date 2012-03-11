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
package org.primefaces.component.schedule;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;

public class ScheduleRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Schedule schedule = (Schedule) component;

        if(context.getExternalContext().getRequestParameterMap().containsKey(schedule.getClientId(context))) {
            encodeEvents(context, schedule);
        } 
        else {
            encodeMarkup(context, schedule);
            encodeScript(context, schedule);
        }
	}
	
	protected void encodeEvents(FacesContext context, Schedule schedule) throws IOException {
		String clientId = schedule.getClientId(context);
		ScheduleModel model = (ScheduleModel) schedule.getValue();
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		
        if(model instanceof LazyScheduleModel) {
            String startDateParam = params.get(clientId + "_start");
            String endDateParam = params.get(clientId + "_end");

            Date startDate = new Date(Long.valueOf(startDateParam));
            Date endDate = new Date(Long.valueOf(endDateParam));

            LazyScheduleModel lazyModel = ((LazyScheduleModel) model);
            lazyModel.clear();							//Clear old events
            lazyModel.loadEvents(startDate, endDate);	//Lazy load events
        }
		
		encodeEventsAsJSON(context, schedule, model);
	}
	
	protected void encodeEventsAsJSON(FacesContext context, Schedule schedule, ScheduleModel model) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(schedule.calculateTimeZone());

		writer.write("{");
		writer.write("\"events\" : [");
		
        if(model != null) {
            for(Iterator<ScheduleEvent> iterator = model.getEvents().iterator(); iterator.hasNext();) {
                ScheduleEvent event = iterator.next();
                calendar.setTime(event.getStartDate());
                long startDateInMillis = calendar.getTimeInMillis();

                calendar.setTime(event.getEndDate());
                long endDateInMillis = calendar.getTimeInMillis();

                writer.write("{");
                writer.write("\"id\": \"" + event.getId() + "\"");	
                writer.write(",\"title\": \"" + escapeText(event.getTitle()) + "\"");
                writer.write(",\"start\": " + startDateInMillis);	
                writer.write(",\"end\": " + endDateInMillis);	
                writer.write(",\"allDay\":" + event.isAllDay());
                writer.write(",\"editable\":" + event.isEditable());
                if(event.getStyleClass() != null)
                    writer.write(",\"className\":\"" + event.getStyleClass() + "\"");

                writer.write("}");

                if(iterator.hasNext())
                    writer.write(",");
            }
        }
		
		writer.write("]}");	
	}

	protected void encodeScript(FacesContext context, Schedule schedule) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = schedule.getClientId(context);

        startScript(writer, clientId);
		
		writer.write("$(function() {");
        writer.write("PrimeFaces.cw('Schedule','" + schedule.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");		
		writer.write(",defaultView:'"+ schedule.getView() + "'");
		writer.write(",locale:'"+ schedule.calculateLocale(context) + "'");
        writer.write(",offset:" + schedule.calculateTimeZone().getRawOffset());

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
        if(schedule.getAxisFormat() != null) writer.write(",axisFormat:'" + schedule.getAxisFormat() + "'");
        if(schedule.getTimeFormat() != null) writer.write(",timeFormat:'" + schedule.getTimeFormat() + "'");
        
        //behaviors
        encodeClientBehaviors(context, schedule);
		
		writer.write("});});");
		
		endScript(writer);        		
	}

	protected void encodeMarkup(FacesContext context, Schedule schedule) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = schedule.getClientId(context);

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
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}