# OutputLabel

OutputLabel is a an extension to the standard outputLabel component.

## Info

| Name | Value |
| --- | --- |
| Tag | outputLabel
| Component Class | org.primefaces.component.outputlabel.OutputLabel
| Component Type | org.primefaces.component.OutputLabel
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.OutputLabelRenderer
| Renderer Class | org.primefaces.component.outputlabel.OutputLabelRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | String | Label to display.
accesskey | null | String | The accesskey attribute is a standard HTML attribute that sets the access key that transfers focus to this element when pressed.
dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
escape | true | Boolean | Defines if value should be escaped or not.
for | null | String | Component to attach the label to.
tabindex | null | String | Position in tabbing order.
title | null | String | Advisory tooltip information.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
onblur | null | String | Client side callback to execute when component loses focus.
onclick | null | String | Client side callback to execute when component is clicked.
ondblclick | null | String | Client side callback to execute when component is double clicked.
onfocus | null | String | Client side callback to execute when component receives focus.
onkeydown | null | String | Client side callback to execute when a key is pressed down over component.
onkeypress | null | String | Client side callback to execute when a key is pressed and released over component.
onkeyup | null | String | Client side callback to execute when a key is released over component.
onmousedown | null | String | Client side callback to execute when a pointer is pressed down over component.
onmouseout | null | String | Client side callback to execute when a pointer is moved away from component.
onmouseover | null | String | Client side callback to execute when a pointer is moved onto component.
onmouseup | null | String | Client side callback to execute when a pointer is released over component.
indicateRequired | auto | String | auto, true or false. Displays * symbol if the input is required.

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
PrimeFaces also checks for @NotNull, but only when `javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL` is set to `true`.

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
