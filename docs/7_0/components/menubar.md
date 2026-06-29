# Menubar

Menubar is a horizontal navigation component.

## Info

| Name | Value |
| --- | --- |
| Tag | menubar
| Component Class | org.primefaces.component.menubar.Menubar
| Component Type | org.primefaces.component.Menubar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MenubarRenderer
| Renderer Class | org.primefaces.component.menubar.MenubarRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Name of the client side widget
model | null | MenuModel | MenuModel instance to create menus programmatically
style | null | String | Inline style of menubar
styleClass | null | String | Style class of menubar
autoDisplay | false | Boolean | Defines whether the first level of submenus will be displayed on mouseover or not. When set to false, click event is required to display.
tabindex | 0 | String | Position of the element in the tabbing order.
toggleEvent | hover | String | Event to toggle the submenus, valid values are "hover" and "click".

## Getting started with Menubar
Submenus and menuitems as child components are required to compose the menubar.

```xhtml
<p:menubar>
    <p:submenu label="Mail">
        <p:menuitem value="Gmail" url="http://www.google.com" />
        <p:menuitem value="Hotmail" url="http://www.hotmail.com" />
        <p:menuitem value="Yahoo Mail" url="http://mail.yahoo.com" />
    </p:submenu>
    <p:submenu label="Videos">
        <p:menuitem value="Youtube" url="http://www.youtube.com" />
        <p:menuitem value="Break" url="http://www.break.com" />
    </p:submenu>
</p:menubar>
```
## Nested Menus
To create a menubar with a higher depth, nest submenus in parent submenus.

```xhtml
<p:menubar>
    <p:submenu label="File">
        <p:submenu label="New">
            <p:menuitem value="Project" url="#"/>
            <p:menuitem value="Other" url="#"/>
        </p:submenu>
        <p:menuitem value="Open" url="#"></p:menuitem>
        <p:menuitem value="Quit" url="#"></p:menuitem>
    </p:submenu>
    <p:submenu label="Edit">
        <p:menuitem value="Undo" url="#"></p:menuitem>
        <p:menuitem value="Redo" url="#"></p:menuitem>
    </p:submenu>
    <p:submenu label="Help">
        <p:menuitem label="Contents" url="#" />
        <p:submenu label="Search">
            <p:submenu label="Text">
                <p:menuitem value="Workspace" url="#" />
            </p:submenu>
            <p:menuitem value="File" url="#" />
        </p:submenu>
    </p:submenu>
</p:menubar>
```

## Root Menuitem
Menubar supports menuitem as root menu options as well;

```xhtml
<p:menubar>
    <p:menuitem label="Logout" action="#{bean.logout}"/>
</p:menubar>
```
## Ajax and Non-Ajax Actions
As menu uses menuitems, it is easy to invoke actions with or without ajax as well as navigation. See
menuitem documentation for more information about the capabilities.

```xhtml
<p:menubar>
    <p:submenu label="Options">
        <p:menuitem value="Save" action="#{bean.save}" update="comp"/>
        <p:menuitem value="Update" action="#{bean.update}" ajax="false"/>
        <p:menuitem value="Navigate" url="http://www.primefaces.org"/>
    </p:submenu>
</p:menubar>
```
## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Skinning
Menubar resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-menubar | Container element of menubar.
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

