
# InputText

InputText is an extension to standard inputText with skinning capabilities.

## Getting Started with InputText
InputText usage is same as standard inputText;

```xhtml
<p:inputText value="#{bean.propertyName}" />
```
```java
public class Bean {
    private String propertyName;
    //getter and setter
}
```
## Client Side API
Widget: _PrimeFaces.widget.InputText_

| Method | Params | Return Type | Description |
| --- | --- |-------------| --- |
enable() | - | void        | Enables the input field.
disable() | - | void        | Disables the input field.
getValue() | - | string      | Gets the current value.
