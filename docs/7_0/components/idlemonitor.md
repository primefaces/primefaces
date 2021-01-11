# IdleMonitor

IdleMonitor watches user actions on a page and notify callbacks in case they go idle or active again.

## Info

| Name | Value |
| --- | --- |
| Tag | idleMonitor
| Component Class | org.primefaces.component.idlemonitor.IdleMonitor
| Component Type | org.primefaces.component.IdleMonitor
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.IdleMonitorRenderer
| Renderer Class | org.primefaces.component.idlemonitor.IdleMonitor

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
timeout | 300000 | Integer | Time to wait in milliseconds until deciding if the user is idle. Default is 5 minutes.
onidle | null | String | Client side callback to execute when user goes idle.
onactive | null | String | Client side callback to execute when user goes idle.
multiWindowSupport | false | Boolean | When set to true, the lastAccessed state will be shared between all browser windows for the same servlet context.
widgetVar | null | String | Name of the client side widget.

## Getting Started with IdleMonitor
To begin with, you can hook-in to client side events that are called when a user goes idle or
becomes active again. Example below toggles visibility of a dialog to respond these events.


```xhtml
<p:idleMonitor onidle="PF('idleDialog').show();" onactive="PF('idleDialog').hide();"/>
<p:dialog header="What's happening?" widgetVar="idleDialog" modal="true">
    <h:outputText value="Dude, are you there?" />
</p:dialog>
```
## Controlling Timeout
By default, idleMonitor waits for 5 minutes (300000 ms) until triggering the onidle event. You can
customize this duration with the timeout attribute.

## Ajax Behavior Events
IdleMonitor provides two ajax behavior events which are _idle_ and _active_ that are fired according to
user status changes. Example below displays messages for each event;

```xhtml
<p:idleMonitor timeout="5000" update="messages">
    <p:ajax event="idle" listener="#{bean.idleListener}" update="msg" />
    <p:ajax event="active" listener="#{bean.activeListener}" update="msg" />
</p:idleMonitor>
<p:growl id=”msg” />
```
```java
public class Bean {
    public void idleListener() {
        //Add facesmessage
    }
    public void idle() {
        //Add facesmessage
    }
}
```
## Client Side API
Widget: _PrimeFaces.widget.IdleMonitor_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
pause() | - | void | Pauses the monitor.
resume() | - | void | Resumes monitoring
reset() | - | void | Resets the timer of monitor.
