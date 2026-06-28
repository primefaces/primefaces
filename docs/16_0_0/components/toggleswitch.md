# ToggleSwitch

ToggleSwitch is used to select a boolean value.

> ToggleSwitch is designed to replace the old p:inputSwitch component.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.ToggleSwitch.html)

## Getting started with ToggleSwitch
ToggleSwitch requires a boolean reference as the value.

```xhtml
<p:toggleSwitch value="#{bean.propertyName}" />
```
```java
public class Bean {
    private boolean propertyName;
    //getter and setter
}
```

## Client Side API
Widget: _PrimeFaces.widget.ToggleSwitch_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
toggle() | - | void | Toggles the state.
check() | - | void | Switches to on state.
uncheck() | - | void | Switches to off state.
disable() | - | void | Disables the input field
enable() | - | void | Enables the input field 

## Skinning
ToggleSwitch resides in a main container element which _style_ and _styleClass_ options apply.
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-toggleswitch | Main container element.
.ui-toggleswitch-checked | Main container element when checked.
.ui-toggleswitch-slider | Background element where the handle slides.

As skinning style classes are global, see the main theming section for more information.

