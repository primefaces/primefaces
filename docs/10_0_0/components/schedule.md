# Schedule

Schedule provides an Outlook Calendar, iCal like JSF component to manage events which is based on FullCalendar.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.schedule-1.html)

## Info

| Name | Value |
| --- | --- |
Tag | schedule
Component Class | org.primefaces.component.schedule.Schedule
Component Type | org.primefaces.component.Schedule
Component Family | org.primefaces.component
Renderer Type | org.primefaces.component.ScheduleRenderer
Renderer Class | org.primefaces.component.schedule.ScheduleRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
value | null | Object | An org.primefaces.model.ScheduleModel instance representing the backed model
locale | null | Object | Locale for localization, can be String or a java.util.Locale instance
dir | ltr | String | Defines direction of schedule. Valid values are "ltr" (default) and "rtl".
allDaySlot | true | Boolean | Determines if all-day slot will be displayed in timeGridWeek or timeGridDay views
aspectRatio | null | Float | Ratio of calendar width to height, higher the value shorter the height is
centerHeaderTemplate | title | String | Content of center of header.
clientTimeZone | local | String | Defines how to interpret the dates at browser. Valid values are "local", "UTC" and ids like "America/Chicago". (see https://fullcalendar.io/docs/timeZone)
columnFormat | null | String | Deprecated, use columnHeaderFormat instead. Format for column headers.
columnHeaderFormat | null | String | Format for column headers. (eg `timeGridWeek: {weekday: 'narrow'}` or `timeGridWeek: {weekday: 'short'}, timeGridDay: {day: 'numeric'}`) see: https://fullcalendar.io/docs/v4/columnHeaderFormat
displayEventEnd | null | String | Whether or not to display an event's end time text when it is rendered on the calendar. Value can be a boolean to globally configure for all views or a comma separated list such as "month:false,basicWeek:true" to configure per view.
draggable | true | Boolean | When true, events are draggable.
extender | null | String | Name of JavaScript function to extend the options of the underlying FullCalendar plugin.
height | null | String | Sets the height of the entire calendar, including header and footer. By default, this option is unset and the calendar’s height is calculated by aspectRatio. If "auto" is specified, the view’s contents will assume a natural height and no scrollbars will be used.
initialDate | null | java.time.LocalDate | The initial date that is used when schedule loads. If ommitted, the schedule starts on the current date
leftHeaderTemplate | prev, next, today | String | Content of left side of header.
maxTime | null | String | Maximum time to display in a day view.
minTime | null | String | Minimum time to display in a day view.
nextDayThreshold | 09:00:00 | String | When an event's end time spans into another day, the minimum time it must be in order for it to render as if it were on that day. Default is `09:00:00`.
noOpener | true | Boolean | Whether for URL events access to the opener window from the target site should be prevented (phishing protection), default value is true.
resizable | true | Boolean | When true, events are resizable.
rightHeaderTemplate | dayGridMonth, timeGridWeek, timeGridDay | String | Content of right side of header.
scrollTime | 06:00:00 | String | Determines how far down the scroll pane is initially scrolled down.
showHeader | true | Boolean | Specifies visibility of header content.
showWeekends | true | Boolean | Specifies inclusion Saturday/Sunday columns in any of the views
showWeekNumbers | false | Boolean | Display week numbers in schedule.
slotDuration | 00:30:00 | String | The frequency for displaying time slots.
slotEventOverlap | true | Boolean | If true contemporary events will be rendered one overlapping the other, else they will be rendered side by side.
slotLabelFormat | null | String | Determines the text that will be displayed within a time slot. The default English value will produce times that look like 5pm and 5:30pm. (see https://momentjs.com/docs/#/displaying/)
slotLabelInterval | null | String | The frequency that the time slots should be labeled with text. If not specified, a reasonable value will be automatically computed based on slotDuration. When specifying this option, give a Duration-ish input, like "01:00" or {hours:1}. This will cause the header labels to appear on the hour marks, even if slotDuration was hypothetically 15 or 30 minutes long.
style | null | String | Style of the main container element of schedule
styleClass | null | String | Style class of the main container element of schedule
timeFormat | null | String | Determines the time-text that will be displayed on each event. (see https://momentjs.com/docs/#/displaying/)
timeZone | null | Object | String or a java.time.ZoneId instance to specify the timezone used for date conversion, defaults to ZoneId.systemDefault().
tooltip | false | Boolean | Displays description of events on a tooltip.
urlTarget | _blank | String | Target for events with urls. Clicking on such events in the schedule will not trigger the selectEvent but open the url using this target instead. Default is "_blank".
view | dayGridMonth | String | The view type to use, possible values are 'dayGridMonth', 'dayGridWeek', 'dayGridDay', 'timeGridWeek', 'timeGridDay', 'listYear' , 'listMonth', 'listWeek', 'listDay'.
weekNumberCalculation | local | String | The method for calculating week numbers that are displayed. Valid values are "local"(default), "ISO" and "custom".
weekNumberCalculator | null | String | Client side function to use in custom `weekNumberCalculation`. This must be a JavaScript expression that results in an integer week number. You can use the local variable `date`, which points to the `Date` object for which the week number is to be determined. To illustrate, if you were to specify `date.getFullYear()`, the year would be shown instead of the actual week number.  

## Getting started with Schedule
Schedule needs to be backed by an _org.primefaces.model.ScheduleModel_ instance, a schedule
model consists of _org.primefaces.model.ScheduleEvent_ instances.

```java
public class ScheduleBean {
    private ScheduleModel model;

    public ScheduleBean() {
        eventModel = new ScheduleModel<ScheduleEvent>();
        DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                .title("title")
                .startDate(LocalDateTime.of(2019, 7, 27, 12, 00))
                .endDate(LocalDateTime.of(2019, 7, 27, 12, 30))
                .build();

        eventModel.addEvent(event);
    }
    public ScheduleModel getModel() {
        return model;
    }
}
```
```xhtml
<p:schedule value="#{scheduleBean.model}" />
```
DefaultScheduleEvent is the default implementation of ScheduleEvent interface. Mandatory
properties required to create a new event are the title, start date and end date. Other properties such
as allDay get sensible default values.

Table below describes each property in detail.

| Property | Description | 
| --- | --- |
id | Used internally by PrimeFaces, auto generated.
groupId | Events that share a groupId will be dragged and resized together automatically.
title | Title of the event.
startDate | Start date of type java.time.LocalDateTime.
endDate | End date of type java.time.LocalDateTime.
allDay | Flag indicating event is all day.
styleClass | Visual style class to enable multi resource display.
data | Optional data you can set to be represented by Event.
editable | Whether the event is editable or not.
overlap | If false (default), prevents this event from being dragged/resized over other events. Also prevents other events from being dragged/resized over this event.
description | Tooltip text to display on mouseover of an event.
url | Events with url set, do not trigger the selectEvent but open the url instead.
renderingMode | Which event rendering mode of the full calendar should be used? 
dynamicProperties | Add additional properties to the event json. Can be used with the javascript extender method.

## Ajax Behavior Events
Schedule provides various ajax behavior events to respond user actions.


| Event | Listener Parameter | Fired |
| --- | --- | --- |
dateSelect | org.primefaces.event.SelectEvent<LocalDateTime> | When a date is selected.
dateDblSelect | org.primefaces.event.SelectEvent<LocalDateTime> | When a date is double click selected.
eventSelect | org.primefaces.event.SelectEvent<ScheduleEvent> | When an event is selected.
eventMove | org.primefaces.event.ScheduleEntryMoveEvent | When an event is moved.
eventResize | org.primefaces.event.ScheduleEntryResizeEvent | When an event is resized.
viewChange | org.primefaces.event.SelectEvent<String> | When a view is changed.

## Ajax Updates
Schedule has a quite complex UI which is generated on-the-fly by the client side
PrimeFaces.widget.Schedule widget to save bandwidth and increase page load performance. As a
result when you try to update schedule like with a regular PrimeFacess PPR, you may notice a UI
lag as the DOM will be regenerated and replaced. Instead, Schedule provides a simple client side
api and the _update_ method.

Whenever you call update, schedule will query its server side ScheduleModel instance to check for
updates, transport method used to load events dynamically is JSON, as a result this approach is
much more effective then updating with regular PPR. An example of this is demonstrated at editable
schedule example, save button is calling PF(' _widgetvar').update()_ at oncomplete event handler.

## TimeZone
By default, schedule takes care of timezone differences by calculating the client's browser timezone
and the event date so that events are displayed at the clients local time.

See also https://fullcalendar.io/docs/timeZone, setting `timeZone=local`.

## Editable Schedule
Let’s put it altogether to come up a fully editable and complex schedule.

```xhtml
<h:form>
    <p:schedule value="#{bean.eventModel}" editable="true" widgetVar="myschedule">
        <p:ajax event="dateSelect" listener="#{bean.onDateSelect}" update="eventDetails" oncomplete="eventDialog.show()" />
        <p:ajax event="eventSelect" listener="#{bean.onEventSelect}" />
    </p:schedule>
    <p:dialog widgetVar="eventDialog" header="Event Details">
        <h:panelGrid id="eventDetails" columns="2">
            <p:outputLabel for="title" value="Titles:" />
            <p:inputText id="title" value="#{bean.event.title}" required="true" />
            <p:outputLabel for="from" value="From:" />
            <p:calendar id="from" value="#{bean.event.startDate}" timeZone="GMT+2" pattern="dd/MM/yyyy HH:mm" />
            <p:outputLabel for="to" value="To:" />
            <p:calendar id="to" value="#{bean.event.endDate}" timeZone="GMT+2" pattern="dd/MM/yyyy HH:mm" />
            <p:outputLabel for="allDay" value="All Day (see #1164):" />
            <h:selectBooleanCheckbox id="allDay" value="#{bean.event.allDay}" />
            <p:commandButton type="reset" value="Reset" />
            <p:commandButton id="addButton" value="Save" action="#{bean.addEvent}" oncomplete="PF('myschedule').update();PF('eventDialog').hide();" />
        </h:panelGrid>
    </p:dialog>
</h:form>
```

```java
public class ScheduleBean {

  private ScheduleModel model;
  private ScheduleEvent event = new DefaultScheduleEvent();

  public ScheduleBean() {
    model = new DefaultScheduleModel();
  }

  public ScheduleModel getModel() {
    return model;
  }

  public ScheduleEvent getEvent() {
    return event;
  }

  public void setEvent(ScheduleEvent event) {
    this.event = event;
  }

  public void addEvent() {
    if (event.getId() == null)
      model.addEvent( event );
    else
      model.updateEvent(event);
    event = new DefaultScheduleEvent(); //reset dialog form
  }

  public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
    event = selectEvent.getObject();
  }
	
  public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
    event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject()).endDate(selectEvent.getObject().plusHours(1)).build(); 
  }
}
```
## Lazy Loading
Schedule assumes whole set of events are eagerly provided in ScheduleModel, if you have a huge
data set of events, lazy loading features would help to improve performance. In lazy loading mode,
only the events that belong to the displayed time frame are fetched whereas in default eager more
all events need to be loaded.

```xhtml
<p:schedule value="#{scheduleBean.lazyModel}" />
```
To enable lazy loading of Schedule events, you just need to provide an instance of
_org.primefaces.model.LazyScheduleModel_ and implement the _loadEvents_ methods. _loadEvents_
method is called with new boundaries every time displayed timeframe is changed.


```java
public class ScheduleBean {
    private ScheduleModel lazyModel;

    public ScheduleBean() {
        lazyModel = new LazyScheduleModel() {
        @Override
        public void loadEvents(LocalDateTime start, LocalDateTime end) {
            //addEvent(...);
            //addEvent(...);
        }
    };
    }
    public ScheduleModel getLazyModel() {
        return lazyModel;
    }
}
```
## Customizing Header
Header controls of Schedule can be customized based on templates, valid values of template options
are;

- **title**: Text of current month/week/day information
- **prev**: Button to move calendar back one month/week/day.
- **next**: Button to move calendar forward one month/week/day.
- **prevYear**: Button to move calendar back one year
- **nextYear**: Button to move calendar forward one year
- **today**: Button to move calendar to current month/week/day.
- **viewName**: Button to change the view type based on the view type.

These controls can be placed at three locations on header which are defined with
_leftHeaderTemplate_ , _rightHeaderTemplate_ and _centerTemplate_ attributes.

```xhtml
<p:schedule value="#{scheduleBean.model}" leftHeaderTemplate"today" rightHeaderTemplate="prev,next" centerTemplate="dayGridMonth, timeGridWeek, timeGridDay">
</p:schedule>
```

## Views
9 different views are supported, these are "dayGridMonth", "dayGridWeek", "dayGridDay", "timeGridWeek", "timeGridDay", "listYear" , "listMonth", "listWeek" and "listDay".

Some examples...

#### timeGridWeek

```xhtml
<p:schedule value="#{scheduleBean.model}" view="timeGridWeek"/>
```
#### timeGridDay

```xhtml
<p:schedule value="#{scheduleBean.model}" view="timeGridDay"/>
```
#### listMonth

```xhtml
<p:schedule value="#{scheduleBean.model}" view="listMonth"/>
```

## Locale Support

FullCalendar comes with it´s own localization which is packed into the Schedule-component.
By default locale information is retrieved from the view’s locale and can be overridden by the locale
attribute. Locale attribute can take a locale key as a | String | or a java.util.Locale instance. 

```xhtml
<p:schedule value="#{scheduleBean.model}" locale="tr"/>
```
## Event Limit
To display a link when there are too many events on a slot, use _setEventLimit(true)_ on model.

## Extender Method
If the schedule component is lacking functions/options that are provided by the FullCalendar library, 
you can access them via the extender function. For more details about the configuration of FullCalender,
look [at their documentation](https://fullcalendar.io/docs/).

```xhtml
<h:form>
    <p:schedule value="#{scheduleBean.model}" extender="initSchedule"/>
    <h:outputScript>
        function initSchedule() {
            // Configure fullCalendar
            this.cfg.eventOrder = "doNotSort"; // dummy 
            this.cfg.views = {
                week: {
                    titleFormat: "D. MMMM YYYY",
                    columnFormat: "ddd D.M.",
                    displayEventTime: false
                },
                day: {
                    displayEventTime: false
                }
            };
            ...
            // Configure moment.js 
            window.moment.locale("de", {
                longDateFormat: {
                    LT: "HH:mm",
                    LTS: "HH:mm:ss",
                    L: "DD.MM.YYYY",
                    LL: "D. MMMM YYYY",
                    LLL: "D. MMMM YYYY HH:mm",
                    LLLL: "dddd, D. MMMM YYYY HH:mm"
                }
            });
            // Callback :: eventRender
            this.cfg.eventRender = function (event, element, view) {
                // show title of background events 
                if (event.rendering === 'background' && event.title !== 'null') {
                    element.append(event.title);
                }
                if (event.rendering !== 'background') {
                    element.attr('title', event.title);
                }
            };
    </h:outputScript>
</h:form>
``` 

## Skinning
Schedule resides in a main container which _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information.

