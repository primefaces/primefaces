# PanelMenu

PanelMenu is a hybrid component of accordionPanel and tree components.

## Getting started with PanelMenu
PanelMenu consists of submenus and menuitems. First level of submenus are rendered as accordion
panels and descendant submenus are rendered as tree nodes. Just like in any other menu component,
menuitems can be utilized to do ajax requests, non-ajax requests and simple GET navigations.

```xhtml
<p:panelMenu style="width:200px">
    <p:submenu label="Ajax Menuitems">
        <p:menuitem value="Save" action="#{buttonBean.save}" />
        <p:menuitem value="Update" action="#{buttonBean.update}" />
    </p:submenu>
    <p:submenu label="Non-Ajax Menuitem">
        <p:menuitem value="Delete" action="#{buttonBean.delete}" ajax="false"/>
    </p:submenu>
    <p:submenu label="Navigations" >
        <p:submenu label="Links" icon="ui-icon-extlink">
            <p:submenu label="PrimeFaces" icon="ui-icon-heart">
            <p:menuitem value="Home" url="http://www.primefaces.org" />
            <p:menuitem value="Docs" url="http://www.primefaces.org/..." />
            <p:menuitem value="Support" url="http://www.primefaces.org/..." />
            </p:submenu>
        </p:submenu>
        <p:menuitem value="Mobile" outcome="/mobile/index" />
    </p:submenu>
</p:panelMenu>
```
## Default State
By default, all submenus are collapsed, set expanded on a submenu to true to initially display a
submenu as expanded.

## Stateful
If you use `stateful="true"` to keep the state of the menu by default it is per page.  This means the state is
not remembered across page navigations.  If you would like it to be global and remembered across pages then set
the `statefulGlobal="true"` attribute to enable it.

### Client-side Widget Methods

PanelMenu provides client-side JavaScript methods via its widget API. Use the `widgetVar` to access the widget instance.

#### `expandAll()`

Expands all menu panels and submenus.

```javascript
PF('myPanelMenu').expandAll();
```

#### `collapseAll()`

Collapses all expanded menu panels and submenus.

```javascript
PF('myPanelMenu').collapseAll();
```

#### `clearState()`

Clears the UI state of the panel menu stored in `localStorage`.

```javascript
PF('myPanelMenu').clearState();
```

#### Example

```xhtml
<p:panelMenu widgetVar="myPanelMenu">
    ...
</p:panelMenu>

<script type="text/javascript">
    // Expand all panels
    PF('myPanelMenu').expandAll();

    // Collapse all panels
    PF('myPanelMenu').collapseAll();

    // Clear any saved menu state
    PF('myPanelMenu').clearState();
</script>
```

For advanced usage, refer to the widget JavaScript API in the [PrimeFaces PanelMenu Widget documentation](https://primefaces.github.io/primefaces/15_0_0/#/components/panelmenu?id=widget).


## Skinning
PanelMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-panelmenu | Main container element.
.ui-panelmenu-header | Header of a panel.
.ui-panelmenu-content | Footer of a panel.
.ui-panelmenu .ui-menu-list | Tree container.
.ui-panelmenu .ui-menuitem | A menuitem in tree.

As skinning style classes are global, see the main theming section for more information.

