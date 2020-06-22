# InputMask

InputMask forces an input to fit in a defined mask template.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.inputmask.html)

## Info

| Name | Value |
| --- | --- |
| Tag | inputMask
| Component Class | org.primefaces.component.inputmask.InputMask
| Component Type | org.primefaces.component.InputMask
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.InputMaskRenderer
| Renderer Class | org.primefaces.component.inputmask.InputMaskRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
accesskey | null | String | Access key that when pressed transfers focus to the input element.
alt | null | String | Alternate textual description of the input field.
autoClear | true | Boolean | Clears the field on blur when incomplete input is entered.
autocomplete | null | String | Controls browser autocomplete behavior.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
converterMessage | null | String | Message to be displayed when conversion fails.
dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
disabled | false | Boolean | Disables input field
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
label | null | String | A localized user presentable name.
lang | null | String | Code describing the language used in the generated markup for this component.
mask | null | String | Mask template
maxlength | null | Integer | Maximum number of characters that may be entered in this field.
onblur | null | String | Client side callback to execute when input element loses focus.
onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
onclick | null | String | Client side callback to execute when input element is clicked.
ondblclick | null | String | Client side callback to execute when input element is double clicked.
onfocus | null | String | Client side callback to execute when input element receives focus.
onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
onkeyup | null | String | Client side callback to execute when a key is released over input element.
onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
onselect | null | String | Client side callback to execute when text within input element is selected by user.
placeholder | null | String | Specifies a short hint.
readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
required | false | Boolean | Mask component as required
requiredMessage | null | String | Message to be displayed when required field validation fails.
size | null | Integer | Number of characters used to determine the width of the input element.
slotChar | '_' | String | PlaceHolder in mask template. Default to underscore '_'.
style | null | String | Inline style of the input element.
styleClass | null | String | Style class of the input element.
tabindex | null | Integer | Position of the input element in the tabbing order.
title | null | String | Advisory tooltip informaton.
validateMask | false | Boolean | Defines whether mask pattern would be validated or not on the server side.
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
validatorMessage | null | String | Message to be displayed when validation fields.
value | null | Object | Value of the component than can be either an EL expression of a literal text
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
widgetVar | null | String | Name of the client side widget.

## Getting Started with InputMask
InputMask below enforces input to be in 99/99/9999 date format.

```xhtml
<p:inputMask value="#{bean.field}" mask="99/99/9999" />
```

## Mask Samples
Here are more samples based on different masks;

```xhtml
<h:outputText value="Phone: " />
<p:inputMask value="#{bean.phone}" mask="(999) 999-9999"/>
<h:outputText value="Phone with Ext: " />
<p:inputMask value="#{bean.phoneExt}" mask="(999) 999-9999 [x99999]"/>
<h:outputText value="SSN: " />
<p:inputMask value="#{bean.ssn}" mask="999-99-9999"/>
<h:outputText value="Product Key: " />
<p:inputMask value="#{bean.productKey}" mask="a*-999-a999"/>
```

## Client Side API
Widget: _PrimeFaces.widget.AccordionPanel_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getValue() | none | void | Returns the current value of this input field including the mask like "12/31/1999".|
| getValueUnmasked() | none | void | Returns the current value of this input field without the mask like "12311999".|
| setValue(value) |value: the new value | void | Sets the value of this input field to the given value. If the value does not fit the mask, it is adjusted appropriately.|

## Skinning
_style_ and _styleClass_ options apply to the displayed input element. As skinning style classes are
global, see the main theming section for more information.

