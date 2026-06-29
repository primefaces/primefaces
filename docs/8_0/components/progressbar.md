# ProgressBar

ProgressBar is a process status indicator that can either work purely on client side or interact with
server side using ajax.

## Info

| Name | Value |
| --- | --- |
| Tag | propressBar
| Component Class | org.primefaces.component.progressbar.ProgressBar
| Component Type | org.primefaces.component.Progressbar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ProgressBarRenderer
| Renderer Class | org.primefaces.component.progressbar.ProgressBarRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget
value | 0 | Integer | Value of the progress bar
disabled | false | Boolean | Disables or enables the progressbar
ajax | false | Boolean | Specifies the mode of progressBar, in ajax mode progress value is retrieved from a backing bean.
interval | 3000 | Integer | Interval in seconds to do periodic requests in ajax mode.
style | null | String | Inline style of the main container element.
styleClass | null | String | Style class of the main container element.
labelTemplate | {value} | String | Template of the progress label.
displayOnly | false | Boolean | Enables static display mode.
global | true | Boolean | Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.
mode | determinate | String | Defines the mode of the progress, valid values are "determinate" and "indeterminate".
animationDuration | 500 | Integer | Animation duration in milliseconds determining how long the animation will run. Default is 500.

## Getting started with the ProgressBar
ProgressBar has two modes, "client"(default) or "ajax". Following is a pure client side progressBar.

```xhtml
<p:progressBar widgetVar="pb" />
<p:commandButton value="Start" type="button" onclick="start()" />
<p:commandButton value="Cancel" type="button" onclick="cancel()" />
```
```js
<script type="text/javascript">
    function start() {
    this.progressInterval = setInterval(function(){
        PF('pb').setValue(PF('pb').getValue() + 10);
    }, 2000);
}
function cancel() {
    clearInterval(this.progressInterval);
    PF('pb').setValue(0);
}
</script>
```
## Ajax Progress
Ajax mode is enabled by setting ajax attribute to true, in this case the value defined on a managed
bean is retrieved periodically and used to update the progress.

```xhtml
<p:progressBar ajax="true" value="#{progressBean.progress}" />
```
```java
public class ProgressBean {
    private int progress;
    //getter-setter
}
```

## Interval
ProgressBar is based on polling and 3000 milliseconds is the default interval for ajax progress bar
meaning every 3 seconds progress value will be recalculated. In order to set a different value, use
the interval attribute.

```xhtml
<p:progressBar interval="5000" />
```

## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| complete | javax.faces.event.AjaxBehaviorEvent | Is fired when the progress is completed


Example below demonstrates how to use this event:

```java
public class ProgressBean {
    private int progress;

    public void handleComplete() {
        //Add a faces message
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
```
```xhtml
<p:progressBar value="#{progressBean.progress}" ajax="true">
    <p:ajax event="complete" listener="#{progressBean.handleComplete}" update="messages" />
</p:progressBar>
<p:growl id="messages" />
```

## Display Only
Assume you have a process like a ticket purchase that spans various pages where each page has
different use cases such as customer info, seat selection, billing, payment and more. In order to
display static value of the process on each page, you can use a static progressBar.

```xhtml
<p:progressBar value="50" displayOnly="true" />
```
## Client Side API
Widget: _PrimeFaces.widget.ProgressBar_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
getValue() | - | Number | Returns current value
setValue(value) | value: Value to display | void | Sets current value
start() | - | void | Starts ajax progress bar
cancel() | - | void | Stops ajax progress bar

## Skinning
ProgressBar resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies |
| --- | --- |
.ui-progressbar | Main container.
.ui-progressbar-value | Value of the progressbar
.ui-progressbar-label | Progress label.

As skinning style classes are global, see the main theming section for more information.

