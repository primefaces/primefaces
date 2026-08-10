# Dock

Dock component mimics the well known dock interface of Mac OS X.

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