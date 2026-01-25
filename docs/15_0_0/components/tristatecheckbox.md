# TriStateCheckbox

TriStateCheckbox adds a new state to a checkbox value.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.TriStateCheckbox-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | triStateCheckbox
| Component Class | org.primefaces.component.triStateCheckbox.TriStateCheckbox
| Component Type | org.primefaces.component.TriStateCheckbox
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TriStateCheckboxRenderer
| Renderer Class | org.primefaces.component.triStateCheckbox.TriStateCheckboxRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Boolean | Boolean value of the component (null, true, false).
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validating the input
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Disables the component.
readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
stateOneIcon | null | String | Icon of the state one.
stateTwoIcon | null | String | Icon of the state two.
stateThreeIcon | null | String | Icon of the state three.
stateOneTitle | null | String | Title for state one.
stateTwoTitle | null | String | Title for state two
stateThreeTitle | null | String | Title for state three.
itemLabel | null | String | Label displayed next to checkbox.
tabindex | null | String | Specifies tab order for tab key navigation.
onchange | null | String | Client side callback to execute on state change.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
label | null | String | A localized user presentable name.
escape | true | Boolean | Defines whether HTML in label would be escaped or not.

## Getting started with TriStateCheckbox
TriStateCheckbox supports three states: null (unchecked), true (checked), and false (indeterminate).

### Using Boolean values

```xhtml
<p:triStateCheckbox value="#{bean.booleanValue}"/>
```

```java
public class Bean {
    private Boolean booleanValue; // Can be true, false, or null
    //getter-setter
}
```

## Value Handling
The TriStateCheckbox component automatically handles conversion between String and Boolean values:

- **Boolean values**: Used directly as `Boolean.TRUE`, `Boolean.FALSE`, or `null`
- **Three states**: 
  - `null` - Unchecked state (state one)
  - `true` - Checked state (state two) 
  - `false` - Indeterminate state (state three)


## Client Side API
Widget: _PrimeFaces.widget.TriStateCheckbox_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
toggle() | - | void | Switches to next state.
disable() | - | void | Disables the input field
enable() | - | void | Enables the input field

## Skinning
TriStateCheckbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies |
| --- | --- |
.ui-chkbox | Main container element.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.
.ui-chkbox-label | Checkbox label.
