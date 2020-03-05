# Galleria

Galleria is used to display a set of images.

## Info

| Name | Value |
| --- | --- |
| Tag | galleria
| Component Class | org.primefaces.component.galleria.Galleria
| Component Type | org.primefaces.component.Galleria
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.GalleriaRenderer
| Renderer Class | org.primefaces.component.galleria.GalleriaRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| value | null | Collection | Collection of data to display.
| var | null | String | Name of variable to access an item in collection.
| style | null | String | Inline style of the container element.
| styleClass | null | String | Style class of the container element.
| effect | fade | String | Name of animation to use.
| effectSpeed | 700 | Integer | Duration of animation in milliseconds.
| panelWidth | 600 | Integer | Width of the viewport.
| panelHeight | 400 | Integer | Height of the viewport.
| frameWidth | 60 | Integer | Width of the frames.
| frameHeight | 40 | Integer | Height of the frames.
| showFilmstrip | true | Boolean | Defines visibility of filmstrip.
| showCaption | false | Boolean | Defines visibility of captions.
| transitionInterval | 4000 | Integer | Defines interval of slideshow.
| autoPlay | true | Boolean | Images are displayed in a slideshow in autoPlay.
| tabindex | 0 | String | Specifies the tab order of element in tab navigation.

## Getting Started with Galleria
Images to displayed are defined as children of galleria;

```xhtml
<p:galleria effect="slide" effectDuration="1000">
    <p:graphicImage value="/images/image1.jpg" title="image1" alt="image1 desc" />
    <p:graphicImage value="/images/image2.jpg" title="image1" alt=" image2 desc" />
    <p:graphicImage value="/images/image3.jpg" title="image1" alt=" image3 desc" />
    <p:graphicImage value="/images/image4.jpg" title="image1" alt=" image4 desc" />
</p:galleria>
```
Galleria displays the details of an image using an overlay which is displayed by clicking the
information icon. Title of this popup is retrieved from the image _title_ attribute and description from
_alt_ attribute so it is suggested to provide these attributes as well.

## Dynamic Collection
Most of the time, you would need to display a dynamic set of images rather than defining each
image declaratively. For this you can use built-in data iteration feature.

```xhtml
<p:galleria value="#{galleriaBean.images}" var="image" >
    <p:graphicImage value="#{image.path}" title="#{image.title}" alt="#{image.description}" />
</p:galleria>
```
## Effects
There are various effect options to be used in transitions; blind, bounce, clip, drop, explode, fade,
fold, highlight, puff, pulsate, scale, shake, size, slide and transfer.

By default animation takes 500 milliseconds, use _effectSpeed_ option to tune this.


```xhtml
<p:galleria effect="slide" effectSpeed="1000">
    //images
</p:galleria>
```
## Skinning
Galleria resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes

| Class | Applies | 
| --- | --- | 
| .ui-galleria | Container element for galleria.
| .ui-galleria-panel-wrapper | Container of panels.
| .ui-galleria-panel | Container of each image.
| .ui-galleria-caption | Caption element.
| .ui-galleria-nav-prev, .ui-galleria-nav-next | Navigators of filmstrip.
| .ui-galleria-filmstrip-wrapper | Container of filmstrip.
| .ui-galleria-filmstrip | Filmstrip element.
| .ui-galleria-frame | Frame element in a filmstrip.
| .ui-galleria-frame-content | Content of a frame.
| .ui-galleria-frame-image | Thumbnail image.
