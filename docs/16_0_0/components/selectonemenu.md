# SelectOneMenu

SelectOneMenu is an extended version of the standard SelectOneMenu.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectOneMenu-1.html)

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
alwaysDisplayLabel | false | Boolean | Always display the `label` value instead of the selected item label.
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
ariaDescribedBy | null | String | The aria-describedby attribute is used to define a component id that describes the current element for accessibility.
autocomplete | null | String | Controls browser autocomplete behavior for editable input field
autoWidth | auto | String | Calculates a fixed width based on the width of the maximum option label. If the value is "auto", it's only calculated when its not placed inside a ui-fluid and no width was specified on the component. "false" = never calculate, "true" = always calculate.
caseSensitive | false | Boolean | Defines if filtering would be case sensitive.
converterMessage | null | String | Message to be displayed when conversion fails.
dir | ltr | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
disabled | false | Boolean | Disables the component.
dynamic | false | Boolean | Defines if dynamic loading is enabled for the element's panel. If the value is "true", the overlay is not rendered on page load to improve performance.
editable | false | Boolean | When true, input becomes editable.
filter | false | Boolean | Renders an input field as a filter.
filterFunction | null | String | Client side function to use in custom filtering.
filterMatchMode | startsWith | String | Match mode for filtering, valid values are startsWith, contains, endsWith and custom.
filterNormalize | false | Boolean | Defines if filtering would be done using normalized values (accents will be removed from characters).
filterPlaceholder | null | String | Watermark displayed in the filter input field before the user enters a value.
height | auto | Integer | Height of the overlay.
hideNoSelectionOption | false | boolean  | Flag indicating that, if this component is activated by the user, The "no selection option", if any, must be hidden.
label | null | String | User presentable name used in conjuction with `alwaysDisplayLabel` to display instead of selected item.
labelTemplate | null | String | Displays label of the element in a custom template. Valid placeholder is {0}.
maxlength | null | Integer | Number of maximum characters allowed in editable selectOneMenu.
onblur | null | String | Client side callback to execute when element loses focus.
onchange | null | String | Client side callback to execute on value change.
onfocus | null | String | Client side callback to execute when element receives focus.
onkeydown | null | String | Client side callback to execute on keydown.
onkeyup | null | String | Client side callback to execute on keyup.
panelStyle | null | String | Style of the dropdown panel container element.
panelStyleClass | null | String | Style  class of the dropdown panel container element.
requiredMessage | null | String | Message to be displayed when required field validation fails.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
syncTooltip | false | Boolean | Updates the title of the component with the description of the selected item.
tabindex | null | String | Tabindex of the input.
title | null | String | Advisory tooltip information.
touchable | null | Boolean | Enable touch support (if the browser supports it). Default is the global primefaces.TOUCHABLE, which can be overwritten on component level.
validator | null | MethodExpr | A method expression that refers to a method validating the input
validatorMessage | null | String | Message to be displayed when validation fields.
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
var | null | String | Name of the item iterator.
widgetVar | null | String | Name of the client side widget.

## Getting started with SelectOneMenu
Basic SelectOneMenu usage is same as the standard one.

## Custom Content
SelectOneMenu can display custom content in overlay panel by using column component and the
var option to refer to each item. Facets for column _header_ and overall _footer_ may also be used.

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
        <f:facet name="header">
             <h:outputText value="Player"/>
         </f:facet>
    </p:column>

    <f:facet name="footer">
         <p:divider />
         <h:outputText value="#{menuBean.players.size()} available players" style="font-weight:bold;"/>
    </f:facet>
</p:selectOneMenu>
```

## Editable
Editable SelectOneMenu provides a UI to either choose from the predefined options or enter a
manual input. Set editable option to true to use this feature.

## Filtering
When filtering is enabled with setting `filter` on, an input field is rendered at overlay header and on
keyup event filtering is executed on client side using `filterMatchMode`. Default modes of
`filterMatchMode` are `startsWith`, `contains`, `endsWith` and `custom`. Custom mode requires a javascript
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

The following AJAX behavior events are available for this component. If no event is specific the default event is called.  
In addition to the standard events like "change", custom "itemSelect" event is also available to invoke when an item is selected from dropdown.  
  
**Default Event:** valueChange  
**Available Events:** blur, clear, change, click, dblclick, focus, itemSelect, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, select, valueChange  

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
.ui-selectonemenu-rtl | When RTL direction is set
.ui-selectonemenu-footer | Style applied to footer facet

