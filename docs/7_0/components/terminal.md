# Terminal

Terminal is an ajax powered web based terminal that brings desktop terminals to JSF.

## Info

| Name | Value |
| --- | --- |
| Tag | terminal
| Component Class | org.primefaces.component.terminal.Terminal
| Component Type | org.primefaces.component.Terminal
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TerminalRenderer
| Renderer Class | org.primefaces.component.terminal.TerminalRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
welcomeMessage | null | String | Welcome message to be displayed on initial load.
prompt | prime $ | String | Primary prompt text.
commandHandler | null | MethodExpr | Method to be called with arguments to process.
widgetVar | null | String | Name of the client side widget.
escape | true | Boolean | Defines if the terminal is escaped or not.

## Getting started with the Terminal
A command handler is required to interpret commands entered in terminal.

```xhtml
<p:terminal commandHandler="#{terminalBean.handleCommand}" />
```
```java
public class TerminalBean {
    public String handleCommand(String command, String[] params) {
        if(command.equals("greet"))
            return "Hello " + params[0];
        else if(command.equals("date"))
            return new Date().toString();
        else
            return command + " not found";
    }
}
```
Whenever a command is sent to the server, handleCommand method is invoked with the command
name and the command arguments as a | String | array.

## Client Side API
Client side widget exposes _clear()_ and _focus()_ methods. Following shows how to add focus on a
terminal nested inside a dialog;

```xhtml
<p:commandButton type="button" Value="Apply Focus" onclick="PF('term').focus();"/>
<p:terminal widgetVar="term" commandHandler="#{terminalBean.handleCommand}" />
```
## Skinning
Terminal resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-terminal | Main container element.
.ui-terminal-content | Content display of previous commands with responses.
.ui-terminal-prompt | Prompt text.
