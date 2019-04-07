# ImageCompare

ImageCompare provides a user interface to compare two images.

## Info

| Name | Value |
| --- | --- |
| Tag | imageCompare
| Component Class | org.primefaces.component.imagecompare.ImageCompare
| Component Type | org.primefaces.component.ImageCompare
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ImageCompareRenderer
| Renderer Class | org.primefaces.component.imagecompare.ImageCompareRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
leftImage | null | String | Source of the image placed on the left side
rightImage | null | String | Source of the image placed on the right side
width | null | String | Width of the images
height | null | String | Height of the images
style | null | String | Inline style of the container element
styleClass | null | String | Style class of the container element

## Getting started with ImageCompare
ImageCompare is created with two images with same height and width. It is required to set width
and height of the images as well.

```xhtml
<p:imageCompare leftImage="xbox.png" rightImage="ps3.png" width="438" height="246"/>
```
## Skinning
Both images are placed inside a div container element, _style_ and _styleClass_ attributes apply to this
element.