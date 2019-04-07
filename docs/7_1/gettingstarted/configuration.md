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
| SUBMIT | full | Defines ajax submit mode, full or partial. |
| DIR | ltr | Defines orientation, ltr or rtl. |
| RESET_VALUES | false | When enabled, ajax updated inputs are reset. |
| CLIENT_SIDE_VALIDATION | false | Controls client side validatation. |
| UPLOADER | auto | Defines uploader mode; auto , native or commons. |
| TRANSFORM_METADATA | false | Transforms bean validation metadata to html attributes. |
| LEGACY_WIDGET_NAMESPACE | false | Enables window scope so that widgets can be accessed using widgetVar.method() in addition to default PF namespace approach like PF('widgetVar').method(). |
| FONT_AWESOME | false | Enabled font-awesome icons. |
| INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES | false | Whether to load the CSV messages from server. |
| MOVE_SCRIPTS_TO_BOTTOM | false | Moves all inline scripts to end of body tag for better performance and smaller html output. |