# Skeleton

Skeleton is a placeholder to display instead of the actual content.

## Info

| Name | Value |
| --- | --- |
| Tag | skeleton
| Component Class | org.primefaces.component.skeleton.Skeleton
| Component Type | org.primefaces.component.Skeleton
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.SkeletonRenderer
| Renderer Class | org.primefaces.component.skeleton.SkeletonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| shape | rectangle | String | Shape of the element, options are "rectangle" and "circle".
| size | null | String | Size of the Circle or Square.
| width | 100 | String | 	Width of the element.
| height | 1rem | String | Height of the element.
| borderRadius | null | String | Border radius of the element, defaults to value from theme.
| animation | wave | String | Type of the animation, valid options are "wave" and "none".
| style | null | String | Style of the Skeleton.
| styleClass | null | String | StyleClass of the Skeleton.

## Getting Started
Skeleton displays a rectangle in its simplest form.

```xhtml
<p:skeleton />
```

## Circle
The other option is the circle by setting ```shape``` property as "circle".

```xhtml
<p:skeleton shape="circle"/>
```

## Size
In order to customize the size, use ```width``` and ```height``` properties for rectangles and ```size``` for Circle 
and Square shapes.

```xhtml
<p:skeleton width="100%" height="2rem" />
<p:skeleton shape="circle" size="50px" />
```

## Border Radius
The default border radius of a rectangle is specified by the theme and can be overriden using the ```borderRadius``` 
property.

```xhtml
<p:skeleton borderRadius="16px" />
```

## Animation
Animation can be turned of by setting ```animation``` to "none".

```xhtml
<p:skeleton animation="none" />
```

## Skinning of Skeleton
Skeleton resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-skeleton | Container element.
|.ui-skeleton-circle | Container element of a determinate progressbar.
|.ui-skeleton-animation-none | Container element of an indeterminate progressbar.
