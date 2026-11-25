# Disabled Select Items

Select components (`SelectOneMenu`, `SelectOneRadio`, `SelectOneListbox`, `SelectOneButton`, `CascadeSelect`,
`MultiSelectListbox`, `SelectManyCheckbox`, `SelectManyMenu`, `SelectManyButton`, `SelectCheckboxMenu`) check the
submitted value against the select items on the server. A disabled item cannot be selected; how it is handled depends on
whether it is a regular item or a placeholder.

## Regular disabled item

A disabled item with a non-empty value can only be submitted by manipulating the request, or when the item was
disabled after the page was rendered (e.g. `itemDisabled="#{!item.available}"`).

- It is rejected with a validation error ("Value is not valid"), independent of `required`.
- The model value is not updated, the action is not invoked.
- If the current model value is a disabled item, it is kept when the form is submitted unchanged.
- An editable `SelectOneMenu` rejects an input which matches the label or value of a disabled item in the same way.

## Placeholder

A placeholder is an item with an empty value (`""` or `#{null}`), usually combined with `noSelectionOption="true"`
and optionally `itemDisabled="true"`. Leaving the placeholder selected submits an empty value, which is never treated
as a disabled item:

- `required="false"`: the model value is set to `null`.
- `required="true"`: the usual "Value is required" message.

```xml
<p:selectOneMenu value="#{bean.country}" required="true">
    <f:selectItem itemLabel="Please select" itemValue="#{null}" noSelectionOption="true" itemDisabled="true" />
    <f:selectItems value="#{bean.countries}" />
</p:selectOneMenu>
```

!> A disabled placeholder must use an empty value. A disabled placeholder with a non-empty value
(e.g. `itemValue="-1"`) is a regular disabled item and fails validation once it is submitted.
