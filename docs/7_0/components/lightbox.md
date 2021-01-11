# LightBox

Lightbox is a powerful overlay that can display images, multimedia content, custom content and
external urls.

## Info

| Name | Value |
| --- | --- |
| Tag | lightBox
| Component Class | org.primefaces.component lightbox.LightBox
| Component Type | org.primefaces.component.LightBox
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.LightBoxRenderer
| Renderer Class | org.primefaces.component.lightbox.LightBoxRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
style | null | String | Style of the container element not the overlay element.
styleClass | null | String | Style class of the container element not the overlay element.
width | null | String | Width of the overlay in iframe mode.
height | null | String | Height of the overlay in iframe mode.
iframe | false | Boolean | Specifies an iframe to display an external url in overlay.
iframeTitle | null | String | Title of the iframe element.
visible | false | Boolean | Displays lightbox without requiring any user interaction by default.
onHide | null | String | Client side callback to execute when lightbox is displayed.
onShow | null | String | Client side callback to execute when lightbox is hidden.
blockScroll | false | Boolean | Whether to block scrolling of the document when sidebar is active.

## Images
The images displayed in the lightBox need to be nested as child outputLink components. Following
lightBox is displayed when any of the links are clicked.

```xhtml
<p:lightBox>
    <h:outputLink value="sopranos/sopranos1.jpg" title="Sopranos 1">
        <h:graphicImage value="sopranos/sopranos1_small.jpg/>
    </h:outputLink>
    <h:outputLink value="sopranos/sopranos2.jpg" title="Sopranos 2">
        <h:graphicImage value="sopranos/sopranos2_small.jpg/>
    </h:outputLink>
    <h:outputLink value="sopranos/sopranos3.jpg" title="Sopranos 3">
        <h:graphicImage value="sopranos/sopranos3_small.jpg/>
    </h:outputLink>
    //more
</p:lightBox>
```
## IFrame Mode
LightBox also has the ability to display iframes inside the page overlay, following lightbox displays
the PrimeFaces homepage when the link inside is clicked.

```xhtml
<p:lightBox iframe="true">
    <h:outputLink value="http://www.primefaces.org" title="PrimeFaces HomePage">
        <h:outputText value="PrimeFaces HomePage"/>
    </h:outputLink>
</p:lightBox>
```
Clicking the outputLink will display PrimeFaces homepage within an iframe.

## Inline Mode
Inline mode acts like a modal dialog, you can display other JSF content on the page using the
lightbox overlay. Simply place your overlay content in the "inline" facet. Clicking the link in the
example below will display the panelGrid contents in overlay.


```xhtml
<p:lightBox>
    <h:outputLink value="#" title="Leo Messi" >
        <h:outputText value="The Messiah"/>
    </h:outputLink>
    <f:facet name="inline">
    //content here
    </f:facet>
</p:lightBox>
```
Lightbox inline mode doesn’t support advanced content like complex widgets. Use a dialog instead
for advanced cases involving custom content.

## Client Side API
Widget: _PrimeFaces.widget.LightBox_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Displays lightbox.
hide() | - | void | Hides lightbox.
showURL(opt) | opt | void | Displays a URL in a iframe. opt parameter has three variables. width and height for iframe dimensions and src for the page url.

## Skinning
Lightbox resides in a main container element which _style_ and _styleClass_ options apply. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-lightbox | Main container element.
.ui-lightbox-content-wrapper | Content wrapper element.
.ui-lightbox-content | Content container.
.ui-lightbox-nav-right | Next image navigator.
.ui-lightbox-nav-left | Previous image navigator.
.ui-lightbox-loading | Loading image.
.ui-lightbox-caption | Caption element.
