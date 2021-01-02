# Dock

Dock component mimics the well known dock interface of Mac OS X.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.dock.html)

## Info

| Name | Value |
| --- | --- |
| Tag | dock
| Component Class | org.primefaces.component.dock.Dock
| Component Type | org.primefaces.component.Dock
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DockRenderer
| Renderer Class | org.primefaces.component.dock.DockRenderer

## Attributes

| Name | Default | Type | Description | 
| --- |---| --- | --- |
| id | null | String | Unique identifier of the component
| widgetVar | null | String | Name of the client side widget.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | MenuModel | MenuModel instance to create menus programmatically
| position | bottom | String | Position of the dock, bottom or top.
| halign | center | String | Horizontal alignment. left, center, or right
| blockScroll | true | Boolean | Whether to block scrolling of the document.
| animate | true | Boolean | Whether to animate the OSX bounce effect when clicking an item.
| animationDuration | 1600 | Integer | How long in milliseconds to animate the bounce effect.
| dir | ltr | String | Defines direction of dock. Valid values are "ltr" (default) and "rtl".
| ~itemWidth~ | 40 | Integer | (DEPRECATED, use CSS below instead) Initial width of items.
| ~maxWidth~ | 50 | Integer | (DEPRECATED, use CSS below instead) Maximum width of items.
| ~proximity~ | 90 | Integer | (DEPRECATED, use CSS below instead) Distance to enlarge.


## Getting started with the Dock
A dock is composed of menu items.

```xhtml
<p:dock>
    <p:menuitem value="Home" icon="/images/dock/home.png" url="#" />
    <p:menuitem value="Music" icon="/images/dock/music.png" url="#" />
    <p:menuitem value="Video" icon="/images/dock/video.png" url="#"/>
    <p:menuitem value="Email" icon="/images/dock/email.png" url="#"/>
    <p:menuitem value="Link" icon="/images/dock/link.png" url="#"/>
    <p:menuitem value="RSS" icon="/images/dock/rss.png" url="#"/>
    <p:menuitem value="History" icon="/images/dock/history.png" url="#"/>
</p:dock>
```
## Position
Dock can be located in two locations, _top_ or _bottom_ (default). For a dock positioned at top set
position to top.

## Dock Effect
When mouse is over the dock items, icons are zoomed in. The configuration of this effect is done
via CSS sizing of the default and hover styles.

```css
/* Default size */
.ui-dock img {
    width: 48px;
}
/* Zoomed size */
.ui-dock-item.active:hover img {
    width: 128px;
}
```

## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Skinning
Following is the list of structural style classes.

| Class | Applies | 
| --- | --- | 
| .ui-dock | Main container.
| .ui-dock-container | Menu item container.
| .ui-dock-item | Each menu item.

As skinning style classes are global, see the main theming section for more information.