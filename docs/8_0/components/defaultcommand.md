# DefaultCommand

Which command to submit the form with when enter key is pressed a common problem in web apps
not just specific to JSF. Browsers tend to behave differently as there doesn’t seem to be a standard
and even if a standard exists, IE probably will not care about it. There are some ugly workarounds
like placing a hidden button and writing javascript for every form in your app. DefaultCommand
solves this problem by normalizing the command(e.g. button or link) to submit the form with on
enter key press.

## Info

| Name | Value |
| --- | --- |
| Tag | defaultCommand
| Component Class | org.primefaces.component.defaultcommand.DefaultCommand
| Component Type | org.primefaces.component.DefaultCommand
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DefaultCommandRenderer
| Renderer Class | org.primefaces.component.defaultcommand.DefaultCommandRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget
| target | null | String | Identifier of the default command component.
| scope | null | String | Identifier of the ancestor component to enable multiple default commands in a form.

## Getting Started with the DefaultCommand
DefaultCommand must be nested inside a form requires _target_ option to reference a clickable
command. Example below triggers _btn2_ when enter key is pressed. Note that an input must have
focused due to browser nature.


```xhtml
<h:form id="form">
    <h:panelGrid columns="3" cellpadding="5">
        <h:outputLabel for="name" value="Name:" style="font-weight:bold"/>
        <p:inputText id="name" value="#{defaultCommandBean.text}" />
        <h:outputText value="#{defaultCommandBean.text}" id="display" />
    </h:panelGrid>
    <p:commandButton value="Button1" id="btn1" action="#{bean.submit1}" ajax="false"/>
    <p:commandButton value="Button2" id="btn2" action="#{bean.submit2}" />
    <h:commandButton value="Button3" id="btn3" action="#{bean.submit3}" />
    <p:defaultCommand target="bt2" />
</h:form>
```
## Scope
If you need multiple default commands on same page use scope attribute that refers to the ancestor
component of the target input.