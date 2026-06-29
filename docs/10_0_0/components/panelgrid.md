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

## Responsive Style, StyleClass, and ID
PanelGrid offers responsive support using `layout="flex"` for PrimeFlex or `layout="grid"` for Grid CSS and
 a non-responsive layout using `layout="tabular"`. Flex and Grid layout wraps components in a DIV and grid layout
 further wraps a row in a DIV.  These DIVs may now be styled by utilizing `row` and `column` components which optionally
 represent the cell and may be utilized to attach `style`, `styleClass` and `id`.

`layout="grid"` supports `column` and `row`.
`layout="flex"` supports `column`.

`row` may contain individual components, or additional `column` cells, within the grid responsive layout.
The respective cell style will be utilized from the provided `columnClasses` and from any `styleClass` on the
component.  If more components are contained within the row component then the respective `columnClasses` will
be utilized based `columnClasses[column%columns]`.


```xml
<p:panelGrid 
        columns="2" 
        layout="grid"
        columnClasses="ui-grid-col-8,ui-grid-col-4,ui-grid-col-8,ui-grid-col-4">
    <h:outputLabel for="field01" value="Firstname: *" />
    <p:inputText id="field01" value="#{bean.firstname}" label="Firstname" />

    <p:column id="col01" styleClass="foo-cell" style="background-color:red;" >
       <h:outputLabel for="field02" value="Firstname: *" />
    </p:column>
    <p:column id="col02">
       <p:inputText id="field02" value="#{bean.firstname}" label="Firstname" />
    </p:column>

    <p:row id="row01"  styleClass="foo-row" style="background-color:red;">
       <h:outputLabel for="field03" value="Firstname: *" />
       <p:inputText id="field03" value="#{bean.firstname}" label="Firstname" />
       <h:outputLabel for="field04" value="Firstname: *" />
       <p:column id="col03">
           <p:inputText id="field04" value="#{bean.firstname}" label="Firstname" />
       </p:column>
    </p:row>

    <p:row id="row02"  styleClass="foo-row" style="background-color:red;">
       <p:column id="col04">
           <h:outputLabel for="field05" value="Firstname: *" />
       </p:column>
       <p:column id="col05">
           <p:inputText id="field05" value="#{bean.firstname}" label="Firstname" />
       </p:column>
    </p:row>
</p:panelGrid>
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

