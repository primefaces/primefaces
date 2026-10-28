# SelectCheckboxMenu

SelectCheckboxMenu is a multi select component that displays options in an overlay.

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
<p:selectCheckboxMenu value="#{bean.selectedOptions}" label="Movies" filterMatchMode="custom" filterFunction="customFilter" filter="true">
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
.p-selectcheckboxmenu | Main container element.
.p-selectcheckboxmenu-label-container | Label container.
.p-selectcheckboxmenu-label | Label.
.p-selectcheckboxmenu-trigger | Dropdown icon.
.p-selectcheckboxmenu-panel | Overlay panel.
.p-selectcheckboxmenu-header | Header in overlay panel.
.p-selectcheckboxmenu-footer | Footer in overlay panel.
.p-selectcheckboxmenu-filter-container | Container for filter in overlay panel header.
.p-selectcheckboxmenu-filter | Filter in overlay panel header.
.p-selectcheckboxmenu-close | Closer in overlay panel header.
.p-selectcheckboxmenu-items-wrapper | Wrapper for option list container.
.p-selectcheckboxmenu-items | Option list container.
.p-selectcheckboxmenu-item-group | Each option group in the collection.
.p-selectcheckboxmenu-item | Each options in the collection.
.p-chkbox | Container of a checkbox.
.p-chkbox-box | Container of checkbox icon.
.p-chkbox-icon | Checkbox icon.
