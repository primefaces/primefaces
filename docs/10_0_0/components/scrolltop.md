# ScrollTop

ScrollTop gets displayed after a certain scroll position and used to navigates to the top of the page quickly.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.scrolltop.html)

## Info

| Name | Value |
| --- | --- |
| Tag | scrollTop
| Component Class | org.primefaces.component.scrolltop.ScrollTop
| Component Type | org.primefaces.component.ScrollTop
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.ScrollTopRenderer
| Renderer Class | org.primefaces.component.scrolltop.ScrollTopRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| target | window | String | Target of the ScrollTop, valid values are "window" and "parent".
| threshold | 400 | Integer | Defines the threshold value of the vertical scroll position of the target to toggle the visibility.
| icon | pi pi-chevron-up | String | Icon to display.
| behavior | smooth | String | Defines the scrolling behavior, "smooth" adds an animation and "auto" scrolls with a jump.
| widgetVar | null | String | Name of the client side widget.
| style | null | String | Style of the ScrollTop.
| styleClass | null | String | StyleClass of the ScrollTop.

## Getting Started
Without any configuration, ScrollTop listens window scroll.

```xhtml
<p:scrollTop />
```

## Threshold
When the vertical scroll position reaches a certain value, ScrollTop gets displayed. This value is defined with the 
```threshold``` property that defaults to 400.

```xhtml
<p:scrollTop threshold="200"/>
```

## Target Element
ScrollTop can also be assigned to its parent element by setting ```target``` as "parent".

```xhtml
<div style="height: 400px; overflow: auto">
    Content that overflows to container
    <p:scrollTop />
</div>
```

## Skinning of ScrollTop
ScrollTop resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-scrolltop | Container element.
|.ui-scrolltop-sticky | Container element when attached to its parent.
