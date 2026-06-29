# TabMenu

TabMenu is a navigation component that displays menuitems as tabs.

## Info

| Name | Value |
| --- | --- |
| Tag | tabMenu
| Component Class | org.primefaces.component.tabmenu.TabMenu
| Component Type | org.primefaces.component.TabMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TabMenuRenderer
| Renderer Class | org.primefaces.component.tabmenu.TabMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
model | null | MenuModel | MenuModel instance to build menu dynamically.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
activeIndex | 0 | Integer | Index of the active tab.
widgetVar | null | String | Name of the client side widget.

## Getting started with TabMenu
TabMenu requires menuitems as children components, each menuitem is rendered as a tab. Just like
in any other menu component, menuitems can be utilized to do ajax requests, non-ajax requests and
simple GET navigations.


```xhtml
<p:tabMenu activeIndex="0">
    <p:menuitem value="Overview" outcome="main" icon="ui-icon-star"/>
    <p:menuitem value="Demos" outcome="demos" icon="ui-icon-search" />
    <p:menuitem value="Documentation" outcome="docs" icon="ui-icon-document"/>
    <p:menuitem value="Support" outcome="support" icon="ui-icon-wrench"/>
    <p:menuitem value="Social" outcome="social" icon="ui-icon-person" />
</p:tabMenu>
```
## Skinning TabMenu
TabMenu resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-tabmenu | Main container element.
.ui-tabmenu-nav | Container for tabs.
.ui-tabmenuitem | Menuitem container.
.ui-menuitem | Anchor of a menuitem.

As skinning style classes are global, see the main theming section for more information.

