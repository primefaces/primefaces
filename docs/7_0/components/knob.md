# Knob

Knob is an input component to insert numeric values in a range.

## Info

| Name | Value |
| --- | --- |
| Tag | knob
| Component Class | org.primefaces.component.knob.Knob
| Component Type | org.primefaces.component.Knob
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.KnobRenderer
| Renderer Class | org.primefaces.component.knob.KnobRenderer

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
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed after failed validation.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
min | 0 | Integer | Min valid value of the component.
max | 100 | Integer | Max valid value of the component.
step | 1 | Integer | Increment/decrement step of the component.
thickness | null | Float | Thickness of the bar.
width | auto | String | Width of the component.
height | auto | String | Height of the component.
foregroundColor | null | Object | Foreground color of the component, you can use an hex value, a css constant or a java.awt.Color object
backgroundColor | null | Object | Background color of the component, you can use an hex value, a css constant or a java.awt.Color object.
colorTheme | null | String | Theme of the knob.
disabled | false | Boolean | Disables the input element
showLabel | true | Boolean | Set false to hide the label.
cursor | false | Boolean | Set true to show only a cursor instead of the full bar.
labelTemplate | {value} | String | Template of the progress value e.g. "{value}%"
onchange | null | String | Client side callback to invoke when value changes.
lineCap | butt | String | Gauge stroke endings. Valid values are "butt" (default) and "round".
styleClass | null | String | Style class of the component.

## Getting Started with Knob
Knob is used as an input component with a value.

```xhtml
<p:knob value="#{bean.propertyName}" />
```
## Boundaries and Step
Boundaries and step can be customized using min, max and step properties.

```xhtml
<p:knob value="#{bean.propertyName}" min="100" max="1000" step="50"/>
```

## Label
Label at the center is visible by default and can be hidden by setting showLabel to false. In addition
it can be customized using labelTemplate option.

```xhtml
<p:knob value="#{bean.propertyName}" labelTemplate="{value}%"/>
```
## Colors
Color scheme of the know are changed with foregroundColor and backgroundColor options.

```xhtml
<p:knob foregroundColor="red" backgroundColor="#00000" value="25"/>
```
## Client Side API
Widget: _PrimeFaces.widget.Know_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
setValue(value) | value | void | Updates the value with given value
getValue() | - | number | Returns the current knob value.
increment() | - | void | Increments current value by step factor.
Decrement() | - | void | Decremenets current value by step factor.

