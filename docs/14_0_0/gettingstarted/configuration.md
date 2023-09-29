# Configuration

PrimeFaces does not require any mandatory configuration and follows configuration by exception
pattern of JavaEE. Here is the list of all configuration options defined with a context-param such as;

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>saga</param-value>
</context-param>
```


| Name | Default | Description |
| --- | --- | --- |
| CLIENT_SIDE_LOCALISATION | false | Adds `"locales/locale-" + locale.getLanguage() + ".js"` automatically for your locale. |
| CLIENT_SIDE_VALIDATION | false | Enables/disables global client side validation . |
| COOKIES_SAME_SITE | Strict | Defines the SameSite value for all cookies, which will be added by PrimeFaces. Only supported in Faces 4.0 or higher. |
| CSP | false | Enable Content Security Policy to prevent cross-site scripting (XSS), clickjacking and other code injection attacks. Values `true`, `false`, `reportOnly` and `policyProvided` |
| CSP_POLICY | null | Custom CSP Policy that allows you to allowlist sites that you need JavaScript from such as `script-src 'self' https: *.googleapis.com` |
| CSP_REPORT_ONLY_POLICY | null | When CSP is `reportOnly` this can be a directive for report only back to a URI endpoint like `report-uri /csp-violation-report-endpoint/`. |
| DIR | ltr | Defines orientation; 'ltr' or 'rtl' for right-to-left support. |
| EARLY_POST_PARAM_EVALUATION | false | Make p:ajax behave like f:ajax for queued AJAX requests. See: https://github.com/primefaces/primefaces/issues/109 |
| EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING | null | Comma separated list of exceptions for PrimeExceptionHandler to ignore e.g. `javax.faces.application.ViewExpiredException,javax.persistence.RollbackException`. |
| FLEX | false | Use PrimeFlex instead of Grid CSS in components with responsive-modes. (not implemented by all components yet) |
| HIDE_RESOURCE_VERSION | false | Determines whether to hide version information in resource paths. |
| HTML5_COMPLIANCE | false | Mark true if you know your site is HTML5 doctype so PF won't render certain non-HTML5 compliant values like `text/javascript` on scripts |
| INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES | false | Whether to load messages for the client side validation (CSV) from server via the MessageInterpolator. |
| LEGACY_WIDGET_NAMESPACE | false | Enables window scope so that widgets can be accessed using widgetVar.method() in addition to default PF namespace approach like PF('widgetVar').method(). |
| MARK_INPUT_AS_INVALID_ON_ERROR_MSG | false | Marks a input as invalid, when a FacesMessage is added for a UIInput with 'SEVERITY_ERROR'. This will show the red border on the client side, when the input is updated. |
| MOVE_SCRIPTS_TO_BOTTOM | false | Moves all inline scripts to end of body tag for better performance and smaller HTML output. |
| MULTI_VIEW_STATE_STORE | session | Store MultiViewState per Session ('session') or per ClientWindow ('client-window') |
| PRIME_ICONS | true | Auto includes PrimeIcons font based icons. True by default for most themes use PrimeIcons. Only disable if you know you do not use PrimeIcons. |
| RESET_VALUES | false | When enabled, AJAX updated inputs are always reset. |
| SUBMIT | full | Defines ajax submit mode; 'full' or 'partial'. |
| THEME | saga | Theme of the application. |
| TOUCHABLE | true | Globally enables/disables touch support on browsers that support touch. |
| TRANSFORM_METADATA | false | Transforms bean validation metadata to HTML attributes. |
