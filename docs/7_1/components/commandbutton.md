# CommandButton

CommandButton is an extended version of standard commandButton with ajax and theming.

## Info

| Name | Value |
| --- | --- |
| Tag | commandButton
| Component Class | org.primefaces.component.commandbutton.CommandButton
| Component Type | org.primefaces.component.CommandButton
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CommandButtonRenderer
| Renderer Class | org.primefaces.component.commandbutton.CommandButtonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | String | Label for the button
| action | null | MethodExpr/String | A method expression or a String outcome that’d be processed when button is clicked.
| actionListener | null | MethodExpr | An actionlistener that’d be processed when button is clicked.
| immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application  phase.
| type | submit | String | Sets the behavior of the button.
| ajax | true | Boolean | Specifies the submit mode, when set to true(default), submit would be made with Ajax.
| async | false | Boolean | When set to true, ajax requests are not queued.
| process | @all | String | Component(s) to process partially instead of whole view.
| update | @none | String | Component(s) to be updated with ajax.
| onstart | null | String | Client side callback to execute before ajax request is begins.
| oncomplete | null | String | Client side callback to execute when ajax request is completed.
| onsuccess | null | String | Client side callback to execute when ajax request succeeds.
| onerror | null | String | Client side callback to execute when ajax request fails.
| global | true | Boolean | Defines whether to trigger ajaxStatus or not.
| delay | null | String | If less than _delay_ milliseconds elapses between calls to _request()_ only the most recent one is sent and all other requests are discarded. | If this option is not specified, or if the value of _delay_ is the literal string 'none' without the quotes, no delay is used.
| partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
| partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed  components.
| resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
| ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
| timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
| style | null | String | Inline style of the button element.
| styleClass | null | String | StyleClass of the button element.
| onblur | null | String | Client side callback to execute when button loses focus.
| onchange | null | String | Client side callback to execute when button loses focus and its value has been modified since gaining focus.
| onclick | null | String | Client side callback to execute when button is clicked.
| ondblclick | null | String | Client side callback to execute when button is double clicked.
| onfocus | null | String | Client side callback to execute when button receives focus.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over button.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over button.
| onkeyup | null | String | Client side callback to execute when a key is released over button.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over button.
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within button.
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from button.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto button.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over button.
| onselect | null | String | Client side callback to execute when text within button is selected by user.
| accesskey | null | String | Access key that when pressed transfers focus to the button.
| alt | null | String | Alternate textual description of the button.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables the button.
| label | null | String | A localized user presentable name.
| lang | null | String | Code describing the language used in the generated markup for this component.
| tabindex | null | Integer | Position of the button element in the tabbing order.
| title | null | String | Advisory tooltip information.
| icon | null | String | Icon of the button as a css class.
| iconPos | left | String | Position of the icon.
| inline | false | String | Used by PrimeFaces mobile only.
| escape | true | Boolean | Defines whether label would be escaped or not.
| widgetVar | null | String | Name of the client side widget.
| form | null | String | Form to serialize for an ajax request. Default is the enclosing form.
| renderDisabledClick | true | Boolean | When enabled, click event can be added to disabled button.


## Getting started with CommandButton
CommandButton usage is similar to standard commandButton, by default commandButton submits
its enclosing form with ajax.

```xhtml
<p:commandButton value="Save" action="#{bookBean.saveBook}" />
```
```java
public class BookBean {
    public void saveBook() {
    //Save book
    }
}
```
## Reset Buttons
Reset buttons do not submit the form, just resets the form contents.

```xhtml
<p:commandButton type="reset" value="Reset" />
```
## Push Buttons
Push buttons are used to execute custom javascript without causing an ajax/non-ajax request. To
create a push button set type as "button".

```xhtml
<p:commandButton type="button" value="Alert" onclick="alert(‘Prime’)" />
```
## AJAX and Non-AJAX
CommandButton has built-in ajax capabilities, ajax submit is enabled by default and configured
using _ajax_ attribute. When ajax attribute is set to false, form is submitted with a regular full page
refresh.

The _update_ attribute is used to partially update other component(s) after the ajax response is
received. Update attribute takes a comma or white-space separated list of JSF component ids to be
updated. Basically any JSF component, not just PrimeFaces components should be updated with the
Ajax response. In the following example, form is submitted with ajax and _display_ outputText is
updated with the ajax response.

```xhtml
<h:form>
    <h:inputText value="#{bean.text}" />
    <p:commandButton value="Submit" update="display"/>
    <h:outputText value="#{bean.text}" id="display" />
</h:form>
```

**Tip**: You can use the ajaxStatus component to notify users about the ajax request.


## Icons
An icon on a button is provided using _icon_ option. iconPos is used to define the position of the
button which can be “left” or “right”.

```xhtml
<p:commandButton value="With Icon" icon="disk"/>
<p:commandButton icon="disk"/>
```
.disk is a simple css class with a background property;

```css
.disk {
    background-image: url(‘disk.png’) !important;
}
```
You can also use the pre-defined icons from ThemeRoller like _ui-icon-search_.

## Client Side API
Widget: _PrimeFaces.widget.CommandButton_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| disable() | - | void | Disables button
| enable() | - | void | Enables button

## Skinning
CommandButton renders a button tag which _style_ and _styleClass_ applies. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-button | Button element
| .ui-button-text-only | Button element when icon is not used
| .ui-button-text | Label of button

As skinning style classes are global, see the main theming section for more information.
