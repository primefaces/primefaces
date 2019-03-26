# HotKey

HotKey is a generic key binding component that can bind any formation of keys to javascript event
handlers or ajax calls.

## Info

| Name | Value |
| --- | --- |
| Tag | hotkey
| Component Class | org.primefaces.component.hotkey.HotKey
| Component Type | org.primefaces.component.HotKey
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.HotKeyRenderer
| Renderer Class | org.primefaces.component.hotkey.HotKeyRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
bind | null | String | The Key binding.
handler | null | String | Javascript event handler to be executed when the key binding is pressed.
action | null | MethodExpr | A method expression that’d be processed in the partial request caused by uiajax.
actionListener | null | MethodExpr | An actionlistener that’d be processed in the partial request caused by uiajax.
immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application phase.
async | false | Boolean | When set to true, ajax requests are not queued.
process | null | String | Component id(s) to process partially instead of whole view.
update | null | String | Client side id of the component(s) to be updated after async partial submit request.
onstart | null | String | Javascript handler to execute before ajax request is begins.
oncomplete | null | String | Javascript handler to execute when ajax request is completed.
onsuccess | null | String | Javascript handler to execute when ajax request succeeds.
onerror | null | String | Javascript handler to execute when ajax request fails.
global | true | Boolean | Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.
delay | null | String | If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used.
partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components.
resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
form | null | String | Form to serialize for an ajax request. Default is the enclosing form.

## Getting Started with HotKey
HotKey is used in two ways, either on client side with the event handler or with ajax support.
Simplest example would be;

```xhtml
<p:hotkey bind="a" handler="alert(‘Pressed a’);" />
```
When this hotkey is on page, pressing the a key will alert the ‘Pressed key a’ text.

## Key combinations
Most of the time you’d need key combinations rather than a single key.


```xhtml
<p:hotkey bind="ctrl+s" handler="alert(‘Pressed ctrl+s’);" />
<p:hotkey bind="ctrl+shift+s" handler="alert(‘Pressed ctrl+shift+s’)" />
```
## Integration
Here’s an example demonstrating how to integrate hotkeys with a client side api. Using left and
right keys will switch the images displayed via the p:imageSwitch component.

```xhtml
<p:hotkey bind="left" handler="PF('switcher').previous();" />
<p:hotkey bind="right" handler="PF('switcher').next();" />
<p:imageSwitch widgetVar="switcher">
    //content
</p:imageSwitch>
```
## Ajax Support
Ajax is a built-in feature of hotKeys meaning you can do ajax calls with key combinations.
Following form can be submitted with the _ctrl+shift+s_ combination.

```xhtml
<h:form>
    <p:hotkey bind="ctrl+shift+s" update="display" />
    <h:panelGrid columns="2">
        <h:outputLabel for="name" value="Name:" />
        <h:inputText id="name" value="#{bean.name}" />
    </h:panelGrid>
    <h:outputText id="display" value="Hello: #{bean.firstname}" />
</h:form>
```
Note that hotkey will not be triggered if there is a focused input on page.
