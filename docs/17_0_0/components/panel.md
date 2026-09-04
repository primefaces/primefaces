# Panel

Panel is a grouping component with content toggle, close and menu integration.

## Getting started with Panel
Panel encapsulates other components.

```xhtml
<p:panel>
    //Child components here...
</p:panel>
```
## Header and Footer
Header and Footer texts can be provided by _header_ and _footer_ attributes or the corresponding facets.
When same attribute and facet name are used, facet will be used.

```xhtml
<p:panel header="Header Text">
    <f:facet name="footer">
        <h:outputText value="Footer Text" />
    </f:facet>
    //Child components here...
</p:panel>
```

## Popup Menu
Panel has built-in support to display a fully customizable popup menu, an icon to display the menu
is placed at top-right corner. This feature is enabled by defining a menu component and defining it
as the options facet.

```xhtml
<p:panel closable="true">
    //Child components here...
    <f:facet name="options">
        <p:menu>
            //Menuitems
        </p:menu>
    </f:facet>
</p:panel>
```
## Custom Action
If you’d like to add custom actions to panel titlebar, use actions facet with icon markup;

```xhtml
<p:panel>
    <f:facet name="actions">
        <h:commandLink styleClass="ui-panel-titlebar-icon ui-corner-all ui-state-default">
            <h:outputText styleClass="ui-icon ui-icon-help" />
        </h:commandLink>
    </f:facet>
    //content
</p:panel>
```
## Skinning Panel
Panel resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-panel | Main container element of panel
.ui-panel-titlebar | Header container
.ui-panel-title | Header text
.ui-panel-titlebar-icon | Option icon in header
.ui-panel-content | Panel content
.ui-panel-footer | Panel footer

As skinning style classes are global, see the main theming section for more information.

