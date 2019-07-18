# Panel

Panel is a grouping component with content toggle, close and menu integration.

## Info

| Name | Value |
| --- | --- |
| Tag | panel
| Component Class | org.primefaces.component.panel.Panel
| Component Type | org.primefaces.component.Panel
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PanelRenderer
| Renderer Class | org.primefaces.component.panel.PanelRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
header | null | String | Header text
footer | null | String | Footer text
toggleable | false | Boolean | Makes panel toggleable.
toggleSpeed | 1000 | Integer | Speed of toggling in milliseconds
collapsed | false | Boolean | Renders a toggleable panel as collapsed.
style | null | String | Style of the panel
styleClass | null | String | Style class of the panel
closable | false | Boolean | Make panel closable.
closeSpeed | 1000 | Integer | Speed of closing effect in milliseconds
visible | true | Boolean | Renders panel as visible.
closeTitle | null | String | Tooltip for the close button.
toggleTitle | null | String | Tooltip for the toggle button.
menuTitle | null | String | Tooltip for the menu button.
toggleOrientation | vertical | String | Defines the orientation of the toggling, valid values are vertical and horizontal.
widgetVar | null | String | Name of the client side widget
toggleableHeader | false | Boolean | Defines if the panel is toggleable by clicking on the whole panel header.

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
## Ajax Behavior Events
Panel provides custom ajax behavior events for toggling and closing features.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
toggle | org.primefaces.event.ToggleEvent | When panel is expanded or collapsed.
close | org.primefaces.event.CloseEvent | When panel is closed.

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

