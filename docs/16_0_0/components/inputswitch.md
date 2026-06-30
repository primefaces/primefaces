# InputSwitch

InputSwitch is used to select a boolean value.

## Getting started with InputSwitch
InputSwitch requires a boolean reference as the value.

```xhtml
<p:inputSwitch value="#{bean.propertyName}" />
```
```java
public class Bean {
    private boolean propertyName;
    //getter and setter
}
```
## Labels
Labels are customized using onLabel and offLabel options. Setting showLabels as false, turns off
labels altogether.

```xhtml
<p:inputSwitch value="#{bean.propertyName}" onLabel="yes" offLabel="no"/>
```
## Client Side API
Widget: _PrimeFaces.widget.InputSwitch_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
toggle() | - | void | Toggles the state.
check() | - | void | Switches to on state.
uncheck() | - | void | Switches to off state.
disable() | - | void | Disables the input field
enable() | - | void | Enables the input field

## Skinning
InputSwitch resides in a main container element which _style_ and _styleClass_ options apply.
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-inputswitch | Main container element.
.ui-inputswitch-off | Off state element.
.ui-inputswitch-on | On state element.
.ui-inputswitch-handle | Switch handle.

As skinning style classes are global, see the main theming section for more information.

