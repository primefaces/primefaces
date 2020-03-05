# PanelMenu

PanelMenu is a hybrid component of accordionPanel and tree components.

## Info

| Name | Value |
| --- | --- |
| Tag | panelMenu
| Component Class | org.primefaces.component.panelmenu.PanelMenu
| Component Type | org.primefaces.component.PanelMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PanelMenuRenderer
| Renderer Class | org.primefaces.component.panelmenu.PanelMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
model | null | MenuModel | MenuModel instance to build menu dynamically.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
widgetVar | null | String | Name of the client side widget.
stateful | true | Boolean | Menu state is saved in a cookie

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

