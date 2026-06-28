# Menu

Menu is a navigation component with submenus and menuitems.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Menu.html)

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
@Named
@RequestScoped
public class MenuBean {
    private MenuModel model;

    public MenuBean() {
        model = new DefaultMenuModel();

        //First submenu
        DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Submenu")
                .build();

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("External")
                .url("http://www.primefaces.org")
                .icon("pi pi-home")
                .build();
        firstSubmenu.getElements().add(item);

        model.getElements().add(firstSubmenu);

        //Second submenu
        DefaultSubMenu secondSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Actions")
                .build();

        item = DefaultMenuItem.builder()
                .value("Save")
                .icon("pi pi-save")
                .command("#{menuBean.save}")
                .update("messages")
                .build();
        secondSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Delete")
                .icon("pi pi-times")
                .command("#{menuBean.delete}")
                .ajax(false)
                .build();
        secondSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Redirect")
                .icon("pi pi-search")
                .command("#{menuBean.redirect}")
                .build();
        secondSubmenu.getElements().add(item);

        model.getElements().add(secondSubmenu);
    }

    public MenuModel getModel() { 
        return model;
    }
}
```
For all UI component counterpart such as p:menuitem, p:submenu, p:divider a corresponding
interface with a default implementation exists in MenuModel API. Regarding actions, if you need to
pass parameters in ajax or non-ajax commands, use setParam(key, value) method.

**Note**: MenuModel API is supported by all menu components that have model attribute.

## Toggleable
Enabling toggleable option makes the header of submenus clickable to expand and collapse their
content.

```xhtml
<p:menu toggleable="true">
```

## Accessibility

When the menu contains more than a few items and the menu is shown in an overlay, the height of the overlay may grow taller than the available height of the browser's viewport. Even when the height is smaller, part of the overlay may still end up outside the viewport, as by default the overlay is rendered at the current position of the menu button. 

If the body of your web page is scrollable, this is not an issue, users can scroll down to see the remaining entries. However, it is also common to use a layout where the body of the web page takes up 100% of the available height and is not scrollable. Then users may end up with no way of accessing some entries of the overlay menu. In this case, please consider using the `collision` and `maxHeight` options to ensure the overlay menu is always within the bounds of the viewport:

```xml
<p:menu overlay="true" collision="flipfit" maxHeight="90vh"
    trigger="btn" my="left top" at="bottom left">
    <!-- many submenus and menuitems -->
</p:menu>
<p:commandButton id="btn" value="Show Menu" type="button"/>
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

