# Knob

Knob is an input component to insert numeric values in a range.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Knob-1.html)

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
Widget: _PrimeFaces.widget.Knob_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
setValue(value) | value | void | Updates the value with given value
getValue() | - | number | Returns the current knob value.
increment() | - | void | Increments current value by step factor.
decrement() | - | void | Decrements current value by step factor.
disable() | - | void | Disables the input field
enable() | - | void | Enables the input field

