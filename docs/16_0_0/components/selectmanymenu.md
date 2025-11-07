# SelectManyMenu

SelectManyMenu is an extended version of the standard SelectManyMenu.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectManyMenu.html)

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
ariaDescribedBy | null | String | The aria-describedby attribute is used to define a component id that describes the current element for accessibility.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
caseSensitive | false | Boolean | Defines if filtering would be case sensitive.
collectionType | null | String | Optional attribute that is a literal string that is the fully qualified class name of a concrete class that implements `java.util.Collection` or an EL expression that evaluates to either 1. such a String, or 2. the `Class` object itself.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
converterMessage | null | String | Message to be displayed when conversion fails.
disabled | false | Boolean | Disables the component.
filter | false | Boolean | Displays an input filter for the list.
filterFunction | null | String | Client side function to use in custom filterMatchMode.
filterMatchMode | null | String | Match mode for filtering, valid values are startsWith (default), contains, endsWith and custom.
filterNormalize | false | Boolean | Defines if filtering would be done using normalized values (accents will be removed from characters).
hideNoSelectionOption | false | boolean  | Flag indicating that, if this component is activated by the user, The "no selection option", if any, must be hidden.
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
label | null | String | User presentable name.
metaKeySelection | true | Boolean | The meta key (SHIFT or CTRL) must be held down to multi-select items.
onchange | null | String | Callback to execute on value change.
onclick | null | String | Callback for click event.
ondblclick | null | String | Callback for dblclick event.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
required | false | Boolean | Marks component as required
requiredMessage | null | String | Message to be displayed when required field validation fails.
scrollHeight | null | Integer | Defines the height of the scrollable area
showCheckbox | false | Boolean | When true, a checkbox is displayed next to each item.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | null | String | Position of the input element in the tabbing order.
validator | null | MethodExpr | A method expression that refers to a method validating the input
validatorMessage | null | String | Message to be displayed when validation fields.
value | null | Object | Value of the component referring to a List.
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
var | null | String | Name of iterator to be used in custom content display.
widgetVar | null | String | Name of the client side widget.

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
Filtering is enabled by setting filter attribute to true. There are four filter modes; `startsWith`,
`contains`, `endsWith` and `custom`. In custom mode, `filterFunction` must be defined as the name of the
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
unselectAll() | - | void | Unselects all the options.
select(item) | item | void | Select the item.
unselect(item) | item | void | Unselect the item.
focus(item) | item | void | Focus the item.

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, dblclick, focus, itemSelect, itemUnselect, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, valueChange` 

| Event | Listener Parameter | Fired |
| --- | --- | --- |
itemSelect | org.primefaces.event.SelectEvent | When a item is added.
itemUnselect | org.primefaces.event.UnselectEvent | When a item is removed. 

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```

## Skinning
SelectManyMenu resides in a container that _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectmanymenu | Main container element.
.ui-selectlistbox-item | Each item in list.
