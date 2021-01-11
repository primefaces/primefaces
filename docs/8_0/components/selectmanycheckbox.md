# SelectManyCheckbox

SelectManyCheckbox is an extended version of the standard SelectManyCheckbox.

## Info

| Name | Value |
| --- | --- |
| Tag | selectManyCheckbox
| Component Class | org.primefaces.component.selectmanycheckbox.SelectManyCheckbox
| Component Type | org.primefaces.component.SelectManyCheckbox
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectManyCheckboxRenderer
| Renderer Class | org.primefaces.component.selectmanycheckbox.SelectManyCheckboxRenderer

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
layout | lineDirection | String | Layout of the checkboxes, valid values are lineDirection , pageDirection, grid, responsive and custom.
columns | 0 | Integer | Number of columns in grid layout.
onchange | null | String | Callback to execute on value change.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | null | String | Position of the component in the tabbing order.

## Getting started with SelectManyCheckbox
SelectManyCheckbox usage is same as the standard one.

## Layouts
There are five layouts options for the checkboxes;

- **lineDirection** : Checkboxes are displayed horizontally
- **pageDirection** : Checkboxes are displayed vertically
- **grid** : Checkboxes are displayed in grid with given columns value.
- **responsive** : This is same as grid except, checkboxes are displayed vertically after a certain
    screen breakpoint.
- **custom** : In this mode, selectManyCheckbox leaves the layout to you using p:checkbox
    components. Next section provides more detail about this mode.

## Custom Layout
Custom layout is defined using the standalone checkbox components that match the selectItems of
the selectManyCheckbox by their itemIndex option. Note that in this mode selectManyCheckbox
does not render anything visible.

Following example uses customizable Grid CSS layout.


```xhtml
<p:selectManyCheckbox id="custom" value="#{checkboxView.selectedConsoles2}" layout="custom">
    <f:selectItem itemLabel="Xbox One" itemValue="Xbox One" />
    <f:selectItem itemLabel="PS4" itemValue="PS4" />
</p:selectManyCheckbox>
<div class="ui-grid ui-grid-responsive">
    <div class="ui-grid-row">
        <div class="ui-grid-col-6">
            <p:outputLabel for="opt1" value="Xbox One" style="display:block"/>
            <p:checkbox id="opt1" for="custom" itemIndex="0" />
        </div>
        <div class="ui-grid-col-6">
            <p:outputLabel for="opt2" value="PS4" style="display:block"/>
            <p:checkbox id="opt2" for="custom" itemIndex="1" />
        </div>
    </div>
</div>
```
## Skinning
SelectManyCheckbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectmanycheckbox | Main container element.
.ui-chkbox | Container of a checkbox.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.
