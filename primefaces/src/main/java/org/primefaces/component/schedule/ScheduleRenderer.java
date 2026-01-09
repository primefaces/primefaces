/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.WidgetBuilder;

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

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import org.json.JSONArray;
import org.json.JSONObject;

@FacesRenderer(rendererType = Schedule.DEFAULT_RENDERER, componentFamily = Schedule.COMPONENT_FAMILY)
public class ScheduleRenderer extends CoreRenderer<Schedule> {

    @Override
    public void decode(FacesContext context, Schedule component) {
        String clientId = component.getClientId(context);
        String viewId = clientId + "_view";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (params.containsKey(viewId)) {
            component.setView(params.get(viewId));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Schedule component) throws IOException {
        if (ComponentUtils.isRequestSource(component, context) && component.isEventRequest(context)) {
            encodeEvents(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeEvents(FacesContext context, Schedule component) throws IOException {
        String clientId = component.getClientId(context);
        ScheduleModel model = component.getValue();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (model instanceof LazyScheduleModel) {
            String startDateParam = params.get(clientId + "_start");
            String endDateParam = params.get(clientId + "_end");

            ZoneId zoneId = CalendarUtils.calculateZoneId(component.getTimeZone());
            LocalDateTime startDate =  CalendarUtils.toLocalDateTime(zoneId, startDateParam);
            LocalDateTime endDate =  CalendarUtils.toLocalDateTime(zoneId, endDateParam);

            LazyScheduleModel lazyModel = ((LazyScheduleModel) model);
            lazyModel.clear(); //Clear old events
            lazyModel.loadEvents(startDate, endDate); //Lazy load events
        }

        encodeEventsAsJSON(context, component, model);
    }

    protected void encodeEventsAsJSON(FacesContext context, Schedule component, ScheduleModel model) throws IOException {
        ZoneId zoneId = CalendarUtils.calculateZoneId(component.getTimeZone());

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
                if (event.getEndDate() != null) {
                    jsonObject.put("end", dateTimeFormatter.format(event.getEndDate().atZone(zoneId)));
                }
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

    protected void encodeScript(FacesContext context, Schedule component) throws IOException {
        Locale locale = component.calculateLocale(context);
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("Schedule", component)
                .attr("urlTarget", component.getUrlTarget(), "_blank")
                .attr("noOpener", component.isNoOpener(), true)
                .attr("locale", locale.toString())
                .attr("tooltip", component.isTooltip(), false);

        String columnFormat = component.getColumnHeaderFormat() != null ? component.getColumnHeaderFormat() : component.getColumnFormat();
        if (columnFormat != null) {
            wb.append(",columnFormatOptions:{" + columnFormat + "}");
        }

        String extender = component.getExtender();
        if (extender != null) {
            wb.nativeAttr("extender", extender);
        }

        wb.append(",options:{");
        wb.append("locale:\"").append(LocaleUtils.toJavascriptLocale(locale)).append("\",");
        wb.append("initialView:\"").append(EscapeUtils.forJavaScript(translateViewName(component.getView().trim()))).append("\"");
        wb.attr("dayMaxEventRows", component.getValue().isEventLimit(), false);

        //timeGrid offers an additional eventLimit - integer value; see https://fullcalendar.io/docs/v5/dayMaxEventRows; not exposed yet by PF-schedule
        wb.attr("lazyFetching", false);

        Object initialDate = component.getInitialDate();
        if (initialDate != null) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            wb.attr("initialDate", ((LocalDate) initialDate).format(dateTimeFormatter), null);
        }

        if (component.isShowHeader()) {
            wb.append(",headerToolbar:{start:'")
                    .append(component.getLeftHeaderTemplate()).append("'")
                    .attr("center", component.getCenterHeaderTemplate())
                    .attr("end", translateViewNames(component.getRightHeaderTemplate()))
                    .append("}");
        }
        else {
            wb.attr("headerToolbar", false);
        }

        if (ComponentUtils.isRTL(context, component)) {
            wb.attr("direction", "rtl");
        }

        boolean isShowWeekNumbers = component.isShowWeekNumbers();

        wb.attr("allDaySlot", component.isAllDaySlot(), true)
                .attr("height", component.getHeight(), null)
                .attr("slotDuration", component.getSlotDuration(), "00:30:00")
                .attr("scrollTime", component.getScrollTime(), "06:00:00")
                .attr("timeZone", component.getClientTimeZone(), "local")
                .attr("slotMinTime", component.getMinTime(), null)
                .attr("slotMaxTime", component.getMaxTime(), null)
                .attr("aspectRatio", component.getAspectRatio(), Double.MIN_VALUE)
                .attr("weekends", component.isShowWeekends(), true)
                .attr("eventStartEditable", component.isDraggable())
                .attr("eventDurationEditable", component.isResizable())
                .attr("selectable", component.isSelectable(), false)
                .attr("slotLabelInterval", component.getSlotLabelInterval(), null)
                .attr("eventTimeFormat", component.getTimeFormat(), null) //https://momentjs.com/docs/#/displaying/
                .attr("weekNumbers", isShowWeekNumbers, false)
                .attr("nextDayThreshold", component.getNextDayThreshold(), "09:00:00")
                .attr("slotEventOverlap", component.isSlotEventOverlap(), true);

        if (LangUtils.isNotBlank(component.getSlotLabelFormat())) {
            wb.nativeAttr("slotLabelFormat",
                    component.getSlotLabelFormat().startsWith("[")
                        ? component.getSlotLabelFormat()
                        : "['" + EscapeUtils.forJavaScript(component.getSlotLabelFormat()) + "']");
        }

        String displayEventEnd = component.getDisplayEventEnd();
        if (displayEventEnd != null) {
            if ("true".equals(displayEventEnd) || "false".equals(displayEventEnd)) {
                wb.nativeAttr("displayEventEnd", displayEventEnd);
            }
            else {
                wb.nativeAttr("displayEventEnd", "{" + displayEventEnd + "}");
            }
        }

        if (isShowWeekNumbers) {
            String weekNumCalculation = component.getWeekNumberCalculation();
            String weekNumCalculator = component.getWeekNumberCalculator();

            if ("custom".equals(weekNumCalculation)) {
                if (weekNumCalculator != null) {
                    wb.append(",weekNumberCalculation: function(date){ return ")
                            .append(component.getWeekNumberCalculator())
                            .append("}");
                }
            }
            else {
                wb.attr("weekNumberCalculation", weekNumCalculation, "local");
            }
        }

        wb.append("}");

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Schedule component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }
        String styleClass = getStyleClassBuilder(context)
                .add("ui-widget")
                .add(component.getStyleClass())
                .build();
        writer.writeAttribute("class", styleClass, "class");

        encodeStateParam(context, component);

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, Schedule component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeStateParam(FacesContext context, Schedule component) throws IOException {
        String id = component.getClientId(context) + "_view";
        String view = component.getView();

        renderHiddenInput(context, id, view, false);
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
