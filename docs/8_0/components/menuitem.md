# MenuItem

MenuItem is used by various menu components.

## Info

| Name | Value |
| --- | --- |
| Tag | menuItem
| Tag | Class org.primefaces.component.menuitem.MenuItemTag
| Component Class | org.primefaces.component.menuitem.MenuItem
| Component Type | org.primefaces.component.MenuItem
| Component Family | org.primefaces.component |

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
value | null | String | Label of the menuitem
actionListener | null | MethodExpr | Action listener to be invoked when menuitem is clicked.
action | null | MethodExpr | Action to be invoked when menuitem is clicked.
immediate | false | Boolean | When true, action of this menuitem is processed after apply request phase.
url | null | String | Url to be navigated when menuitem is clicked
target | null | String | Target type of url navigation
style | null | String | Style of the menuitem label
styleClass | null | String | StyleClass of the menuitem label
onclick | null | String | Javascript event handler for click event
async | false | Boolean | When set to true, ajax requests are not queued.
process | @all | String | Components to process partially instead of whole view.
update | @none | String | Components to update after ajax request.
disabled | false | Boolean | Disables the menuitem.
onstart | null | String | Javascript handler to execute before ajax request is begins.
oncomplete | null | String | Javascript handler to execute when ajax request is completed.
onsuccess | null | String | Javascript handler to execute when ajax request succeeds.
onerror | null | String | Javascript handler to execute when ajax request fails.
global | true | Boolean | Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.
delay | null | String | If less than _delay_ milliseconds elapses between calls to _request()_ only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of _delay_ is the literal string 'none' without the quotes, no delay is used.
partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components.
resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
ajax | true | Boolean | Specifies submit mode.
icon | null | String | Path of the menuitem image.
title | null | String | Advisory tooltip information.
outcome | null | String | Navigation case outcome.
includeViewParams | false | Boolean | Defines if page parameters should be in target URI.
fragment | null | String | Identifier of the target page element to scroll to.
disableClientWindow | false | Boolean | Disable appending the ClientWindow on the rendering of this element.
containerStyle | null | String | Inline style of the menuitem container.
containerStyleClass | null | String | Style class of the menuitem container.
form | null | String | Form to serialize for an ajax request. Default is the enclosing form.
escape | true | Boolean | Defines whether value would be escaped or not
rel | null | String | Defines the relationship between the current document and the linked document.

## Getting started with MenuItem
MenuItem is a generic component used by the following components.

- Menu
- MenuBar
- MegaMenu
- Breadcrumb
- Dock
- Stack
- MenuButton
- SplitButton
- PanelMenu
- TabMenu
- SlideMenu
- TieredMenu

Note that some attributes of menuitem might not be supported by these menu components. Refer to
the specific component documentation for more information.

## Navigation vs Action
Menuitem has two use cases, directly navigating to a url with GET or doing a POST to execute an
action. This is decided by url or outcome attributes, if either one is present menuitem does a GET
request, if not parent form is posted with or without ajax decided by _ajax_ attribute.

## Icons
There are two ways to specify an icon of a menuitem, you can either use bundled icons within
PrimeFaces or provide your own via css.

#### ThemeRoller Icons ####

```xhtml
<p:menuitem icon="ui-icon-disk" ... />
```
#### Custom Icons ####

```xhtml
<p:menuitem icon="barca" ... />
```
```css
.barca {
    background: url(barca_logo.png) no-repeat;
    height:16px;
    width:16px;
}
```
