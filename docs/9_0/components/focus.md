# Focus

Focus is a utility component that makes it easy to manage the element focus on a JSF page.

## Info

| Name | Value |
| --- | --- |
| Tag | focus
| Component Class | org.primefaces.component.focus.Focus
| Component Type | org.primefaces.component.Focus.FocusTag
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.FocusRenderer
| Renderer Class | org.primefaces.component.focus.FocusRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| for | null | String | Specifies the exact component to set focus
| context | null | String | The root component to start first input search.
| minSeverity | error | String | Minimum severity level to be used when finding the first invalid component

## Getting started with Focus
By default focus will find the _first enabled and visible input component_ on page and apply focus.
Input component can be any element such as input, textarea and select.

```xhtml
<p:focus />
<p:inputText ... />
<h:inputText ... />
<h:selectOneMenu ... />
```
Following is a simple example;


```xhtml
<h:form>
    <p:panel id="panel" header="Register">
        <p:focus />
        <p:messages />
        <h:panelGrid columns="3">
            <h:outputLabel for="firstname" value="Firstname: *" />
            <h:inputText id="firstname" value="#{pprBean.firstname}" required="true" label="Firstname" />
            <p:message for="firstname" />
            <h:outputLabel for="surname" value="Surname: *" />
            <h:inputText id="surname" value="#{pprBean.surname}" required="true" label="Surname"/>
            <p:message for="surname" />
        </h:panelGrid>
        <p:commandButton value="Submit" update="panel" action="#{pprBean.savePerson}" />
    </p:panel>
</h:form>
```
When this page initially opens up, input text with id "firstname" will receive focus as it is the first
input component.

## Validation Aware
Another useful feature of focus is that when validations fail, _first invalid component_ will receive a
focus. So in previous example if firstname field is valid but surname field has no input, a validation
error will be raised for surname, in this case focus will be set on surname field implicitly. Note that
for this feature to work on ajax requests, you need to update p:focus component as well.

## Explicit Focus
Additionally, using for attribute focus can be set explicitly on an input component which is useful
when using a dialog.

```xhtml
<p:focus for="text"/>
<h:inputText id="text" value="{bean.value}" />
```