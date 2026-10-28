# SelectBooleanButton

SelectBooleanButton is used to select a binary decision with a toggle button.

## Getting started with SelectBooleanButton
SelectBooleanButton usage is similar to selectBooleanCheckbox.

```xhtml
<p:selectBooleanButton id="value2" value="#{bean.value}" onLabel="Yes" offLabel="No" onIcon="pi pi-check" offIcon="pi pi-times" />
```
```java
public class Bean {
    private boolean value;
    //getter and setter
}
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
.p-selectbooleanbutton | Main container element.

