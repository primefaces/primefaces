# MenuButton

MenuButton displays different commands in a popup menu.

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
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
value | null | String | Label of the button
style | null | String | Style of the main container element
styleClass | null | String | Style class of the main container element
widgetVar | null | String | Name of the client side widget
model | null | MenuModel | MenuModel instance to create menus programmatically
disabled | false | Boolean | Disables or enables the button.
icon | null | String | Icon of the menu button.
iconPos | left | String | Position of the icon, valid values are left and right.
title | null | String | Advisory tooltip information.
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
menuStyleClass | null | String | Style class of the overlay menu element.
ariaLabel | null | String | The aria-label attribute is used to define a string that labels the current element for accessibility.

## Getting started with the MenuButton
MenuButton consists of one ore more menuitems. Following menubutton example has three
menuitems, first one is used triggers an action with ajax, second one does the similar but without
ajax and third one is used for redirect purposes.

```xhtml
<p:menuButton value="Options">
    <p:menuitem value="Save" action="#{bean.save}" update="comp" />
    <p:menuitem value="Update" action="#{bean.update}" ajax="false" />
    <p:menuitem value="Go Home" url="/home.jsf" />
</p:menuButton>
```
## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Skinning
MenuButton resides in a main container which _style_ and _styleClass_ attributes apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-menu | Container element of menu.
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item
.ui-button | Button element
.ui-button-text | Label of button
