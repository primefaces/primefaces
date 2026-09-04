# OutputLabel

OutputLabel is a an extension to the standard outputLabel component.

## Getting Started with OutputLabel
Usage is same as standard outputLabel, an input component is associated with for attribute.

```xhtml
<p:outputLabel for="input" value="Label" />
<p:inputText id="input" value="#{bean.text}" />
```

## Auto Label
OutputLabel sets its value as the label of the target component to be displayed in validation errors
so the target component does not need to define the label attribute again.

```xhtml
<h:outputLabel for="input" value="Field" />
<p:inputText id="input" value="#{bean.text}" label="Field"/>
```
can be rewritten as;

```xhtml
<p:outputLabel for="input" value="Field" />
<p:inputText id="input" value="#{bean.text}" />
```

## Support for Advanced Components
Some PrimeFaces input components like spinner, autocomplete does not render just basic inputs so
standard outputLabel component cannot apply focus to these, however PrimeFaces outputLabel can.

```xhtml
<h:outputLabel for="input" value="Can’t apply focus" />
<p:outputLabel for="input" value="Can apply focus" />
<p:spinner id="input" value="#{bean.text}" />
```

## Required Indicator (*)
When _indicateRequired_ is `auto` and the target input is required, or _indicateRequired_ is `true`, outputLabel displays * symbol next to the value.

In case of _indicateRequired_ is set to `auto` and the input is not marked as required, PrimeFaces will automatically check for @NotBlank/@NotEmpty.
PrimeFaces also checks for @NotNull, but only when `jakarta.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL` is set to `true`.

In case of _indicateRequired_ is set to `autoSkipDisabled` it is the same as `auto` except is will check the state of the 
input component if it is read-only or disabled.

## Validation failed
In case any validation fails on target input, the label will also be displayed with theme aware error styles.

## Skinning
Label renders a label element that _style_ and _styleClass_ attributes apply. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-outputlabel | Label element
.ui-state-error | Label element when input is invalid
.ui-outputlabel-rfi | Required field indicator.
