# ToggleSwitch

ToggleSwitch is used to select a boolean value.

> ToggleSwitch is designed to replace the old p:inputSwitch component.

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

## Confirmation

Attach `p:confirm` to ask before changing the value. Cancelling leaves the selection unchanged and does not send the change AJAX request. Confirming applies the selection and then runs the change listener. Both mouse and keyboard interactions support confirmation.

```xhtml
<p:toggleSwitch value="#{bean.value}">
    <p:ajax listener="#{bean.onChange}" update="messages" />
    <p:confirm message="Apply this change?" />
</p:toggleSwitch>

<p:confirmDialog global="true">
    <p:commandButton value="Yes" type="button" styleClass="ui-confirmdialog-yes" />
    <p:commandButton value="No" type="button" styleClass="ui-confirmdialog-no" />
</p:confirmDialog>
```

For a confirmation popup, use `type="popup"` on `p:confirm` and a global `p:confirmPopup` with `ui-confirm-popup-yes` and `ui-confirm-popup-no` buttons.
