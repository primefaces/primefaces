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

## Customizable Icons
The state icons are icon tokens of the theme (see [Theming](/core/theming.md?id=icons)): `--p-checkbox-checked-icon`
(`true`) and `--p-checkbox-crossed-icon` (`false`), `null` shows no icon. Set them in `:root, [data-p-scope]` for all checkboxes or on a
style class for some:

```css
.custom-icons-tristate {
    --p-checkbox-checked-icon: var(--p-icon-plus);
    --p-checkbox-crossed-icon: var(--p-icon-minus);
}
```

```xhtml
<p:triStateCheckbox value="#{bean.value}" styleClass="custom-icons-tristate"/>
```

The icon is rendered as `<span class="p-chkbox-icon p-icon p-checkbox-icon">` plus the state class
`p-checkbox-checked-icon` or `p-checkbox-crossed-icon`.

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
.p-chkbox | Main container element.
.p-chkbox-box | Container of checkbox icon.
.p-chkbox-icon | Checkbox icon.
.p-chkbox-label | Checkbox label.
