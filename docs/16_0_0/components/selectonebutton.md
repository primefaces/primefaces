# SelectOneButton

SelectOneButton is an input component to do a single select.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectOneButton-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | selectOneButton
| Component Class | org.primefaces.component.selectonebutton.SelectOneButton
| Component Type | org.primefaces.component.SelectOneButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectOneButtonRenderer
| Renderer Class | org.primefaces.component.selectonebutton.SelectOneButtonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
converterMessage | null | String | Message to be displayed when conversion fails.
disabled | false | Boolean | Disables the component.
hideNoSelectionOption | false | boolean  | Flag indicating that, if this component is activated by the user, The "no selection option", if any, must be hidden.
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
label | null | String | User presentable name.
layout | null | String | Layout of the buttons, valid values are null (default) or "custom".
onchange | null | String | Callback to execute on value change.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
required | false | Boolean | Marks component as required
requiredMessage | null | String | Message to be displayed when required field validation fails.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | 0 | String | Position of the element in the tabbing order.
unselectable | true | Boolean | Whether selection can be cleared.
validator | null | MethodExpr | A method expression that refers to a method validating the input
validatorMessage | null | String | Message to be displayed when validation fields.
value | null | Object | Value of the component referring to a List.
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
widgetVar | null | String | Name of the client side widget.

## Getting started with SelectOneButton
SelectOneButton usage is same as selectOneRadio component, buttons just replace the radios.

## Custom Layout
SelectOneButton provides a flexible layout option so that buttons can be located anywhere on the page with custom styling.

There are two ways of using a custom layout. Referenced and via a facet named `custom`.
Note that the facet variant offers better accessibility.

### Referenced
This is implemented by setting layout option to custom. In custom mode, selectOneButton renders hidden inputs and custom buttons are placed as siblings in the parent container.

```xhtml
<p:selectOneButton id="customButton" value="#{buttonView.paymentMethod}" layout="custom">
    <f:selectItem itemLabel="PayPal" itemValue="PayPal"/>
    <f:selectItem itemLabel="Bitcoin" itemValue="Bitcoin"/>
    <f:selectItem itemLabel="Cash" itemValue="Cash"/>
</p:selectOneButton>

<div class="flex flex-wrap justify-content-start mt-3">
    <div class="payment-button paypal" role="radio" tabindex="0">
        <i class="pi pi-paypal payment-icon"></i>
        <div class="payment-title">PayPal</div>
        <div class="payment-tagline">Secure online payment</div>
    </div>
    <div class="payment-button bitcoin" role="radio" tabindex="0">
        <i class="pi pi-bitcoin payment-icon"></i>
        <div class="payment-title">Bitcoin</div>
        <div class="payment-tagline">Cryptocurrency payment</div>
    </div>
    <div class="payment-button cash" role="radio" tabindex="0">
        <i class="pi pi-money-bill payment-icon"></i>
        <div class="payment-title">Cash</div>
        <div class="payment-tagline">Pay on delivery</div>
    </div>
</div>
```

Custom buttons should have `role="radio"` attribute and be placed in the same parent container as the selectOneButton component. The order of buttons should match the order of selectItems.

### Facet

This is implemented by adding custom components to a facet named `custom`.

```xhtml
<p:selectOneButton id="customButton" value="#{buttonView.color}">
    <f:selectItem itemLabel="Red" itemValue="Red"/>
    <f:selectItem itemLabel="Green" itemValue="Green"/>
    <f:selectItem itemLabel="Blue" itemValue="Blue"/>

    <f:facet name="custom">
        <div class="custom-button" role="radio" tabindex="0">
            <span class="legend" style="background:red; display:inline-block; width:1rem; height:1rem; border-radius:0.25rem; vertical-align:middle; margin-right:0.5rem;"/> Red
        </div>
        <div class="custom-button" role="radio" tabindex="0">
            <span class="legend" style="background:green; display:inline-block; width:1rem; height:1rem; border-radius:0.25rem; vertical-align:middle; margin-right:0.5rem;"/> Green
        </div>
        <div class="custom-button" role="radio" tabindex="0">
            <span class="legend" style="background:blue; display:inline-block; width:1rem; height:1rem; border-radius:0.25rem; vertical-align:middle; margin-right:0.5rem;"/> Blue
        </div>
    </f:facet>
</p:selectOneButton>
```

Custom buttons should have `role="radio"` attribute and be placed within the custom facet. The order of buttons should match the order of selectItems. When a custom facet is present, the layout is automatically set to "custom".

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `change`  
**Available Events:** `change`  

```xhtml
<p:ajax event="change" listener="#{bean.handlechange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.SelectOneButton_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
enable() | - | void | Enables the component.
disabled() | - | void | Disables the component.

## Skinning
SelectOneButton resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectonebutton | Main container element.
