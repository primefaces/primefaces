# Separator

Separator displays a horizontal line to separate content.

## Info

| Name | Value |
| --- | --- |
| Tag | separator
| Component Class | org.primefaces.component.separator.Separator
| Component Type | org.primefaces.component.Separator
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SeparatorRenderer
| Renderer Class | org.primefaces.component.separator.SeparatorRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
title | null | String | Advisory tooltip informaton.
style | null | String | Inline style of the separator.
styleClass | null | String | Style class of the separator.

## Getting started with Separator
In its simplest form, separator is used as;

```xhtml
//content
<p:separator />
//content
```
## Dimensions
Separator renders a _<hr />_ tag which style and styleClass options apply.


```xhtml
<p:separator style="width:500px;height:20px" />
```
## Special Separators
Separator can be used inside other components such as menu when supported.

```xhtml
<p:menu>
    //submenu or menuitem
    <p:separator />
    //submenu or menuitem
</p:menu>
```
## Skinning
As mentioned in dimensions section, style and styleClass options can be used to style the separator.
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-separator | Separator element

As skinning style classes are global, see the main theming section for more information.

