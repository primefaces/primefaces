# Message

Message is a pre-skinned extended version of the standard JSF message component.

## Info

| Name | Value |
| --- | --- |
| Tag | message
| Component Class | org.primefaces.component.message.Message
| Component Type | org.primefaces.component.Message
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MessageRenderer
| Renderer Class | org.primefaces.component.message.MessageRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
showSummary | false | Boolean | Specifies if the summary of the FacesMessage should be displayed.
showDetail | true | Boolean | Specifies if the detail of the FacesMessage should be displayed.
for | null | String | Id of the component whose messages to display.
redisplay | true | Boolean | Defines if already rendered messages should be displayed
display | both | String | Defines the display mode.
escape | true | Boolean | Defines whether html would be escaped or not.
severity | null | String | Comma separated list of severities to display only.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.

## Getting started with Message
Message usage is exactly same as standard message.

```xhtml
<h:inputText id="txt" value="#{bean.text}" />
<p:message for="txt" />
```
## Display Mode
Message component has three different display modes;

- text : Only message text is displayed.
- icon : Only message severity is displayed and message text is visible as a tooltip.
- both (default) : Both icon and text are displayed.

## Severity Levels
Using severity attribute, you can define which severities can be displayed by the component. For
instance, you can configure messages to only display infos and warnings.

```xhtml
<p:message severity="info,warn" for="txt"/>
```
## Escaping
Component escapes html content in messages by default, in case you need to display html, disable
escape option.

```xhtml
<p:message escape="false" for="txt" />
```
## Skinning
Full list of CSS selectors of message is as follows;

| Class | Applies | 
| --- | --- | 
ui-message-{severity} | Container element of the message
ui-message-{severity}-summary | Summary text
ui-message-{severity}-detail | Detail text

{severity} can be ‘info’, ‘error’, ‘warn’ and error.

