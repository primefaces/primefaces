# Layout

Layout component features a highly customizable borderLayout model making it very easy to
create complex layouts even if you’re not familiar with web design.

> Layout and LayoutUnit are deprecated, use  [FlexGrid](https://www.primefaces.org/showcase/ui/panel/flexGrid.xhtml) or [GridCSS](/components/gridcss.md) instead. They'll be removed on 7.1.

## Info

| Name | Value |
| --- | --- |
| Tag | layout
| Component Class | org.primefaces.component.layout.Layout
| Component Type | org.primefaces.component.Layout
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.LayoutRenderer
| Renderer Class | org.primefaces.component.layout.LayoutRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
fullPage | false | Boolean | Specifies whether layout should span all page or not.
style | null | String | Style to apply to container element, this is only applicable to element based layouts.
styleClass | null | String | Style class to apply to container element, this is only applicable to element based layouts.
onResize | null | String | Client side callback to execute when a layout unit is resized.
onClose | null | String | Client side callback to execute when a layout unit is closed.
onToggle | null | String | Client side callback to execute when a layout unit is toggled.
resizeTitle | null | String | Title label of the resize button.
collapseTitle | null | String | Title label of the collapse button.
expandTitle | null | String | Title label of the expand button.
closeTitle | null | String | Title label of the close button.

## Getting started with Layout
Layout is based on a borderLayout model that consists of 5 different layout units which are top, left,
center, right and bottom. This model is visualized in the schema below;

## Full Page Layout
Layout has two modes, you can either use it for a full page layout or for a specific region in your
page. This setting is controlled with the fullPage attribute which is false by default.

The regions in a layout are defined by layoutUnits, following is a simple full page layout with all
possible units. Note that you can place any content in each layout unit.


```xhtml
<p:layout fullPage="true">
    <p:layoutUnit position="north" size="50">
        <h:outputText value="Top content." />
    </p:layoutUnit>
    <p:layoutUnit position="south" size="100">
        <h:outputText value="Bottom content." />
    </p:layoutUnit>
    <p:layoutUnit position="west" size="300">
        <h:outputText value="Left content" />
    </p:layoutUnit>
    <p:layoutUnit position="east" size="200">
        <h:outputText value="Right Content" />
    </p:layoutUnit>
    <p:layoutUnit position="center">
        <h:outputText value="Center Content" />
    </p:layoutUnit>
</p:layout>
```
## Forms in Full Page Layout
When working with forms and full page layout, avoid using a form that contains layoutunits as
generated dom may not be the same. So following is **invalid**.

```xhtml
<p:layout fullPage="true">
    <h:form>
        <p:layoutUnit position="west" size="100">
        <h:outputText value="Left Pane" />
    </p:layoutUnit>
    <p:layoutUnit position="center">
        <h:outputText value="Right Pane" />
    </p:layoutUnit>
    </h:form>
</p:layout>
```
A layout unit must have it’s own form instead, also avoid trying to update layout units because of
same reason, update it’s content instead.

## Dimensions
Except center layoutUnit, other layout units **must** have dimensions defined via _size_ option.

## Element based layout
Another use case of layout is the element based layout. This is the default case actually so just
ignore fullPage attribute or set it to false. Layout example below demonstrates creating a split panel
implementation.


```xhtml
<p:layout style="width:400px;height:200px">
    <p:layoutUnit position="west" size="100">
        <h:outputText value="Left Pane" />
    </p:layoutUnit>
    <p:layoutUnit position="center">
        <h:outputText value="Right Pane" />
    </p:layoutUnit>
    //more layout units
</p:layout>
```
## Ajax Behavior Events
Layout provides custom ajax behavior events for each layout state change.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
toggle | org.primefaces.event.ToggleEvent | When a unit is expanded or collapsed.
close | org.primefaces.event.CloseEvent | When a unit is closed.
resize | org.primefaces.event.ResizeEvent | When a unit is resized.

## Stateful Layout
Making layout stateful would be easy, once you create your data to store the user preference, you
can update this data using ajax event listeners provided by layout. For example if a layout unit is
collapsed, you can save and persist this information. By binding this persisted information to the
collapsed attribute of the layout unit layout will be rendered as the user left it last time.

## Client Side API
Widget: _PrimeFaces.widget.Layout_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
toggle(position) | position | void | Toggles layout unit.
show(position) | position | void | Shows layout unit.
hide(unit) | position | void | Hides layout unit.

## Skinning
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-layout | Main wrapper container element
.ui-layout-doc | Layout container
.ui-layout-unit | Each layout unit container
.ui-layout-{position} | Position based layout unit
.ui-layout-unit-header | Layout unit header
.ui-layout-unit-content | Layout unit body

As skinning style classes are global, see the main theming section for more information.

