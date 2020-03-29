# Fragment

Fragment component is used to define automatically partially process and update sections whenever
ajax request is triggered by a descendant component.

## Info

| Name | Value |
| --- | --- |
| Tag | fragment
| Component Class | org.primefaces.component.fragment.Fragment
| Component Type | org.primefaces.component.Fragment
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.FragmentRenderer
| Renderer Class | org.primefaces.component.fragment.FragmentRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| autoUpdate | false | Boolean | Updates the fragment automatically.

## Getting started with Fragment
In the following case, required input field outside the fragment is ignored and only the contents of
the fragment are processed-updated automatically on button click since button is inside the
fragment. Fragment makes it easy to define partial ajax process and update without explicitly
defining component identifiers.


```xhtml
<h:form>
    <h:panelGrid columns="2">
        <p:outputLabel for="ign" value="Required:" />
        <p:inputText id="ign" required="true" />
    </h:panelGrid>
    <p:fragment autoUpdate="true">
        <h:panelGrid columns="4" cellpadding="5">
            <h:outputLabel for="name" value="Name:" />
            <p:inputText id="name" value="#{pprBean.firstname}" />
            <p:commandButton value="Submit"/>
            <h:outputText value="#{pprBean.firstname}" />
        </h:panelGrid>
    </p:fragment>
</h:form>
```
AutoUpdate has different notion compared to autoUpdate of message, growl and outputPanel. The
fragment is updated automatically after an ajax request if the source is a descendant. In other
mentioned components, there is no such restriction as they are updated for every ajax request
regardless of the source.