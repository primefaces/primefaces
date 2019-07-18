# PickList

PickList is used for transferring data between two different collections.

## Info

| Name | Value |
| --- | --- |
| Tag | pickList
| Component Class | org.primefaces.component.picklist.Panel
| Component Type | org.primefaces.component.PickList
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PickListRenderer
| Renderer Class | org.primefaces.component.picklist.PickListRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
var | null | String | Name of the iterator.
itemLabel | null | String | Label of an item.
itemValue | null | Object | Value of an item.
style | null | String | Style of the main container.
styleClass | null | String | Style class of the main container.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Disables the component.
effect | null | String | Name of the animation to display.
effectSpeed | null | String | Speed of the animation.
addLabel | Add | String | Title of add button.
addAllLabel | Add All | String | Title of add all button.
removeLabel | Remove | String | Title of remove button.
removeAllLabel |Remove All | String | Title of remove all button.
moveUpLabel | Move Up | String | Title of move up button.
moveTopLabel | Move Top | String | Title of move top button.
moveDownLabel | Move Down | String | Title of move down button.
moveButtomLabel | Move Buttom | String | Title of move bottom button.
showSourceControls | false | Boolean | Specifies visibility of reorder buttons of source list.
showTargetControls | false | Boolean | Specifies visibility of reorder buttons of target list.
onTransfer | null | String | Client side callback to execute when an item is transferred from one list to another.
label | null | String | A localized user presentable name.
itemDisabled | false | Boolean | Specified if an item can be picked or not.
showSourceFilter | false | Boolean | Displays and input filter for source list.
showTargetFilter | false | Boolean | Displays and input filter for target list.
filterMatchMode | startsWith | String | Match mode for filtering, valid values are startsWith, contains, endsWith and custom.
filterFunction | null | String | Name of the javascript function for custom filtering.
showCheckbox | false | Boolean | When true, a checkbox is displayed next to each item.
labelDisplay | tooltip | String | Defines how the button labels displayed, valid values are "tooltip" (default) and "inline".
orientation | horizontal | String | Defines layout orientation, valid values are "vertical" and "horizontal".
responsive | false | Boolean | In responsive mode, picklist adjusts itself based on screen width.
escape | true | Boolean | Defines if labels of the component is escaped or not.
tabindex | null | String | Position of the element in the tabbing order.
filterEvent | keyup | String | Client side event to invoke picklist filtering for input fields. Default is keyup.
filterDelay | 300 | Integer  | Delay to wait in milliseconds before sending each filter query. Default is 300.

## Getting started with PickList
You need to create custom model called _org.primefaces.model.DualListModel_ to use PickList. As
the name suggests it consists of two lists, one is the source list and the other is the target. As the first
example we’ll create a DualListModel that contains basic Strings.

```java
public class PickListBean {
    private DualListModel<String> cities;

    public PickListBean() {
        List<String> source = new ArrayList<String>();
        List<String> target = new ArrayList<String>();
        citiesSource.add("Istanbul");
        citiesSource.add("Ankara");
        citiesSource.add("Izmir");
        citiesSource.add("Antalya");
        citiesSource.add("Bursa");
        //more cities
        cities = new DualListModel<String>(citiesSource, citiesTarget);
    }
    public DualListModel<String> getCities() {
        return cities;
    }
    public void setCities(DualListModel<String> cities) {
        this.cities = cities;
    }
}
```
And bind the cities dual list to the picklist;


```xhtml
<p:pickList value="#{pickListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}">
```
When the enclosed form is submitted, the dual list reference is populated with the new values and
you can access these values with DualListModel.getSource() and DualListModel.getTarget() api.

## POJOs
Most of the time you would deal with complex pojos rather than simple types like String.
This use case is no different except the addition of a converter. Following pickList displays a list of
players(name, age ...).

```xhtml
<p:pickList value="#{pickListBean.players}" var="player"
itemLabel="#{player.name}" itemValue="#{player}" converter="player">
```
PlayerConverter in this case should implement _javax.faces.convert.Converter_ contract and
implement getAsString, getAsObject methods. Note that a converter is always necessary for
primitive types like long, integer, boolean as well.

In addition custom content instead of simple strings can be displayed by using columns.

```xhtml
<p:pickList value="#{pickListBean.players}" var="player" iconOnly="true" effect="bounce" itemValue="#{player}" converter="player"
    showSourceControls="true" showTargetControls="true">
    <p:column style="width:25%">
        <p:graphicImage value="/images/barca/#{player.photo}"/>
    </p:column>
    <p:column style="width:75%";>
        #{player.name} - #{player.number}
    </p:column>
</p:pickList>
```
## Reordering
PickList support reordering of source and target lists, these are enabled by _showSourceControls_ and
_showTargetControls_ options.

## Effects
An animation is displayed when transferring when item to another or reordering a list, default effect
is fade and following options are available to be applied using _effect_ attribute; blind, bounce, clip,
drop, explode, fold, highlight, puff, pulsate, scale, shake, size and slide. _effectSpeed_ attribute is used
to customize the animation speed, valid values are _slow_ , _normal_ and _fast_.

## Captions
Caption texts for lists are defined with facets named _sourceCaption_ and _targetCaption_ ;


```xhtml
<p:pickList value="#{pickListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}" onTransfer="handleTransfer(e)">
    <f:facet name="sourceCaption">Available</f:facet>
    <f:facet name="targetCaption">Selected</f:facet>
</p:pickList>
```
## Filtering
PickList provides built-in client side filtering. Filtering is enabled by setting the corresponding
filtering attribute of a list. For source list this is _showSourceFilter_ and for target list it is
_showTargetFilter_. Default match mode is startsWith and contains, endsWith are also available
options.

When you need to a custom match mode set _filterMatchMode_ to custom and write a javascript
function that takes itemLabel and filterValue as parameters. Return false to hide an item and true to
display.

```xhtml
<p:pickList value="#{pickListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}" showSourceFilter="true" showTargetFilter="true"
    filterMatchMode="custom" filterFunction="myfilter">
</p:pickList>
```
```js
function myfilter(itemLabel, filterValue) {
    //return true or false
}
```
## onTransfer
If you’d like to execute custom javascript when an item is transferred, bind your javascript function
to _onTransfer_ attribute.

```xhtml
<p:pickList value="#{pickListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}" onTransfer="handleTransfer(e)">
```
```js
<script type="text/javascript">
    function handleTransfer(e) {
        //item = e.item
        //fromList = e.from
        //toList = e.toList
        //type = e.type (type of transfer; command, dblclick or dragdrop)
    }
</script>
```
## Ajax Behavior Events
| Event | Listener Parameter | Fired |
| --- | --- | --- |
select | org.primefaces.event.SelectEvent | When an item selected.
unselect | org.primefaces.event.UnselectEvent | When an item unselected.
reorder | javax.faces.event.AjaxBehaviorEvent | When list is reordered.
transfer | org.primefaces.event.TransferEvent | When an item is moved to another list.

```xhtml
<p:pickList value="#{pickListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}">
    <p:ajax event="transfer" listener="#{pickListBean.handleTransfer}" />
</p:pickList>
```
```java
public class PickListBean {
    //DualListModel code
    public void handleTransfer(TransferEvent event) {
        //event.getItems() : List of items transferred
        //event.isAdd() : Is transfer from source to target
        //event.isRemove() : Is transfer from target to source
    }
}
```
## Skinning
PickList resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list
of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-picklist | Main container element(table) of picklist
.ui-picklist-list | Lists of a picklist
.ui-picklist-list-source | Source list
.ui-picklist-list-target | Target list
.ui-picklist-source-controls | Container element of source list reordering controls
.ui-picklist-target-controls | Container element of target list reordering controls
.ui-picklist-button | Buttons of a picklist
.ui-picklist-button-move-up | Move up button
.ui-picklist-button-move-top | Move top button
.ui-picklist-button-move-down | Move down button
.ui-picklist-button-move-bottom | Move bottom button
.ui-picklist-button-add | Add button
.ui-picklist-button-add-all | Add all button
.ui-picklist-button-remove-all | Remove all button
.ui-picklist-button-add | Add button
.ui-picklist-vertical | Container element of a vertical picklist

As skinning style classes are global, see the main theming section for more information.

