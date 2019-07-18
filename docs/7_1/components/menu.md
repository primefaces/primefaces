# Menu

Menu is a navigation component with submenus and menuitems.

## Info

| Name | Value |
| --- | --- |
| Tag | menu
| Component Class | org.primefaces.component.menu.Menu
| Component Type | org.primefaces.component.Menu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MenuRenderer
| Renderer Class | org.primefaces.component.menu.MenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Name of the client side widget.
model | null | MenuModel | A menu model instance to create menu programmatically.
trigger | null | String | Target component to attach the overlay menu.
my | null | String | Corner of menu to align with trigger element.
at | null | String | Corner of trigger to align with menu element.
overlay | false | Boolean | Defines positioning type of menu, either static or overlay.
style | null | String | Inline style of the main container element.
styleClass | null | String | Style class of the main container element.
triggerEvent | click | String | Event to show the dynamic positioned menu.
tabindex | 0 | String | Position of the element in the tabbing order. Default is 0.
toggleable | false | Boolean | Defines whether clicking the header of a submenu toggles the visibility of children menuitems.

## Getting started with the Menu
A menu is composed of submenus and menuitems.

```xhtml
<p:menu>
    <p:menuitem value="Gmail" url="http://www.google.com" />
    <p:menuitem value="Hotmail" url="http://www.hotmail.com" />
    <p:menuitem value="Yahoo Mail" url="http://mail.yahoo.com" />
    <p:menuitem value="Youtube" url="http://www.youtube.com" />
    <p:menuitem value="Break" url="http://www.break.com" />
    <p:menuitem value="Metacafe" url="http://www.metacafe.com" />
    <p:menuitem value="Facebook" url="http://www.facebook.com" />
    <p:menuitem value="MySpace" url="http://www.myspace.com" />
</p:menu>
```

Submenus are used to group menuitems;

```xhtml
<p:menu>
    <p:submenu label="Mail">
        <p:menuitem value="Gmail" url="http://www.google.com" />
        <p:menuitem value="Hotmail" url="http://www.hotmail.com" />
        <p:menuitem value="Yahoo Mail" url="http://mail.yahoo.com" />
    </p:submenu>
    <p:submenu label="Videos">
        <p:menuitem value="Youtube" url="http://www.youtube.com" />
        <p:menuitem value="Break" url="http://www.break.com" />
        <p:menuitem value="Metacafe" url="http://www.metacafe.com" />
    </p:submenu>
    <p:submenu label="Social Networks">
        <p:menuitem value="Facebook" url="http://www.facebook.com" />
        <p:menuitem value="MySpace" url="http://www.myspace.com" />
    </p:submenu>
</p:menu>
```
## Overlay Menu
Menu can be positioned on a page in two ways; "static" and "dynamic". By default it’s static
meaning the menu is in normal page flow. In contrast dynamic menus is not on the normal flow of
the page allowing them to overlay other elements.

A dynamic menu is created by setting _overlay_ option to true and defining a trigger to show the
menu. Location of menu on page will be relative to the trigger and defined by my and at options
that take combination of four values;

- left
- right
- bottom
- top


For example, clicking the button below will display the menu whose top left corner is aligned with
bottom left corner of button.

```xhtml
<p:menu overlay="true" trigger="btn" my="left top" at="bottom left">
    ...submenus and menuitems
</p:menu>
<p:commandButton id="btn" value="Show Menu" type="button"/>
```
## Ajax and Non-Ajax Actions
As menu uses menuitems, it is easy to invoke actions with or without ajax as well as navigation. See
menuitem documentation for more information about the capabilities.

```xhtml
<p:menu>
    <p:submenu label="Options">
        <p:menuitem value="Save" action="#{bean.save}" update="comp"/>
        <p:menuitem value="Update" action="#{bean.update}" ajax="false"/>
        <p:menuitem value="Navigate" url="http://www.primefaces.org"/>
    </p:submenu>
</p:menu>
```
## Dynamic Menus
Menus can be created programmatically as well, this is more flexible compared to the declarative
approach. Menu metadata is defined using an _org.primefaces.model.MenuModel_ instance,
PrimeFaces provides the built-in _org.primefaces.model.DefaultMenuModel_ implementation.

For further customization you can also create and bind your own MenuModel implementation. (e.g.
One with JPA @Entity annotation to able able to persist to a database).

```xhtml
<p:menu model="#{menuBean.model}" />
```

```java
public class MenuBean {
    private MenuModel model;

    public MenuBean() {
        model = new DefaultMenuModel();
        //First submenu
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Dynamic Submenu");
        DefaultMenuItem item = new DefaultMenuItem("External");
        item.setUrl("http://www.primefaces.org");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);
        model.addElement(firstSubmenu);
        //Second submenu
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Dynamic Actions");
        item = new DefaultMenuItem("Save");
        item.setIcon("ui-icon-disk");
        item.setCommand("#{menuBean.save}");
        item.setUpdate("messages");
        secondSubmenu.addElement(item);
        item = new DefaultMenuItem("Delete");
        item.setIcon("ui-icon-close");
        item.setCommand("#{menuBean.delete}");
        item.setAjax(false);
        secondSubmenu.addElement(item);
        item = new DefaultMenuItem("Redirect");
        item.setIcon("ui-icon-search");
        item.setCommand("#{menuBean.redirect}");
        secondSubmenu.addElement(item);
        model.addElement(secondSubmenu);
    }
    public MenuModel getModel() { 
        return model;
    }
}
```
For all UI component counterpart such as p:menuitem, p:submenu, p:separator a corresponding
interface with a default implementation exists in MenuModel API. Regarding actions, if you need to
pass parameters in ajax or non-ajax commands, use setParam(key, value) method.

**Note**: MenuModel API is supported by all menu components that have model attribute.

## Toggleable
Enabling toggleable option makes the header of submenus clickable to expand and collapse their
content.

```xhtml
<p:menu toggleable="true">
```
## Skinning
Menu resides in a main container element which _style_ and _styleClass_ attributes apply. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-menu | Container element of menu
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item
.ui-menu-sliding | Container of ipod like sliding menu

As skinning style classes are global, see the main theming section for more information.

