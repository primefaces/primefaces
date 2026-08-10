# Message

Message is a pre-skinned extended version of the standard Jakarta Faces message component.

## Getting started with Message
Message usage is exactly same as standard message.

```xhtml
<h:inputText id="txt" value="#{bean.text}" />
<p:message for="txt" />
```

## Display Mode
Message component has four different display modes;

- text : Only message text is displayed.
- icon : Only message severity is displayed and message text is visible as a tooltip.
- both (default) : Both icon and text are displayed.
- tooltip : Message text is visible as a tooltip of the target component.

## Severity Levels
Using severity attribute, you can define which severities can be displayed by the component. For
instance, you can configure messages to only display infos and warnings.

```xhtml
<p:message severity="info,warn" for="txt"/>
```

## Escaping
Component escapes HTML content in messages by default, in case you need to display HTML, disable
escape option.

```xhtml
<p:message escape="false" for="txt" />
```

## Client Side API
Widget: _PrimeFaces.widget.Messages_

| Method               | Params | Return Type | Description                |
|----------------------| --- | --- |----------------------------|
| render(facesMessage) | facesMessage: see JSDoc about PrimeFaces.FacesMessage  | void | renders the message        |
| clear()              | none  | void | clears the current message |

## Skinning
Full list of CSS selectors of message is as follows;

| Class | Applies |
| --- | --- |
ui-message-{severity} | Container element of the message
ui-message-{severity}-summary | Summary text
ui-message-{severity}-detail | Detail text
ui-message-{severity}-icon | Icon of the message

{severity} can be `info`, `warn`, `error` and `fatal`.

