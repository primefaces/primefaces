# MenuButton

MenuButton displays different commands in a popup menu.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.MenuButton.html)

## Info

| Name | Value |
| --- | --- |
| Tag | menuButton
| Component Class | org.primefaces.component.menubutton.MenuButton
| Component Type | org.primefaces.component.MenuButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MenuButtonRenderer
| Renderer Class | org.primefaces.component.menubutton.MenuButtonRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
| ariaLabel | null | String | The aria-label attribute is used to define a string that labels the current element for accessibility.
| autoDisplay | true | Boolean | Defines whether the first level of submenus will be displayed on mouseover or not. When set to false, click event is required to display.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| buttonIcon | null | String | Icon class to display in place of the button label. If specified, the icon will be shown instead of the value (label) of the button.
| buttonStyle | null | String | Style of the button
| buttonStyleClass | null | String | Style class of the button
| collision | flip | String | For the overlay menu that shows up when the menu button is clicked. When the overlay menu overflows the window in some direction, move it to an alternative position. Supported values are flip, fit, flipfit and none. See https://api.jqueryui.com/position/ for more details. Defaults to flip. When you the body of your layout does not scroll, you may also want to set the option maxHeight.
| delay | 0 | int | Delay in milliseconds before displaying the submenu. Default is 0 meaning immediate.
| disabled | false | Boolean | Disables or enables the button.
| disableOnAjax | true | Boolean | If true, the button will be disabled during Ajax requests triggered by its menu items.
| icon | null | String | Icon of the menu button.
| iconPos | left | String | Position of the icon, valid values are left and right.
| id | null | String | Unique identifier of the component.
| maxHeight | null | String | The maximum height of the overlay menu that shows up when the menu button is clicked. May be either a number (such as 200), which is interpreted as a height in pixels. Alternatively, may also be a CSS length such as 90vh or 10em. Useful in case the body of your layout does not scroll, especially in combination with the collision property.
| menuStyleClass | null | String | Style class of the overlay menu element.
| model | null | MenuModel | MenuModel instance to create menus programmatically
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| style | null | String | Style of the main container element
| styleClass | null | String | Style class of the main container element
| title | null | String | Advisory tooltip information.
| toggleEvent | hover | String | Event to toggle the submenus, valid values are "hover" and "click".
| value | null | String | Label of the button
| widgetVar | null | String | Name of the client side widget


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
