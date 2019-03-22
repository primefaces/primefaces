# SelectManyMenu

SelectManyMenu is an extended version of the standard SelectManyMenu.

## Info

| Name | Value |
| --- | --- |
| Tag | selectManyMenu
| Component Class | org.primefaces.component.selectmanymenu.SelectManyMenu
| Component Type | org.primefaces.component.SelectManyMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectManyMenuRenderer
| Renderer Class | org.primefaces.component.selectmanymenu.SelectManyMenuRenderer

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
onclick | null | String | Callback for click event.
ondblclick | null | String | Callback for dblclick event.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | null | String | Position of the input element in the tabbing order.
var | null | String | Name of iterator to be used in custom content display.
showCheckbox | false | Boolean | When true, a checkbox is displayed next to each item.
filter | false | Boolean | Displays an input filter for the list.
filterMatchMode | null | String | Match mode for filtering, valid values are startsWith (default), contains, endsWith and custom.
filterFunction | null | String | Client side function to use in custom filterMatchMode.
caseSensitive | false | Boolean | Defines if filtering would be case sensitive.
scrollHeight | null | Integer | Defines the height of the scrollable area

## Getting started with SelectManyMenu
SelectManyMenu usage is same as the standard one.

## Custom Content
Custom content can be displayed for each item using column components.


```xhtml
<p:selectManyMenu value="#{bean.selectedPlayers}" converter="player" var="p">
    <f:selectItems value="#{bean.players}" var="player" itemLabel="#{player.name}" itemValue="#{player}" />
    <p:column>
        <p:graphicImage value="/images/barca/#{p.photo}" width="40"/>
    </p:column>
    <p:column>
        #{p.name} - #{p.number}
    </p:column>
</p:selectManyMenu>
```
## Filtering
Filtering is enabled by setting filter attribute to true. There are four filter modes; _startsWith_ ,
_contains_ , _endsWith_ and _custom_. In custom mode, _filterFunction_ must be defined as the name of the
javascript function that takes the item value and filter as parameters to return a boolean to accept or
reject a value. To add a filter to previous example;

```xhtml
<p:selectManyMenu value="#{menuBean.selectedPlayer}" converter="player" var="p" filter="true" filterMatchMode="contains">
    ...
</p:selectManyMenu>
```
## Checkbox
SelectManyMenu has built-in support for checkbox based multiple selection, when enabled by
_showCheckbox_ option, checkboxes are displayed next to each column.


## Client Side API
Widget: _PrimeFaces.widget.SelectManyMenu_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
selectAll() | - | void | Selects all the options.

## Skinning
SelectManyMenu resides in a container that _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectmanymenu | Main container element.
.ui-selectlistbox-item | Each item in list.
