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
        <h:commandLink styleClass="p-panel-titlebar-icon p-state-default">
            <h:outputText styleClass="pi pi-question-circle" />
        </h:commandLink>
    </f:facet>
    //content
</p:panel>
```

## Customizable Icons
The header icons for closing, toggling and the popup menu are icon tokens of the theme (see
[Theming](/core/theming.md?id=icons)): `--p-panel-close-icon`, `--p-panel-toggle-collapsed-icon`,
`--p-panel-toggle-expanded-icon` and `--p-panel-menu-icon`. Set them in `:root, [data-p-scope]` for all panels or on a style class for
some:

```css
.custom-icons-panel {
    --p-panel-close-icon: var(--p-icon-trash);
    --p-panel-toggle-collapsed-icon: var(--p-icon-down);
    --p-panel-toggle-expanded-icon: var(--p-icon-up);
}
```

```xhtml
<p:panel header="Custom Icons" toggleable="true" closable="true" styleClass="custom-icons-panel">
    //content
</p:panel>
```

The icons are rendered as `<span class="p-icon p-close-icon p-panel-close-icon">` (`p-toggle-icon p-panel-toggle-icon`,
`p-options-icon p-panel-menu-icon`); the state of the toggle icon is the `aria-expanded` attribute of the toggler.

## Skinning Panel
Panel resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.p-panel | Main container element of panel
.p-panel-titlebar | Header container
.p-panel-title | Header text
.p-panel-titlebar-icon | Option icon in header
.p-panel-content | Panel content
.p-panel-footer | Panel footer

As skinning style classes are global, see the main theming section for more information.

