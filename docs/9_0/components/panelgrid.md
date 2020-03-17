# PanelGrid

PanelGrid is an extension to the standard panelGrid component with additional features such as
theming and colspan-rowspan.

## Info

| Name | Value |
| --- | --- |
| Tag | panelGrid
| Component Class | org.primefaces.component.panelgrid.PanelGridRenderer
| Component Type | org.primefaces.component.PanelGrid
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PanelGridRenderer
| Renderer Class | org.primefaces.component.panelgrid.PanelGridRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
columns | 0 | Integer | Number of columns in grid.
style | null | String | Inline style of the panel/table.
contentStyle | null | String | Inline style of the panel-content.
styleClass | null | String | Style class of the panel/table.
contentStyleClass | null | String | Style class of the panel-content.
columnClasses | null | String | Comma separated list of column style classes.<br/>For layout=grid: Grid CSS - classes<br/>For layout=flex: PrimeFlex (FlexGrid) - classes; primeflex.css must be included into the template.xhtml
layout | tabular | String | Displays data in a 'tabular' layout, 'grid' layout or 'flex' layout. The grid and flex layout are responsive layouts. Default value is 'tabular'.
role | grid | String | Role for aria.

## Getting started with PanelGrid
Basic usage of panelGrid is same as the standard one.

```xhtml
<p:panelGrid columns="2">
    <h:outputLabel for="firstname" value="Firstname:" />
    <p:inputText id="firstname" value="#{bean.firstname}" label="Firstname" />
    <h:outputLabel for="surname" value="Surname:" />
    <p:inputText id="surname" value="#{bean.surname}" label="Surname"/>
</p:panelGrid>
```
## Header and Footer
PanelGrid provides facets for header and footer content.

```xhtml
<p:panelGrid columns="2">
    <f:facet name="header">
        Basic PanelGrid
    </f:facet>
    <h:outputLabel for="firstname" value="Firstname: *" />
    <p:inputText id="firstname" value="#{bean.firstname}" label="Firstname" />
    <h:outputLabel for="surname" value="Surname: *" />
    <p:inputText id="surname" value="#{bean.surname}" label="Surname"/>
    <f:facet name="footer">
        <p:commandButton type="button" value="Save" icon="ui-icon-check" />
    </f:facet>
</p:panelGrid>
```

## Rowspan and Colspan
PanelGrid supports rowspan and colspan options as well, in this case row and column markup
should be defined manually.

```xhtml
<p:panelGrid>
    <p:row>
        <p:column rowspan="3">AAA</p:column>
        <p:column colspan="4">BBB</p:column>
    </p:row>
    <p:row>
        <p:column colspan="2">CCC</p:column>
        <p:column colspan="2">DDD</p:column>
    </p:row>
    <p:row>
        <p:column>EEE</p:column>
        <p:column>FFF</p:column>
        <p:column>GGG</p:column>
        <p:column>HHH</p:column>
    </p:row>
</p:panelGrid>
```
**Note** that this approach does not support grid layout.

## Blank Mode
To remove borders add ui-noborder style class to the component using styleClass attribute and to
remove borders plus background color, apply ui-panelgrid-blank style.

## Responsive
PanelGrid offers responsive support using `layout="flex"` for PrimeFlex or `layout="grid"` for Grid CSS. 
However, you may find your labels not lining up in horizontal forms.  You can use the following CSS to address this:
```xml
<style type="text/css">
   .customPanelGrid .ui-panelgrid-content {
            align-items: center;
    }
</style>

<p:panelGrid columns="4" 
             layout="flex" 
             columnClasses="p-col-12 p-md-3 p-xl-2, p-col-12 p-md-9 p-xl-4, p-col-12 p-md-3 p-xl-2, p-col-12 p-md-9 p-xl-4" 
             styleClass="customPanelGrid">
```

## Skinning
PanelGrid resides in a main container which _style_ and _styleClass_ attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-panelgrid | Main container element of panelGrid.
.ui-panelgrid-header | Header.
.ui-panelgrid-footer | Footer.
.ui-panelgrid-even | Even numbered rows.
.ui-panelgrid-odd | Odd numbered rows.

As skinning style classes are global, see the main theming section for more information.

