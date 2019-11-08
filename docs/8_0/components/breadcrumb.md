# BreadCrumb

Breadcrumb is a navigation component that provides contextual information about page hierarchy
in the workflow.

## Info

| Name | Value |
| --- | --- |
| Tag | breadCrumb
| Component Class | org.primefaces.component.breadcrumb.BreadCrumb
| Component Type | org.primefaces.component.BreadCrumb
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.BreadCrumbRenderer
| Renderer Class | org.primefaces.component.breadcrumb.BreadCrumbRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| model | null | MenuModel | MenuModel instance to create menus programmatically
| style | null | String | Style of main container element.
| styleClass | null | String | Style class of main container
| homeDisplay | icon | String | Defines display mode of root link, valid values are "icon" default and "text".
| tabindex | 0 | String | Position of the items in the tabbing order. Default is 0.
| lastItemDisabled | false | Boolean | Boolean flag indicating whether the last item should be disabled.

## Getting Started with BreadCrumb
Steps are defined as child menuitem components in breadcrumb.

```xhtml
<p:breadCrumb>
    <p:menuitem label="Categories" url="#" />
    <p:menuitem label="Sports" url="#" />
    //more menuitems
</p:breadCrumb>
```
## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Options Facet
In order to place custom content inside breadcrumb, options facet is provided. Options facet content
is aligned right by default.

## Skinning
Breadcrumb resides in a container element that _style_ and _styleClass_ options apply. Following is the
list of structural style classes;


| Class | Applies |
| --- | --- |
| .ui-breadcrumb | Main breadcrumb container element.
| .ui-breadcrumb .ui-menu-item-link | Each menuitem.
| .ui-breadcrumb .ui-menu-item-text | Each menuitem label.
| .ui-breadcrumb-chevron | Seperator of menuitems.

As skinning style classes are global, see the main theming section for more information.

## Tips

- If there is a dynamic flow, use model option instead of creating declarative p:menuitem
    components and bind your MenuModel representing the state of the flow.
- Breadcrumb can do ajax/non-ajax action requests as well since p:menuitem has this option. In this
    case, breadcrumb must be nested in a form.
- url option is the key for a menuitem, if it is defined, it will work as a simple link. If you’d like to
    use menuitem to execute command with or without ajax, do not define the url option.
