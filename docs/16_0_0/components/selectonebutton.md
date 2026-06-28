# SelectOneButton

SelectOneButton is an input component to do a single select.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectOneButton-1.html)

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
