# Captcha

Captcha is a form validation component based on Recaptcha API V2.

## Getting Started with Captcha
Catpcha is implemented as an input component with a built-in validator that is integrated with
reCaptcha or hCaptcha. First thing to do is to sign up to reCaptcha/hCaptcha to get public&private keys. Once you have
the keys for your domain, add them to `web.xml` as follows;

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

**Google**
```xhtml
<p:captcha  type="g-recaptcha" />
```

**hCaptcha**
```xhtml
<p:captcha type="h-captcha" />
```

## Themes
Captcha features light and dark modes for theme, light is the default one.

```xhtml
<p:captcha theme="dark" type="g-recaptcha"/>
```
## Languages
Text instructions displayed on captcha is customized with the _language_ attribute. Below is a captcha
with Turkish text.

```xhtml
<p:captcha language="tr"  type="g-recaptcha"/>
```
## Overriding Validation Messages
By default captcha displays it’s own validation messages, this can be easily overridden by the Jakarta Faces
message bundle mechanism. Corresponding keys are;

| Description | Key |
| --- | --- |
| Summary | primefaces.captcha.INVALID |
| Detail | primefaces.captcha.INVALID_detail |

## Content Security Policy
If you use the PrimeFaces Content Security Policy protection you must add Google to the allow list Captcha to work properly:

```xml
<context-param>
    <param-name>primefaces.CSP_POLICY</param-name>
    <param-value>script-src 'self' https: *.googleapis.com</param-value>
</context-param>
```

## Tips

- Use label option to provide readable error messages in case validation fails.
- Enable _secure_ option to support https otherwise browsers will give warnings.
- See [https://developers.google.com/recaptcha/intro](https://developers.google.com/recaptcha/intro) to learn more about how reCaptcha works.
