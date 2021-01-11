# MegaMenu

MegaMenu is a horizontal navigation component that displays submenus together.

## Info

| Name | Value |
| --- | --- |
| Tag | megaMenu
| Component Class | org.primefaces.component.megamenu.MegaMenu
| Component Type | org.primefaces.component.MegaMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MegaMenuRenderer
| Renderer Class | org.primefaces.component.megamenu.MegaMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Name of the client side widget
model | null | MenuModel | MenuModel instance to create menus programmatically
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
autoDisplay | true | Boolean | Defines whether submenus will be displayed on mouseover or not. When set to false, click event is required to display.
activeIndex | null | Integer | Index of the active root menu to display as highlighted. By default no root is highlighted.
orientation | horizontal | String | Defines the orientation of the root menuitems, valid values are "horizontal" and "vertical".

## Getting Started with MegaMenu
Layout of MegaMenu is grid based and root items require columns as children.

```xhtml
<p:megaMenu>
    <p:submenu label="TV" icon="ui-icon-check">
        <p:column>
            <p:submenu label="TV.1">
                <p:menuitem value="TV.1.1" url="#" />
                <p:menuitem value="TV.1.2" url="#" />
            </p:submenu>
            <p:submenu label="TV.2">
                <p:menuitem value="TV.2.1" url="#" />
                <p:menuitem value="TV.2.2" url="#" />
                <p:menuitem value="TV.2.3" url="#" />
            </p:submenu>
            <p:submenu label="TV.3">
                <p:menuitem value="TV.3.1" url="#" />
                <p:menuitem value="TV.3.2" url="#" />
            </p:submenu>
        </p:column>
        <p:column>
            <p:submenu label="TV.4">
                <p:menuitem value="TV.4.1" url="#" />
                <p:menuitem value="TV.4.2" url="#" />
            </p:submenu>
            <p:submenu label="TV.5">
                <p:menuitem value="TV.5.1" url="#" />
                <p:menuitem value="TV.5.2" url="#" />
                <p:menuitem value="TV.5.3" url="#" />
            </p:submenu>
            <p:submenu label="TV.6">
                <p:menuitem value="TV.6.1" url="#" />
                <p:menuitem value="TV.6.2" url="#" />
                <p:menuitem value="TV.6.3" url="#" />
            </p:submenu>
        </p:column>
    </p:submenu>
    //more root items
</p:megaMenu>
```

## Custom Content
Any content can be placed inside columns.

```xhtml
<p:column>
    <strong>Sopranos</strong>
    <p:graphicImage value="/images/sopranos/sopranos1.jpg" width="200"/>
</p:column>
```
## Root Menuitem
MegaMenu supports menuitem as root menu options as well. Following example allows a root
menubar item to execute an action to logout the user.

```xhtml
<p:megaMenu>
    //submenus
    <p:menuitem label="Logout" action="#{bean.logout}"/>
</p:megaMenu>
```
## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Skinning
MegaMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-megamenu | Container element of menubar.
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

