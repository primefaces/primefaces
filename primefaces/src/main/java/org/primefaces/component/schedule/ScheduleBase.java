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

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.schedule.ScheduleEntryMoveEvent;
import org.primefaces.event.schedule.ScheduleEntryResizeEvent;
import org.primefaces.event.schedule.ScheduleRangeEvent;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "dateSelect", event = SelectEvent.class, description = "Fires when a date is selected."),
    @FacesBehaviorEvent(name = "dateDblSelect", event = SelectEvent.class, description = "Fires when a date is double-clicked."),
    @FacesBehaviorEvent(name = "eventSelect", event = SelectEvent.class, description = "Fires when an event is selected."),
    @FacesBehaviorEvent(name = "eventDblSelect", event = SelectEvent.class, description = "Fires when an event is double-clicked."),
    @FacesBehaviorEvent(name = "eventMove", event = ScheduleEntryMoveEvent.class, description = "Fires when an event is moved."),
    @FacesBehaviorEvent(name = "eventResize", event = ScheduleEntryResizeEvent.class, description = "Fires when an event is resized."),
    @FacesBehaviorEvent(name = "viewChange", event = SelectEvent.class, description = "Fires when the calendar view is changed."),
    @FacesBehaviorEvent(name = "rangeSelect", event = ScheduleRangeEvent.class, description = "Fires when a time range is selected.")
})
public abstract class ScheduleBase extends UIComponentBase implements Widget, RTLAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ScheduleRenderer";

    public ScheduleBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "An org.primefaces.model.ScheduleModel instance representing the backed model.")
    public abstract org.primefaces.model.ScheduleModel getValue();

    @Property(description = "User locale for i18n localization messages. The attribute can be either a String or java.util.Locale object.")
    public abstract Object getLocale();

    @Property(description = "Ratio of calendar width to height, higher the value shorter the height is.",
            defaultValue = "Double.MIN_VALUE")
    public abstract double getAspectRatio();

    @Property(description = "The view type to use, possible values are 'dayGridMonth', 'dayGridWeek', 'dayGridDay',"
            + " 'timeGridWeek', 'timeGridDay', 'listYear' , 'listMonth', 'listWeek', 'listDay'.",
            defaultValue = "dayGridMonth")
    public abstract String getView();

    @Property(description = "The initial date that is used when schedule loads. If omitted, the schedule starts on the current date.")
    public abstract Object getInitialDate();

    @Property(description = "Specifies inclusion Saturday/Sunday columns in any of the views.",
            defaultValue = "true")
    public abstract boolean isShowWeekends();

    @Property(description = "Style of the main container element of schedule.")
    public abstract String getStyle();

    @Property(description = "Style class of the main container element of schedule.")
    public abstract String getStyleClass();

    @Property(description = "When true, events are draggable.",
            defaultValue = "true")
    public abstract boolean isDraggable();

    @Property(description = "When true, events are resizable.",
            defaultValue = "true")
    public abstract boolean isResizable();

    @Property(description = "Enables selection of time ranges by clicking and dragging on the schedule, see https://fullcalendar.io/docs/select-callback."
            + " Uses the ajax-event \"rangeSelect\" instead of \"dateSelect\".",
            defaultValue = "false")
    public abstract boolean isSelectable();

    @Property(description = "Specifies visibility of header content.",
            defaultValue = "true")
    public abstract boolean isShowHeader();

    @Property(description = "Content of left side of header.",
            defaultValue = "prev,next today")
    public abstract String getLeftHeaderTemplate();

    @Property(description = "Content of center of header.",
            defaultValue = "title")
    public abstract String getCenterHeaderTemplate();

    @Property(description = "Content of right side of header.",
            defaultValue = "dayGridMonth,timeGridWeek,timeGridDay")
    public abstract String getRightHeaderTemplate();

    @Property(description = "Determines if all-day slot will be displayed in agendaWeek or agendaDay views.",
            defaultValue = "true")
    public abstract boolean isAllDaySlot();

    @Property(description = "The frequency for displaying time slots.",
            defaultValue = "00:30:00")
    public abstract String getSlotDuration();

    @Property(description = "Determines how far down the scroll pane is initially scrolled down.",
            defaultValue = "06:00:00")
    public abstract String getScrollTime();

    @Property(description = "Minimum time to display in a day view.",
            implicitDefaultValue = "00:00:00.")
    public abstract String getMinTime();

    @Property(description = "Maximum time to display in a day view.",
            implicitDefaultValue = "24:00:00")
    public abstract String getMaxTime();

    @Property(description = "TThe frequency that the time slots should be labelled with text. Example: like \"01:00\" or \"{hours:1}\".")
    public abstract String getSlotLabelInterval();

    @Property(description = "Determines the text that will be displayed within a time slot.")
    public abstract String getSlotLabelFormat();

    @Property(description = "Determines the time-text that will be displayed on each event. (Moment.js - format)")
    public abstract String getTimeFormat();

    @Property(description = "Deprecated, use columnHeaderFormat instead. Format for column headers.")
    public abstract String getColumnFormat();

    @Property(description = "Format for column headers. (eg `timeGridWeek: {weekday: 'narrow'}` or"
            + " `timeGridWeek: {weekday: 'short'}, timeGridDay: {day: 'numeric'}`)")
    public abstract String getColumnHeaderFormat();

    @Property(description = "String or a java.util.TimeZone instance to specify the timezone used for date conversion to ISO_8601 format.",
        implicitDefaultValue = "ZoneId.systemDefault()")
    public abstract Object getTimeZone();

    @Property(description = "Timezone to define how to interpret the dates at browser."
            + " Valid values are \"local\", \"UTC\" and ids like \"America/Chicago\".",
            defaultValue = "local")
    public abstract String getClientTimeZone();

    @Property(description = "Displays description of events on a tooltip, default value is false.",
            defaultValue = "false")
    public abstract boolean isTooltip();

    @Property(description = "Display week numbers in schedule.",
            defaultValue = "false")
    public abstract boolean isShowWeekNumbers();

    @Property(description = "Name of javascript function to extend the options of the underlying fullcalendar plugin.")
    public abstract String getExtender();

    @Property(description = "Whether or not to display an event's end time text when it is rendered on the calendar."
            + " Value can be a boolean to globally configure for all views or a comma separated list such as \"month:false,basicWeek:true\""
            + " to configure per view.")
    public abstract String getDisplayEventEnd();

    @Property(description = "The method for calculating week numbers that are displayed. Valid values are \"local\" (default), \"ISO\" and \"custom\".",
            defaultValue = "local")
    public abstract String getWeekNumberCalculation();

    @Property(description = "Client side function to use in custom weekNumberCalculation.")
    public abstract String getWeekNumberCalculator();

    @Property(description = "When an event's end time spans into another day, the minimum time it must be in order for it to render as if it were on that day.",
            defaultValue = "09:00:00")
    public abstract String getNextDayThreshold();

    @Property(description = "If true contemporary events will be rendered one overlapping the other, else they will be rendered side by side.",
            defaultValue = "true")
    public abstract boolean isSlotEventOverlap();

    @Property(description = "Target for events with urls. Clicking on such events in the schedule will not trigger"
            + " the selectEvent but open the url using this target instead.",
            defaultValue = "_blank")
    public abstract String getUrlTarget();

    @Property(description = "Whether for URL events access to the opener window from the target site should be prevented (phishing protection).",
            defaultValue = "true")
    public abstract boolean isNoOpener();

    @Property(description = "Sets the height of the entire calendar, including header and footer."
            + " By default, this option is unset and the calendar's height is calculated by aspectRatio."
            + " If \"auto\" is specified, the view's contents will assume a natural height and no scrollbars will be used.")
    public abstract String getHeight();
}