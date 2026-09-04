# MenuItem

MenuItem is used by various menu components.

## Getting started with MenuItem
MenuItem is a generic component used by the following components.

- Menu
- MenuBar
- MegaMenu
- Breadcrumb
- Dock
- Stack
- MenuButton
- SplitButton
- PanelMenu
- TabMenu
- SlideMenu
- TieredMenu

Note that some attributes of menuitem might not be supported by these menu components. Refer to
the specific component documentation for more information.

## Navigation vs Action
Menuitem has two use cases, directly navigating to a URL with GET or doing a POST to execute an
action. This is decided by URL or outcome attributes, if either one is present MenuItem does a GET
request, if not parent form is posted with or without AJAX decided by _ajax_ attribute.

## Custom Rendering
For special use cases you might want to completely control the rendering of a MenuItem. You can
use the `<f:facet name="custom">` to control the rendering like this.

```xhtml
<p:menuitem>
   <f:facet name="custom">
       <h:link outcome="NewPage" value="My Custom Link"/>
    </f:facet>
</p:menuitem>
```

## Icons
There are two ways to specify an icon of a MenuItem, you can either use bundled icons within
PrimeFaces or provide your own via CSS.

#### ThemeRoller Icons ####

```xhtml
<p:menuitem icon="ui-icon-disk" ... />
```
#### Custom Icons ####

```xhtml
<p:menuitem icon="barca" ... />
```
```css
.barca {
    background: url(barca_logo.png) no-repeat;
    height:16px;
    width:16px;
}
```
