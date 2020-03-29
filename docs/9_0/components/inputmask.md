# InputMask

InputMask forces an input to fit in a defined mask template.

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
mask | null | String | Mask template
validateMask | false | Boolean | Defines whether mask pattern would be validated or not on the server side.
slotChar | null | String | PlaceHolder in mask template.
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Mask component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
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
size | null | Integer | Number of characters used to determine the width of the input element.
style | null | String | Inline style of the input element.
styleClass | null | String | Style class of the input element.
tabindex | null | Integer | Position of the input element in the tabbing order.
title | null | String | Advisory tooltip informaton.
autoClear | true | Boolean | Clears the field on blur when incomplete input is entered.

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
<p:inputMask value="#{bean.phoneExt}" mask="(999) 999-9999? x99999"/>
<h:outputText value="SSN: " />
<p:inputMask value="#{bean.ssn}" mask="999-99-9999"/>
<h:outputText value="Product Key: " />
<p:inputMask value="#{bean.productKey}" mask="a*-999-a999"/>
```
## Skinning
_style_ and _styleClass_ options apply to the displayed input element. As skinning style classes are
global, see the main theming section for more information.

