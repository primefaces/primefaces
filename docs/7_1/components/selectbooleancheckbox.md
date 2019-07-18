# SelectBooleanCheckbox

SelectBooleanCheckbox is an extended version of the standard checkbox with theme integration.

## Info

| Name | Value |
| --- | --- |
| Tag | selectBooleanCheckbox
| Component Class | org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox
| Component Type | org.primefaces.component.SelectBooleanCheckbox
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.SelectBooleanCheckboxRenderer
| Renderer Class | org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckboxRenderer

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
itemLabel | null | String | Label displayed next to checkbox.
tabindex | null | String | Specifies tab order for tab key navigation.
onfocus | null | String | Client side callback to execute when checkbox receives focus.
onblur | null | String | Client side callback to execute when checkbox loses focus.
title | null | String | Advisory tooltip information.
escape | false | Boolean | Defines if label of the component is escaped or not.

## Getting started with SelectBooleanCheckbox
SelectBooleanCheckbox usage is same as the standard one.

## Client Side API
Widget: _PrimeFaces.widget.SelectBooleanCheckbox_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
check() | - | void | Checks the checkbox.
uncheck() | - | void | Unchecks the checkbox.
toggle() | - | void | Toggles check state.

## Skinning
SelectBooleanCheckbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-chkbox | Main container element.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.

