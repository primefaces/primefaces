# InputSwitch

InputSwitch is used to select a boolean value.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.inputswitch.html)

## Info

| Name | Value |
| --- | --- |
| Tag | inputSwitch
| Component Class | org.primefaces.component.inputswitch.InputSwitch
| Component Type | org.primefaces.component.InputSwitch
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.InputSwitchRenderer
| Renderer Class | org.primefaces.component.inputswitch.InputSwitchRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validating the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
onLabel | on | String | Label for on state.
offLabel | off | String | Label for off state.
label | null | String | User presentable name.
disabled | null | String | Disables or enables the switch.
onchange | false | Boolean | Client side callback to execute on value change event.
style | null | String | Inline style of the main container.
styleClass | null | String | Style class of the main container.
tabindex | null | Integer | The tabindex attribute specifies the tab order of an element when the "tab" button is used for navigating.
showLabels | null | String | Controls the visibility of the labels, defaults to true.
onfocus | null | String | Client side callback to execute when component receives focus.
onblur | null | Sring | Client side callback to execute when component loses focus.

## Getting started with InputSwitch
InputSwitch requires a boolean reference as the value.

```xhtml
<p:inputSwitch value="#{bean.propertyName}" />
```
```java
public class Bean {
    private boolean propertyName;
    //getter and setter
}
```
## Labels
Labels are customized using onLabel and offLabel options. Setting showLabels as false, turns off
labels altogether.

```xhtml
<p:inputSwitch value="#{bean.propertyName}" onLabel="yes" offLabel="no"/>
```
## Client Side API
Widget: _PrimeFaces.widget.InputSwitch_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
toggle() | - | void | Toggles the state.
check() | - | void | Switches to on state.
uncheck() | - | void | Switches to off state.

## Skinning
InputSwitch resides in a main container element which _style_ and _styleClass_ options apply.
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-inputswitch | Main container element.
.ui-inputswitch-off | Off state element.
.ui-inputswitch-on | On state element.
.ui-inputswitch-handle | Switch handle.

As skinning style classes are global, see the main theming section for more information.

