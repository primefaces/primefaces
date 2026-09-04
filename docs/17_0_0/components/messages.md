# Messages

Messages is a pre-skinned extended version of the standard Jakarta Faces messages component.

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
Messages escapes HTML content in messages, disable escape option to display content as HTML.

```xhtml
<p:messages escape="false" />
```

## Client Side API
Widget: _PrimeFaces.widget.Messages_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| appendMessage(facesMessage) | facesMessage: see JSDoc about PrimeFaces.FacesMessage  | void | appends the message |
| clearMessages() | none  | void | clears all current messages |


## Skinning
Full list of CSS selectors of message is as follows;

| Class | Applies |
| --- | --- |
ui-messages-{severity} | Container element of the message
ui-messages-{severity}-summary | Summary text
ui-messages-{severity}-detail | Detail text
ui-messages-{severity}-icon | Icon of the message.

* {severity} can be `info`, `warn`, `error` and `fatal`.

