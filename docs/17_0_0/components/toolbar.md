# Toolbar

Toolbar is a horizontal grouping component for commands and other content.

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
        <p:commandButton type="button" value="New" icon="pi pi-file" />
        <p:commandButton type="button" value="Open" icon="pi pi-folder-open"/>
        <p:divider layout="vertical"/>
        <p:commandButton type="button" title="Save" icon="pi pi-save"/>
        <p:commandButton type="button" title="Delete" icon="pi pi-trash"/>
        <p:commandButton type="button" title="Print" icon="pi pi-print"/>
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
.p-toolbar | Main container
.p-toolbar-group-left | Left toolbarGroup container
.p-toolbar-group-right | Right toolbarGroup container

As skinning style classes are global, see the main theming section for more information.

