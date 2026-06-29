# TieredMenu

TieredMenu is used to display nested submenus with overlays.

## Info

| Name | Value |
| --- | --- |
| Tag | tieredMenu
| Component Class | org.primefaces.component.tieredmenu.TieredMenu
| Component Type | org.primefaces.component.TieredMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TieredMenuRenderer
| Renderer Class | org.primefaces.component.tieredmenu.TieredMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
model | null | MenuModel | MenuModel instance for programmatic menu.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
autoDisplay | true | Boolean | Defines whether the first level of submenus will be displayed on mouseover or not. When set to false, click event is required to display.
trigger | null | String | Id of the component whose triggerEvent will show the dynamic positioned menu.
my | null | String | Corner of menu to align with trigger element.
at | null | String | Corner of trigger to align with menu element.
overlay | false | Boolean | Defines positioning, when enabled menu is displayed with absolute position relative to the trigger. Default is false, meaning static positioning.
triggerEvent | click | String | Event name of trigger that will show the dynamic positioned menu.
toggleEvent | hover | String | Event to toggle the submenus, valid values are "hover" and "click".

## Getting started with the TieredMenu
TieredMenu consists of submenus and menuitems, submenus can be nested and each nested
submenu will be displayed in an overlay.

```xhtml
<p:tieredMenu>
    <p:submenu label="Ajax Menuitems" icon="ui-icon-refresh">
        <p:menuitem value="Save" action="#{buttonBean.save}" update="messages" icon="ui-icon-disk" />
        <p:menuitem value="Update" action="#{buttonBean.update}" update="messages" icon="ui-icon-arrowrefresh-1-w" />
    </p:submenu>
    <p:submenu label="Non-Ajax Menuitem" icon="ui-icon-newwin">
        <p:menuitem value="Delete" action="#{buttonBean.delete}" update="messages" ajax="false" icon="ui-icon-close"/>
    </p:submenu>
    <p:separator />
    <p:submenu label="Navigations" icon="ui-icon-extlink">
        <p:submenu label="Prime Links">
            <p:menuitem value="Prime" url="http://www.prime.com.tr" />
            <p:menuitem value="PrimeFaces" url="http://www.primefaces.org" />
        </p:submenu>
        <p:menuitem value="Mobile" url="/mobile" />
    </p:submenu>
</p:tieredMenu>
```
## AutoDisplay
By default, submenus are displayed when mouse is over root menuitems, set autoDisplay to false to
require a click on root menuitems to enable autoDisplay mode.

```xhtml
<p:tieredMenu autoDisplay="false">
    //content
</p:tieredMenu>
```

## Overlay
TieredMenu can be positioned relative to a trigger component, following sample attaches a
tieredMenu to the button so that whenever the button is clicked tieredMenu will be displayed in an
overlay itself.

```xhtml
<p:commandButton type="button" value="Show" id="btn" />
<p:tieredMenu autoDisplay="false" trigger="btn" my="left top" at="left bottom">
    //content
</p:tieredMenu>
```
## Client Side API
Widget: _PrimeFaces.widget.TieredMenu_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows overlay menu.
hide() | - | void | Hides overlay menu.
align() | - | void | Aligns overlay menu with trigger.

## Skinning
TieredMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-menu .ui-tieredmenu | Container element of menu.
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

