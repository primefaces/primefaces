# Poll

Poll is an ajax component that has the ability to send periodical ajax requests.

## Info

| Name | Value |
| --- | --- |
| Tag | poll
| Component Class | org.primefaces.component.poll.Poll
| Component Type | org.primefaces.component.Poll
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PollRenderer
| Renderer Class | org.primefaces.component.poll.PollRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Name of the client side widget.
interval | 2 | Integer, java.time.Duration | Interval in seconds to do periodic ajax requests.
process | @all | String | Component id(s) to process partially instead of whole view.
update | @none | String | Component(s) to be updated with ajax.
listener | null | MethodExpr | A method expression to invoke by polling.
immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application phase.
async | false | Boolean | When set to true, ajax requests are not queued.
onstart | null | String | Javascript handler to execute before ajax request is begins.
oncomplete | null | String | Javascript handler to execute when ajax request is completed.
onsuccess | null | String | Javascript handler to execute when ajax request succeeds.
onerror | null | String | Javascript handler to execute when ajax request fails.
global | true | Boolean | Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.
delay | null | String | If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used.
partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components.
autoStart | true | Boolean | In autoStart mode, polling starts automatically on page load, to start polling on demand set to false.
stop | false | Boolean | Stops polling when true.
resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
form | null | String | Form to serialize for an ajax request. Default is the enclosing form.
intervalType | second | String | Type of interval value. Valid values are "second" (default) and "millisecond".

## Getting started with Poll
Poll below invokes increment method on CounterBean every 2 seconds and _txt_count_ is updated
with the new value of the count variable. Note that poll must be nested inside a form.

```xhtml
<h:outputText id="txt_count" value="#{counterBean.count}" />
<p:poll listener="#{counterBean.increment}" update="txt_count" />
```

```java
public class CounterBean {
    private int count;

    public void increment() {
        count++;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
```
## Tuning timing
By default the periodic interval is 2 seconds, this is changed with the interval attribute. Following
poll works every 5 seconds.

```xhtml
<h:outputText id="txt_count" value="#{counterBean.count}" />
<p:poll listener="#{counterBean.increment}" update="txt_count" interval="5" />
```
## Start and Stop
Poll can be started and stopped using client side api;

```xhtml
<h:form>
    <h:outputText id="txt_count" value="#{counterBean.count}" />
    <p:poll interval="5" action="#{counterBean.increment}" update="txt_count" widgetVar="myPoll" autoStart="false" />
    <a href="#" onclick="PF('myPoll').start();">Start</a>
    <a href="#" onclick="PF('myPoll').stop();">Stop</a>
</h:form>
```
Or bind a boolean variable to the _stop_ attribute and set it to false at any arbitrary time.

