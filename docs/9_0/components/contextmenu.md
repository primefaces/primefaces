# ContextMenu

ContextMenu provides an overlay menu displayed on mouse right-click event.

## Info

| Name | Value |
| --- | --- |
| Tag | contextMenu
| Component Class | org.primefaces.component.contextmenu.ContextMenu
| Component Type | org.primefaces.component.ContextMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ContextMenuRenderer
| Renderer Class | org.primefaces.component.contextmenu.ContextMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| for | null | String | Id of the component to attach to
| style | null | String | Style of the main container element
| styleClass | null | String | Style class of the main container element
| model | null | MenuModel | Menu model instance to create menu programmatically.
| nodeType | null | String | Specific type of tree nodes to attach to.
| event | null | String | Event to bind contextMenu display, default is contextmenu aka right click.
| beforeShow | null | String | Client side callback to execute before showing.
| selectionMode | multiple | String | Defines the selection behavior, e.g "single" or "multiple".
| targetFilter | null | String | Selector to filter the elements to attach the menu.

## Getting started with ContextMenu
ContextMenu is created with submenus and menuitems. Optional for attribute defines which
component the contextMenu is attached to. When for is not defined, contextMenu is attached to the
page meaning, right-click on anywhere on page will display the menu.

```xhtml
<p:contextMenu>
    <p:menuitem value="Save" action="#{bean.save}" update="msg"/>
    <p:menuitem value="Delete" action="#{bean.delete}" ajax="false"/>
    <p:menuitem value="Go Home" url=" http://www.primefaces.org " target="_blank"/>
</p:contextMenu
```
ContextMenu example above is attached to the whole page and consists of three different
menuitems with different use cases. First menuitem triggers an ajax action, second one triggers a
non-ajax action and third one is used for navigation.

## Attachment
ContextMenu can be attached to any JSF component, this means right clicking on the attached
component will display the contextMenu. Following example demonstrates an integration between
contextMenu and imageSwitcher, contextMenu here is used to navigate between images.

```xhtml
<p:imageSwitch id="images" widgetVar="gallery" slideshowAuto="false">
    <p:graphicImage value="/images/nature1.jpg" />
    <p:graphicImage value="/images/nature2.jpg" />
    <p:graphicImage value="/images/nature3.jpg" />
    <p:graphicImage value="/images/nature4.jpg" />
</p:imageSwitch>
<p:contextMenu for="images">
    <p:menuitem value="Previous" url="#" onclick="PF('gallery').previous()" />
    <p:menuitem value="Next" url="#" onclick="PF('gallery').next()" />
</p:contextMenu>
```
Now right-clicking anywhere on an image will display the contextMenu like;


## Data Components
Data components like datatable, tree and treeTable has special integration with context menu, see
the documentation of these component for more information.

## Dynamic Menus
ContextMenus can be created programmatically as well, see the dynamic menus part in menu
component section for more information and an example.

## Skinning
ContextMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-contextmenu | Container element of menu
| .ui-menu-list | List container
| .ui-menuitem | Each menu item
| .ui-menuitem-link | Anchor element in a link item
| .ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.
