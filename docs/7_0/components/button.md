# Button

Button is an extension to the standard h:button component with skinning capabilities.

## Info

| Name | Value |
| --- | --- |
| Tag | button
| Component Class | org.primefaces.component.button.Button
| Component Type | org.primefaces.component.Button
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ButtonRenderer
| Renderer Class | org.primefaces.component.button.ButtonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| widgetVar | null | String | Name of the client side widget.
| value | null | Object | Value of the component than can be either an EL expression of a literal text.
| outcome | null | String | Used to resolve a navigation case.
| includeViewParams | false | Boolean | Whether to include page parameters in target URI
| fragment | null | String | Identifier of the target page which should be scrolled to.
| disabled | false | Boolean | Disables button.
| accesskey | null | String | Access key that when pressed transfers focus to button.
| alt | null | String | Alternate textual description.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| image | null | String | Style class for the button icon. (deprecated: use icon)
| lang | null | String | Code describing the language used in the generated markup for this component.
| onblur | null | String | Client side callback to execute when button loses focus.
| onchange | null | String | Client side callback to execute when button loses focus and its value has been modified since gaining focus.
| onclick | null | String | Client side callback to execute when button is clicked.
| ondblclick | null | String | Client side callback to execute when button is double clicked.
| onfocus | null | String | Client side callback to execute when button receives focus.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over button.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over button.
| onkeyup | null | String | Client side callback to execute when a key is released over button.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over button.
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within button
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from button.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto button.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over button.
| style | null | String | Inline style of the button.
| styleClass | null | String | Style class of the button.
| tabindex | null | Integer | Position in the tabbing order.
| title | null | String | Advisory tooltip informaton.
| href | null | String | Resource to link directly to implement anchor behavior.
| icon | null | String | Icon of the button.
| iconPos | left | String | Position of the button icon.
| target | _self | String | The window target.
| escape | true | Boolean | Defines whether label would be escaped or not.
| inline | false | Boolean | Displays as inline instead of 100% width, mobile only.
| disableClientWindow | false | Boolean | Disable appending the ClientWindow on the rendering of this element.

## Getting Started with Button
p:button usage is same as standard h:button, an outcome is necessary to navigate using GET
requests. Assume you are at source.xhtml and need to navigate target.xhtml.

```xhtml
<p:button outcome="target" value="Navigate"/>
```
## Parameters
Parameters in URI are defined with nested <f:param /> tags.

```xhtml
<p:button outcome="target" value="Navigate">
    <f:param name="id" value="10" />
</p:button>
```
## Icons
Icons for button are defined via css and _icon_ attribute, if you use title instead of value, only icon
will be displayed and title text will be displayed as tooltip on mouseover. You can also use icons
from PrimeFaces themes such ui-icon-check.

```xhtml
<p:button outcome="target" icon="star" value="With Icon"/>
<p:button outcome="target" icon="star" title="With Icon"/>
```
```css
.star {
    background-image: url("images/star.png");
}
```
## Skinning
Button renders a _button_ tag which _style_ and _styleClass_ applies. As skinning style classes are global,
see the main theming section for more information. Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-button | Button element
| .ui-button-text-only | Button element when icon is not used
| .ui-button-text | Label of button
