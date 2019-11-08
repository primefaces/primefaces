# LinkButton

LinkButton a simple link, which is styled as a button and integrated with JSF navigation model.

## Info

| Name | Value |
| --- | --- |
| Tag | linkButton
| Component Class | org.primefaces.component.linkbutton.LinkButton
| Component Type | org.primefaces.component.Link
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.LinkButtonRenderer
| Renderer Class | org.primefaces.component.linkbutton.LinkButtonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
value | null | Object | Value of the component than can be either an EL expression of a literal text.
outcome | null | String | Used to resolve a navigation case.
includeViewParams | false | Boolean | Whether to include page parameters in target URI
fragment | null | String | Identifier of the target page which should be scrolled to.
disabled | false | Boolean | Disables button.
disableClientWindow | false | Boolean | Disable appending the ClientWindow on the rendering of this element.
accesskey | null | String | Access key that when pressed transfers focus to button.
charset | null | String | Character encoding of the resource designated by this hyperlink.
coords | null | String | Position and shape of the hot spot on the screen for client use in image maps.
dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
hreflang | null | String | Language code of the resource designated by the link.
rel | null | String | Relationship from the current document to the anchor specified by the link, values are provided by a space- separated list of link types.
rev | null | String | A reverse link from the anchor specified by this link to the current document, values are provided by a space-separated list of link types.
shape | null | String | Shape of hot spot on the screen, valid values are default, rect, circle and poly.
tabindex | null | String | Position of the element in the tabbing order.
target | null | String | Name of a frame where the resource targeted by this link will be displayed.
title | null | String | Advisory tooltip informaton.
type | null | String | Type of resource referenced by the link.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
onblur | null | String | Client side callback to execute when button loses focus.
onclick | null | String | Client side callback to execute when button is clicked.
ondblclick | null | String | Client side callback to execute when button is double clicked.
onfocus | null | String | Client side callback to execute when button receives focus.
onkeydown | null | String | Client side callback to execute when a key is pressed down over button.
onkeypress | null | String | Client side callback to execute when a key is pressed and released over button.
onkeyup | null | String | Client side callback to execute when a key is released over button.
onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over button.
onmousemove | null | String | Client side callback to execute when a pointer button is moved within button
onmouseout | null | String | Client side callback to execute when a pointer button is moved away from button.
onmouseover | null | String | Client side callback to execute when a pointer button is moved onto button.
onmouseup | null | String | Client side callback to execute when a pointer button is released over button.
href | null | String | Inline style of the button.
escape | true | Boolean | Defines if label of the component is escaped or not.
icon | null | String | Icon of the button.
iconPos | left | String | Position of the button icon.

## Getting Started with LinkButton
Usage is the same as standard h:link, an outcome is necessary to navigate using GET requests.

```xhtml
<p:linkButton outcome="target" value="Navigate"/>
```
To navigate without outcome based approach, use href attribute.

```xhtml
<p:linkButton href="http://www.primefaces.org" value="Navigate"/>
```
