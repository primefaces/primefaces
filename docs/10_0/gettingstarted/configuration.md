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
| COOKIES_SAME_SITE |  | Defines the sameSite value for all cookies, which will be added by PrimeFaces. Currently this is only supported for cookies added on the clientside as Servlet API doesn't support it yet. |
| CSP | false | Enable Content Security Policy to prevent cross-site scripting (XSS), clickjacking and other code injection attacks |
| CSP_POLICY | null | Custom CSP Policy that allows you to whitelist sites that you need JavaScript from such as `script-src 'self' https: *.googleapis.com` |
| DIR | ltr | Defines orientation; 'ltr' or 'rtl' for right-to-left support. |
| EARLY_POST_PARAM_EVALUATION | false | Make p:ajax behave like f:ajax for queued AJAX requests. See: https://github.com/primefaces/primefaces/issues/109 |
| EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING | null | Comma separated list of exceptions for PrimeExceptionHandler to ignore e.g. `javax.faces.application.ViewExpiredException,javax.persistence.RollbackException`. |
| FONT_AWESOME | false | Auto includes Font-Awesome 4.7 icons. |
| FLEX | false | Use PrimeFlex instead of Grid CSS in components with responsive-modes. (not implemented by all components yet) |
| INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES | false | Whether to load messages for the client side validation (CSV) from server via the MessageInterpolator. |
| LEGACY_WIDGET_NAMESPACE | false | Enables window scope so that widgets can be accessed using widgetVar.method() in addition to default PF namespace approach like PF('widgetVar').method(). |
| MARK_INPUT_AS_INVALID_ON_ERROR_MSG | false | Marks a input as invalid, when a FacesMessage is added for a UIInput with 'SEVERITY_ERROR'. This will show the red border on the client side, when the input is updated. |
| MOVE_SCRIPTS_TO_BOTTOM | false | Moves all inline scripts to end of body tag for better performance and smaller HTML output. |
| MULTI_VIEW_STATE_STORE | session | Store MultiViewState per Session ('session') or per ClientWindow ('client-window') |
| RESET_VALUES | false | When enabled, AJAX updated inputs are always reset. |
| SUBMIT | full | Defines ajax submit mode; 'full' or 'partial'. |
| THEME | nova-light | Theme of the application. |
| TOUCHABLE | true | Globally enables/disables touch support on browsers that support touch. |
| TRANSFORM_METADATA | false | Transforms bean validation metadata to HTML attributes. |
| UPLOADER | auto | Defines uploader mode; 'auto', 'native' or 'commons'. 'auto' means 'native' on JSF2.2+, otherwise 'commons'. |
