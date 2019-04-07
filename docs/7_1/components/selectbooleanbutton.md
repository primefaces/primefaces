# SelectBooleanButton

SelectBooleanButton is used to select a binary decision with a toggle button.

## Info

| Name | Value |
| --- | --- |
| Tag | selectBooleanButton
| Component Class | org.primefaces.component.selectbooleanbutton.SelectBooleanButton
| Component Type | org.primefaces.component.SelectBooleanButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectBooleanButtonRenderer
| Renderer Class | org.primefaces.component.selectbooleanbutton.SelectBooleanButtonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component referring to a List.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Disables the component.
label | null | String | User presentable name.
onchange | null | String | Callback to execute on value change.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
onLabel | null | String | Label to display when button is selected.
offLabel | null | String | Label to display when button is unselected.
onIcon | null | String | Icon to display when button is selected.
offIcon | null | String | Icon to display when button is unselected.
tabindex | 0 | String | Position of the element in the tabbing order.
title | null | String | Advisory tooltip information.
onfocus | null | String | Client side callback to execute when button receives focus.
onblur | null | String | Client side callback to execute when button loses focus.

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
## Skinning
SelectBooleanButton resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectbooleanbutton | Main container element.

