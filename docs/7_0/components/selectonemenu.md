# SelectOneMenu

SelectOneMenu is an extended version of the standard SelectOneMenu.

## Info

| Name | Value |
| --- | --- |
| Tag | selectOneMenu
| Component Class | org.primefaces.component.selectonemenu.SelectOneMenu
| Component Type | org.primefaces.component.SelectOneMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectOneMenuRenderer
| Renderer Class | org.primefaces.component.selectonemenu.SelectOneMenuRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
effect | blind | String | Name of the toggle animation.
effectSpeed | normal | String | Duration of toggle animation, valid values are "slow", "normal" and "fast".
disabled | false | Boolean | Disables the component.
label | null | String | User presentable name.
onchange | null | String | Client side callback to execute on value change.
onkeyup | null | String | Client side callback to execute on keyup.
onkeydown | null | String | Client side callback to execute on keydown.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
var | null | String | Name of the item iterator.
height | auto | Integer | Height of the overlay.
tabindex | null | String | Tabindex of the input.
editable | false | Boolean | When true, input becomes editable.
filter | false | Boolean | Renders an input field as a filter.
filterMatchMode | startsWith | String | Match mode for filtering, valid values are startsWith, contains, endsWith and custom.
filterFunction | null | String | Client side function to use in custom filtering.
caseSensitive | false | Boolean | Defines if filtering would be case sensitive.
maxlength | null | Integer | Number of maximum characters allowed in editable selectOneMenu.
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
title | null | String | Advisory tooltip information.
syncTooltip | false | Boolean | Updates the title of the component with the description of the selected item.
labelTemplate | null | String | Displays label of the element in a custom template. Valid placeholder is {0}.
onfocus | null | String | Client side callback to execute when element receives focus.
onblur | null | String | Client side callback to execute when element loses focus.
autoWidth | true | Boolean | Calculates a fixed width based on the width of the maximum option label. Set to false for custom width.
dynamic | false | Boolean | Defines if dynamic loading is enabled for the element's panel. If the value is "true", the overlay is not rendered on page load to improve performance.

## Getting started with SelectOneMenu
Basic SelectOneMenu usage is same as the standard one.

## Custom Content
SelectOneMenu can display custom content in overlay panel by using column component and the
var option to refer to each item.

```java
public class MenuBean {
    private List<Player> players;
    private Player selectedPlayer;

    public OrderListBean() {
        players = new ArrayList<Player>();
        players.add(new Player("Messi", 10, "messi.jpg"));
        //more players
    }
    //getters and setters
}
```
```xhtml
<p:selectOneMenu value="#{menuBean.selectedPlayer}" converter="player" var="p">
    <f:selectItem itemLabel="Select One" itemValue="" />
    <f:selectItems value="#{menuBean.players}" var="player" itemLabel="#{player.name}" itemValue="#{player}"/>
    <p:column>
        <p:graphicImage value="/images/barca/#{p.photo}" width="40" height="50"/>
    </p:column>
    <p:column>
        #{p.name} - #{p.number}
    </p:column>
</p:selectOneMenu>
```

## Effects
An animation is executed to show and hide the overlay menu, default effect is fade and following
options are available for _effect_ attribute; blind, bounce, clip, drop, explode, fold, highlight, puff,
pulsate, scale, shake, size, slide and none.

## Editable
Editable SelectOneMenu provides a UI to either choose from the predefined options or enter a
manual input. Set editable option to true to use this feature.

## Filtering
When filtering is enabled with setting _filter_ on, an input field is rendered at overlay header and on
keyup event filtering is executed on client side using _filterMatchMode_. Default modes of
filterMatchMode are startsWith, contains, endsWith and custom. Custom mode requires a javascript
function to do the filtering.

```xhtml
<p:selectOneMenu value="#{bean.selectedOptions}" filterMatchMode="custom" filterFunction="customFilter">
    <f:selectItems value="#{bean.options}" />
</p:selectOneMenu>
```
```js
function customFilter(itemLabel, filterValue) {
    //return true to accept and false to reject
}
```

## Ajax Behavior Events
In addition to the standard events like "change", custom "itemSelect" event is also available to
invoke when an item is selected from dropdown.

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specific the default event is called.  
In addition to the standard events like "change", custom "itemSelect" event is also available to invoke when an item is selected from dropdown.  
  
**Default Event:** valueChange  
**Available Events:** blur, change, click, dblclick, focus, itemSelect, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, select, valueChange  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.SelectOneMenu_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows overlay menu.
hide() | - | void | Hides overlay menu.
blur() | - | void | Invokes blur event.
focus() | - | void | Invokes focus event.
enable() | - | void | Enables component.
disable() | - | void | Disabled component.
selectValue(value) | value: itemValue | void | Selects given value.
getSelectedValue() | - | Object | Returns value of selected item.
getSelectedLabel() | - | String | Returns label of selected item.

## Skinning
SelectOneMenu resides in a container element that _style_ and _styleClass_ attributes apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;


| Class | Applies | 
| --- | --- | 
.ui-selectonemenu | Main container.
.ui-selectonemenu-label | Label of the component.
.ui-selectonemenu-trigger | Container of dropdown icon.
.ui-selectonemenu-items | Items list.
.ui-selectonemenu-items | Each item in the list.

