# PanelGrid

PanelGrid is an extension to the standard panelGrid component with additional features such as
theming and colspan-rowspan.

## Info

| Name | Value |
| --- | --- |
| Tag | panelGrid
| Component Class | org.primefaces.component.panelgrid.PanelGrid
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
columns | 12 | Integer | Number of columns in responsive layout.
style | null | String | Inline style of the panel/table.
contentStyle | null | String | Inline style of the panel-content.
styleClass | null | String | Style class of the panel/table.
contentStyleClass | null | String | Style class of the panel-content.
columnClasses | null | String | Comma separated list of column style classes.<br/>For layout=grid: Grid CSS - classes<br/>For layout=flex: PrimeFlex (FlexGrid) - classes; primeflex.css must be included into the template.xhtml<br/>For layout=tailwind: Tailwind CSS utility classes
layout | tabular | String | Displays data in a 'tabular' layout, 'grid' layout, 'flex' layout, or 'tailwind' layout. The grid, flex, and tailwind layouts are responsive layouts. Default value is 'grid'.
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
             columnClasses="col-12 md:col-3 xl:col-2, col-12 md:col-9 xl:col-4, col-12 md:col-3 xl:col-2, col-12 md:col-9 xl:col-4" 
             styleClass="customPanelGrid">
```

Note: This documentation refers to version 3.0.0 of PrimeFlex.

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

## Tailwind CSS Layout
PanelGrid offers an additional responsive layout using `layout="tailwind"` for Tailwind CSS. This layout renders using CSS Grid with Tailwind utility classes and provides automatic responsive breakpoints based on the number of columns.

### Basic Usage
```xml
<p:panelGrid columns="2" layout="tailwind" contentStyleClass="gap-4">
    <h:outputLabel for="firstname" value="Firstname:" />
    <p:inputText id="firstname" value="#{bean.firstname}" />
    <h:outputLabel for="surname" value="Surname:" />
    <p:inputText id="surname" value="#{bean.surname}" />
</p:panelGrid>
```

### Responsive Breakpoints
The Tailwind layout automatically applies responsive grid columns based on the number of columns specified:
- **2 columns**: `grid-cols-1 sm:grid-cols-2` (1 column on mobile, 2 on tablet+)
- **3 columns**: `grid-cols-1 sm:grid-cols-2 lg:grid-cols-3` (responsive 1→2→3)
- **4 columns**: `grid-cols-1 sm:grid-cols-2 lg:grid-cols-4` (responsive 1→2→4)
- And so on up to 12 columns

### Controlling Width
By default, the Tailwind grid takes full width. Use the `styleClass` attribute to control this:
```xml
<p:panelGrid columns="2" layout="tailwind" 
             styleClass="max-w-2xl"
             contentStyleClass="gap-4">
    <!-- content -->
</p:panelGrid>
```

Common width classes:
- `styleClass="w-auto"` - Auto width (content-based)
- `styleClass="inline-grid"` - Inline grid (similar to table behavior)
- `styleClass="max-w-2xl"` - Maximum width constraint
- `styleClass="max-w-4xl mx-auto"` - Centered with max width

### Controlling Spacing
Use `contentStyleClass` to control the gap between cells:
```xml
<p:panelGrid columns="2" layout="tailwind" contentStyleClass="gap-6">
    <!-- content -->
</p:panelGrid>
```

Common gap values:
- `contentStyleClass="gap-2"` - Small gap (8px)
- `contentStyleClass="gap-4"` - Medium gap (16px)
- `contentStyleClass="gap-6"` - Large gap (24px)
- `contentStyleClass="gap-0"` - No gap

### Column Classes
The `columnClasses` attribute works with Tailwind layout to apply different styles to alternating columns:
```xml
<p:panelGrid columns="2" layout="tailwind" 
             columnClasses="font-semibold, bg-gray-50"
             contentStyleClass="gap-4">
    <!-- First column gets: font-semibold -->
    <!-- Second column gets: bg-gray-50 -->
</p:panelGrid>
```

### Colspan and Column Styling
The Tailwind layout supports `<p:column>` components with `colspan`, `id`, `style`, and `styleClass`:
```xml
<p:panelGrid columns="4" layout="tailwind" contentStyleClass="gap-4">
    <p:column colspan="2" styleClass="bg-blue-100 p-4">
        <h:outputText value="This spans 2 columns" />
    </p:column>
    <p:column colspan="2" styleClass="bg-green-100 p-4">
        <h:outputText value="This also spans 2 columns" />
    </p:column>
    <p:column>Single</p:column>
    <p:column>Single</p:column>
    <p:column>Single</p:column>
    <p:column>Single</p:column>
</p:panelGrid>
```

### Aligning Labels
To vertically center labels with input fields, add `items-center` to the `contentStyleClass`:
```xml
<p:panelGrid columns="2" layout="tailwind" 
             contentStyleClass="gap-4 items-center">
    <h:outputLabel for="firstname" value="Firstname:" />
    <p:inputText id="firstname" value="#{bean.firstname}" />
</p:panelGrid>
```

### Limitations
- `layout="tailwind"` supports `<p:column>` components with colspan and rowspan
- `<p:row>` components are not supported in Tailwind layout
- Ensure all required Tailwind CSS grid classes are included in your compiled CSS configuration

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