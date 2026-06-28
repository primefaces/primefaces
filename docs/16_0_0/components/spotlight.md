# Spotlight

Spotlight highlights a certain component on page.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Spotlight.html)

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