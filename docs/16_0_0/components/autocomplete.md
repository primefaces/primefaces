# AutoComplete

AutoComplete provides live suggestions while an input is being typed.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.AutoComplete-1.html)

## Getting Started with AutoComplete
AutoComplete is an input component so it requires a value as usual. Suggestions are loaded by
calling a server side completeMethod that takes a single string parameter which is the text entered.

``` xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}" />
```

```java
public class Bean {
    private String text;

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
    private String name;
    //getter setter
}
```
## Limiting the Results
Number of results shown can be limited, by default there is no limit. When the suggestions exceed
the number of results, a text defined by `moreText` is displayed to indicate this case. There is no
default action when the moreText is clicked, you need to use `moreTextSelect` ajax behavior event to handle
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

## Footer
A footer can be added to the suggestion list using the `footer` facet. You could use this for example to offer UI to
add a new item. For example:

```xhtml
<f:facet name="footer">
    <p:button value="Add new" onclick="..."/>
</f:facet>
```

Note that the panel is placed at the end of the body in the DOM tree, so not in the same form as the `AutoComplete`
component is in. So you might want to wrap the contents of this facet in a form if needed.

## Empty Message
When there are no suggestions found the empty message is localized on the client side using the `emptySearchMessage` translation key.

```js
PrimeFaces.locales['en'].emptySearchMessage = 'No suggestions found';
```

However if you want to provide a custom empty message on a specific AutoComplete component, you can use the `widgetPostConstruct` attribute to change it.

```xhtml
<p:autoComplete completeMethod="#{bean.actionAutocomplete}"  widgetVar="autoComplete" value="#{bean.selectedValue}" > 
   <f:attribute name="widgetPostConstruct" value="widget.emptyMessage = '#{bean.myCustomEmptyMessage}';"/>
</p:autoComplete>
```

## Multiple Selection
`AutoComplete` supports multiple selection as well, to use this feature set multiple option to true and
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

To improve performance (and avoid Jakarta Faces-lifecycle-costs during calling completeMethod) AutoComplete can consume REST-endpoints to provide suggestions to the user.
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

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
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

## LazyDataModel
You might have a `LazyDataModel` in your project which is almost suitable to serve as an `AutoComplete` suggestions
provider. It can be used with `AutoComplete` by using the `lazyModel` in combination with the `lazyField` attribute.
The query string will be applied on the provided `lazyField` property using `MatchMode.CONTAINS`.

## Instant selection

Instead of waiting for user to submit the form manually to process the selected item, you can enable
instant AJAX selection by using the _itemSelect_ AJAX behavior. Example below demonstrates how to
display a message about the selected item instantly.

```xhtml
<p:autoComplete value="#{bean.text}" completeMethod="#{bean.complete}">
    <p:ajax event="itemSelect" listener="#{bean.handleSelect}" update="msg" />
</p:autoComplete>
<p:messages id="msg" />
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
| .ui-state-loading | Container element; while search queries are executed. |

As skinning style classes are global, see the main theming section for more information.

## Tips

- Do not forget to use a converter when working with pojos.
- Enable forceSelection if you would like to accept values only from suggested list.
- Increase query delay to avoid unnecessary load to server as a result of user typing fast.
- Enable caching to avoid duplicate queries.
