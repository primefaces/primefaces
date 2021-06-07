/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.*;

public class ScheduleRenderer extends CoreRenderer {

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

        if (ComponentUtils.isRequestSource(schedule, context) && schedule.isEventRequest(context)) {
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

            ZoneId zoneId = CalendarUtils.calculateZoneId(schedule.getTimeZone());
            LocalDateTime startDate =  CalendarUtils.toLocalDateTime(zoneId, startDateParam);
            LocalDateTime endDate =  CalendarUtils.toLocalDateTime(zoneId, endDateParam);

            LazyScheduleModel lazyModel = ((LazyScheduleModel) model);
            lazyModel.clear(); //Clear old events
            lazyModel.loadEvents(startDate, endDate); //Lazy load events
        }

        encodeEventsAsJSON(context, schedule, model);
    }

    protected void encodeEventsAsJSON(FacesContext context, Schedule schedule, ScheduleModel model) throws IOException {
        ZoneId zoneId = CalendarUtils.calculateZoneId(schedule.getTimeZone());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

        JSONArray jsonEvents = new JSONArray();

        if (model != null) {
            for (ScheduleEvent<?> event : model.getEvents()) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", event.getId());
                if (LangUtils.isNotBlank(event.getGroupId())) {
                    jsonObject.put("groupId", event.getGroupId());
                }
                jsonObject.put("title", event.getTitle());
                jsonObject.put("start", dateTimeFormatter.format(event.getStartDate().atZone(zoneId)));
                jsonObject.put("end", dateTimeFormatter.format(event.getEndDate().atZone(zoneId)));
                jsonObject.put("allDay", event.isAllDay());
                if (event.isDraggable() != null) {
                    jsonObject.put("startEditable", event.isDraggable());
                }
                if (event.isResizable() != null) {
                    jsonObject.put("durationEditable", event.isResizable());
                }
                jsonObject.put("overlap", event.isOverlapAllowed());
                if (event.getStyleClass() != null) {
                    jsonObject.put("classNames", event.getStyleClass());
                }
                if (event.getDescription() != null) {
                    jsonObject.put("description", event.getDescription());
                }
                if (event.getUrl() != null) {
                    jsonObject.put("url", event.getUrl());
                }
                if (event.getDisplay() != null) {
                    jsonObject.put("display", Objects.toString(event.getDisplay(), null));
                }
                if (event.getBackgroundColor() != null) {
                    jsonObject.put("backgroundColor", event.getBackgroundColor());
                }
                if (event.getBorderColor() != null) {
                    jsonObject.put("borderColor", event.getBorderColor());
                }
                if (event.getTextColor() != null) {
                    jsonObject.put("textColor", event.getTextColor());
                }

                if (event.getDynamicProperties() != null) {
                    for (Map.Entry<String, Object> dynaProperty : event.getDynamicProperties().entrySet()) {
                        String key = dynaProperty.getKey();
                        Object value = dynaProperty.getValue();
                        if (value instanceof LocalDateTime) {
                            value = ((LocalDateTime) value).format(dateTimeFormatter);
                        }
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
        Locale locale = schedule.calculateLocale(context);
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("Schedule", schedule)
                .attr("urlTarget", schedule.getUrlTarget(), "_blank")
                .attr("noOpener", schedule.isNoOpener(), true)
                .attr("locale", locale.toString())
                .attr("tooltip", schedule.isTooltip(), false);

        String columnFormat = schedule.getColumnHeaderFormat() != null ? schedule.getColumnHeaderFormat() : schedule.getColumnFormat();
        if (columnFormat != null) {
            wb.append(",columnFormatOptions:{" + columnFormat + "}");
        }

        String extender = schedule.getExtender();
        if (extender != null) {
            wb.nativeAttr("extender", extender);
        }

        wb.append(",options:{");
        wb.append("locale:\"").append(LocaleUtils.toJavascriptLocale(locale)).append("\",");
        wb.append("initialView:\"").append(EscapeUtils.forJavaScript(translateViewName(schedule.getView().trim()))).append("\"");
        wb.attr("dayMaxEventRows", schedule.getValue().isEventLimit(), false);

        //timeGrid offers an additional eventLimit - integer value; see https://fullcalendar.io/docs/v5/dayMaxEventRows; not exposed yet by PF-schedule
        wb.attr("lazyFetching", false);

        Object initialDate = schedule.getInitialDate();
        if (initialDate != null) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            wb.attr("initialDate", ((LocalDate) initialDate).format(dateTimeFormatter), null);
        }

        if (schedule.isShowHeader()) {
            wb.append(",headerToolbar:{start:'")
                    .append(schedule.getLeftHeaderTemplate()).append("'")
                    .attr("center", schedule.getCenterHeaderTemplate())
                    .attr("end", translateViewNames(schedule.getRightHeaderTemplate()))
                    .append("}");
        }
        else {
            wb.attr("headerToolbar", false);
        }

        if (ComponentUtils.isRTL(context, schedule)) {
            wb.attr("direction", "rtl");
        }

        boolean isShowWeekNumbers = schedule.isShowWeekNumbers();

        wb.attr("allDaySlot", schedule.isAllDaySlot(), true)
                .attr("height", schedule.getHeight(), null)
                .attr("slotDuration", schedule.getSlotDuration(), "00:30:00")
                .attr("scrollTime", schedule.getScrollTime(), "06:00:00")
                .attr("timeZone", schedule.getClientTimeZone(), "local")
                .attr("slotMinTime", schedule.getMinTime(), null)
                .attr("slotMaxTime", schedule.getMaxTime(), null)
                .attr("aspectRatio", schedule.getAspectRatio(), Double.MIN_VALUE)
                .attr("weekends", schedule.isShowWeekends(), true)
                .attr("eventStartEditable", schedule.isDraggable())
                .attr("eventDurationEditable", schedule.isResizable())
                .attr("slotLabelInterval", schedule.getSlotLabelInterval(), null)
                .attr("eventTimeFormat", schedule.getTimeFormat(), null) //https://momentjs.com/docs/#/displaying/
                .attr("weekNumbers", isShowWeekNumbers, false)
                .attr("nextDayThreshold", schedule.getNextDayThreshold(), "09:00:00")
                .attr("slotEventOverlap", schedule.isSlotEventOverlap(), true);

        if (LangUtils.isNotBlank(schedule.getSlotLabelFormat())) {
            wb.nativeAttr("slotLabelFormat", schedule.getSlotLabelFormat());
        }

        String displayEventEnd = schedule.getDisplayEventEnd();
        if (displayEventEnd != null) {
            if ("true".equals(displayEventEnd) || "false".equals(displayEventEnd)) {
                wb.nativeAttr("displayEventEnd", displayEventEnd);
            }
            else {
                wb.nativeAttr("displayEventEnd", "{" + displayEventEnd + "}");
            }
        }

        if (isShowWeekNumbers) {
            String weekNumCalculation = schedule.getWeekNumberCalculation();
            String weekNumCalculator = schedule.getWeekNumberCalculator();

            if ("custom".equals(weekNumCalculation)) {
                if (weekNumCalculator != null) {
                    wb.append(",weekNumberCalculation: function(date){ return ")
                            .append(schedule.getWeekNumberCalculator())
                            .append("}");
                }
            }
            else {
                wb.attr("weekNumberCalculation", weekNumCalculation, "local");
            }
        }

        wb.append("}");

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

    /**
     * Translates old FullCalendar-ViewName (<=V3) to new FullCalendar-ViewName (>=V4)
     * @param viewNameOld
     * @return
     */
    private String translateViewName(String viewNameOld) {
        switch (viewNameOld) {
            case "month":
                return "dayGridMonth";
            case "basicWeek":
                return "dayGridWeek";
            case "basicDay":
                return "dayGridDay";
            case "agendaWeek":
                return "timeGridWeek";
            case "agendaDay":
                return "timeGridDay";
            default:
                return viewNameOld;
        }
    }

    private String translateViewNames(String viewNamesOld) {
        if (viewNamesOld != null) {
            return Stream.of(viewNamesOld.split(","))
                    .map(v -> translateViewName(v.trim()))
                    .collect(Collectors.joining(","));
        }

        return null;
    }
}
