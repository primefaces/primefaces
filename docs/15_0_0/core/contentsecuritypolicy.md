# Content Security Policy

Content Security Policy (CSP) is a computer security standard introduced to prevent cross-site scripting (XSS),
clickjacking and other code injection attacks resulting from execution of malicious content in the trusted web
page context. It is widely supported by modern web browsers. CSP provides a standard method for website owners
to declare approved origins of content that browsers should be allowed to load on that website covered types
are JavaScript, CSS, HTML frames, web workers, fonts, images, embeddable objects such as Java applets, ActiveX,
audio and video files, and other HTML5 features.

PrimeFaces provides CSP support by simply enabling a context parameter

## Configuration

CSP is disabled by default and a global parameter is required to turn it on.

```xml
<context-param>
    <param-name>primefaces.CSP</param-name>
    <param-value>true</param-value>
</context-param>
```

In large web applications you might decide to report CSP violations only as a start instead of directly forcing them. 
Browsers will report CSP violations to a user-defined HTTP endpoint using `report-uri` directive.

```xml
<context-param>
    <param-name>primefaces.CSP</param-name>
    <param-value>reportOnly</param-value>
</context-param>
<context-param>
    <param-name>primefaces.CSP_REPORT_ONLY_POLICY</param-name>
    <param-value>report-uri /my-csp-reports; script-src 'self'</param-value>
</context-param>
```
⚠️ Note: primefaces.CSP_REPORT_ONLY_POLICY works the same way as primefaces.CSP_POLICY:
you can provide a complete CSP policy string containing the directives you need (for example, script-src, default-src, etc.).
If you do not provide a full policy, PrimeFaces will automatically apply a default policy of:

```
script-src 'self'
```
This ensures the report-only header always contains a valid directive, even if only report-uri is configured.

## Policy
There are many ways to configure CSP for different levels of security. Currently, PrimeFaces has chosen to
support the NONCE (number used once) based checking for script evaluation only. Nonce attributes are automatically
added to all script tags discovered by PrimeFaces. Nonce attributes are composed of base64 values and are verified
against the nonce sent in the CSP header, and only matching nonces are allowed to execute.

**HTTP Header**
```java
response.addHeader("Content-Security-Policy", "script-src 'self' 'nonce-YTQyM2ZiNTktNjFhZS00ZjI1LWEzMWItZGYzOTE0ZWQ1NDU1'");

```

**Script Output**
```xml
<script type="text/javascript"
        src="/showcase/jakarta.faces.resource/jquery/jquery.js.xhtml?ln=primefaces&amp;v=8.0"
        nonce="YTQyM2ZiNTktNjFhZS00ZjI1LWEzMWItZGYzOTE0ZWQ1NDU1" />
```
## Default Policy
The default policy is extremely strict and will only allow scripts from 'self' to execute and no inline
scripts or Javascript eval() statements are allowed.

```
script-src 'self' 'nonce-XYZ123456'
```

## Custom Policy
We cannot know every Javascript usage on every PrimeFaces website. You may have custom code in your
application that you need to allow other CSP directives such as `unsafe-inline` or allowlist a website
such as `https://www.google-analytics.com`.  By using the `primefaces.CSP_POLICY` context parameter you
can override the default policy. NOTE: If you use anything custom the `script-src` must be the last 
thing in CSP_POLICY because we add the NONCE on the end of this policy.

```xml
<context-param>
    <param-name>primefaces.CSP_POLICY</param-name>
    <param-value>frame-ancestors 'none'; script-src 'self' https://www.google-analytics.com</param-value>
</context-param>
```

## Google reCaptcha / Maps
If you use the PrimeFaces Captcha or Google Maps component you must use a custom policy that adds Google API usage as follows:

```xml
<context-param>
    <param-name>primefaces.CSP_POLICY</param-name>
    <param-value>frame-ancestors 'none'; script-src 'self' https: *.googleapis.com</param-value>
</context-param>
```

## PrimeFaces Extensions SunEditor
If you use the PrimeFaces Extensions SunEditor component you must add `blob:` for full functionality:

```xml
<context-param>
    <param-name>primefaces.CSP_POLICY</param-name>
    <param-value>script-src 'self' blob:</param-value>
</context-param>
```

## PrimeFaces Extensions Clipboard
If you use the PrimeFaces Extensions Clipboard component and want to use `url="javascript:void(0);"` to perform the copy you must add `unsafe-hashes`:

```xml
<context-param>
    <param-name>primefaces.CSP_POLICY</param-name>
    <param-value>script-src 'self' 'unsafe-hashes' 'sha256-kbHtQyYDQKz4SWMQ8OHVol3EC0t3tHEJFPCSwNG9NxQ='</param-value>
</context-param>
```

## Event Handlers
Inline code is considered harmful, especially inline event handlers. CSP solves this problem by banning inline
script entirely: it's the only way to be sure. This ban includes not only scripts embedded directly in `script` tags,
but also inline event handlers and `javascript:` URLs.  PrimeFaces handles inline events by preprocessing the HTML
before it is sent to the browser and converting inline event handlers to Jquery "on" event handlers.

For example:
```xml
<button id="btnHello" onclick="sayHello();">Say Hello</button>
```

Is automatically converted to:
```xml
<button id="btnHello">Say Hello</button>
```
```javascript
$('#btnHello').on('click', sayHello());
```

## Known Limitations
Currently CSP in combination with `<f:ajax>` cannot be used with all Faces implementations / versions.

MyFaces supports it since 2.3-next (which will be 4.0 in the future),
Mojarra doesn't support it in general: https://github.com/eclipse-ee4j/mojarra/issues/4542

As workaround, you can always use `<p:ajax>` instead.

Currently CSP in combination with `<h:commandLink onclick="">` cannot be used with all Faces implementations / versions.
This is due to the native jsf.js being used for the onClick which uses Javascript `new Function()` and is not allowed by CSP.

As workaround, you can always use `<p:commandLink>` instead.

## Troubleshooting

If you have enabled CSP on your PrimeFaces application and you find issues with your application,
it is recommended to use your browser Console (F12) to diagnose the issues. There is a possibility
you are using invalid code or Javascript on your site that is now being blocked.  For example,
the Javascript **eval()** function to execute arbitrary code.

It is also possible that you have detected an issue with one of the Javascript plugins that PrimeFaces
uses.  If this is the case, please report it to the GitHub Issues page.

## Policy Provided (Liferay Portlets)
!> Caution: Using `policyProvided` is not recommended for most users and has a special use case for Liferay Portlets as it does not support CSP.

See: https://github.com/primefaces/primefaces/issues/12812#issuecomment-2505572989
When using PrimeFaces in a component (like portlets for instance), the platform hosting the component might need to
manage the CSP headers and nonce on scripts. For instance in a CMS where different kinds of components can be used
and all need to have the same nonce. In these cases the option `policyProvided` can be used. This will make
PrimeFaces CSP compliant, while not setting the CSP Header and add the nonce to JavaScripts.

```xml
<context-param>
    <param-name>primefaces.CSP</param-name>
    <param-value>policyProvided</param-value>
</context-param>
```