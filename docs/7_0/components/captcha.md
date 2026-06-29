# Captcha

Captcha is a form validation component based on Recaptcha API V2.

## Info

| Name | Value |
| --- | --- |
| Tag | captcha
| Component Class | org.primefaces.component.captcha.Captcha
| Component Type | org.primefaces.component.Captcha
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CaptchaRenderer
| Renderer Class | org.primefaces.component.captcha.CaptchaRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| value | null | Object | Value of the component than can be either an EL expression of a literal text.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| required | false | Boolean | Marks component as required.
| validator | null | MethodExpr | A method binding expression that refers to a method validationg the input. 
| valueChangeListener | null | ValueChange Listener | A method binding expression that refers to a method for handling a valuchangeevent.
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| converterMessage | null | String | Message to be displayed when conversion fails.
| validatorMessage | null | String | Message to be displayed when validation fields.
| publicKey | null | String | Public recaptcha key for a specific domain (deprecated)
| theme | red | String | Theme of the captcha.
| language | en | String | Key of the supported languages.
| tabindex | null | Integer | Position of the input element in the tabbing order.
| label | null | String | User presentable field name.
| callback | null | String | The name of your callback function to be executed when the user submits a successful CAPTCHA response. The user's response, g-recaptcha-response, will be the input for your callback function.
| expired | null | String | Callback executed when the captcha response expires and the user needs to solve a new captcha.

## Getting Started with Captcha
Catpcha is implemented as an input component with a built-in validator that is integrated with
reCaptcha. First thing to do is to sign up to reCaptcha to get public&private keys. Once you have
the keys for your domain, add them to web.xml as follows;

```xml
<context-param>
    <param-name>primefaces.PRIVATE_CAPTCHA_KEY</param-name>
    <param-value>YOUR_PRIVATE_KEY</param-value>
</context-param>
<context-param>
    <param-name>primefaces.PUBLIC_CAPTCHA_KEY</param-name>
    <param-value>YOUR_PUBLIC_KEY</param-value>
</context-param>
```
That is it, now you can use captcha as follows;

```xhtml
<p:captcha />
```
## Themes
Captcha features light and dark modes for theme, light is the default one.

```xhtml
<p:captcha theme="dark"/>
```
## Languages
Text instructions displayed on captcha is customized with the _language_ attribute. Below is a captcha
with Turkish text.

```xhtml
<p:captcha language="tr"/>
```
## Overriding Validation Messages
By default captcha displays it’s own validation messages, this can be easily overridden by the JSF
message bundle mechanism. Corresponding keys are;

| Description | Key |
| --- | --- |
| Summary | primefaces.captcha.INVALID |
| Detail | primefaces.captcha.INVALID_detail |

## Tips

- Use label option to provide readable error messages in case validation fails.
- Enable _secure_ option to support https otherwise browsers will give warnings.
- See [https://developers.google.com/recaptcha/intro](https://developers.google.com/recaptcha/intro) to learn more about how reCaptcha works.
