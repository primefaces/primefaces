# Spotlight

Spotlight highlights a certain component on page.

## Info

| Name | Value |
| --- | --- |
| Tag | spotlight
| Component Class | org.primefaces.component.spotlight.Spotlight
| Component Type | org.primefaces.component.Spotlight
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SpotlightRenderer
| Renderer Class | org.primefaces.component.spotlight.SpotlightRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | false | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
target | null | String | Component to highlight.
active | false | Boolean | When true, spotlight is activated initially.
blockScroll | false | Boolean | Whether to block scrolling of the document when sidebar is active.

## Getting started with Spotlight
Spotlight is accessed using client side api. Clicking the button highlights the panel below;

```xhtml
<p:panel id="pnl" header="Panel">
    //content
</p:panel>
<p:commandButton value="Highlight" onclick="PF('spot').show()" />
<p:spotlight target="pnl" widgetVar="spot" />
```
## Client Side API
Widget: _PrimeFaces.widget.Spotlight_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Highlights target.
hide() | - | void | Removes highlight.

## Skinning
Slider resides in a main container which _style_ and _styleClass_ attributes apply. These attributes are
handy to specify the dimensions of the slider. As skinning style classes are global, see the main
theming section for more information. Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-spotlight | Mask element, common to all regions.
.ui-spotlight-top | Top mask element.
.ui-spotlight-bottom | Bottom mask element.
.ui-spotlight-left | Left mask element.
.ui-spotlight-right | Right mask element.