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
| THEME | omega | Theme of the application. |
| SUBMIT | full | Defines ajax submit mode; 'full' or 'partial'. |
| DIR | ltr | Defines orientation; 'ltr' or 'rtl'. |
| RESET_VALUES | false | When enabled, AJAX updated inputs are always reseted. |
| CLIENT_SIDE_VALIDATION | false | Enables/disables global client side validation . |
| UPLOADER | auto | Defines uploader mode; 'auto', 'native' or 'commons'. 'auto' means 'native' on JSF2.2+, otherwise 'commons'. |
| TRANSFORM_METADATA | false | Transforms bean validation metadata to HTML attributes. |
| LEGACY_WIDGET_NAMESPACE | false | Enables window scope so that widgets can be accessed using widgetVar.method() in addition to default PF namespace approach like PF('widgetVar').method(). |
| FONT_AWESOME | false | Auto includes Font-Awesome 4.7. |
| INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES | false | Whether to load messages for the client side validation (CSV) from server via the MessageInterpolator. |
| MOVE_SCRIPTS_TO_BOTTOM | false | Moves all inline scripts to end of body tag for better performance and smaller html output. |