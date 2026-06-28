# OrderList

OrderList is used to sort a collection featuring drag&drop based reordering, transition effects and
pojo support.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.OrderList.html)

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
