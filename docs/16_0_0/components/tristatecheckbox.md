# TriStateCheckbox

TriStateCheckbox adds a new state to a checkbox value.

## Getting started with TriStateCheckbox
TriStateCheckbox supports three states: null (unchecked), true (checked), and false (indeterminate).

### Using Boolean values

```xhtml
<p:triStateCheckbox value="#{bean.booleanValue}"/>
```

```java
public class Bean {
    private Boolean booleanValue; // Can be true, false, or null
    //getter-setter
}
```

## Value Handling
The TriStateCheckbox component automatically handles conversion between String and Boolean values:

- **Boolean values**: Used directly as `Boolean.TRUE`, `Boolean.FALSE`, or `null`
- **Three states**: 
  - `null` - Unchecked state (state one)
  - `true` - Checked state (state two) 
  - `false` - Indeterminate state (state three)


## Client Side API
Widget: _PrimeFaces.widget.TriStateCheckbox_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
toggle() | - | void | Switches to next state.
disable() | - | void | Disables the input field
enable() | - | void | Enables the input field

## Skinning
TriStateCheckbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies |
| --- | --- |
.ui-chkbox | Main container element.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.
.ui-chkbox-label | Checkbox label.
