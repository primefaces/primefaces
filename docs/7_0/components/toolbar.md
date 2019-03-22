# Toolbar

Toolbar is a horizontal grouping component for commands and other content.

## Info

| Name | Value |
| --- | --- |
| Tag | toolbar
| Component Class | org.primefaces.component.toolbar.Toolbar
| Component Type | org.primefaces.component.Toolbar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ToolbarRenderer
| Renderer Class | org.primefaces.component.toolbar.ToolbarRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
style | null | String | Inline style of the container element.
styleClass | null | String | Style class of the container element.

## Getting Started with the Toolbar
Toolbar has two placeholders(left and right) that are defined with facets. You can also use
toolbarGroup as an alternative to facets.

```xhtml
<p:toolbar>
    <f:facet name="left"></f:facet>
    <f:facet name="right"></f:facet>
</p:toolbar>
```
Any content can be placed inside toolbar.


```xhtml
<p:toolbar>
    <f:facet name="left">
        <p:commandButton type="push" value="New" image="ui-icon-document" />
        <p:commandButton type="push" value="Open" image="ui-icon-folder-open"/>
        <span class="ui-separator">
            <span class="ui-icon ui-icon-grip-dotted-vertical" />
        </span>
        <p:commandButton type="push" title="Save" image="ui-icon-disk"/>
        <p:commandButton type="push" title="Delete" image="ui-icon-trash"/>
        <p:commandButton type="push" title="Print" image="ui-icon-print"/>
    </f:facet>
    <f:facet name="right">
        <p:menuButton value="Navigate">
            <p:menuitem value="Home" url="#" />
            <p:menuitem value="Logout" url="#" />
        </p:menuButton>
    </f:facet>
</p:toolbar>
```
## Skinning
Toolbar resides in a container element which _style_ and _styleClass_ options apply. Following is the list
of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-toolbar | Main container
.ui-toolbar .ui-separator | Divider in a toolbar
.ui-toolbar-group-left | Left toolbarGroup container
.ui-toolbar-group-right | Right toolbarGroup container

As skinning style classes are global, see the main theming section for more information.

