# InputNumber

InputNumber formats input fields with numeric Strings. It supports currency symbols, minimum
and maximum value, negative numbers, and a lot of round methods.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.inputnumber-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | inputNumber
| Component Class | org.primefaces.component.inputnumber.InputNumber
| Component Type | org.primefaces.component.InputNumber
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.InputNumberRenderer
| Renderer Class | org.primefaces.component.inputnumber.InputNumberRenderer

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
requiredMessage | null | String | Message to be displayed after failed validation.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
accesskey | null | String | Access key that when pressed transfers focus to the input element.
alt | null | String | Alternate textual description of the input field.
autocomplete | null | String | Controls browser autocomplete behavior.
dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
disabled | false | Boolean | Disables input field
label | null | String | A localized user presentable name.
lang | null | String | Code describing the language used in the generated markup for this component.
maxlength | null | Integer | Maximum number of characters that may be entered in this field.
onblur | null | String | Client side callback to execute when input element loses focus.
onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
oninput | null | String | Client side callback to execute when an element gets user input.
onclick | null | String | Client side callback to execute when input element is clicked.
ondblclick | null | String | Client side callback to execute when input element is double clicked.
onfocus | null | String | Client side callback to execute on input element focus.
onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
onkeyup | null | String | Client side callback to execute when a key is released over input element.
onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
onwheel | null | String | Client side callback to execute when the mouse wheel rolls up or down over an element.
onselect | null | String | Client side callback to execute when text within input element is selected by user.
oncut | null | String | Client side callback to execute when the user copies the content of an element.
oncopy | null | String | Client side callback to execute when the user cuts the content of an element.
onpaste | null | String | Client side callback to execute when the user pastes some content in an element.
oncontextmenu | null | String | Client side callback to execute when a context menu is triggered.
oninvalid | null | String | Client side callback to execute when an element is invalid.
onreset | null | String | Client side callback to execute when the Reset button in a form is clicked.
onsearch | null | String | Client side callback to execute when the user writes something in a search field.
ondrag | null | String | Client side callback to execute when an element is dragged.
ondragend | null | String | Client side callback to execute at the end of a drag operation.
ondragenter | null | String | Client side callback to execute when an element has been dragged to a valid drop target.
ondragleave | null | String | Client side callback to execute when an element leaves a valid drop target.
ondragover | null | String | Client side callback to execute when an element is being dragged over a valid drop target.
ondragstart | null | String | Client side callback to execute at the start of a drag operation.
ondrop | null | String | Client side callback to execute when dragged element is being dropped.
onscroll | null | String | Client side callback to execute when an element's scrollbar is being scrolled.
placeholder | null | String | Specifies a short hint.
readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
size | null | Integer | Number of characters used to determine the width of the input element.
style | null | String | Inline style of the input element.
styleClass | null | String | Style class of the input element.
tabindex | null | Integer | Position of the input element in the tabbing order.
title | null | String | Advisory tooltip information.
type | text | String | Input field type.
decimalPlaces | 2 | String | Number of decimal places. If value is Integer/Long/Short/BigInteger number defaults to 0 else defaults to 2 only if the initial value is not `null`.
decimalSeparator | *1 | String | Decimal separator char.
decimalSeparatorAlternative | null | String | Allow to declare an alternative decimal separator which is automatically replaced by `decimalCharacter` when typed.
emptyValue | focus | String | Defines what to display when the input value is empty (possible options are null, focus, press, always, min, max, zero, number, or a string representing a number)
leadingZero | allow | Sting | Controls leading zero behavior. Valid values are "allow"(default), "deny" and "keep".
inputMode | null | String | HTML5 inputmode attribute for hinting at the type of data this control has for touch devices. Default is 'numeric' if decimalPlaces==0, 'decimal' if decimalPlaces>0.
inputStyle | null | String | Inline style of the input element.
inputStyleClass | null | String | Style class of the input element.
maxValue | 10000000000000 | String | Maximum values allowed.
minValue | -10000000000000 | String | Minimum value. Warning: If minValue is greater than 0, then you will effectively prevent your users to entirely delete the content of their input.
modifyValueOnWheel | true | Boolean | Allows the user to increment or decrement the element value with the mouse wheel.
padControl | true | Boolean | Controls padding of the decimal places. If true, always pads the decimal with zeros.
roundMethod | Round-Half-Up-Symmetric | String | Controls the rounding method.
symbol | none | String | Desired symbol or unit.
symbolPosition | p | String | Position of the symbol. 'p' for prefix 's' for suffix
signPosition | null | String | Placement of the negative/positive sign relative to the symbolPosition option The sign is placed on either side of the symbolPosition, which can be placed on either side of the numbers. 'p' for prefix 's' for suffix 'l' for left 'r' for right
thousandSeparator | *1 | String | Thousand separator char.

*1 Depends on locale defined via faces-config.xml

## Getting Started with InputNumber
Without any configuration, input number will parse the value and format it as a number using
decimal and thousand separator.

```xhtml
<p:inputNumber value="#{bean.propertyName}" />
```
## Examples
Here are some examples demonstrating various cases;

#### Suffix currency symbol and comma for decimal separator

```xhtml
<p:inputNumber id="input2" value="0.0" symbol=" CHF" symbolPosition="s" decimalSeparator="," thousandSeparator="." />
```
#### Maximum and minimum values (-1000.999 to 1000.000)

```xhtml
<p:inputNumber id="input3" value="0.0" minValue="-1000.999" maxValue="1000" />
```
#### Custom decimal places

```xhtml
<p:inputNumber id="input4" value="0.0" decimalPlaces="6" />
```
#### Empty value (empty) and required

```xhtml
<p:inputNumber id="input5" value="251.31" symbol="%" symbolPosition="s" required="true" emptyValue="empty" />
```
#### Empty value (zero)

```xhtml
<p:inputNumber id="input6" value="60.0" symbol="%" symbolPosition="s" emptyValue="zero" />
```
#### Empty value (always show symbol)

```xhtml
<p:inputNumber id="Input7" value="" symbol="%" symbolPosition="s" emptyValue="always" />
```
#### 15 Decimals using BigDecimal

```xhtml
<p:inputNumber id="Input8" value="1234.000000001" decimalPlaces="15" />
```

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, wheel`  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.InputNumber_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| enable() | none | void | Enables the component |
| disabled() | none | void | Disables the component |
| setValue(value) | value: the numeric value to set | void | Sets the value of the InputNumber|
| getValue() | none | String | Gets the current value of the InputNumber as a String. Formatted if formatted=true attribute.|
