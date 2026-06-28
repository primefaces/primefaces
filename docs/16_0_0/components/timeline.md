# TimeLine

Timeline is an interactive graph to visualize events in time.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Timeline-1.html)

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
        xmlns:h="jakarta.faces.html"
        xmlns:f="jakarta.faces.core"
        xmlns:p="http://primefaces.org/ui">
    <h:head>
        <f:facet name="first">
            <h:outputScript name="path/to/your/moment.js" />
            <h:outputScript name="path/to/your/moment/locale/es.js" />
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

## Client Side API
Widget: _PrimeFaces.widget.Timeline_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| move(moveFactor, options, callback) | __moveFactor:__ a Number that determines the moving amount. A positive value will move right, a negative value will move left.<br><br> __animation:__ (optional) boolean or {duration: number, easingFunction: string}. If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided to specify duration and easing function. Default duration is 500 ms, and default easing function is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", "easeInQuint", "easeOutQuint", "easeInOutQuint". <br><br> __callback:__ (optional) A callback function that executes after move Timeline. | void | Moves the timeline the given movefactor to the left or right.
| zoom(zoomFactor, options, callback) | __zoomFactor:__ a number between -1 and +1. If positive zoom in, and if negative zoom out. <br><br> __animation:__ (optional) boolean or {duration: number, easingFunction: string}. If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided to specify duration and easing function. Default duration is 500 ms, and default easing function is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", "easeInQuint", "easeOutQuint", "easeInOutQuint". <br><br> __callback:__ (optional) A callback function that executes after Timeline gets zoomed in or out. | void | Zooms the timeline the given zoomfactor in or out.
| cancelAdd() | - | void | Cancel the event of adding a Timeline item. It must be called inside the _onstart_ property of Ajax Behavior _add_ event.
| cancelChange() | - | void | Cancel the event of change a Timeline item. It must be called inside the _onstart_ property of Ajax Behavior _changed_ event.
| cancelDelete() | - | void | Cancel the event of delete a Timeline item. It must be called inside the _onstart_ property of Ajax Behavior _delete_ event.

