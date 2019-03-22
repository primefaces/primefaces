# Sidebar

Sidebar is a panel component displayed as an overlay at the edges of the screen.

## Info

| Name | Value |
| --- | --- |
| Tag | sidebar
| Component Class | org.primefaces.component.sidebar.Sidebar
| Component Type | org.primefaces.component.Sidebar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SidebarMenuRenderer
| Renderer Class | org.primefaces.component.sidebar.SidebarMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
visible | false | Boolean | Specifies the visibility of the sidebar.
position | left | String | Position of the sidebar.
fullScreen | false | Boolean | Whether to enable the whole screen.
blockScroll | false | Boolean | Whether to block scrolling of the document when sidebar is active.
baseZIndex | 0 | Integer | Base zIndex value to use in layering.
appendTo | null | String | Appends the sidebar to the given search expression.
onShow | null | String | Client side callback to execute when sidebar is displayed.
onHide | null | String | Client side callback to execute when sidebar is hidden.

## Getting started with the Sidebar
Sidebar is used a container to other components and display is toggle using the show() and hide()
methods of the widget.

```xhtml
<p:sidebar widgetVar="sb">
    <h1>Sidebar</h1>
    <p:commandButton value="Cancel" onclick="PF('sb').hide()"/>
</p:sidebar>
<p:commandButton type="button" onclick="PF('sb').show()" />
```
## Position
Four positions are available to display the sidebar at the edges of the screen. Valid values are; _left_ ,
_right_ , _top_ and _bottom_.

```xhtml
<p:sidebar widgetVar="sb" position="top">
    <h1>Sidebar</h1>
    <p:commandButton value="Cancel" onclick="PF('sb').hide()"/>
</p:sidebar>
```

## Full Screen
Instead of aligning the sidebar to an edge of the screen, enable fullScreen property to make it cover
the whole page.

```xhtml
<p:sidebar widgetVar="sb" fullScreen="true">
    <h1>Sidebar</h1>
    <p:commandButton value="Cancel" onclick="PF('sb').hide()"/>
</p:sidebar>
```
## Base ZIndex
Dynamic layering in PrimeFaces starts from the 1000 as the zIndex, to display sidebar above other
content specify baseZIndex property. In example below, initial zIndex of the sidebar will be at least
1500 and will be incremented after each display.

```xhtml
<p:sidebar widgetVar="sb" baseZIndex="500">
    <h1>Sidebar</h1>
    <p:commandButton value="Cancel" onclick="PF('sb').hide()"/>
</p:sidebar>
```
## Client Side API
Widget: _PrimeFaces.widget.Sidebar_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows overlay menu.
hide() | - | void | Hides overlay menu.

## Skinning
SlideMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-sidebar | Container element.
.ui-sidebar-left | Container element of a sidebar whose position is left.
.ui-sidebar-right | Container element of a sidebar whose position is right.
.ui-sidebar-top | Container element of a sidebar whose position is top.
.ui-sidebar-bottom | Container element of a sidebar whose position is bottom.
.ui-sidebar-full |Container element of a full screen sidebar.
.ui-sidebar-close | Close icon.

As skinning style classes are global, see the main theming section for more information.

