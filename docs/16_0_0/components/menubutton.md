# MenuButton

MenuButton displays different commands in a popup menu.

## Getting started with the MenuButton
MenuButton consists of one ore more menuitems. Following menubutton example has three
menuitems, first one is used triggers an action with ajax, second one does the similar but without
ajax and third one is used for redirect purposes.

```xhtml
<p:menuButton value="Options">
    <p:menuitem value="Save" action="#{bean.save}" update="comp" />
    <p:menuitem value="Update" action="#{bean.update}" ajax="false" />
    <p:menuitem value="Go Home" url="/home.xhtml" />
</p:menuButton>
```
## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Accessibility

When the menu contains more than a few items, the height of the overlay may grow taller than the available height of the browser's viewport. Even when the height is smaller, part of the overlay may still end up outside the viewport, as by default the overlay is rendered at the current position of the menu button.

If the body of your web page is scrollable, this is not an issue, users can scroll down to see the remaining entries. However, it is also common to use a layout where the body of the web page takes up 100% of the available height and is not scrollable. Then users may end up with no way of accessing some entries of the overlay menu. In this case, please consider using the `collision` and `maxHeight` options to ensure the overlay menu is always within the bounds of the viewport:

```xhtml
<p:menuButton value="Options" collision="flipfit" maxHeight="90vh">
    <!-- many menuitems -->
</p:menuButton>
```

### Button Icon Example

You can use the `buttonIcon` attribute to display an icon in place of the button label. When this attribute is set, the icon class you provide will be shown instead of the value (label) of the button.

```xhtml
<p:menuButton buttonIcon="pi pi-save" title="Options">
    <p:menuitem value="Save" action="#{bean.save}" />
    <p:menuitem value="Update" action="#{bean.update}" />
    <p:menuitem value="Delete" action="#{bean.delete}" />
</p:menuButton>
```

In the above example, the menu button will display the "save" icon (`pi pi-save`) instead of a text label.


## Client Side API
Widget: `PrimeFaces.widget.MenuButton`

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show() | - | void | Displays overlay.
| hide() | - | void | Hides overlay.
| disable() | - | void | Disables the button.
| enable() | - | void | Enables the button.

## Skinning
MenuButton resides in a main container which _style_ and _styleClass_ attributes apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies |
| --- | --- |
| .ui-menu | Container element of menu.
| .ui-menu-list | List container
| .ui-menuitem | Each menu item
| .ui-menuitem-link | Anchor element in a link item
| .ui-menuitem-text | Text element in an item
| .ui-button | Button element
| .ui-button-text | Label of button
