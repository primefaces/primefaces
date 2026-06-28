# SelectBooleanButton

SelectBooleanButton is used to select a binary decision with a toggle button.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectBooleanButton.html)

## Getting started with SelectBooleanButton
SelectBooleanButton usage is similar to selectBooleanCheckbox.

```xhtml
<p:selectBooleanButton id="value2" value="#{bean.value}" onLabel="Yes" offLabel="No" onIcon="ui-icon-check" offIcon="ui-icon-close" />
```
```java
public class Bean {
    private boolean value;
    //getter and setter
}
```

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, dblclick, focus, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, select, valueChange`  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.SelectBooleanButton_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |

## Skinning
SelectBooleanButton resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectbooleanbutton | Main container element.

