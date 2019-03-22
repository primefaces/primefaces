# Printer

Printer allows sending a specific JSF component to the printer, not the whole page.

## Info

| Name | Value |
| --- | --- |
| Tag | printer
| Behavior Class | org.primefaces.component.behavior.Printer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
target | null | String | Target component to print.

## Getting started with the Printer
Printer is attached to any command component like a button or a link. Examples below
demonstrates how to print a simple output text or a particular image on page;

```xhtml
<h:commandButton id="btn" value="Print">
    <p:printer target="output" />
</h:commandButton>
<h:outputText id="output" value="PrimeFaces Rocks!" />
<h:outputLink id="lnk" value="#">
    <p:printer target="image" />
    <h:outputText value="Print Image" />
</h:outputLink>
<p:graphicImage id="image" value="/images/nature1.jpg" />
```
