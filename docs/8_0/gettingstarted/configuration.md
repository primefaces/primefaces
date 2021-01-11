# Configuration

PrimeFaces does not require any mandatory configuration and follows configuration by exception
pattern of JavaEE. Here is the list of all configuration options defined with a context-param such as;

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>nova-light</param-value>
</context-param>
```


| Name | Default | Description |
| --- | --- | --- |
| CLIENT_SIDE_VALIDATION | false | Enables/disables global client side validation . |
| CSP | false | Enable Content Security Policy to prevent cross-site scripting (XSS), clickjacking and other code injection attacks |
| CSP_POLICY | null | Custom CSP Policy that allows you to whitelist sites that you need JavaScript from such as `script-src 'self' https: *.googleapis.com` |
| DIR | ltr | Defines orientation; 'ltr' or 'rtl'. |
| EARLY_POST_PARAM_EVALUATION | false | Make p:ajax behave like f:ajax for queued AJAX requests. See: https://github.com/primefaces/primefaces/issues/109 |
| FONT_AWESOME | false | Auto includes Font-Awesome 4.7 icons. |
| INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES | false | Whether to load messages for the client side validation (CSV) from server via the MessageInterpolator. |
| LEGACY_WIDGET_NAMESPACE | false | Enables window scope so that widgets can be accessed using widgetVar.method() in addition to default PF namespace approach like PF('widgetVar').method(). |
| MOVE_SCRIPTS_TO_BOTTOM | false | Moves all inline scripts to end of body tag for better performance and smaller HTML output. |
| RESET_VALUES | false | When enabled, AJAX updated inputs are always reset. |
| SUBMIT | full | Defines ajax submit mode; 'full' or 'partial'. |
| THEME | aristo | Theme of the application. |
| TRANSFORM_METADATA | false | Transforms bean validation metadata to HTML attributes. |
| UPLOADER | auto | Defines uploader mode; 'auto', 'native' or 'commons'. 'auto' means 'native' on JSF2.2+, otherwise 'commons'. |