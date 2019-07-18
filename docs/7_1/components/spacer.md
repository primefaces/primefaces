# Spacer

Spacer is used to put spaces between elements.

## Info

| Name | Value |
| --- | --- |
| Tag | spacer
| Component Class | org.primefaces.component.spacer.Spacer
| Component Type | org.primefaces.component.Spacer
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SpacerRenderer
| Renderer Class | org.primefaces.component.spacer.SpacerRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
title | null | String | Advisory tooltip informaton.
style | null | String | Inline style of the spacer.
styleClass | null | String | Style class of the spacer.
width | null | String | Width of the space.
height | null | String | Height of the space.

## Getting started with Spacer
Spacer is used by either specifying width or height of the space.

```xhtml
Spacer in this example separates this text <p:spacer width="100" height="10"> and
<p:spacer width="100" height="10"> this text.
```
