# CommandLink

CommandLink extends standard JSF commandLink with Ajax capabilities.

## Info

| Name | Value |
| --- | --- |
| Tag | commandLink
| Component Class | org.primefaces.component.commandlink.CommandLink
| Component Type | org.primefaces.component.CommandLink
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CommandLinkRenderer
| Renderer Class | org.primefaces.component. commandlink.CommandLinkRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | String | Href value of the rendered anchor.
| action | null | MethodExpr/String | A method expression or a String outcome that’d be processed when link is clicked.
| actionListener | null | MethodExpr | An actionlistener that’d be processed when link is clicked.
| immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application phase.
| async | false | Boolean | When set to true, ajax requests are not queued.
| process | @all | String | Component(s) to process partially instead of whole view.
| ajax | true | Boolean | Specifies the submit mode, when set to true(default), submit would be made with Ajax.
| update | @none | String | Component(s) to be updated with ajax.
| onstart | null | String | Client side callback to execute before ajax request is begins.
| oncomplete | null | String | Client side callback to execute when ajax request is completed.
| onsuccess | null | String | Client side callback to execute when ajax request succeeds.
| onerror | null | String | Client side callback to execute when ajax request fails.
| global | true | Boolean | Defines whether to trigger ajaxStatus or not.
| delay | null | String | If less than _delay_ milliseconds elapses between calls to _request()_ only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of _delay_ is the literal string 'none' without the quotes, no delay is used.
| partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only.
| partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components.
| resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset.
| ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made.
| timeout | 0 | Integer | Timeout for the ajax request in milliseconds.
| style | null | String | Style to be applied on the anchor element
| styleClass | null | String | StyleClass to be applied on the anchor element
| onblur | null | String | Client side callback to execute when link loses focus.
| onclick | null | String | Client side callback to execute when link is clicked.
| ondblclick | null | String | Client side callback to execute when link is double clicked.
| onfocus | null | String | Client side callback to execute when link receives focus.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over link.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over link.
| onkeyup | null | String | Client side callback to execute when a key is released over link.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over link.
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within link.
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from link.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto link.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over link.
| accesskey | null | String | Access key that when pressed transfers focus to the link.
| charset | null | String | Character encoding of the resource designated by this hyperlink.
| coords | null | String | Position and shape of the hot spot on the screen for client use in image maps.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables the link
| hreflang | null | String | Languae code of the resource designated by the link.
| rel | null | String | Relationship from the current document to the anchor specified by the link, values are provided by a space-separated list of link types.
| rev | null | String | A reverse link from the anchor specified by this link to the current document, values are provided by a space-separated list of link types.
| shape | null | String | Shape of hot spot on the screen, valid values are default, rect, circle and poly.
| tabindex | null | Integer | Position of the button element in the tabbing order.
| target | null | String | Name of a frame where the resource targeted by this link will be displayed.
| title | null | String | Advisory tooltip information.
| type | null | String | Type of resource referenced by the link.
| form | null | String | Form to serialize for an ajax request. Default is the enclosing form.

## Getting Started with CommandLink
CommandLink is used just like the standard h:commandLink, difference is form is submitted with
ajax by default.


```java
public class BookBean {
    public void saveBook() {
    //Save book
    }
}
```
```xhtml
<p:commandLink action="#{bookBean.saveBook}">
    <h:outputText value="Save" />
</p:commandLink>
```
## Skinning
CommandLink renders an html anchor element that _style_ and _styleClass_ attributes apply.
