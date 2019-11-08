# SelectOneButton

SelectOneButton is an input component to do a single select.

## Info

| Name | Value |
| --- | --- |
| Tag | selectOneButton
| Component Class | org.primefaces.component.selectonebutton.SelectOneButton
| Component Type | org.primefaces.component.SelectOneButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectOneButtonRenderer
| Renderer Class | org.primefaces.component.selectonebutton.SelectOneButtonRenderer

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
tabindex | 0 | String | Position of the element in the tabbing order.
unselectable | true | Boolean | Whether selection can be cleared.

## Getting started with SelectOneButton
SelectOneButton usage is same as selectOneRadio component, buttons just replace the radios.

## Client Side API
Widget: _PrimeFaces.widget.SelectOneButton_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
enable() | - | void | Enables the component.
disabled() | - | void | Disables the component.

## Skinning
SelectOneButton resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectonebutton | Main container element.
