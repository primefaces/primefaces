# Clock

Clock displays server or client datetime live.

## Getting Started with Clock
Clock has two modes, _client_ (default) and _server_. In simple mode, datetime is displayed by just
adding component on page. On page load, clock is initialized and start running based on client time.

```xhtml
<p:clock />
```

## Server Mode
In server mode, clock initialized itself with the server’s time or the custom time set in `value` and starts 
running on client side. To make sure client clock and server clock is synced, you can enable autoSync option that 
makes an AJAX call to the server periodically to refresh the server time with client.

```xhtml
<p:clock mode="server"/>
<p:clock mode="server" value="#{clockView.dateTime}"/>
```

## DateTime Format
Datetime format used can be changed using pattern attribute.

```xhtml
<p:clock pattern="HH:mm:ss dd.MM.yyyy" />
```

## Client Side API
Widget: _PrimeFaces.widget.Clock_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| start() | - | void | Starts this clock if it is not already running..
| stop() | - | void | Stops this clock it is currently running.
| sync() | - | void | Synchronizes this clock so that it shows the current time. This will trigger an AJAX update of this component.
| isAnalogClock() | - | boolean | Checks whether this clock is displayed as an analog or digital clock.

## Skinning
Clock resides in a container element which _style_ and _styleClass_ options apply. Following is the list
of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-clock | Container element. |
