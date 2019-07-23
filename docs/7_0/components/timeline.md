# TimeLine

Timeline is an interactive graph to visualize events in time.

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
locale | null | Object | User locale for i18n messages. The attribute can be either a String or Locale object.
timeZone | null | Object | Target time zone to convert start / end dates for displaying. This time zone is the time zone the user would like to see dates in UI. The attribute can be either a String or TimeZone object or null. If null, timeZone defaults to the server's time zone the application is running in.
browserTimeZone | null | Object | Time zone the user's browser / PC is running in. This time zone allows to correct the conversion of start / end dates to the target timeZone for displaying. The attribute can be either a String or TimeZone object or null. Note: browserTimeZone should be provided if the target timeZone is provided. If null, browserTimeZone defaults to the server's timeZone.
height | auto | String | The height of the timeline in pixels, as a percentage, or "auto". When the height is set to "auto", the height of the timeline is automatically adjusted to fit the contents. If not, it is possible that events get stacked so high, that they are not visible in the timeline. When height is set to "auto", a minimum height can be specified with the option minHeight. Default is "auto".
minHeight | 0 | Integer | Specifies a minimum height for the Timeline in pixels. Useful when height is set to "auto".
width | 100% | String | The width of the timeline in pixels or as a percentage.
responsive | true | Boolean | Check if the timeline container is resized, and if so, resize the timeline. Useful when the webpage (browser window) or a layout pane / unit containing the timeline component is resized.
axisOnTop | false | Boolean | If false, the horizontal axis is drawn at the bottom. If true, the axis is drawn on top.
dragAreaWidth | 10 | Integer | The width of the drag areas in pixels. When an event with date range is selected, it has a drag area on the left and right side, with which the start or end dates of the event can be manipulated.
editable | false | Boolean | If true, the events can be edited, changed, created and deleted. Events can only be editable when the option selectable is true (default). When editable is true, the timeline can fire AJAX events "change", "edit", "add", "delete", "drop". This global setting "editable" can be overwritten for individual events by setting a value in field "editable".
selectable | true | Boolean | If true, events on the timeline are selectable. Selectable events can fire AJAX "select" events.
unselectable | true | Boolean | If true, you can unselect an item by clicking in the empty space of the timeline. If false, you cannot unselect an item, there will be always one item selected.
zoomable | true | Boolean | If true, the timeline is zoomable. When the timeline is zoomed, AJAX "rangechange" events are fired.
moveable | true | Boolean | If true, the timeline is movable. When the timeline is moved, AJAX "rangechange" events are fired.
start | null | Date | The initial start date for the axis of the timeline. If not provided, the earliest date present in the events is taken as start date.
end | null | Date | The initial end date for the axis of the timeline. If not provided, the latest date present in the events is taken as end date.
min | null | Date | Set a minimum Date for the visible range. It will not be possible to move beyond this minimum.
max | null | Date | Set a maximum Date for the visible range. It will not be possible to move beyond this maximum.
zoomMin | 10L | Long | Set a minimum zoom interval for the visible range in milliseconds. It will not be possible to zoom in further than this minimum.
zoomMax | 315360000000000L | Long | Set a maximum zoom interval for the visible range in milliseconds. It will not be possible to zoom out further than this maximum. Default value equals 315360000000000 ms (about 10000 years).
preloadFactor | 0.0f | Float | Preload factor is a positive float value or 0 which can be used for lazy loading of events. When the lazy loading feature is active, the calculated time range for preloading will be multiplicated by the preload factor. The result of this multiplication specifies the additional time range which will be considered for the preloading during moving / zooming too. For example, if the calculated time range for preloading is 5 days and the preload factor is 0.2, the result is 5 * 0.2 = 1 day. That means, 1 day backwards and / or 1 day onwards will be added to the original calculated time range. The event's area to be preloaded is wider then. This helps to avoid frequently, time-consuming fetching of events. Default value is 0.
eventMargin | 10 | Integer | The minimal margin in pixels between events.
eventMarginAxis | 10 | Integer | The minimal margin in pixels between events and the horizontal axis.
eventStyle | box | String | Specifies the style for the timeline events. Choose from "dot" or "box".
groupsChangeable | true | Boolean | If true, items can be moved from one group to another. Only applicable when groups are used.
groupsOnRight | false | Boolean | If false, the groups legend is drawn at the left side of the timeline. If true, the groups legend is drawn on the right side.
groupsOrder | true | Boolean | Allows to customize the way groups are ordered. When true (default), groups will be ordered by content alphabetically (when the list of groups is missing) or by native ordering of TimelineGroup object in the list of groups (when the list of groups is available). When false, groups will not be ordered at all.
groupsWidth | null | String | By default, the width of the groups legend is adjusted to the group names. A fixed width can be set for the groups legend by specifying the "groupsWidth" as a string, for example "200px".
groupMinHeight | 0 | Integer | The minimum height of each individual group even if they have no items. The group height is set as the greatest value between items height and the groupMinHeight. Default is 0.
snapEvents | true | Boolean | If true, the start and end of an event will be snapped nice integer values when moving or resizing the event. Default is true.
stackEvents | true | Boolean | If true, the start and end of an event will be snapped nice integer values when moving or resizing the event.
showCurrentTime | true | Boolean | If true, the timeline shows a red, vertical line displaying the current time.
showMajorLabels | true | Boolean | By default, the timeline shows both minor and major date labels on the horizontal axis. For example the minor labels show minutes and the major labels show hours. When "showMajorLabels" is false, no major labels are shown.
showMinorLabels | true | Boolean | By default, the timeline shows both minor and major date labels on the horizontal axis. For example the minor labels show minutes and the major labels show hours. When "showMinorLabels" is false, no minor labels are shown. When both "showMajorLabels" and "showMinorLabels" are false, no horizontal axis will be visible.
showButtonNew | false | Boolean | Show the button "Create new event" in the a navigation menu.
showNavigation | false | Boolean | Show a navigation menu with buttons to move and zoom the timeline.
timeChangeable | true | Boolean | If false, items can not be moved or dragged horizontally (neither start time nor end time is changable). This is useful when items should be editable but can only be changed regarding group or content (typical use case: scheduling events).
dropHoverStyleClass | null | String | Style class to apply when an acceptable draggable is dragged over.
dropActiveStyleClass | null | String | Style class to apply when an acceptable draggable is being dragged over.
dropAccept | null | String | Selector to define the accepted draggables.
dropScope | null | String | Scope key to match draggables and droppables.
animate | true | Boolean | When true, events are moved animated when resizing or moving them. This is very pleasing for the eye, but does require more computational power.
animateZoom | true | Boolean | When true, events are moved animated when zooming the Timeline. This looks cool, but does require more computational power.

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
        Calendar cal = Calendar.getInstance();
        cal.set(2014, Calendar.JUNE, 12, 0, 0, 0);
        model.add(new TimelineEvent("PrimeUI 1.1", cal.getTime()));
        cal.set(2014, Calendar.OCTOBER, 11, 0, 0, 0);
        model.add(new TimelineEvent("PrimeFaces 5.1.3", cal.getTime()));
    }
}
```
## Examples
For examples on editing, grouping, styling, ranges, linked timelines and lazy loading please visit:

https://www.primefaces.org/showcase/ui/data/timeline/basic.xhtml

## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
page | org.primefaces.event.data.PageEvent | On pagination.
sort | org.primefaces.event.data.SortEvent | When a column is sorted.
add | org.primefaces.event.timeline.TimelineAddEvent | On event add.
change | org.primefaces.event.timeline.TimelineModificationEvent | On event change.
changed | org.primefaces.event.timeline.TimelineModificationEvent | On event change complete.
edit | org.primefaces.event.timeline.TimelineModificationEvent | On event edit.
delete | org.primefaces.event.timeline.TimelineModificationEvent | On event delete.
select | org.primefaces.event.timeline.TimelineSelectEvent | On event select.
rangechange | org.primefaces.event.timeline.TimelineRangeEvent | On range change.
rangechanged | org.primefaces.event.timeline.TimelineRangeEvent | On range change complete.
lazyload | org.primefaces.event.timeline.TimelineLazyLoadEvent | On lazy load.
drop | org.primefaces.event.timeline.TimelineDragDropEvent | On drop from outside.

