# AutoComplete

AutoComplete provides live suggestions while an input is being typed.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.autocomplete-1.html)

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
| accesskey | null | String | Access key that when pressed transfers focus to the input element.
| active | true | Boolean | Defines if autocomplete functionality is enabled.
| alt | null | String | Alternate textual description of the input field.
| at | left bottom | String | Position of input with respect to panel.
| autocomplete | null | String | Controls browser autocomplete behavior.
| autoHighlight | true | Boolean | Highlights the first suggested item automatically.
| autoSelection | true | Boolean | Defines if auto selection of items that are equal to the typed input is enabled. If true, an item that is equal to the typed input is selected.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| completeEndpoint | null | String | REST-endpoint for fetching autocomplete-suggestions. (instead of completeMethod) Can´t be combined with dynamic=true, queryMode!=server, cache=true. 
| completeMethod | null | Method Expr | Method providing suggestions.
| converter | null | Object | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
| converterMessage | null | String | Message to be displayed when conversion fails.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables input field
| dropdown | false | Boolean | Enables dropdown mode when set true.
| dropdownTabindex | null | String | Position of the dropdown button in the tabbing order.
| dynamic | false | Boolean | Defines if dynamic loading is enabled for the element's panel. If the value is "true", the overlay is not rendered on page load to improve performance.
| effect | null | String | Effect to use when showing/hiding suggestions.
| effectDuration | 400 | Integer | Duration of effect in milliseconds.
| escape | true | Boolean | Defines if autocomplete results are escaped or not.
| forceSelection | false | Boolean | When enabled, autoComplete only accepts input from the selection list.
| groupByTooltip | null | String | Tooltip to display on group headers.
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| inputmode | null | String | Hint at the type of data this control has for touch devices to display appropriate virtual keyboard.
| inputStyle | null | String | Inline style of the input element.
| inputStyleClass | null | String | Style class of the input element.
| itemLabel | null | String | Label of the item.
| itemValue | null | String | Value of the item.
| label | null | String | A localized user presentable name.
| lang | null | String | Code describing the language used in the generated markup for this component.
| maxlength | null | Integer | Maximum number of characters that may be entered in this field.
| maxResults | unlimited | Integer | Maximum number of results to be displayed.
| minQueryLength | 1 | Integer | Number of characters to be typed before starting to query.
| moreText | ... | String | The text shown in panel when the suggested list is greater than maxResults.
| multiple | false | Boolean | When true, enables multiple selection.
| my | left top | String | Position of panel with respect to input.
| onblur | null | String | Client side callback to execute when input element loses focus.
| onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
| onclick | null | String | Client side callback to execute when input element is clicked.
| oncontextmenu | null | String | Client side callback to execute when a context menu is triggered.
| oncopy | null | String | Client side callback to execute when the user cuts the content of an element.
| oncut | null | String | Client side callback to execute when the user copies the content of an element.
| ondblclick | null | String | Client side callback to execute when input element is double clicked.
| ondrag | null | String | Client side callback to execute when an element is dragged.
| ondragend | null | String | Client side callback to execute at the end of a drag operation.
| ondragenter | null | String | Client side callback to execute when an element has been dragged to a valid drop target.
| ondragleave | null | String | Client side callback to execute when an element leaves a valid drop target.
| ondragover | null | String | Client side callback to execute when an element is being dragged over a valid drop target.
| ondragstart | null | String | Client side callback to execute at the start of a drag operation.
| ondrop | null | String | Client side callback to execute when dragged element is being dropped.
| onfocus | null | String | Client side callback to execute on input element focus.
| oninput | null | String | Client side callback to execute when an element gets user input.
| oninvalid | null | String | Client side callback to execute when an element is invalid.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
| onkeyup | null | String | Client side callback to execute when a key is released over input element.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
| onpaste | null | String | Client side callback to execute when the user pastes some content in an element.
| onreset | null | String | Client side callback to execute when the Reset button in a form is clicked.
| onscroll | null | String | Client side callback to execute when an element's scrollbar is being scrolled.e input value.
| onsearch | null | String | Client side callback to execute when the user writes something in a search field.
| onselect | null | String | Client side callback to execute when text within input element is selected by user.
| onwheel | null | String | Client side callback to execute when the mouse wheel rolls up or down over an element.
| panelStyle | null | String | Inline style of the items container element.
| panelStyleClass | null | String | Style class of the items container element.
| queryDelay | 300 | Integer | Delay to wait in milliseconds before sending each query to the server.
| queryMode | server | String | Specifies query mode, valid values are "server" (default), "client" and "hybrid". [more](https://github.com/primefaces/primefaces/issues/5298)
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| required | false | Boolean | Marks component as required.
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| scrollHeight | null | Integer | Defines the height of the items viewport.
| selectLimit | null | Integer | Limits the multiple selection. Default is unlimited.
| tabindex | null | String | Position of the input field in the tabbing order.
| type | text | String | Input field type.
| unique | false | Boolean | Ensures uniqueness of selected items.
| validator | null | Method Expr | A method expression that refers to a method validationg the input.
| validatorMessage | null | String | Message to be displayed when validation fails.
| value | null | Object | Value of the component than can be either an EL expression of a literal text.
| valueChangeListener | null | Method Expr | A method expression that refers to a method for handling a valuchangeevent.
| var | null | String | Name of the iterator used in pojo based suggestion.
| widgetVar | null | String | Name of the client side widget.

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
define a list as your back-end model. Use BACKSPACE key to remove a selected item and CTRL or SHIFT+BACKSPACE 
to remove all items at once.
Following example demonstrates multiple selection with custom content support.

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

## Consuming REST-endpoints

To improve performance (and avoid JSF-lifecycle-costs during calling completeMethod) AutoComplete can consume REST-endpoints to provide suggestions to the user.
Or existing REST-endpoints may be re-used.

AutoComplete does a HTTP-GET against the REST-endpoint and passes query-url-parameter. (eg `/rest/theme/autocomplete?query=lu`)

The REST-endpoint has to return following JSON-response: 
```json
{"suggestions":[{"value":"0","label":"Nova-Light"},{"value":"1","label":"Nova-Dark"},{"value":"2","label":"Nova-Colored"}],"moreAvailable":false}
```
Each suggestion-item needs to have value- and label-property. In most cases you can simply use `org.primefaces.model.rest.AutoCompleteSuggestionResponse` as response as the following example shows. 

Sample REST-service based one JAX-RS and CDI: 

```java
import org.primefaces.model.rest.AutoCompleteSuggestion;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Named("themeRestService")
@Path("/theme")
public class ThemeService {

    @Inject
    private org.primefaces.showcase.service.ThemeService service;

    @GET
    @Path("/autocomplete")
    @Produces({ MediaType.APPLICATION_JSON })
    public AutoCompleteSuggestionResponse autocomplete(@QueryParam("query") String query) {
        String queryLowerCase = query.toLowerCase();
        List<Theme> allThemes = service.getThemes();
        return new AutoCompleteSuggestionResponse(allThemes.stream()
                .filter(t -> t.getName().toLowerCase().contains(queryLowerCase))
                .map(t -> new AutoCompleteSuggestion(Integer.toString(t.getId()), t.getDisplayName()))
                .collect(Collectors.toList()));
    }
}
``` 

Sample-useage within AutoComplete. Note `completeEndpoint`-attribute. 
```xhtml
<p:autoComplete id="themePojoRest" value="#{autoCompleteView.theme}" var="theme" itemLabel="#{theme.displayName}" itemValue="#{theme}" converter="#{themeConverter}" completeEndpoint="#{request.contextPath}/rest/theme/autocomplete" forceSelection="true" />
``` 

## Ajax Behavior Events
The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, clear, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, itemSelect, itemUnselect, keydown, keypress, keyup, moreTextSelect, emptyMessageSelect, mousedown, mousemove, mouseout, mouseover, mouseup, paste, query, reset, scroll, search, select, valueChange, wheel`


Instead of waiting for user to submit the form manually to process the selected item, you can enable
instant AJAX selection by using the _itemSelect_ AJAX behavior. Example below demonstrates how to
display a message about the selected item instantly.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}">
    <p:ajax event="itemSelect" listener="#{bean.handleSelect}" update="msg" />
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
| moreTextSelect | - | When moreText is selected. |
| clear | - | When all text is deleted. |
| emptyMessageSelect | - | When emptyMessage is selected. |

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
| activate() | - | void | Activates search behavior |
| addItem(item) | JQuery or String | void | Adds the given suggestion item. |
| clear() | - | void | Clears the input field |
| close() | - | void | Hides suggested items menu |
| deactivate() | - | void | Deactivates search behavior |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |
| removeAllItems() | - | void | In multiple mode removes all selected items |
| removeItem(item) | JQuery or String | void | Removes the given suggestion item. |
| search(value) | value: keyword for search | void | Initiates a search with given value |

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
