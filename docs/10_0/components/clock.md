# Clock

Clock displays server or client datetime live.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.clock.html)

## Info

| Name | Value |
| --- | --- |
| Tag | clock
| Component Class | org.primefaces.component.clock.Clock
| Component Type | org.primefaces.component.Clock
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ClockRenderer
| Renderer Class | org.primefaces.component.clock.ClockRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| pattern | null | String | Datetime format.
| mode | client | String | Mode value, valid values are 'client' and 'server'.
| autoSync | false | Boolean | Syncs time periodically in server mode.
| syncInterval | 60000 | Integer | Defines in milliseconds the sync interval in autoSync setting.
| timeZone | TimeZone.getDefault() | Object | String or a java.util.TimeZone instance to specify the timezone used for date conversion in 'server' mode.
| displayMode | digital | String | Display mode, valid values are 'digital' and 'analog'.
| value | null | Object | Custom clock time in 'server' mode must be either Date or LocalDateTime. Null means use server clock time.

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
