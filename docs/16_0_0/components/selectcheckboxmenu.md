# SelectCheckboxMenu

SelectCheckboxMenu is a multi select component that displays options in an overlay.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectCheckboxMenu-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | selectCheckboxMenu
| Component Class | org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu
| Component Type | org.primefaces.component.SelectCheckboxMenu
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SelectCheckboxMenuRenderer
| Renderer Class | org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenuRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
ariaDescribedBy | null | String | The aria-describedby attribute is used to define a component id that describes the current element for accessibility.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
caseSensitive | false | Boolean | Defines if filtering would be case sensitive.
collectionType | null | String | Optional attribute that is a literal string that is the fully qualified class name of a concrete class that implements `java.util.Collection` or an EL expression that evaluates to either 1. such a String, or 2. the `Class` object itself.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
converterMessage | null | String | Message to be displayed when conversion fails.
disabled | false | Boolean | Disables the component.
dynamic | false | Boolean | Defines if dynamic loading is enabled for the element's panel. If the value is "true", the overlay is not rendered on page load to improve performance.
emptyLabel | null | String | Label to be shown in updateLabel mode when no item is selected. If not set the label is shown.
filter | false | Boolean | Renders an input field as a filter.
filterFunction | null | String | Client side function to use in custom filtering.
filterMatchMode | startsWith | String | Match mode for filtering, valid values are startsWith, contains, endsWith and custom.
filterNormalize | false | Boolean | Defines if filtering would be done using normalized values (accents will be removed from characters).
filterPlaceholder | null | String  | Placeholder text to show when filter input is empty.
hideNoSelectionOption | false | boolean  | Flag indicating that, if this component is activated by the user, The "no selection option", if any, must be hidden.
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
label | null | String | User presentable name.
labelSeparator | , | String | Separator for joining item lables if updateLabel is set to true. Default is ",".
multiple | false | Boolean | Whether to show selected items as multiple labels.
onHide | null | String | Client side callback to execute when overlay is hidden.
onShow | null | String | Client side callback to execute when overlay is displayed.
onchange | null | String | Callback to execute on value change.
panelStyle | null | String | Inline style of the overlay.
panelStyleClass | null | String | Style class of the overlay.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
required | false | Boolean | Marks component as required
requiredMessage | null | String | Message to be displayed when required field validation fails.
scrollHeight | null | String | Maximum height of the overlay, e.g., "200vh" or "200%". Default is "200px".
selectedLabel | null | String | Label to be shown in updateLabel mode when one or more items are selected. If not set the label is shown.
showHeader | true | Boolean | When enabled, the header of panel is displayed.
showSelectAll | true | Boolean | When enabled, the select all checkbox is displayed.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.
tabindex | null | String | Position of the element in the tabbing order.
title | null | String | Advisory tooltip information.
updateLabel | false | Boolean | When enabled, the label is updated on every change, else it statically displays the `selectedLabel`.
validator | null | MethodExpr | A method expression that refers to a method validating the input
validatorMessage | null | String | Message to be displayed when validation fields.
value | null | Object | Value of the component referring to a List.
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
var | null | String | Name of iterator to be used in custom content display.
widgetVar | null | String | Name of the client side widget.

## Getting started with SelectCheckboxMenu
SelectCheckboxMenu usage is same as the standard selectManyCheckbox or PrimeFaces
selectManyCheckbox components.

```xhtml
<p:selectCheckboxMenu value="#{bean.selectedOptions}" label="Movies">
    <f:selectItems value="#{bean.options}" />
</p:selectCheckboxMenu>
```
## Labels
Selected items are not displayed as the component label by default, setting _updateLabel_ to true
displays item as a comma separated list and for an advanced display use _multiple_ property instead
which renders the items as separate tokens similar to the chips component.

## Filtering
When filtering is enabled with setting `filter` on, an input field is rendered at overlay header and on
keyup event filtering is executed on client side using `filterMatchMode`. Default modes of
filterMatchMode are `startsWith`, `contains`, `endsWith` and `custom`. Custom mode requires a javascript
function to do the filtering.

```xhtml
<p:selectCheckboxMenu value="#{bean.selectedOptions}" label="Movies" filterMatchMode="custom" filterFunction="customFilter" filter="on">
    <f:selectItems value="#{bean.options}" />
</p:selectCheckboxMenu>
```
```js
function customFilter(itemLabel, filterValue) {
    //return true to accept and false to reject
}
```

## Custom Content
SelectCheckboxMenu can display custom content in overlay panel by using column component and the
var option to refer to each item. Facets for column `header` and overall `footer` may also be used.

```java
public class MenuBean {
    private List<Player> players;
    private List<Player> selectedPlayers;

    public OrderListBean() {
        players = new ArrayList<Player>();
        players.add(new Player("Messi", 10, "messi.jpg"));
        //more players
    }
    //getters and setters
}
```
```xhtml
<p:selectCheckboxMenu value="#{menuBean.selectedPlayers}" converter="player" var="p">
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
         <h:outputText value="#{menuBean.players.size()} available players"
                       style="font-weight: bold"/>
    </f:facet>
</p:selectCheckboxMenu>
```

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `change`  
**Available Events:** `change, itemSelect, itemUnselect, toggleSelect`  

| Event | Listener Parameter | Fired |
| --- | --- | --- |
toggleSelect | org.primefaces.event.ToggleSelectEvent | When toggle all checkbox changes.
itemSelect | org.primefaces.event.SelectEvent | When a item is added via the checkbox.
itemUnselect | org.primefaces.event.UnselectEvent | When a item is removed via the close-icon.

```xhtml
<p:ajax event="change" listener="#{bean.handlechange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.SelectCheckBoxMenu_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |
| show() | - | void | Brings up the overlay panel with the available checkbox options. |
| hide() | - | void | Hides the overlay panel with the available checkbox options. |
| togglePanel() | - | void | Bring up the overlay panel if its not showing or hide it if it is showing. |

## Skinning
SelectCheckboxMenu resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;


| Class | Applies |
| --- | --- |
.ui-selectcheckboxmenu | Main container element.
.ui-selectcheckboxmenu-label-container | Label container.
.ui-selectcheckboxmenu-label | Label.
.ui-selectcheckboxmenu-trigger | Dropdown icon.
.ui-selectcheckboxmenu-panel | Overlay panel.
.ui-selectcheckboxmenu-header | Header in overlay panel.
.ui-selectcheckboxmenu-footer | Footer in overlay panel.
.ui-selectcheckboxmenu-filter-container | Container for filter in overlay panel header.
.ui-selectcheckboxmenu-filter | Filter in overlay panel header.
.ui-selectcheckboxmenu-close | Closer in overlay panel header.
.ui-selectcheckboxmenu-items-wrapper | Wrapper for option list container.
.ui-selectcheckboxmenu-items | Option list container.
.ui-selectcheckboxmenu-item-group | Each option group in the collection.
.ui-selectcheckboxmenu-item | Each options in the collection.
.ui-chkbox | Container of a checkbox.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.
