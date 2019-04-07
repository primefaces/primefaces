# Sticky

Sticky component positions other components as fixed so that these components stay in window
viewport during scrolling.

## Info

| Name | Value |
| --- | --- |
| Tag | sticky
| Component Class | org.primefaces.component.sticky.Sticky
| Component Type | org.primefaces.component.Sticky
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.StickyRenderer
| Renderer Class | org.primefaces.component.sticky.StickyRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
target | null | String | Component to make sticky.
margin | 0 | Integer | Margin to the top of the page during fixed scrolling.

## Getting started with Sticky
Sticky requires a target to keep in viewport on scroll. Here is a sticky toolbar;

```xhtml
<p:toolbar id="tb">
    <p:toolbarGroup align="left">
        <p:commandButton type="button" value="New" icon="ui-icon-document" />
        <p:commandButton type="button" value="Open" icon="ui-icon-folder-open"/>
        <p:separator />
        <p:commandButton type="button" title="Save" icon="ui-icon-disk"/>
        <p:commandButton type="button" title="Delete" icon="ui-icon-trash"/>
        <p:commandButton type="button" title="Print" icon="ui-icon-print"/>
    </p:toolbarGroup>
</p:toolbar>
<p:sticky target="tb" />
```
## Skinning
There are no visual styles of sticky however, _ui-sticky_ class is applied to the target when the position
is fixed. When target is restored to its original location this is removed.

