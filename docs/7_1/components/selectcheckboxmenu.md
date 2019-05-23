# SelectCheckboxMenu

SelectCheckboxMenu is a multi select component that displays options in an overlay.

## Info

| Name | Value |
| --- | --- |
| Tag | selectCheckboxMenu
| Component Class | org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu
| Component Type | org.primefaces.component.SelectCheckboxMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectCheckboxMenuRenderer
| Renderer Class | org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component referring to a List.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Disables the component.
label | null | String | User presentable name.
onchange | null | String | Callback to execute on value change.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
scrollHeight | null | Integer | Height of the overlay.
onShow | null | String | Client side callback to execute when overlay is displayed.
onHide | null | String | Client side callback to execute when overlay is hidden.
filter | false | Boolean | Renders an input field as a filter.
filterMatchMode | startsWith | String | Match mode for filtering, valid values are startsWith, contains, endsWith and custom.
filterFunction | null | String | Client side function to use in custom filtering.
caseSensitive | false | Boolean | Defines if filtering would be case sensitive.
panelStyle | null | String | Inline style of the overlay.
panelStyleClass | null | String | Style class of the overlay.
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
tabindex | null | String | Position of the element in the tabbing order.
title | null | String | Advisory tooltip information.
showHeader | true | Boolean | When enabled, the header of panel is displayed.
updateLabel | false | Boolean | When enabled, the selected items are displayed on label.
multiple | false | Boolean | Whether to show selected items as multiple labels.
dynamic | false | Boolean | Defines if dynamic loading is enabled for the element's panel. If the value is "true", the overlay is not rendered on page load to improve performance.
labelSeparator | , | String | Separator for joining item lables if updateLabel is set to true. Default is ",".
emptyLabel | null | String | Label to be shown in updateLabel mode when no item is selected. If not set the label is shown.
filterPlaceholder | null | String  | Placeholder text to show when filter input is empty.

## Getting started with SelectCheckboxMenu
SelectCheckboxMenu usage is same as the standard selectManyCheckbox or PrimeFaces
selectManyCheckbox components.

```xhtml
<p:selectCheckboxMenu value="#{bean.selectedOptions}" label="Movies">
    <f:selectItems value="#{bean.options}" />
</p:selectCheckboxMenu>
```
## Labels
Selected items are not displayed as the component label by default, setting _updateLabel_ to true
displays item as a comma separated list and for an advanced display use _multiple_ property instead
which renders the items as separate tokens similar to the chips component.

## Filtering
When filtering is enabled with setting _filter_ on, an input field is rendered at overlay header and on
keyup event filtering is executed on client side using _filterMatchMode_. Default modes of
filterMatchMode are startsWith, contains, endsWith and custom. Custom mode requires a javascript
function to do the filtering.

```xhtml
<p:selectCheckboxMenu value="#{bean.selectedOptions}" label="Movies" filterMatchMode="custom" filterFunction="customFilter" filter="on">
    <f:selectItems value="#{bean.options}" />
</p:selectCheckboxMenu>
```
```js
function customFilter(itemLabel, filterValue) {
    //return true to accept and false to reject
}
```
## Ajax Behavior Events
In addition to common dom events like change, selectCheckboxMenu provides _toggleSelect_ event.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
toggleSelect | org.primefaces.event.ToggleSelectEvent | When toggle all checkbox changes.

## Skinning
SelectCheckboxMenu resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;


| Class | Applies | 
| --- | --- | 
.ui-selectcheckboxmenu | Main container element.
.ui-selectcheckboxmenu-label-container | Label container.
.ui-selectcheckboxmenu-label | Label.
.ui-selectcheckboxmenu-trigger | Dropdown icon.
.ui-selectcheckboxmenu-panel | Overlay panel.
.ui-selectcheckboxmenu-items | Option list container.
.ui-selectcheckboxmenu-item | Each options in the collection.
.ui-chkbox | Container of a checkbox.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.

