# SelectOneMenu

SelectOneMenu is an extended version of the standard SelectOneMenu.

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

