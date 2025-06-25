# ResetInput

Input components keep their local values at state when validation fails. ResetInput is used to clear
the cached values from state so that components retrieve their values from the backing bean model
instead.

## Info

| Name | Value |
| --- | --- |
| Tag | resetInput
| ActionListener Class | org.primefaces.component.resetinput.ResetInputActionListener

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
target | null | String | Comma or white space separated list of component identifiers.
clearModel | false | Boolean | Whether to assign | null | values to bound values as well.

## Getting started with ResetInput
ResetInput is attached to action source components like commandButton and commandLink.

```xhtml
<h:form id="form">
    <p:panel id="panel" header="New User" style="margin-bottom:10px;">
        <p:messages id="messages" />
        <h:panelGrid columns="2">
            <h:outputLabel for="firstname" value="Firstname: *" />
            <p:inputText id="firstname" value="#{pprBean.firstname}" required="true" label="Firstname">
                <f:validateLength minimum="2" />
            </p:inputText>
            <h:outputLabel for="surname" value="Surname: *" />
            <p:inputText id="surname" value="#{pprBean.surname}" required="true" label="Surname"/>
        </h:panelGrid>
    </p:panel>
    <p:commandButton value="Submit" update="panel" action="#{pprBean.savePerson}" />
    <p:commandButton value="Reset Tag" update="panel" process="@this">
        <p:resetInput target="panel" />
    </p:commandButton>
    <p:commandButton value="Reset Non-Ajax" action="#{pprBean.reset}" immediate="true" ajax="false">
        <p:resetInput target="panel" />
    </p:commandButton>
</h:form>
```

ResetInput supports both ajax and non-ajax actions, for non-ajax actions set immediate true on the
source component so lifecycle jumps to render response after resetting. To reset multiple
components at once, provide a list of ids or just provide an ancestor component like the panel in
sample above.

## Reset Programmatically
ResetInput tag is the declarative way to reset input components, another way is resetting
programmatically. This is also handy if inputs should get reset based on a condition. Following
sample demonstrates how to use PrimeFaces.current() to do the reset within an ajaxbehavior
listener. Parameter of the reset method can be a single clientId or a collection of clientIds.

```xhtml
<p:inputText value="#{bean.value}">
    <p:ajax event="blur" listener="#{bean.listener}" />
</p:inputText>
```
```java
public void listener() {
    PrimeFaces.current().resetInputs("form:panel")
}
```
**Tip**
p:ajax has built-in _resetValues_ attribute to reset values of processed components during a request.

