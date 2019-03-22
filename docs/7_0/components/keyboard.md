# Keyboard

Keyboard is an input component that uses a virtual keyboard to provide the input. Notable features
are the customizable layouts and skinning capabilities.

## Info

| Name | Value |
| --- | --- |
| Tag | keyboard
| Component Class | org.primefaces.component.keyboard.Keyboard
| Component Type | org.primefaces.component.Keyboard
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.KeyboardRenderer
| Renderer Class | org.primefaces.component.keyboard.KeyboardRenderer

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
valueChangeListener | null | MethodExpr  | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
password | false | Boolean | Makes the input a password field.
showMode | focus | String | Specifies the showMode, ‘focus’, ‘button’, ‘both’
buttonImage | null | String | Image for the button.
buttonImageOnly | false | Boolean |  When set to true only image of the button would be displayed.
effect | fadeIn | String | Effect of the display animation.
effectDuration | null | String | Length of the display animation.
layout | qwerty | String | Built-in layout of the keyboard.
layoutTemplate | null | String | Template of the custom layout.
keypadOnly | focus | Boolean | Specifies displaying a keypad instead of a keyboard.
promptLabel | null | String | Label of the prompt text.
closeLabel | null | String | Label of the close key.
clearLabel | null | String | Label of the clear key.
backspaceLabel | null | String | Label of the backspace key.
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
widgetVar | null | String | Name of the client side widget.

## Getting Started with Keyboard
Keyboard is used just like a simple inputText, by default when the input gets the focus a keyboard is
displayed.

```xhtml
<p:keyboard value="#{bean.value}" />
```

## Built-in Layouts
There’re a couple of built-in keyboard layouts these are ‘qwerty’, ‘qwertyBasic’ and ‘alphabetic’. For
example keyboard below has the alphabetic layout.

```xhtml
<p:keyboard value="#{bean.value}" layout="alphabetic"/>
```
## Custom Layouts
Keyboard has a very flexible layout mechanism allowing you to come up with your own layout.

```xhtml
<p:keyboard value="#{bean.value}" layout="custom" layoutTemplate="prime-back,faces-clear,rocks-close"/>
```
Another example;

```xhtml
<p:keyboard value="#{bean.value}" layout="custom" layoutTemplate="create-space-your-close,owntemplate-shift,easily-space-spacebar"/>
```

A layout template basically consists of built-in keys and your own keys. Following is the list of all
built-in keys.

- back
- clear
- close
- shift
- spacebar
- space
- halfspace

All other text in a layout is realized as seperate keys so "prime" would create 5 keys as "p" "r" "i"
"m" "e". Use dash to seperate each member in layout and use commas to create a new row.

## Keypad
By default keyboard displays whole keys, if you only need the numbers use the keypad mode.

```xhtml
<p:keyboard value="#{bean.value}" keypadOnly="true"/>
```
## ShowMode
There’re a couple of different ways to display the keyboard, by default keyboard is shown once
input field receives the focus. This is customized using the showMode feature which accept values
‘focus’, ‘button’, ‘both’. Keyboard below displays a button next to the input field, when the button is
clicked the keyboard is shown.

```xhtml
<p:keyboard value="#{bean.value}" showMode="button"/>
```
Button can also be customized using the _buttonImage_ and _buttonImageOnly_ attributes.

