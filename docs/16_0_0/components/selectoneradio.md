# SelectOneRadio

SelectOneRadio is an extended version of the standard SelectOneRadio with theme integration.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectOneRadio.html)

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
validator | null | MethodExpr | A method expression that refers to a method validating the input
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Disables the component.
label | null | String | User presentable name. 
layout | lineDirection | String | Layout of the radiobuttons, valid values are lineDirection , pageDirection , custom, and responsive.
flex | false | Boolean | Use modern PrimeFlex-Grid in responsive mode instead of classic Grid CSS. (primeflex.css must be included into the template.xhtml)
columns | 12 | Integer | Number of columns in responsive layout.
onchange | null | String | Callback to execute on value change.
onclick | null | String | Callback to execute on click of a radio.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | null | String | Specifies the tab order of element in tab navigation.
unselectable | false | Boolean | Unselectable mode when true clicking a radio again will clear the selection. Default false.
hideNoSelectionOption | false | Boolean  | Flag indicating that, if this component is activated by the user, The "no selection option", if any, must be hidden.
readonly | false | Boolean | Flag indicating that this input element will prevent changes by the user.
columnClasses | null | String | Comma separated list of column style classes.
ariaDescribedBy | null | String | The aria-describedby attribute is used to define a component id that describes the current element for accessibility.

## Getting started with SelectOneRadio
SelectOneRadio usage is same as the standard one.

## Custom Layout
Standard selectOneRadio component only supports horizontal and vertical rendering of the radio
buttons with a strict table markup. PrimeFaces SelectOneRadio on the other hand provides a
flexible layout option so that radio buttons can be located anywhere on the page.

There are two ways of using a custom layout. Referenced and via a facet named `custom`.
Note that the facet variant offers better accessibility.

### Referenced
This is implemented by setting layout option to custom and with standalone radioButton components. Note
that in custom mode, selectOneRadio itself does not render any output.

```xhtml
<p:selectOneRadio id="customRadio" value="#{formBean.option}" layout="custom">
    <f:selectItem itemLabel="Option 1" itemValue="1" />
    <f:selectItem itemLabel="Option 2" itemValue="2" />
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
</h:panelGrid>
```

RadioButton’s for attribute should refer to a selectOneRadio component and itemIndex points to the
index of the selectItem. When using custom layout option, selectOneRadio component should be
placed above any radioButton that points to the selectOneRadio.

### Facet

This is implemented by setting adding custom components to a facet named `custom`.

```xhtml
<p:selectOneRadio id="customRadio" value="#{radioView.color}" label="Color">
    <f:selectItem itemLabel="Red" itemValue="Red"/>
    <f:selectItem itemLabel="Green" itemValue="Green"/>

    <f:facet name="custom">
        <div class="p-field-radiobutton" role="radio">
            <p:radioButton id="opt1" for="customRadio" itemIndex="0"/>
            <p:outputLabel for="opt1">
                <span class="legend" style="background:red"/> Red
            </p:outputLabel>
        </div>
        <div class="p-field-radiobutton" role="radio">
            <p:radioButton id="opt2" for="customRadio" itemIndex="1"/>
            <p:outputLabel for="opt2">
                <span class="legend" style="background:green"/> Green
            </p:outputLabel>
        </div>
    </f:facet>
</p:selectOneRadio>
```

RadioButton’s for attribute should refer to a selectOneRadio component and itemIndex points to the
index of the selectItem.

For the better accessibility support, you might want to wrap each custom radio button in an element
with a `role="radio"` attribute.

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `change`  
**Available Events:** `change`  

```xhtml
<p:ajax event="change" listener="#{bean.handlechange}" update="msgs" />
```

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
