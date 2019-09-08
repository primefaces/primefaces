/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.schedule;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.WidgetBuilder;

public class ScheduleRenderer extends CoreRenderer {

    private static final Logger LOGGER = Logger.getLogger(ScheduleRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Schedule schedule = (Schedule) component;
        String clientId = schedule.getClientId(context);
        String viewId = clientId + "_view";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (params.containsKey(viewId)) {
            schedule.setView(params.get(viewId));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Schedule schedule = (Schedule) component;

        if (context.getExternalContext().getRequestParameterMap().containsKey(schedule.getClientId(context))) {
            encodeEvents(context, schedule);
        }
        else {
            encodeMarkup(context, schedule);
            encodeScript(context, schedule);
        }
    }

    protected void encodeEvents(FacesContext context, Schedule schedule) throws IOException {
        String clientId = schedule.getClientId(context);
        ScheduleModel model = schedule.getValue();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (model instanceof LazyScheduleModel) {
            String startDateParam = params.get(clientId + "_start");
            String endDateParam = params.get(clientId + "_end");

            Long startMillis = Long.valueOf(startDateParam);
            Long endMillis = Long.valueOf(endDateParam);

            ZoneId zoneId = CalendarUtils.calculateZoneId(schedule.getTimeZone());
            LocalDateTime startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), zoneId);
            LocalDateTime endDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endMillis), zoneId);

            LazyScheduleModel lazyModel = ((LazyScheduleModel) model);
            lazyModel.clear(); //Clear old events
            lazyModel.loadEvents(startDate, endDate); //Lazy load events
        }

        encodeEventsAsJSON(context, schedule, model);
    }

    protected void encodeEventsAsJSON(FacesContext context, Schedule schedule, ScheduleModel model) throws IOException {
        ZoneId zoneId = CalendarUtils.calculateZoneId(schedule.getTimeZone());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(zoneId);

        JSONArray jsonEvents = new JSONArray();

        if (model != null) {
            for (ScheduleEvent event : model.getEvents()) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", event.getId());
                jsonObject.put("title", event.getTitle());
                jsonObject.put("start", dateTimeFormatter.format(event.getStartDate()));
                jsonObject.put("end", dateTimeFormatter.format(event.getEndDate()));
                jsonObject.put("allDay", event.isAllDay());
                jsonObject.put("editable", event.isEditable());
                jsonObject.put("className", event.getStyleClass());
                jsonObject.put("description", event.getDescription());
                jsonObject.put("url", event.getUrl());
                jsonObject.put("rendering", event.getRenderingMode());

                if (event.getDynamicProperties() != null) {
                    for (Map.Entry<String, Object> dynaProperty : event.getDynamicProperties().entrySet()) {
                        String key = dynaProperty.getKey();
                        Object value = dynaProperty.getValue();
                        value = ((LocalDateTime) value).format(dateTimeFormatter);
                        jsonObject.put(key, value);
                    }
                }

                jsonEvents.put(jsonObject);
            }
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("events", jsonEvents);

        ResponseWriter writer = context.getResponseWriter();
        writer.write(jsonResponse.toString());
    }

    protected void encodeScript(FacesContext context, Schedule schedule) throws IOException {
        String clientId = schedule.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Schedule", schedule.resolveWidgetVar(context), clientId)
                .attr("defaultView", schedule.getView())
                .attr("locale", schedule.calculateLocale(context).toString())
                .attr("tooltip", schedule.isTooltip(), false)
                .attr("eventLimit", schedule.getValue().isEventLimit(), false)
                .attr("lazyFetching", false);

        Object initialDate = schedule.getInitialDate();
        if (initialDate != null) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            wb.attr("defaultDate", ((LocalDate) initialDate).format(dateTimeFormatter), null);

        }

        if (schedule.isShowHeader()) {
            wb.append(",header:{left:'")
                    .append(schedule.getLeftHeaderTemplate()).append("'")
                    .attr("center", schedule.getCenterHeaderTemplate())
                    .attr("right", schedule.getRightHeaderTemplate())
                    .append("}");
        }
        else {
            wb.attr("header", false);
        }

        boolean isShowWeekNumbers = schedule.isShowWeekNumbers();

        wb.attr("allDaySlot", schedule.isAllDaySlot(), true)
                .attr("slotDuration", schedule.getSlotDuration(), "00:30:00")
                .attr("scrollTime", schedule.getScrollTime(), "06:00:00")
                .attr("timezone", schedule.getClientTimeZone(), null)
                .attr("minTime", schedule.getMinTime(), null)
                .attr("maxTime", schedule.getMaxTime(), null)
                .attr("aspectRatio", schedule.getAspectRatio(), Double.MIN_VALUE)
                .attr("weekends", schedule.isShowWeekends(), true)
                .attr("eventStartEditable", schedule.isDraggable(), true)
                .attr("eventDurationEditable", schedule.isResizable(), true)
                .attr("slotLabelInterval", schedule.getSlotLabelInterval(), null)
                .attr("slotLabelFormat", schedule.getSlotLabelFormat(), null)
                .attr("timeFormat", schedule.getTimeFormat(), null)
                .attr("weekNumbers", isShowWeekNumbers, false)
                .attr("nextDayThreshold", schedule.getNextDayThreshold(), "09:00:00")
                .attr("slotEventOverlap", schedule.isSlotEventOverlap(), true)
                .attr("urlTarget", schedule.getUrlTarget(), "_blank")
                .attr("noOpener", schedule.isNoOpener(), true);

        String columnFormat = schedule.getColumnHeaderFormat() != null ? schedule.getColumnHeaderFormat() : schedule.getColumnFormat();
        if (columnFormat != null) {
            wb.append(",columnFormatOptions:{" + columnFormat + "}");
        }

        String displayEventEnd = schedule.getDisplayEventEnd();
        if (displayEventEnd != null) {
            if (displayEventEnd.equals("true") || displayEventEnd.equals("false")) {
                wb.nativeAttr("displayEventEnd", displayEventEnd);
            }
            else {
                wb.nativeAttr("displayEventEnd", "{" + displayEventEnd + "}");
            }
        }

        String extender = schedule.getExtender();
        if (extender != null) {
            wb.nativeAttr("extender", extender);
        }

        if (isShowWeekNumbers) {
            String weekNumCalculation = schedule.getWeekNumberCalculation();
            String weekNumCalculator = schedule.getWeekNumberCalculator();

            if (weekNumCalculation.equals("custom")) {
                if (weekNumCalculator != null) {
                    wb.append(",weekNumberCalculation: function(){ return ")
                            .append(schedule.getWeekNumberCalculator())
                            .append("}");
                }
            }
            else {
                wb.attr("weekNumberCalculation", weekNumCalculation, "local");
            }
        }

        encodeClientBehaviors(context, schedule);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Schedule schedule) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = schedule.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if (schedule.getStyle() != null) {
            writer.writeAttribute("style", schedule.getStyle(), "style");
        }
        if (schedule.getStyleClass() != null) {
            writer.writeAttribute("class", schedule.getStyleClass(), "style");
        }

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
        if (view != null) {
            writer.writeAttribute("value", view, null);
        }
        writer.endElement("input");
    }
}
