# Lifecycle

Lifecycle is a utility component which displays the execution time of each JSF phase. It also
synchronizes automatically after each AJAX request.

## Info

| Name | Value |
| --- | --- |
| Tag | lifecycle
| Component Class | org.primefaces.component.lifecycle.Lifecycle
| Component Type | org.primefaces.component.Lifecycle
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.LifecycleRenderer
| Renderer Class | org.primefaces.component.lifecycle.LifecycleRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.

## Getting started with Lifecycle
A phase listener needs to be configured to the use component.

```xml
<lifecycle>
    <phase-listener>
        org.primefaces.component.lifecycle.LifecyclePhaseListener
    </phase-listener>
</lifecycle>
```
Then usage is simple as adding the component to the page.

```xhtml
<p:lifecycle />
```
