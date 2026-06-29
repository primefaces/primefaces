# SlideMenu

SlideMenu is used to display nested submenus with sliding animation.

## Info

| Name | Value |
| --- | --- |
| Tag | slideMenu
| Component Class | org.primefaces.component.slidemenu.SlideMenu
| Component Type | org.primefaces.component.SlideMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SlideMenuRenderer
| Renderer Class | org.primefaces.component.slidemenu.SlideMenuRenderer

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
backLabel | Back | String | Text for back link.
trigger | null | String | Id of the component whose triggerEvent will show the dynamic positioned menu.
my | null | String | Corner of menu to align with trigger element.
at | null | String | Corner of trigger to align with menu element.
overlay | false | Boolean | Defines positioning, when enabled menu is displayed with absolute position relative to the trigger. Default is false, meaning static positioning.
triggerEvent | click | String | Event name of trigger that will show the dynamic positioned menu.

## Getting started with the SlideMenu
SlideMenu consists of submenus and menuitems, submenus can be nested and each nested submenu
will be displayed with a slide animation.

```xhtml
<p:slideMenu>
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
</p:slideMenu>
```
## Overlay
SlideMenu can be positioned relative to a trigger component, following sample attaches a
slideMenu to the button so that whenever the button is clicked menu will be displayed in an overlay
itself.

```xhtml
<p:commandButton type="button" value="Show" id="btn" />
<p:slideMenu trigger="btn" my="left top" at="left bottom">
    //content
</p:slideMenu>
```

## Client Side API
Widget: _PrimeFaces.widget.SlideMenu_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows overlay menu.
hide() | - | void | Hides overlay menu.
align() | - | void | Aligns overlay menu with trigger.

## Skinning
SlideMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-menu .ui-slidemenu | Container element of menu.
.ui-slidemenu-wrapper | Wrapper element for content.
.ui-slidemenu-content | Content container.
.ui-slidemenu-backward | Back navigator.
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

