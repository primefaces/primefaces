# Galleria

Galleria is a content gallery component.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Galleria.html)

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
| varStatus | null | String | Name of the exported request scoped variable for the status of the iteration.
| style | null | String | Inline style of the container element.
| styleClass | null | String | Style class of the container element.
| activeIndex | 0 | Integer | Index of the first item.
| fullScreen | false | Boolean | Whether to display the component on fullscreen.
| closeIcon | null | String | Close icon on fullscreen mode.
| numVisible | 3 | Integer | Number of items per page.
| responsiveOptions | null | Object[] | A model of options for responsive design.
| showThumbnails | true | Boolean | Whether to display thumbnail container.
| showIndicators | false | Boolean | Whether to display indicator container.
| showIndicatorsOnItem | false | Boolean | When enabled, indicator container is displayed on item container.
| showCaption | false | Boolean | Whether to display caption container.
| showItemNavigators | false | Boolean | Whether to display navigation buttons in item container.
| showThumbnailNavigators | true | Boolean | Whether to display navigation buttons in thumbnail container.
| showItemNavigatorsOnHover | false | Boolean | Whether to display navigation buttons on item container's hover.
| changeItemOnIndicatorHover | false | Boolean | When enabled, item is changed on indicator item's hover.
| circular | false | Boolean | Defines if scrolling would be infinite.
| autoPlay | false | Boolean | Items are displayed with a slideshow in autoPlay mode.
| transitionInterval | 4000 | Number | Time in milliseconds to scroll items.
| thumbnailsPosition | bottom | String | Position of thumbnails. Valid values are "bottom", "top", "left" and "right".
| verticalViewPortHeight | 450px | String | Height of the viewport in vertical layout. Default is 450px.
| indicatorsPosition | bottom | String | Position of indicators. Valid values are "bottom", "top", "left" and "right".
| tabindex | 0 | Integer | Position of the output in the tabbing order. Default: "0".

## Getting Started with Galleria
Contents to displayed are defined as children of galleria;

```xhtml
<p:galleria>
    <p:graphicImage value="/images/image1.jpg" title="image1" alt="image1 desc" />
    <p:graphicImage value="/images/image2.jpg" title="image1" alt="image2 desc" />
    <p:graphicImage value="/images/image3.jpg" title="image1" alt="image3 desc" />
    <p:graphicImage value="/images/image4.jpg" title="image1" alt="image4 desc" />
</p:galleria>
```
Usage as above will only display the field called "item". It can be created with the other field as shown in the "Templating" section below.

## Templating
Galleria can be customized using f:facet tags; "header", "footer", "thumbnail" and "indicator".

```xhtml
<p:galleria>
    <p:graphicImage value="/images/image1.jpg" title="image1" alt="image1 desc" />
    <p:graphicImage value="/images/image2.jpg" title="image2" alt="image2 desc" />
    <p:graphicImage value="/images/image3.jpg" title="image3" alt="image3 desc" />
    <p:graphicImage value="/images/image4.jpg" title="image4" alt="image4 desc" />

    <f:facet name="thumbnail">
        <p:graphicImage value="/images/image1s.jpg" title="image1s" alt="image1s in thumbnail" />
        <p:graphicImage value="/images/image2s.jpg" title="image2s" alt="image2s in thumbnail" />
        <p:graphicImage value="/images/image3s.jpg" title="image3s" alt="image3s in thumbnail" />
        <p:graphicImage value="/images/image4s.jpg" title="image4s" alt="image4s in thumbnail" />
    </f:facet>
</p:galleria>
```

## Dynamic Collection
Most of the time, you would need to display a dynamic set of contents rather than defining each
content declaratively. For this you can use built-in data iteration feature.

```xhtml
<p:galleria value="#{galleriaBean.images}" var="image" >
    <p:graphicImage value="#{image.path}" title="#{image.title}" alt="#{image.description}" />

    <f:facet name="thumbnail">
        <p:graphicImage value="#{image.thumbnailPath}" title="#{image.thumbnailTitle}" alt="#{image.thumbnailDescription}" />
    </f:facet>
</p:galleria>
```

## Items per page
Number of items per page is defined using the numVisible property.

```xhtml
<p:galleria numVisible="5">
    //images
</p:galleria>
```

## Responsive
For responsive design, numVisible can be defined using the responsiveOptions property that should be an array of objects whose breakpoint defines the max-width to apply the settings.
ResponsiveOption class can be used to create this array.

```xhtml
<p:galleria responsiveOptions="#{view.responsiveOptions}">
    //images
</p:galleria>
```

```java
...
import org.primefaces.model.ResponsiveOption;

public class GalleriaView {
    ...

    private List<ResponsiveOption> responsiveOptions;

    @PostConstruct
    public void init() {
        ...

        responsiveOptions = new ArrayList<>();
        responsiveOptions.add(new ResponsiveOption("1024px", 5));
        responsiveOptions.add(new ResponsiveOption("768px", 3));
        responsiveOptions.add(new ResponsiveOption("560px", 1));
    }

```

## Client Side API
Widget: _PrimeFaces.widget.Galleria_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show() | - | void | Shows content on fullscreen mode. |
| hide() | - | void | Hides content on fullscreen mode. |
| next() | - | void | Moves to the next content that comes after the currently shown content. |
| prev() | - | void | Moves to the previous content that comes before the currently shown content. |

## Skinning
Galleria resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes

| Class | Applies | 
| --- | --- | 
| .ui-galleria | Container element for galleria.
| .ui-galleria-header | Header section.
| .ui-galleria-footer | Footer section.
| .ui-galleria-item-wrapper | Item wrapper element. It contains item container and indicators.
| .ui-galleria-item-container | Container of the item wrapper. It contains navigation buttons, items and caption content.
| .ui-galleria-indicators | Container of the indicators. It contains indicator items.
| .ui-galleria-thumbnail-content | Thumbnail content element.
| .ui-galleria-thumbnail-container | Container of the thumbnail content. It contains navigation buttons and thumbnail items.
| .ui-galleria-caption | Content of the item caption.