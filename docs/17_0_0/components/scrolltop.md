# ScrollTop

ScrollTop gets displayed after a certain scroll position and used to navigates to the top of the page quickly.

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
    <p:scrollTop target="parent" />
</div>
```

## Skinning of ScrollTop
ScrollTop resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.p-scrolltop | Container element.
|.p-scrolltop-sticky | Container element when attached to its parent.

The icon is the icon token `--p-scrolltop-icon` of the theme (default `--p-icon-up`, see
[Theming](/core/theming.md?id=icons)), rendered as `<span class="p-icon p-up-icon p-scrolltop-icon">`.
