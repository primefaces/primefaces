# RemoteCommand

RemoteCommand provides a way to execute backing bean methods directly from javascript.

## Info

| Name | Value |
| --- | --- |
| Tag | remoteCommand
| Component Class | org.primefaces.component.remotecommand.RemoteCommand
| Component Type | org.primefaces.component.RemoteCommand
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.RemoteCommandRenderer
| Renderer Class | org.primefaces.component.remotecommand.RemoteCommandRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
action | null | MethodExpr | A method expression that’d be processed in the partial request caused by uiajax.
actionListener | null | MethodExpr | An actionlistener that’d be processed in the partial request caused by uiajax.
immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application phase.
name | null | String | Name of the command
async | false | Boolean | When set to true, ajax requests are not queued.
process | @all | String | Component(s) to process partially instead of whole view.
update | @none | String | Component(s) to update with ajax.
onstart | null | String | Javascript handler to execute before ajax request is begins.
oncomplete | null | String | Javascript handler to execute when ajax request is completed.
onsuccess | null | String | Javascript handler to execute when ajax request succeeds.
onerror | null | String | Javascript handler to execute when ajax request fails.
global | true | Boolean | Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.
delay | null | String | If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used.
partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components.
autoRun | false | Boolean | When enabled command is executed on page load.
resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
form | null | String | Form to serialize for an ajax request. Default is the enclosing form.

## Getting started with RemoteCommand
RemoteCommand is used by invoking the command from your javascript code.

```xhtml
<p:remoteCommand name="increment" action="#{counter.increment}" out="count" />
<h:outputText id="count" value="#{counter.count}" />
```
```js
<script type="text/javascript">
    function customfunction() {
        //your custom code
        increment(); //makes a remote call
    }
</script>
```
That’s it whenever you execute your custom javascript function(eg customfunction()), a remote call
will be made, actionListener is processed and output text is updated. Note that remoteCommand
must be nested inside a form.


## Passing Parameters
Remote command can send dynamic parameters in the following way;

```js
increment([{name:'x', value:10}, {name:'y', value:20}]);
```
