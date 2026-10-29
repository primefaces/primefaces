# BreadCrumb

Breadcrumb is a navigation component that provides contextual information about page hierarchy
in the workflow.

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

## Icons
The home icon (`homeDisplay="icon"`) and the separator are icon tokens of the theme (see
[Theming](/core/theming.md?id=icons)): `--p-breadcrumb-home-icon` and `--p-breadcrumb-separator-icon`. Set them in
`:root, [data-p-scope]` for all breadcrumbs or on a style class for some:

```css
.custom-breadcrumb {
    --p-breadcrumb-home-icon: var(--p-icon-file);
}
```

The first link is the icon, it carries the classes `p-icon p-home-icon p-breadcrumb-home-icon`.

## Skinning
Breadcrumb resides in a container element that _style_ and _styleClass_ options apply. Following is the
list of structural style classes;


| Class | Applies |
| --- | --- |
| .p-breadcrumb | Main breadcrumb container element.
| .p-breadcrumb .p-menuitem-link | Each menuitem.
| .p-breadcrumb .p-menuitem-text | Each menuitem label.

As skinning style classes are global, see the main theming section for more information.

## Tips

- If there is a dynamic flow, use model option instead of creating declarative p:menuitem
    components and bind your MenuModel representing the state of the flow.
- Breadcrumb can do ajax/non-ajax action requests as well since p:menuitem has this option. In this
    case, breadcrumb must be nested in a form.
- url option is the key for a menuitem, if it is defined, it will work as a simple link. If you’d like to
    use menuitem to execute command with or without ajax, do not define the url option.
