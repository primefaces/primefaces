# Password

Password component is an extended version of standard inputSecret component with theme
integration and strength indicator.

## Info

| Name | Value |
| --- | --- |
| Tag | password
| Component Class | org.primefaces.component.password.Password
| Component Type | org.primefaces.component.Password
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PasswordRenderer
| Renderer Class | org.primefaces.component.password.PasswordRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean |  Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validationg the input.
valueChangeListener | null | MethodExpr| A method binding expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
feedback | false | Boolean | Enables strength indicator.
inline | false | Boolean |  Displays feedback inline rather than using a popup. 
promptLabel | Please enter a password | String | Label of prompt.
weakLabel | Weak | String | Label of weak password.
goodLabel | Good | String | Label of good password.
strongLabel | Strong | String | Label of strong password.
redisplay | false | Boolean | Whether or not to display previous value.
match | null | String | Id of another password component to match value against.
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

## Getting Started with Password
Password is an input component and used just like a standard input text. When _feedback_ option is
enabled a password strength indicator is displayed.

```xhtml
<p:password value="#{bean.password}" feedback="true|false" />
```

```java
public class Bean {
    private String password;

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password;
    }
}
```
## I18N
Although all labels are in English by default, you can provide custom labels as well. Following
password gives feedback in Turkish.

```xhtml
<p:password value="#{bean.password}" promptLabel="Lütfen şifre giriniz" weakLabel="Zayıf" goodLabel="Orta seviye" strongLabel="Güçlü" feedback= "true"/>
```
## Inline Strength Indicator
By default strength indicator is shown in an overlay, if you prefer an inline indicator just enable
inline mode.

```xhtml
<p:password value="#{mybean.password}" inline="true" feedback= "true"/>
```
## Confirmation
Password confirmation is a common case and password provides an easy way to implement. The
other password component’s id should be used to define the _match_ option.

```xhtml
<p:password id="pwd1" value="#{passwordBean.password6}" feedback="false" match="pwd2" label="Password 1" required="true"/>
<p:password id="pwd2" value="#{passwordBean.password6}" feedback="false" label="Password 2" required="true"/>
```
## Skinning
Structural selectors for password are;

| Class | Applies | 
| --- | --- | 
.ui-password | Input element.
.ui-password-panel | Overlay panel of strength indicator.
.ui-password-meter | Strength meter.
.ui-password-info | Strength label.

As skinning style classes are global, see the main theming section for more information.

