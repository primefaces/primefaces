# Spinner

Spinner is an input component to provide a numerical input via increment and decrement buttons.

## Info

| Name | Value |
| --- | --- |
| Tag | spinner
| Component Class | org.primefaces.component.spinner.Spinner
| Component Type | org.primefaces.component.Spinner
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SpinnerRenderer
| Renderer Class | org.primefaces.component.spinner.SpinnerRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | Boolean value that specifies the lifecycle phase the valueChangeEvents should be processed, when true the events will be fired at "apply request values", if immediate is set to false, valueChange Events are fired in "process validations" phase
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a Expr method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
stepFactor | 1 | Double | Stepping factor for each increment and decrement
min | null | Double | Minimum boundary value
max | null | Double | Maximum boundary value
prefix | null | String | Prefix of the input
suffix | null | String | Suffix of the input
decimalPlaces | null | String | Number of decimal places
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

## Getting Started with Spinner
Spinner is an input component and used just like a standard input text.

```java
public class SpinnerBean {
    private int number;
    //getter and setter
}
```
```xhtml
<p:spinner value="#{spinnerBean.number}" />
```

## Step Factor
Other than integers, spinner also support decimals so the fractional part can be controller with
spinner as well. For decimals use the stepFactor attribute to specify stepping amount. Following
example uses a stepFactor 0.25.

```xhtml
<p:spinner value="#{spinnerBean.number}" stepFactor="0.25"/>
```
```java
public class SpinnerBean {
    private double number;
    //getter and setter
}
```
Output of this spinner would be;

After an increment happens a couple of times.

## Prefix and Suffix
Prefix and Suffix options enable placing fixed strings on input field. Note that you would need to
use a converter to avoid conversion errors since prefix/suffix will also be posted.

```xhtml
<p:spinner value="#{spinnerBean.number}" prefix="$" />
```
## Boundaries
In order to restrict the boundary values, use _min_ and _max_ options.

```xhtml
<p:spinner value="#{spinnerBean.number}" min="0" max="100"/>
```
## Ajax Spinner
Spinner can be ajaxified using client behaviors like f:ajax or p:ajax. In example below, an ajax
request is done to update the outputtext with new value whenever a spinner button is clicked.

```xhtml
<p:spinner value="#{spinnerBean.number}">
    <p:ajax update="display" />
</p:spinner>
<h:outputText id="display" value="#{spinnerBean.number}" />
```

## Skinning
Spinner resides in a container element that using _style_ and _styleClass_ applies. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-spinner | Main container element of spinner
.ui-spinner-input | Input field
.ui-spinner-button | Spinner buttons
.ui-spinner-button-up | Increment button
.ui-spinner-button-down | Decrement button

As skinning style classes are global, see the main theming section for more information.

