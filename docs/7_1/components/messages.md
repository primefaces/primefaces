# Messages

Messages is a pre-skinned extended version of the standard JSF messages component.

## Info

| Name | Value |
| --- | --- |
| Tag | messages
| Component Class | org.primefaces.component.messages.Messages
| Component Type | org.primefaces.component.Messages
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MessagesRenderer
| Renderer Class | org.primefaces.component.messages.MessagesRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
showSummary | true | Boolean | Specifies if the summary of the FacesMessages should be displayed.
showDetail | false | Boolean | Specifies if the detail of the FacesMessages should be displayed.
globalOnly | false | String | When true, only facesmessages with no clientIds are displayed.
redisplay | true | Boolean | Defines if already rendered messages should be displayed
for | null | String | The clientId or name of associated key, takes precedence when used with globalOnly.
forType | null | String | Type of the "for" attribute. Valid values are "key" and "expression".
forIgnores | null | String | Defines a list of keys and clientIds, which should NOT be rendered by this component. Seperated by space or comma.
escape | true | Boolean | Defines whether html would be escaped or not.
severity | null | String | Comma separated list of severities to display only.
closable | false | Boolean | Adds a close icon to hide the messages.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
showIcon | true | Boolean | Defines if severity icons would be displayed.
skipDetailIfEqualsSummary | false | Boolean | Defines if rendering of the detail text should be skipped, if the detail and summaray are equals.

## Getting started with Messages
Message usage is exactly same as standard messages.

```xhtml
<p:messages />
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
instance, you can configure messages to only display infos and warnings.

```xhtml
<p:messages severity="info,warn" />
```

## Escaping
Messages escapes html content in messages, disable escape option to display content as html.

```xhtml
<p:messages escape="false" />
```
## Skinning
Full list of CSS selectors of message is as follows;

| Class | Applies |
| --- | --- |
ui-messages-{severity} | Container element of the message
ui-messages-{severity}-summary | Summary text
ui-messages-{severity}-detail | Detail text
ui-messages-{severity}-icon | Icon of the message.

* {severity} can be ‘info’, ‘error’, ‘warn’ and error.

