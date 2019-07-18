# SplitButton

SplitButton displays a command by default and additional ones in an overlay.

## Info

| Name | Value |
| --- | --- |
| Tag | splitButton
| Component Class | org.primefaces.component.splitbutton.SplitButton
| Component Type | org.primefaces.component.SplitButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SplitButtonRenderer
| Renderer Class | org.primefaces.component.splitbutton.SplitButtonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | String | Label for the button
action | null | MethodExpr/String | A method expression or a | String | outcome that’d be processed when button is clicked.
actionListener | null | MethodExpr | An actionlistener that’d be processed when button is clicked.
immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application phase.
type | submit | String | Sets the behavior of the button.
ajax | true | Boolean | Specifies the submit mode, when set to true(default), submit would be made with Ajax.
async | false | Boolean | When set to true, ajax requests are not queued.
process | @all | String | Component(s) to process partially instead of whole view.
update | @none | String | Component(s) to be updated with ajax.
onstart | null | String | Client side callback to execute before ajax request is begins.
oncomplete | null | String | Client side callback to execute when ajax request is completed.
onsuccess | null | String | Client side callback to execute when ajax request succeeds.
onerror | null | String | Client side callback to execute when ajax request fails.
global | true | Boolean | Defines whether to trigger ajaxStatus or not.
delay | null | String | If less than _delay_ milliseconds elapses between calls to _request()_ only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of _delay_ is the literal string 'none' without the quotes, no delay is used.
partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components.
resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
style | null | String | Inline style of the button element.
styleClass | null | String | StyleClass of the button element.
onblur | null | String | Client side callback to execute when button loses focus.
onchange | null | String | Client side callback to execute when button loses focus and its value has been modified since gaining focus.
onclick | null | String | Client side callback to execute when button is clicked.
ondblclick | null | String | Client side callback to execute when button is double clicked.
onfocus | null | String | Client side callback to execute when button receives focus.
onkeydown | null | String | Client side callback to execute when a key is pressed down over button.
onkeypress | null | String | Client side callback to execute when a key is pressed and released over button.
onkeyup | null | String | Client side callback to execute when a key is released over button.
onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over button.
onmousemove | null | String | Client side callback to execute when a pointer button is moved within button.
onmouseout | null | String | Client side callback to execute when a pointer button is moved away from button.
onmouseover | null | String | Client side callback to execute when a pointer button is moved onto button.
onmouseup | null | String | Client side callback to execute when a pointer button is released over button.
onselect | null | String | Client side callback to execute when text within button is selected by user.
accesskey | null | String | Access key that when pressed transfers focus to the button.
alt | null | String | Alternate textual description of the button.
dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
disabled | false | Boolean | Disables the button.
image | null | String | Style class for the button icon. (deprecated: use icon)
label | null | String | A localized user presentable name.
lang | null | String | Code describing the language used in the generated markup for this component.
tabindex | null | Integer | Position of the button element in the tabbing order.
title | null | String | Advisory tooltip information.
readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
icon | null | String | Icon of the button as a css class.
iconPos | left | String | Position of the icon.
widgetVar | null | String | Name of the client side widget.
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
form | null | String | Form to serialize for an ajax request. Default is the enclosing form.
menuStyleClass | null | String | Style class of the overlay menu element.
model | null | MenuModel | A menu model instance to create the items of splitButton programmatically.
filter | false | Boolean | Displays an input filter for the list. Default is false.
filterMatchMode | null | String | Match mode for filtering, valid values are startsWith (default), contains, endsWith and custom.
filterFunction | null | String | Client side function to use in custom filterMatchMode.
filterPlaceholder | null | String | Watermark displayed in the filter input field before the user enters a value in an HTML5 browser.

## Getting started with SplitButton
SplitButton usage is similar to a regular commandButton. Additional commands are placed inside
the component and displayed in an overlay. In example below, clicking the save button invokes save
method of the bean and updates messages. Nested options defined as menuitems do ajax, non-ajax
requests as well as regular navigation to an external url.

```xhtml
<p:splitButton value="Save" action="#{buttonBean.save}" update="messages" icon="ui-icon-disk">
    <p:menuitem value="Update" action="#{buttonBean.update}" update="messages" icon="ui-icon-arrowrefresh-1-w"/>
    <p:menuitem value="Delete" action="#{buttonBean.delete}" ajax="false" icon="ui-icon-close"/>
    <p:separator />
    <p:menuitem value="Homepage" url="http://www.primefaces.org" icon="ui-icon-extlink"/>
</p:splitButton>
```
## Client Side API
Widget: _PrimeFaces.widget.SplitButton_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Displays overlay.
hide() | - | void | Hides overlay.

## Skinning
SplitButton renders a container element which _style_ and _styleClass_ applies. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-splitbutton | Container element.
.ui-button | Button element
.ui-splitbutton-menubutton | Dropdown button
.ui-button-text-only | Button element when icon is not used
.ui-button-text | Label of button
.ui-menu | Container element of menu
.ui-menu-list | List container
.ui-menuitem | Each menu item
.ui-menuitem-link | Anchor element in a link item
.ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

