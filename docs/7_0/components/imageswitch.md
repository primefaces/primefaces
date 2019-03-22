# ImageSwitch

ImageSwitch component is a simple image gallery component.

## Info

| Name | Value |
| --- | --- |
| Tag | imageSwitch
| Component Class | org.primefaces.component.imageswitch.ImageSwitch
| Component Type | org.primefaces.component.ImageSwitch
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ImageSwitchRenderer
| Renderer Class | org.primefaces.component.imageswitch.ImageSwitchRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
effect | null | String | Name of the effect for transition.
speed | 500 | Integer | Speed of the effect in milliseconds.
slideshowSpeed | 3000 | Integer | Slideshow speed in milliseconds.
slideshowAuto | true | Boolean | Starts slideshow automatically on page load.
style | null | String | Style of the main container.
styleClass | null | String | Style class of the main container.
activeIndex | 0 | Integer | Index of the first image.

## Getting Started with ImageSwitch
ImageSwitch component needs a set of images to display. Provide the image collection as a set of
children components.

```xhtml
<p:imageSwitch effect="FlyIn">
    <p:graphicImage value="/images/nature1.jpg" />
    <p:graphicImage value="/images/nature2.jpg" />
    <p:graphicImage value="/images/nature3.jpg" />
    <p:graphicImage value="/images/nature4.jpg" />
</p:imageSwitch>
```
Most of the time, images could be dynamic, ui:repeat is supported to implement this case.

```xhtml
<p:imageSwitch>
    <ui:repeat value="#{bean.images}" var="image">
        <p:graphicImage value="#{image}" />
    </ui:repeat>
</p:imageSwitch>
```
## Slideshow or Manual
ImageSwitch is in slideShow mode by default, if you’d like manual transitions disable slideshow
and use client side api to create controls.

```xhtml
<p:imageSwitch effect="FlyIn" widgetVar="imageswitch">
    //images
</p:imageSwitch>
<span onclick="PF('imageswitch').previous();">Previous</span>
<span onclick="PF('imageswitch').next();">Next</span>
```
## Client Side API
Widget: _PrimeFaces.widget.ImageSwitch_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
resumelideshow() | - | void | Starts slideshow mode.
stopSlideshow() | - | void | Stops slideshow mode.
toggleSlideshow() | - | void | Toggles slideshow mode.
pauseSlideshow() | - | void | Pauses slideshow mode.
next() | - | void | Switches to next image.
previous() | - | void | Switches to previous image.
switchTo(index) | index | void | Displays image with given index.

## Effect Speed
The speed is considered in terms of milliseconds and specified via the speed attribute.

```xhtml
<p:imageSwitch effect="FlipOut" speed="150">
    //set of images
</p:imageSwitch>
```
## List of Effects
ImageSwitch supports a wide range of transition effects. Following is the full list, note that values
are case sensitive.

- blindX
- blindY
- blindZ
- cover
- curtainX
- curtainY
- fade
- fadeZoom
- growX
- growY
- none
- scrollUp
- scrollDown
- scrollLeft
- scrollRight
- scrollVert
- shuffle
- slideX
- slideY
- toss
- turnUp
- turnDown
- turnLeft
- turnRight
- uncover
- wipe
- zoom
