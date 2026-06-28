
# InputText

InputText is an extension to standard inputText with skinning capabilities.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.InputText.html)

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

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, wheel`  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```