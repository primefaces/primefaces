# SelectManyButton

SelectManyButton is a multi select component using button UI.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectManyButton.html)

## Info

| Name | Value |
| --- | --- |
| Tag | selectManyButton
| Component Class | org.primefaces.component.selectmanybutton.SelectManyButton
| Component Type | org.primefaces.component.SelectManyButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectManyButton
| Renderer Class | org.primefaces.component.selectmanybutton.SelectManyButton

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
layout | null | String | Layout of the buttons, valid values are custom or default.
onchange | null | String | Callback to execute on value change.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
hideNoSelectionOption | false | boolean  | Flag indicating that, if this component is activated by the user, The "no selection option", if any, must be hidden.

## Getting started with SelectManyButton
SelectManyButton usage is same as selectManyCheckbox, buttons just replace checkboxes.

## Custom Layout
SelectManyButton provides a flexible layout option so that buttons can be located anywhere on the page with custom styling.

There are two ways of using a custom layout. Referenced and via a facet named `custom`.
Note that the facet variant offers better accessibility.

### Referenced
This is implemented by setting layout option to custom. In custom mode, selectManyButton renders hidden inputs and custom buttons are placed as siblings in the parent container.

```xhtml
<p:selectManyButton id="customButton" value="#{buttonView.selectedDevices}" layout="custom">
    <f:selectItem itemLabel="Mobile" itemValue="Mobile"/>
    <f:selectItem itemLabel="Tablet" itemValue="Tablet"/>
    <f:selectItem itemLabel="Desktop" itemValue="Desktop"/>
</p:selectManyButton>

<div class="flex flex-wrap justify-content-start mt-3">
    <div class="feature-button mobile" role="checkbox" tabindex="0">
        <i class="pi pi-mobile feature-icon"></i>
        <div class="feature-title">Mobile</div>
        <div class="feature-tagline">Responsive mobile design</div>
    </div>
    <div class="feature-button tablet" role="checkbox" tabindex="0">
        <i class="pi pi-tablet feature-icon"></i>
        <div class="feature-title">Tablet</div>
        <div class="feature-tagline">Tablet optimized view</div>
    </div>
    <div class="feature-button desktop" role="checkbox" tabindex="0">
        <i class="pi pi-desktop feature-icon"></i>
        <div class="feature-title">Desktop</div>
        <div class="feature-tagline">Full desktop experience</div>
    </div>
</div>
```

Custom buttons should have `role="checkbox"` attribute and be placed in the same parent container as the selectManyButton component. The order of buttons should match the order of selectItems.

### Facet

This is implemented by adding custom components to a facet named `custom`.

```xhtml
<p:selectManyButton id="customButton" value="#{buttonView.selectedColors}">
    <f:selectItem itemLabel="Red" itemValue="Red"/>
    <f:selectItem itemLabel="Green" itemValue="Green"/>
    <f:selectItem itemLabel="Blue" itemValue="Blue"/>

    <f:facet name="custom">
        <div class="custom-button" role="checkbox" tabindex="0">
            <span class="legend" style="background:red; display:inline-block; width:1rem; height:1rem; border-radius:0.25rem; vertical-align:middle; margin-right:0.5rem;"/> Red
        </div>
        <div class="custom-button" role="checkbox" tabindex="0">
            <span class="legend" style="background:green; display:inline-block; width:1rem; height:1rem; border-radius:0.25rem; vertical-align:middle; margin-right:0.5rem;"/> Green
        </div>
        <div class="custom-button" role="checkbox" tabindex="0">
            <span class="legend" style="background:blue; display:inline-block; width:1rem; height:1rem; border-radius:0.25rem; vertical-align:middle; margin-right:0.5rem;"/> Blue
        </div>
    </f:facet>
</p:selectManyButton>
```

Custom buttons should have `role="checkbox"` attribute and be placed within the custom facet. The order of buttons should match the order of selectItems. When a custom facet is present, the layout is automatically set to "custom".

## Client Side API
Widget: _PrimeFaces.widget.SelectManyButton_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |

## Skinning
SelectManyButton resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectmanybutton | Main container element.
