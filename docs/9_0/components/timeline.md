# TimeLine

Timeline is an interactive graph to visualize events in time.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.timeline.html)

## Info

| Name | Value |
| --- | --- |
| Tag | timeLine
| Component Class | org.primefaces.component.timeline.TimeLine
| Component Type | org.primefaces.component.TimeLine
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TimeLineRenderer
| Renderer Class | org.primefaces.component.timeline.TimeLineRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
var | null | String | Name of the request-scoped variable for underlaying object in the TimelineEvent for each iteration.
value | null | TimelineModel | An instance of TimelineModel representing the backing model.
varGroup | null | String | Name of the request-scoped variable for underlaying object in the TimelineGroup for each iteration.
locale | view Locale | Object | User locale for i18n localization messages. The attribute can be either a String or java.util.Locale object.
timeZone | null | Object | Target time zone to convert start / end dates of TimelineEvent's in server side. The attribute can be either a String or TimeZone object or null. If null, timeZone defaults to the server's time zone the application is running in.
clientTimeZone | null | Object | Time zone the user would like to see dates in UI. The attribute can be either a String or TimeZone object or null. If null, clientTimeZone defaults to browser's time zone.
height | null | String | The height of the timeline in pixels or as a percentage. When height is undefined or null, the height of the timeline is automatically adjusted to fit the contents. It is possible to set a maximum height using option maxHeight to prevent the timeline from getting too high in case of automatically calculated height.
maxHeight | null | Integer | Specifies the maximum height for the Timeline in pixels.
minHeight | 0 | Integer | Specifies a minimum height for the Timeline in pixels.
horizontalScroll | false | Boolean | Specifies the horizontal scrollable.
verticalScroll | false | Boolean | Specifies the vertical scrollable.
width | 100% | String | The width of the timeline in pixels or as a percentage.
responsive | true | Boolean | Check if the timeline container is resized, and if so, resize the timeline. Useful when the webpage (browser window) or a layout pane / unit containing the timeline component is resized.
~axisOnTop~ | false | Boolean | (Deprecated, use orientationAxis instead.) If false, the horizontal axis is drawn at the bottom. If true, the axis is drawn on top.
orientationAxis | bottom | String | Orientation of the timeline axis: 'top', 'bottom' (default), 'both', or 'none'. If orientation is 'bottom', the time axis is drawn at the bottom. When 'top', the axis is drawn on top. When 'both', two axes are drawn, both on top and at the bottom. In case of 'none', no axis is drawn at all.
orientationItem | bottom | String | Orientation of the timeline items: 'top' or 'bottom' (default). Determines whether items are aligned to the top or bottom of the Timeline.
editable | false | Boolean | If true, the items in the timeline can be manipulated. Only applicable when option selectable is true.
editableAdd | false | Boolean | If true, new items can be created by double tapping an empty space in the Timeline. Takes precedence over editable.
editableGroup | false | Boolean | If true, items can be dragged from one group to another. Only applicable when the Timeline has groups. Takes precedence over editable.
editableRemove | false | Boolean | If true, items can be deleted by first selecting them, and then clicking the delete button on the top right of the item. Takes precedence over editable.
editableOverrideItems | false | Boolean | If true, TimelineEvent specific editables properties are overridden by timeline settings.
selectable | true | Boolean | If true, events on the timeline are selectable. Selectable events can fire AJAX "select" events.
zoomable | true | Boolean | If true, the timeline is zoomable. When the timeline is zoomed, AJAX "rangechange" events are fired.
moveable | true | Boolean | If true, the timeline is movable. When the timeline is moved, AJAX "rangechange" events are fired.
start | null | LocalDateTime  | The initial start date for the axis of the timeline. If not provided, the earliest date present in the events is taken as start date.
end | null | LocalDateTime  | The initial end date for the axis of the timeline. If not provided, the latest date present in the events is taken as end date.
min | null | LocalDateTime  | Set a minimum Date for the visible range. It will not be possible to move beyond this minimum.
max | null | LocalDateTime  | Set a maximum Date for the visible range. It will not be possible to move beyond this maximum.
zoomMin | 10L | Long | Set a minimum zoom interval for the visible range in milliseconds. It will not be possible to zoom in further than this minimum.
zoomMax | 315360000000000L | Long | Set a maximum zoom interval for the visible range in milliseconds. It will not be possible to zoom out further than this maximum. Default value equals 315360000000000 ms (about 10000 years).
preloadFactor | 0.0f | Float | Preload factor is a positive float value or 0 which can be used for lazy loading of events. When the lazy loading feature is active, the calculated time range for preloading will be multiplicated by the preload factor. The result of this multiplication specifies the additional time range which will be considered for the preloading during moving / zooming too. For example, if the calculated time range for preloading is 5 days and the preload factor is 0.2, the result is 5 * 0.2 = 1 day. That means, 1 day backwards and / or 1 day onwards will be added to the original calculated time range. The event's area to be preloaded is wider then. This helps to avoid frequently, time-consuming fetching of events. Default value is 0.
eventMargin | 10 | Integer | The minimal margin in pixels between events.
eventMarginAxis | 10 | Integer | The minimal margin in pixels between events and the horizontal axis.
eventHorizontalMargin | 10 | Integer | The minimal horizontal margin in pixels between items. Takes precedence over eventMargin property.
eventVerticalMargin | 10 | Integer | The minimal vertical margin in pixels between items. Takes precedence over eventMargin property.
eventStyle | null | String | Specifies the default type for the timeline items. Choose from 'box', 'point' and 'range'. If undefined, the Timeline will auto detect the type from the items data: if a start and end date is available, a 'range' will be created, and else, a 'box' is created.
~groupsChangeable~ | true | Boolean | (Deprecated, use editableGroup property instead.) If true, items can be moved from one group to another. Only applicable when groups are used.
groupsOrder | true | Boolean | Allows to customize the way groups are ordered. When true (default), groups will be ordered by content alphabetically (when the list of groups is missing) or by native ordering of TimelineGroup object in the list of groups (when the list of groups is available). When false, groups will not be ordered at all.
groupStyle | null | String | A css text string to apply custom styling for an individual group label, for example "color: red; background-color: pink;".
snap | function | String | When moving items on the Timeline, they will be snapped to nice dates like full hours or days, depending on the current scale. The snap function can be replaced with a custom javascript function, or can be set to null to disable snapping. The signature of the snap function is:<br><br> ````function snap(date: Date, scale: string, step: number) : Date or number````<br><br>The parameter scale can be can be 'millisecond', 'second', 'minute', 'hour', 'weekday, 'week', 'day, 'month, or 'year'. The parameter step is a number like 1, 2, 4, 5.
~snapEvents~ | true | Boolean | (Deprecated, use snap property instead) If true, the start and end of an event will be snapped nice integer values when moving or resizing the event. Default is true.
stackEvents | true | Boolean | If true, the start and end of an event will be snapped nice integer values when moving or resizing the event.
showCurrentTime | true | Boolean | If true, the timeline shows a red, vertical line displaying the current time.
showMajorLabels | true | Boolean | By default, the timeline shows both minor and major date labels on the horizontal axis. For example the minor labels show minutes and the major labels show hours. When "showMajorLabels" is false, no major labels are shown.
showMinorLabels | true | Boolean | By default, the timeline shows both minor and major date labels on the horizontal axis. For example the minor labels show minutes and the major labels show hours. When "showMinorLabels" is false, no minor labels are shown. When both "showMajorLabels" and "showMinorLabels" are false, no horizontal axis will be visible.
~timeChangeable~ | true | Boolean | (Deprecated, use editableTime property instead.) If false, items can not be moved or dragged horizontally (neither start time nor end time is changable). This is useful when items should be editable but can only be changed regarding group or content (typical use case: scheduling events).
clickToUse | false | Boolean | When a Timeline is configured to be clickToUse, it will react to mouse and touch events only when active. When active, a blue shadow border is displayed around the Timeline. The Timeline is set active by clicking on it, and is changed to inactive again by clicking outside the Timeline or by pressing the ESC key.
showTooltips | true | Boolean | If true, items with titles will display a tooltip. If false, item tooltips are prevented from showing.
tooltipFollowMouse | false | Boolean | If true, tooltips will follow the mouse as they move around in the item.
tooltipOverflowMethod | flip | String | Set how the tooltip should act if it is about to overflow out of the timeline. Choose from 'cap', 'flip' and 'none'. If it is set to 'cap', the tooltip will just cap its position to inside to timeline. If set to 'flip', the position of the tooltip will flip around the cursor so that a corner is at the cursor, and the rest of it is visible. If set to 'none', the tooltip will be positioned independently of the timeline, so parts of the tooltip could possibly be hidden or stick ouf of the timeline, depending how CSS overflow is defined for the timeline (by default it's hidden).
tooltipDelay | 500 | Integer | Set a value (in ms) that the tooltip is delayed before showing. Default is 500.
dropHoverStyleClass | null | String | Style class to apply when an acceptable draggable is dragged over.
dropActiveStyleClass | null | String | Style class to apply when an acceptable draggable is being dragged over.
dropAccept | null | String | Selector to define the accepted draggables.
dropScope | null | String | Scope key to match draggables and droppables.
dir | ltr | String | Defines direction of timeline. Valid values are "ltr" (default) and "rtl".
extender | null | String | Name of javascript function to extend the options of the underlying timeline javascript component.

## Getting started with the TimeLine
TimeLine requires a value of org.primefaces.model.timeline.TimelineModel type. An event should
be an instance of org.primefaces.model.timeline.TimelineEvent and included in model via add
method.

```xhtml
<p:timeline id="timeline" value="#{basicTimelineView.model}" height="250px" />
```
```java
public class BasicTimelineView implements Serializable {
    private TimelineModel model;

    @PostConstruct
    protected void initialize() {
        model = new TimelineModel();
        model.add(TimelineEvent.<String>builder().data("PrimeUI 1.1").startDate(LocalDate.of(2014, 6, 12)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeFaces 5.1.3").startDate(LocalDate.of(2014, 10, 11)).build());
    }
}
```
## Custom Menu
A custom menu can be defined using menu facet.

```xhtml
<p:timeline id="timeline" value="#{basicTimelineView.model}" widgetVar="timeline">
    <f:facet name="menu">
        <p:commandButton type="button" icon="fa fa-arrow-left" onclick="PF('timeline').move(-0.2);" />
        <p:commandButton type="button" icon="fa fa-arrow-right" onclick="PF('timeline').move(0.2);" />
        <p:commandButton type="button" icon="fa fa-search-plus" onclick="PF('timeline').zoom(0.2);" />
        <p:commandButton type="button" icon="fa fa-search-minus" onclick="PF('timeline').zoom(-0.2);" />
    </f:facet>
</p:timeline>
```
## Loading Screen
A loading screen can be defined using loading facet.

```xhtml
<p:timeline id="timeline" value="#{basicTimelineView.model}">
    <f:facet name="loading">
        LOADING<br />
        <p:graphicImage name="/demo/images/ajaxloadingbar.gif" />
    </f:facet>
</p:timeline>
```
## Tooltip
If you define title property in _TimelineEvent_ and/or _TimelineGroup_ java objects, then a tooltip is displayed when holding the mouse on the item and/or
group respectively. Title property can be plain text or HTML in case of TimelineEvent and only plain text in case of TimelineGroup.

A facet called "eventTitle" can be defined in <p:timeline> to provide custom content for event's title.
```xhtml
<p:timeline id="timeline" value="#{basicTimelineView.model}" var="data">
    <f:facet name="eventTitle">
        <h:panelGrid columns="2">
            <strong>My property:</strong>
            <h:outputText value="#{data.myproperty}" />
            <strong>Another property:</strong>
            <h:outputText value="#{data.anotherProperty}" />
        </h:panelGrid>
    </f:facet>
</p:timeline>
```
## Extender
Extender property is a javascript function that allows access to the underlying timeline api.

```xhtml
<p:timeline id="timeline" value="#{basicTimelineView.model}" extender="timelineExtender" />

<h:outputScript>
    function timelineExtender() {
       //copy the config options into a variable
       var options = $.extend(true, {}, this.cfg.opts);

       options = {
          //hide weekends and non labor hours
          hiddenDates: [
                {start: '2014-08-09 00:00:00', end: '2014-08-11 00:00:00', repeat:'weekly'},
                {start: '2014-08-09 00:00:00', end: '2014-08-09 06:00:00', repeat:'daily'},
                {start: '2014-08-09 18:00:00', end: '2014-08-10 00:00:00', repeat:'daily'}
          ]
       };

       //merge all options into the main timeline options
       $.extend(true, this.cfg.opts, options);
    };
</h:outputScript>
```
## Localization
By default locale information is retrieved from the view’s locale and can be overridden by the _locale_
attribute which can take a locale key as a | String | or a java.util.Locale instance. Default
language of labels are English and you need to add the necessary translations to your page manually
as PrimeFaces does not include language translations. Localization of Timeline component depends largely on
the localization of javascript library moment.js. To enable localization, your moment.js must be loaded with locales <strong>before</strong>
timeline javascript gets loaded. Following is an example of Spanish localization.

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3c.org/1999/xhtml"
        xmlns:h="http://xmlns.jcp.org/jsf/html"
        xmlns:f="http://xmlns.jcp.org/jsf/core"
        xmlns:p="http://primefaces.org/ui">
    <h:head>
        <f:facet name="first">
            <h:outputScript name=”path/to/your/moment.js” />
            <h:outputScript name=”path/to/your/moment/locale/es.js” />
        </f:facet>
    </h:head>
    <h:body>
        <p:timeline id="timeline" value="#{basicTimelineView.model}" locale="es" />
    </h:body>
</html>
```
## Examples
For examples on editing, grouping, styling, ranges, linked timelines and lazy loading please visit:

https://www.primefaces.org/showcase/ui/data/timeline/basic.xhtml

## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
add | org.primefaces.event.timeline.TimelineAddEvent | On event add.
change | org.primefaces.event.timeline.TimelineModificationEvent | On event change. (Attention: This event creates unnecessary requests. It´s suggested to listen to changed-event.)
changed | org.primefaces.event.timeline.TimelineModificationEvent | On event change complete.
edit | org.primefaces.event.timeline.TimelineModificationEvent | On event edit.
delete | org.primefaces.event.timeline.TimelineModificationEvent | On event delete.
select | org.primefaces.event.timeline.TimelineSelectEvent | On event select.
rangechange | org.primefaces.event.timeline.TimelineRangeEvent | On range change. (Attention: This event creates unnecessary requests. It´s suggested to listen to rangechanged-event.) 
rangechanged | org.primefaces.event.timeline.TimelineRangeEvent | On range change complete.
lazyload | org.primefaces.event.timeline.TimelineLazyLoadEvent | On lazy load.
drop | org.primefaces.event.timeline.TimelineDragDropEvent | On drop from outside.

## Client Side API
Widget: _PrimeFaces.widget.Timeline_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| move(moveFactor, options, callback) | __moveFactor:__ a Number that determines the moving amount. A positive value will move right, a negative value will move left.<br><br> __animation:__ (optional) boolean or {duration: number, easingFunction: string}. If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided to specify duration and easing function. Default duration is 500 ms, and default easing function is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", "easeInQuint", "easeOutQuint", "easeInOutQuint". <br><br> __callback:__ (optional) A callback function that executes after move Timeline. | void | Moves the timeline the given movefactor to the left or right.
| zoom(zoomFactor, options, callback) | __zoomFactor:__ a number between -1 and +1. If positive zoom in, and if negative zoom out. <br><br> __animation:__ (optional) boolean or {duration: number, easingFunction: string}. If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided to specify duration and easing function. Default duration is 500 ms, and default easing function is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", "easeInQuint", "easeOutQuint", "easeInOutQuint". <br><br> __callback:__ (optional) A callback function that executes after Timeline gets zoomed in or out. | void | Zooms the timeline the given zoomfactor in or out.
| cancelAdd() | - | void | Cancel the event of adding a Timeline item. It must be called inside the _onstart_ property of Ajax Behavior _add_ event.
| cancelChange() | - | void | Cancel the event of change a Timeline item. It must be called inside the _onstart_ property of Ajax Behavior _changed_ event.
| cancelDelete() | - | void | Cancel the event of delete a Timeline item. It must be called inside the _onstart_ property of Ajax Behavior _delete_ event.

