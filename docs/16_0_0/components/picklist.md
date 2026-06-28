# PickList

PickList is used for transferring data between two different collections.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.PickList-1.html)

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
PlayerConverter in this case should implement _jakarta.faces.convert.Converter_ contract and
implement getAsString, getAsObject methods. Note that a converter is always necessary for
primitive types like long, integer, boolean as well.

In addition custom content instead of simple strings can be displayed by using columns.

```xhtml
<p:pickList value="#{pickListBean.players}" var="player" iconOnly="true" effect="bounce" itemValue="#{player}" converter="player"
    showSourceControls="true" showTargetControls="true">
    <p:column style="width:25%">
        <p:graphicImage value="/images/barca/#{player.photo}"/>
    </p:column>
    <p:column style="width:75%">
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

## Labels
Label texts and tooltips come from the locale configuration for your language by default. If you need to override them per component you can use `widgetPreConstruct` to manually set them.

```xhtml
<p:pickList value="#{pickListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}" onTransfer="handleTransfer(e)">
    <f:attribute name="widgetPreConstruct" value="cfg.labels.aria.moveUp = 'Slide Up'; cfg.labels.aria.moveDown = 'Slide Down'; cfg.labels.aria.moveTop = 'Slide Top'; cfg.labels.aria.moveBottom = 'Slide Bottom'; cfg.labels.aria.moveToSource = 'Unsubscribe'; cfg.labels.aria.moveToTarget = 'Subscribe'; cfg.labels.aria.moveAllToSource = 'Unsubscribe All'; cfg.labels.aria.moveAllToTarget = 'Subscribe All';" />
    <f:facet name="sourceCaption">Unsubscribed</f:facet>
    <f:facet name="targetCaption">Subscribed</f:facet>
</p:pickList>
```

## Filtering
PickList provides built-in client side filtering. Filtering is enabled by setting the corresponding
filtering attribute of a list. For source list this is `showSourceFilter` and for target list it is
`showTargetFilter`. Default match mode is `startsWith` and `contains`, `endsWith` are also available
options.

When you need to a custom match mode set `filterMatchMode` to custom and write a javascript
function that takes `itemLabel` and `filterValue` as parameters. Return false to hide an item and true to
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
        //type = e.type (type of transfer; command, dblclick, checkbox or dragdrop)
    }
</script>
```

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

