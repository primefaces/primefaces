# TieredMenu

TieredMenu is used to display nested submenus with overlays.

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
    <p:divider />
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

## Facets
TieredMenu supports `start` and `end` facets to control UI output or add logos or extras to the menu bar.

```xhtml
<p:commandButton type="button" value="Show" id="btn" />
<p:tieredMenu autoDisplay="false" trigger="btn" my="left top" at="left bottom">
    <f:facet name="start">
       <p:graphicImage name="images/primefaces-logo.svg" library="showcase" />
    </f:facet>
    <p:menuitem label="Logout" action="#{bean.logout}"/>
    <f:facet name="end">
       <p:button value="Quit" icon="pi pi-fw pi-sign-out" styleClass="ui-button-info"/>
    </f:facet>
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

