# SlideMenu

SlideMenu is used to display nested submenus with sliding animation.

## Getting started with the SlideMenu
SlideMenu consists of submenus and menuitems, submenus can be nested and each nested submenu
will be displayed with a slide animation.

```xhtml
<p:slideMenu>
    <p:submenu label="Ajax Menuitems" icon="pi pi-refresh">
        <p:menuitem value="Save" action="#{buttonBean.save}" update="messages" icon="pi pi-save" />
        <p:menuitem value="Update" action="#{buttonBean.update}" update="messages" icon="pi pi-refresh" />
    </p:submenu>
    <p:submenu label="Non-Ajax Menuitem" icon="pi pi-window-maximize">
        <p:menuitem value="Delete" action="#{buttonBean.delete}" update="messages" ajax="false" icon="pi pi-times"/>
    </p:submenu>
    <p:divider />
    <p:submenu label="Navigations" icon="pi pi-external-link">
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
.p-menu .p-slidemenu | Container element of menu.
.p-slidemenu-wrapper | Wrapper element for content.
.p-slidemenu-content | Content container.
.p-slidemenu-backward | Back navigator.
.p-menu-list | List container
.p-menuitem | Each menu item
.p-menuitem-link | Anchor element in a link item
.p-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

