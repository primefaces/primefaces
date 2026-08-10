# Terminal

Terminal is an ajax powered web based terminal that brings desktop terminals to Jakarta Faces.

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
