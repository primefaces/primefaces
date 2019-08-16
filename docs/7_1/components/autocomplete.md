# AutoComplete

AutoComplete provides live suggestions while an input is being typed.

## Info

| Name | Value |
| --- | --- |
| Tag | autoComplete
| Component Class | org.primefaces.component.autocomplete.AutoComplete
| Component Type | org.primefaces.component.AutoComplete
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.AutoCompleteRenderer
| Renderer Class | org.primefaces.component.autocomplete.AutoCompleteRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| value | null | Object | Value of the component than can be either an EL expression of a literal text.
| converter | null | Object | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| required | false | Boolean | Marks component as required.
| validator | null | Method Expr | A method expression that refers to a method validationg the input.
| valueChangeListener | null | Method Expr | A method expression that refers to a method for handling a valuchangeevent.
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| converterMessage | null | String | Message to be displayed when conversion fails.
| validatorMessage | null | String | Message to be displayed when validation fails.
| widgetVar | null | String | Name of the client side widget.
| completeMethod | null | Method Expr | Method providing suggestions.
| var | null | String | Name of the iterator used in pojo based suggestion.
| itemLabel | null | String | Label of the item.
| itemValue | null | String | Value of the item.
| maxResults | unlimited | Integer | Maximum number of results to be displayed.
| minQueryLength | 1 | Integer | Number of characters to be typed before starting to query.
| queryDelay | 300 | Integer | Delay to wait in milliseconds before sending each query to the server.
| forceSelection | false | Boolean | When enabled, autoComplete only accepts input from the selection list.
| scrollHeight | null | Integer | Defines the height of the items viewport.
| effect | null | String | Effect to use when showing/hiding suggestions.
| effectDuration | 400 | Integer | Duration of effect in milliseconds.
| dropdown | false | Boolean | Enables dropdown mode when set true.
| panelStyle | null | String | Inline style of the items container element.
| panelStyleClass | null | String | Style class of the items container element.
| multiple | false | Boolean | When true, enables multiple selection.
| accesskey | null | String | Access key that when pressed transfers focus to the input element.
| alt | null | String | Alternate textual description of the input field.
| autocomplete | null | String | Controls browser autocomplete behavior.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables input field
| label | null | String | A localized user presentable name.
| lang | null | String | Code describing the language used in the generated markup for this component.
| maxlength | null | Integer | Maximum number of characters that may be entered in this field.
| onblur | null | String | Client side callback to execute when input element loses focus.
| onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
| onclick | null | String | Client side callback to execute when input element is clicked.
| ondblclick | null | String | Client side callback to execute when input element is double clicked.
| onfocus | null | String | Client side callback to execute when input element receives focus.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
| onkeyup | null | String | Client side callback to execute when a key is released over input element.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
| onselect | null | String | Client side callback to execute when text within input element is selected by user.
| placeholder | null | String | Specifies a short hint.
| readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
| size | null | Integer | Number of characters used to determine the width of the input element.
| style | null | String | Inline style of the container element.
| styleClass | null | String | Style class of the container element.
| tabindex | null | Integer | Position of the input element in the tabbing order.
| title | null | String | Advisory tooltip informaton.
| itemtipMyPosition | left top | String | Position of itemtip corner relative to item.
| itemtipAtPosition | right bottom | String | Position of item corner relative to itemtip.
| cache | false | Boolean | When enabled autocomplete caches the searched result list.
| cacheTimeout | 300000 | Integer | Timeout value for cached results.
| emptyMessage | null | String | Text to display when there is no data to display.
| appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
| resultsMessage | null | String | Hint text for screen readers to provide information about the search results.
| groupBy | null | Object | Value to group items in categories.
| queryEvent | keyup | String | Event to initiate the query, valid options are "keyup" and "enter".
| dropdownMode | blank | String | Specifies the behavior dropdown button. Default "blank" mode sends an empty string and "current" mode sends the input value.
| autoHighlight | true | Boolean | Highlights the first suggested item automatically.
| selectLimit | null | Integer | Limits the multiple selection. Default is unlimited.
| inputStyle | null | String | Inline style of the input element.
| inputStyleClass | null | String | Style class of the input element.
| groupByTooltip | null | String | Tooltip to display on group headers.
| my | left top | String | Position of panel with respect to input.
| at | left bottom | String | Position of input with respect to panel.
| active | true | Boolean | Defines if autocomplete functionality is enabled.
| type | text | String | Input field type.
| moreText | ... | String | The text shown in panel when the suggested list is greater than maxResults.
| unique | false | Boolean | Ensures uniqueness of selected items.
| dynamic | false | Boolean | Defines if dynamic loading is enabled for the element's panel. If the value is "true", the overlay is not rendered on page load to improve performance.
| autoSelection | true | Boolean | Defines if auto selection of items that are equal to the typed input is enabled. If true, an item that is equal to the typed input is selected.
| escape | true | Boolean | Defines if autocomplete results are escaped or not.

## Getting Started with AutoComplete
AutoComplete is an input component so it requires a value as usual. Suggestions are loaded by
calling a server side completeMethod that takes a single string parameter which is the text entered.

``` xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" />
```

```java
public class Bean {
    private | String | text;

    public List<String> complete(String query) {
        List<String> results = new ArrayList<String>();
        for (int i = 0; i < 10; i++)
            results.add(query + i);
        return results;
    }
    //getter setter
}
```
## Pojo Support
Most of the time, instead of simple strings you would need work with your domain objects,
autoComplete supports this common use case with the use of a converter and data iterator.
Following example loads a list of players, itemLabel is the label displayed as a suggestion and
itemValue is the submitted value. Note that when working with pojos, you need to plug-in your own
converter.

```xhtml
<p:autoComplete value="#{playerBean.selectedPlayer}" completeMethod="#{playerBean.completePlayer}"
    var="player" itemLabel="#{player.name}" itemValue="#{player}" converter="playerConverter" />
```

```java
public class PlayerBean {
    private Player selectedPlayer;

    public Player getSelectedPlayer() {
        return selectedPlayer;
    }
    public void setSelectedPlayer(Player selectedPlayer) {
        this.selectedPlayer = selectedPlayer;
    }
    public List<Player> complete(String query) {
        List<Player> players = readPlayersFromDatasource(query);
        return players;
    }
}
```
``` java
public class Player {
    private | String | name;
    //getter setter
}
```
## Limiting the Results
Number of results shown can be limited, by default there is no limit. When the suggestions exceed
the number of results, a text defined by _moreText_ is displayed to indicate this case. There is no
default action when the moreText is clicked, you need to use _moreText_ ajax behavior event to handle
more results.

``` xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" maxResults="5" />
```
## Minimum Query Length
By default queries are sent to the server and completeMethod is called as soon as users starts typing
at the input text. This behavior is tuned using the _minQueryLength_ attribute.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" minQueryLength="3" />
```
With this setting, suggestions will start when user types the 3rd character at the input field.

## Query Delay
AutoComplete is optimized using _queryDelay_ option, by default autoComplete waits for 300
milliseconds to query a suggestion request, if you’d like to tune the load balance, give a longer
value. Following autoComplete waits for 1 second after user types an input.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" queryDelay="1000" />
```

## Custom Content
AutoComplete can display custom content by nesting columns.

```xhtml
<p:autoComplete value="#{autoCompleteBean.selectedPlayer}" completeMethod="#{autoCompleteBean.completePlayer}"
    var="p" itemValue="#{p}" converter="player">
    <p:column>
        <p:graphicImage value="/images/barca/#{p.photo}" width="40" height="50"/>
    </p:column>
    <p:column>
        #{p.name} - #{p.number}
    </p:column>
</p:autoComplete>
```
## Dropdown Mode
When dropdown mode is enabled, a dropdown button is displayed next to the input field.
Depending on dropdownMode configuration, clicking this button will either do a search with an
empty query or search with the current value in input.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" dropdown="true" />
```
## Multiple Selection
AutoComplete supports multiple selection as well, to use this feature set multiple option to true and
define a list as your backend model. Following example demonstrates multiple selection with
custom content support.

```xhtml
<p:autoComplete id="advanced" value="#{autoCompleteBean.selectedPlayers}" completeMethod="#{autoCompleteBean.completePlayer}"
    var="p" itemLabel="#{p.name}" itemValue="#{p}" converter="player" multiple="true">
    <p:column style="width:20%;text-align:center">
        <p:graphicImage value="/images/barca/#{p.photo}"/>
    </p:column>
    <p:column style="width:80%">
        #{p.name} - #{p.number}
    </p:column>
</p:autoComplete>
```

```java
public class AutoCompleteBean {
    private List<Player> selectedPlayers;
    //...
}
```

## Caching
Suggestions can be cached on client side so that the same query does not do a request which is
likely to return same suggestions again. To enable this, set _cache_ option to true. There is also a
_cacheTimeout_ option to configure how long it takes to clear a cache automatically.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" cache="true"/>
```

## Ajax Behavior Events
Instead of waiting for user to submit the form manually to process the selected item, you can enable
instant ajax selection by using the _itemSelect_ ajax behavior. Example below demonstrates how to
display a message about the selected item instantly.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}">
    <p:ajax event="itemSelect" listener="bean.handleSelect" update="msg" />
</p:autoComplete>
<p:messages id=”msg” />
```
```java
public class Bean {
    public void handleSelect(SelectEvent event) {
        Object item = event.getObject();
        FacesMessage msg = new FacesMessage("Selected", "Item:" + item);
    }
    //...
}
```
Your listener(if defined) will be invoked with an _org.primefaces.event.Select_ instance that contains a
reference to the selected item. Note that autoComplete also supports events inherited from regular
input text such as blur, focus, mouseover in addition to _itemSelect_. Similarly, _itemUnselect_ event is
provided for multiple autocomplete when an item is removed by clicking the remove icon. In this
case _org.primefaces.event.Unselect_ instance is passed to a listener if defined.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| itemSelect | org.primefaces.event.SelectEvent | On item selection. |
| itemUnselect | org.primefaces.event.UnselectEvent | On item unselection. |
| query | - | On query. |
| moreText | - | When moreText is clicked. |
| clear | - | When all text is deleted. |

## ItemTip
Itemtip is an advanced built-in tooltip when mouse is over on suggested items. Content of the
tooltip is defined via the _itemtip_ facet.

```xhtml
<p:autoComplete value="#{autoCompleteBean.selectedPlayer1}" id="basicPojo" completeMethod="#{autoCompleteBean.completePlayer}"
    var="p" itemLabel="#{p.name}" itemValue="#{p}" converter="player">
    <f:facet name="itemtip">
        <h:panelGrid columns="2" cellpadding="5">
            <f:facet name="header">
                <p:graphicImage value="/images/barca/#{p.photo}" />
            </f:facet>
            <h:outputText value="Name: " />
            <h:outputText id="modelNo" value="#{p.name}" />
            <h:outputText value="Number " />
            <h:outputText id="year" value="#{p.number}" />
            <h:outputText value="Position " />
            <h:outputText value="#{p.position}"/>
        </h:panelGrid>
    </f:facet>
</p:autoComplete>
```

## Client Side API
Widget: _PrimeFaces.widget.AutoComplete_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| search(value) | value: keyword for search | void | Initiates a search with given value |
| close() | - | void | Hides suggested items menu |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |
| deactivate() | - | void | Deactivates search behavior |
| activate() | - | void | Activates search behavior |

## Skinning
Following is the list of structural style classes;

| Class | Applies |
| --- | --- |
| .ui-autocomplete | Container element. |
| .ui-autocomplete-input | Input field. |
| .ui-autocomplete-panel | Container of suggestions list. |
| .ui-autocomplete-items | List of items |
| .ui-autocomplete-item | Each item in the list. |
| .ui-autocomplete-query | Highlighted part in suggestions. |

As skinning style classes are global, see the main theming section for more information.

## Tips

- Do not forget to use a converter when working with pojos.
- Enable forceSelection if you would like to accept values only from suggested list.
- Increase query delay to avoid unnecessary load to server as a result of user typing fast.
- Use emptyMessage option to provide feedback to the users that there are no suggestions.
- Enable caching to avoid duplicate queries.
