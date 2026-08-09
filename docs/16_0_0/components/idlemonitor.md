# IdleMonitor

IdleMonitor watches user actions on a page and notify callbacks in case they go idle or active again.

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

## Events
IdleMonitor provides two behavior events (_idle_ and _active_) that are fired according to
user status changes. If `multiWindowSupport=true` then the _active_ and _idle_ events will be fired for the
active window and all other windows will only fire the _idle_ event.

## Client Side API
Widget: _PrimeFaces.widget.IdleMonitor_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
pause() | - | void | Pauses the idle monitor.
resume() | - | void | Resumes monitoring
reset() | - | void | Resets the timer of idle monitor.
