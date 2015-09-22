/*
 * Copyright 2009-2014 PrimeTek.
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ScheduleRenderer extends CoreRenderer {

    private final static Logger logger = Logger.getLogger(ScheduleRenderer.class.getName());
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
        Schedule schedule = (Schedule) component;
        String clientId = schedule.getClientId(context);
        String viewId = clientId + "_view";
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
        if(params.containsKey(viewId)) {
            schedule.setView(params.get(viewId));
        }
        
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
        TimeZone timeZone = schedule.calculateTimeZone();

        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        iso.setTimeZone(timeZone);
        writer.write("{");
        writer.write("\"events\" : [");
		
        if(model != null) {
            for(Iterator<ScheduleEvent> iterator = model.getEvents().iterator(); iterator.hasNext();) {
                ScheduleEvent event = iterator.next();
                String className = event.getStyleClass();
                String description = event.getDescription();
               
                writer.write("{");
                writer.write("\"id\": \"" + event.getId() + "\"");	
                writer.write(",\"title\": \"" + escapeText(event.getTitle()) + "\"");
                writer.write(",\"start\": \"" + iso.format(event.getStartDate()) + "\"");	
                writer.write(",\"end\": \"" + iso.format(event.getEndDate()) + "\"");
                writer.write(",\"allDay\":" + event.isAllDay());
                writer.write(",\"editable\":" + event.isEditable());
                if(className != null) {
                    writer.write(",\"className\":\"" + className + "\"");
                }
                if(description != null) {
                    writer.write(",\"description\":\"" + escapeText(description) + "\"");
                }
                
                writer.write("}");

                if(iterator.hasNext()) {
                    writer.write(",");
                }
            }
        }
		
		writer.write("]}");	
	}

	protected void encodeScript(FacesContext context, Schedule schedule) throws IOException {
		String clientId = schedule.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Schedule", schedule.resolveWidgetVar(), clientId, "schedule")
            .attr("defaultView", schedule.getView())
            .attr("locale", schedule.calculateLocale(context).toString())
            .attr("tooltip", schedule.isTooltip(), false)
            .attr("eventLimit", ((ScheduleModel) schedule.getValue()).isEventLimit(), false);
        
        Object initialDate = schedule.getInitialDate();
        if(initialDate != null) {
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            
            wb.attr("defaultDate",fmt.format((Date) initialDate), null);
		}
        
        if(schedule.isShowHeader()) {
            wb.append(",header:{left:'")
                .append(schedule.getLeftHeaderTemplate()).append("'")
                .attr("center", schedule.getCenterHeaderTemplate())
                .attr("right", schedule.getRightHeaderTemplate())
                .append("}");
		} 
        else {
            wb.attr("header", false);
		}
        
        //deprecated options
        String slotDuration = schedule.getSlotDuration();
        int slotMinutes = schedule.getSlotMinutes();
        if(slotMinutes != 30) {
            logger.warning("slotMinutes is deprecated, use slotDuration instead.");
            slotDuration = "00:" + slotMinutes + ":00";
        }
        
        String scrollTime = schedule.getScrollTime();
        int firstHour = schedule.getFirstHour();
        if(firstHour != 6) {
            logger.warning("firstHour is deprecated, use scrollTime instead.");
            scrollTime = firstHour + ":00:00";
        }
        
        String clientTimezone = schedule.getClientTimeZone();
        boolean ignoreTimezone = schedule.isIgnoreTimezone();
        if(!ignoreTimezone) {
            logger.warning("ignoreTimezone is deprecated, use clientTimezone instead with 'local' setting.");
            clientTimezone = "local";
        }
        
        wb.attr("allDaySlot", schedule.isAllDaySlot(), true)
            .attr("slotDuration", slotDuration, "00:30:00")
            .attr("scrollTime", scrollTime, "06:00:00")
            .attr("timezone", clientTimezone, null)
            .attr("minTime", schedule.getMinTime(), null)
            .attr("maxTime", schedule.getMaxTime(), null)
            .attr("aspectRatio", schedule.getAspectRatio(), Double.MIN_VALUE)
            .attr("weekends", schedule.isShowWeekends(), true)
            .attr("eventStartEditable", schedule.isDraggable(), true)
            .attr("eventDurationEditable", schedule.isResizable(), true)    
            .attr("axisFormat", schedule.getAxisFormat(), null)
            .attr("timeFormat", schedule.getTimeFormat(), null)
            .attr("weekNumbers", schedule.isShowWeekNumbers(), false);
                
        String columnFormat = schedule.getColumnFormat();
        if(columnFormat != null) {
            wb.attr("columnFormat", columnFormat, null);
        }
        
        String displayEventEnd = schedule.getDisplayEventEnd();
        if(displayEventEnd != null) {
            if(displayEventEnd.equals("true")||displayEventEnd.equals("false"))
                wb.nativeAttr("displayEventEnd", displayEventEnd);
            else
                wb.nativeAttr("displayEventEnd", "{" + displayEventEnd + "}");
        }
        
        String extender = schedule.getExtender();
        if(extender != null) {
            wb.nativeAttr("extender", extender);
        }
            
        encodeClientBehaviors(context, schedule);

        wb.finish();	
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
        
        encodeStateParam(context, schedule);
		
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

    protected void encodeStateParam(FacesContext context, Schedule schedule) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = schedule.getClientId(context) + "_view";
        String view = schedule.getView();
        
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
        writer.writeAttribute("autocomplete", "off", null);
        if(view != null) {
            writer.writeAttribute("value", view, null);
        }
		writer.endElement("input");
    }
}
