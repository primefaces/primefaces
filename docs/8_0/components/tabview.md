# TabView

TabView is a container component to group content in tabs.

## Info

| Name | Value |
| --- | --- |
| Tag | tabView
| Component Class | org.primefaces.component. tabview.TabView
| Component Type | org.primefaces.component.TabView
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TabViewRenderer
| Renderer Class | org.primefaces.component.tabview.TabViewRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Variable name of the client side widget.
activeIndex | 0 | Integer | Index of the active tab.
effect | null | String | Name of the transition effect.
effectDuration | null | String | Duration of the transition effect.
dynamic | false | Boolean | Enables lazy loading of inactive tabs.
cache | true | Boolean | When tab contents are lazy loaded by ajax toggleMode, caching only retrieves the tab contents once and subsequent toggles of a cached tab does not communicate with server. If caching is turned off, tab contents are reloaded from server each time tab is clicked.
onTabChange | null | String | Client side callback to execute when a tab is clicked.
onTabShow | null | String | Client side callback to execute when a tab is shown.
onTabClose | null | String | Client side callback to execute on tab close.
style | null | String | Inline style of the main container.
styleClass | null | String | Style class of the main container.
var | null | String | Name of iterator to refer an item in collection.
value | null | Object | Collection model to display dynamic tabs.
orientation | top | String | Orientation of tab headers.
dir | ltr | String | Defines text direction, valid values are ltr and rtl.
scrollable | false | Boolean | When enabled, tab headers can be scrolled horizontally instead of wrapping.
prependId | true | Boolean | TabView is a naming container thus prepends its id to its children by default, a false value turns this behavior off except for dynamic tabs.
tabindex | 0 | String | Position of the element in the tabbing order.

## Getting started with the TabView
TabView requires one more child tab components to display. Titles can also be defined by using
“title” facet.

```xhtml
<p:tabView>
    <p:tab title="Tab One">
        <h:outputText value="Lorem" />
    </p:tab>
    <p:tab title="Tab Two">
        <h:outputText value="Ipsum" />
    </p:tab>
    <p:tab title="Tab Three">
        <h:outputText value="Dolor" />
    </p:tab>
</p:tabView>
```
## Dynamic Tabs
There’re two toggleModes in tabview, _non-dynamic_ (default) and _dynamic_. By default, all tab
contents are rendered to the client, on the other hand in dynamic mode, only the active tab contents
are rendered and when an inactive tab header is selected, content is loaded with ajax. Dynamic
mode is handy in reducing page size, since inactive tabs are lazy loaded, pages will load faster. To
enable dynamic loading, simply set _dynamic_ option to true.


```xhtml
<p:tabView dynamic="true">
    //tabs
</p:tabView>
```
## Content Caching
Dynamically loaded tabs cache their contents by default, by doing so, reactivating a tab doesn’t
result in an ajax request since contents are cached. If you want to reload content of a tab each time a
tab is selected, turn off caching by _cache_ to false.

## Effects
Content transition effects are controlled with _effect_ and _effectDuration_ attributes. EffectDuration
specifies the speed of the effect, _slow_ , _normal_ (default) and _fast_ are the valid options.

```xhtml
<p:tabView effect="fade" effectDuration="fast">
    //tabs
</p:tabView>
```
## Ajax Behavior Events
_tabChange_ and _tabClose_ are the ajax behavior events of tabview that are executed when a tab is
changed and closed respectively. Here is an example of a tabChange behavior implementation;

```xhtml
<p:tabView>
    <p:ajax event=”tabChange” listener=”#{bean.onChange}” />
    //tabs
</p:tabView>
```
```java
public void onChange(TabChangeEvent event) {
    //Tab activeTab = event.getTab();
    //...
}
```
Your listener(if defined) will be invoked with an _org.primefaces.event.TabChangeEvent_ instance
that contains a reference to the new active tab and the accordion panel itself. For tabClose event,
listener will be passed an instance of _org.primefaces.event.TabCloseEvent._

## Dynamic Number of Tabs
When the tabs to display are not static, use the built-in iteration feature similar to ui:repeat.

```xhtml
<p:tabView value="#{bean.list}" var="listItem">
    <p:tab title="#{listItem.propertyA}">
        <h:outputText value= "#{listItem.propertyB}"/>
        ...More content
    </p:tab>
</p:tabView>
```

## Orientations
Four different orientations are available; _top(default)_ , _left_ , _right_ and _bottom_.

```xhtml
<p:tabView orientation="left">
    //tabs
</p:tabView>
```
## Scrollable Tabs
Tab headers wrap to the next line in case there is not enough space at header area by default. Using
scrollable feature, it is possible to keep headers aligned horizontally and use navigation buttons to
access hidden headers.

```xhtml
<p:tabView scrollable="true">
    //tabs
</p:tabView>
```
## Client Side Callbacks
Tabview has three callbacks for client side. _onTabChange_ is executed when an inactive tab is
clicked, _onTabShow_ is executed when an inactive tab becomes active to be shown and _onTabClose_
when a closable tab is closed. All these callbacks receive index parameter as the index of tab.

```xhtml
<p:tabView onTabChange="handleTabChange(index)">
    //tabs
</p:tabView>
```
```js
function handleTabChange(i) {
    //i = Index of the new tab
}
```
## Client Side API
Widget: _PrimeFaces.widget.TabView_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
select(index) | index: Index of tab to display | void | Activates tab with given index
selectTab(index) | index: Index of tab to display | void | (Deprecated, use select instead) Activates tab with given index
disable(index) | index: Index of tab to disable | void | Disables tab with given index
enable(index) | index: Index of tab to enable | void | Enables tab with given index
remove(index) | index: Index of tab to remove | void | Removes tab with given index
getLength() | - | Number | Returns the number of tabs
getActiveIndex() | - | Number | Returns index of current tab

## Skinning
As skinning style classes are global, see the main theming section for more information. Following
is the list of structural style classes.

| Class | Applies | 
| --- | --- | 
.ui-tabs | Main tabview container element
.ui-tabs-{orientation} | Orientation specific (top, bottom, righ, left) container.
.ui-tabs-nav | Main container of tab headers
.ui-tabs-panel | Each tab container
.ui-tabs-scrollable | Container element of a scrollable tabview.
