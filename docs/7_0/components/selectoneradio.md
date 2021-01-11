# SelectOneRadio

SelectOneRadio is an extended version of the standard SelectOneRadio with theme integration.

## Info

| Name | Value |
| --- | --- |
| Tag | selectOneRadio
| Component Class | org.primefaces.component.selectoneradio.SelectOneRadio
| Component Type | org.primefaces.component.SelectOneRadio
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectOneRadioRenderer
| Renderer Class | org.primefaces.component.selectoneradio.SelectOneRadioRenderer

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
layout | lineDirection | String | Layout of the radiobuttons, valid values are lineDirection , pageDirection , custom, responsive and grid.
columns | 0 | Integer | Number of columns in grid layout.
onchange | null | String | Callback to execute on value change.
onclick | null | String | Callback to execute on click of a radio.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | null | String | Specifies the tab order of element in tab navigation.
plain | false | Boolean | Plain mode displays radiobuttons using native browser rendering instead of themes.
unselectable | false | Boolean | Unselectable mode when true clicking a radio again will clear the selection. Default false.

## Getting started with SelectOneRadio
SelectOneRadio usage is same as the standard one.

## Custom Layout
Standard selectOneRadio component only supports horizontal and vertical rendering of the radio
buttons with a strict table markup. PrimeFaces SelectOneRadio on the other hand provides a
flexible layout option so that radio buttons can be located anywhere on the page. This is
implemented by setting layout option to custom and with standalone radioButton components. Note
that in custom mode, selectOneRadio itself does not render any output.

```xhtml
<p:selectOneRadio id="customRadio" value="#{formBean.option}" layout="custom">
    <f:selectItem itemLabel="Option 1" itemValue="1" />
    <f:selectItem itemLabel="Option 2" itemValue="2" />
    <f:selectItem itemLabel="Option 3" itemValue="3" />
</p:selectOneRadio>
```

```xhtml
<h:panelGrid columns="3">
    <p:radioButton id="opt1" for="customRadio" itemIndex="0"/>
    <h:outputLabel for="opt1" value="Option 1" />
    <p:spinner />
    <p:radioButton id="opt2" for="customRadio" itemIndex="1"/>
    <h:outputLabel for="opt2" value="Option 2" />
    <p:inputText />
    <p:radioButton id="opt3" for="customRadio" itemIndex="2"/>
    <h:outputLabel for="opt3" value="Option 3" />
    <p:calendar />
</h:panelGrid>
```
RadioButton’s for attribute should refer to a selectOneRadio component and itemIndex points to the
index of the selectItem. When using custom layout option, selectOneRadio component should be
placed above any radioButton that points to the selectOneRadio.

## Client Side API
Widget: _PrimeFaces.widget.SelectOneRadio_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
enable() | - | void | Enables component.
disable() | - | void | Disables component.

## Skinning
SelectOneRadio resides in a main container which _style_ and _styleClass_ attributes apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectoneradio | Main container element.
.ui-radiobutton | Container of a radio button.
.ui-radiobutton-box | Container of radio button icon.
.ui-radiobutton-icon | Radio button icon.
