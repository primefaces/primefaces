# OrderList

OrderList is used to sort a collection featuring drag&drop based reordering, transition effects and
pojo support.

## Info

| Name | Value |
| --- | --- |
| Tag | orderList
| Component Class | org.primefaces.component.orderlist.OrderList
| Component Type | org.primefaces.component.OrderList
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.OrderListRenderer
| Renderer Class | org.primefaces.component.orderlist.OrderListRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component referring to a List.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | 0 | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | 0 | Boolean | Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
var | null | String | Name of the iterator.
itemLabel | null | String | Label of an item.
itemValue | null | String | Value of an item.
style | null | String | Inline style of container element.
styleClass | null | String | Style class of container element.
disabled | false | Boolean | Disables the component.
effect | fade | String | Name of animation to display.
moveUpLabel | Move Up | String | Label of move up button.
moveTopLabel | Move Top | String | Label of move top button.
moveDownLabel | Move Down | String | Label of move down button.
moveBottomLabel | Move Bottom | String | Label of move bottom button.
controlsLocation | left | String | Location of the reorder buttons, valid values are “left”, “right” and “none”.
responsive | false | Boolean | In responsive mode, orderList adjusts itself based on screen width.

## Getting started with OrderList
A list is required to use OrderList component.

```java
public class OrderListBean {
    private List<String> cities;

    public OrderListBean() {
        cities = new ArrayList<String>();
        cities.add("Istanbul");
        cities.add("Ankara");
        cities.add("Izmir");
        cities.add("Antalya");
        cities.add("Bursa");
    }
    //getter&setter for cities
}
```

```xhtml
<p:orderList value="#{orderListBean.cities}" var="city" itemLabel="#{city}" itemValue="#{city}""/>
```

## Advanced OrderList
OrderList supports displaying custom content instead of simple labels by using columns. In
addition, pojos are supported if a converter is defined.

```java
public class OrderListBean {
    private List<Player> players;

    public OrderListBean() {
        players = new ArrayList<Player>();
        players.add(new Player("Messi", 10, "messi.jpg"));
        players.add(new Player("Iniesta", 8, "iniesta.jpg"));
        players.add(new Player("Villa", 7, "villa.jpg"));
        players.add(new Player("Xavi", 6, "xavi.jpg"));
    }
    //getter&setter for players
}
```
```xhtml
<p:orderList value="#{orderListBean.players}" var="player" itemValue="#{player}" converter="player">
    <p:column style="width:25%">
        <p:graphicImage value="/images/barca/#{player.photo}" />
    </p:column>
    <p:column style="width:75%;">
        #{player.name} - #{player.number}
    </p:column>
</p:orderList>
```

## Ajax Behavior Events
| Event | Listener Parameter | Fired |
| --- | --- | --- |
select | org.primefaces.event.SelectEvent | When an item selected.
unselect | org.primefaces.event.UnselectEvent | When an item unselected.
reorder | javax.faces.event.AjaxBehaviorEvent | When list is reordered.

## Header
A facet called “caption” is provided to display a header content for the orderlist.

## Effects
An animation is executed during reordering, default effect is fade and following options are
available for _effect_ attribute; blind, bounce, clip, drop, explode, fold, highlight, puff, pulsate, scale,
shake, size and slide.

## Skinning
OrderList resides in a main container which _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-orderlist | Main container element.
.ui-orderlist-list | Container of items.
.ui-orderlist-item | Each item in the list.
.ui-orderlist-caption | Caption of the list.
