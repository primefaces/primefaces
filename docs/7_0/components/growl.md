# Growl

Growl is based on the Mac’s growl notification widget and used to display FacesMessages in an
overlay.

## Info

| Name | Value |
| --- | --- |
| Tag | growl
| Component Class | org.primefaces.component.growl.Growl
| Component Type | org.primefaces.component.Growl
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.GrowlRenderer
| Renderer Class | org.primefaces.component.growl.GrowlRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
sticky | false | Boolean | Specifies if the message should stay instead of hidden automatically.
showSummary | true | Boolean | Specifies if the summary of message should be displayed.
showDetail | false | Boolean | Specifies if the detail of message should be displayed.
globalOnly | false | Boolean | When true, only facesmessages without clientids are displayed.
life | 6000 | Integer | Duration in milliseconds to display non-sticky messages.
redisplay | true | Boolean | Defines if already rendered messaged should be displayed.
for | null | String | Name of associated key, takes precedence when used with globalOnly.
escape | true | Boolean | Defines whether html would be escaped or not.
severity | null | String | Comma separated list of severities to display only.
keepAlive | false | Boolean | Defines if previous messages should be kept on a new message is shown.

## Getting Started with Growl
Growl usage is similar to standard h:messages component. Simply place growl anywhere on your
page, since messages are displayed as an overlay, the location of growl in JSF page does not matter.

```xhtml
<p:growl />
```
## Lifetime of messages
By default each message will be displayed for 6000 ms and then hidden. A message can be made
sticky meaning it’ll never be hidden automatically.

```xhtml
<p:growl sticky="true" />
```
If growl is not working in sticky mode, it’s also possible to tune the duration of displaying
messages. Following growl will display the messages for 5 seconds and then fade-out.

```xhtml
<p:growl life="5000" />
```

## Positioning
Growl is positioned at top right corner by default, position can be controlled with a CSS selector
called _ui-growl_. With the below setting growl will be located at top left corner.


```css
.ui-growl {
    left:20px;
}
```
## Targetable Messages
There may be times where you need to target one or more messages to a specific message
component, for example suppose you have growl and messages on same page and you need to
display some messages on growl and some on messages. Use for attribute to associate messages
with specific components.

```xhtml
<p:messages for="somekey" />
<p:growl for="anotherkey" />
```
```java
FacesContext context = FacesContext.getCurrentInstance();
context.addMessage("somekey", facesMessage1);
context.addMessage("somekey", facesMessage2);
context.addMessage("anotherkey", facesMessage3);
```
In sample above, messages will display first and second message and growl will only display the
3rd message.

## Severity Levels
Using severity attribute, you can define which severities can be displayed by the component. For
instance, you can configure growl to only display infos and warnings.

```xhtml
<p:growl severity="info,warn" />
```
## Escaping
Growl escapes html content in messages, in case you need to display html via growl set escape
option to false.

```xhtml
<p:growl escape="false" />
```
## Skinning
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-growl | Main container element of growl
.ui-growl-item-container | Container of messages
.ui-growl-item | Container of a message
.ui-growl-message | Text message container
.ui-growl-title | Summary of the message
.ui-growl-message | p Detail of the message
.ui-growl-image | Severity icon
.ui-growl-image-info | Info severity icon
.ui-growl-image-warn | Warning severity icon
.ui-growl-image-error | Error severity icon
.ui-growl-image-fatal | Fatal severity icon

As skinning style classes are global, see the main theming section for more information.
