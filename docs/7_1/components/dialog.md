# Dialog

Dialog is a panel component that can overlay other elements on page.

## Info

| Name | Value |
| --- | --- |
| Tag | dialog
| Component Class | org.primefaces.component.dialog.Dialog
| Component Type | org.primefaces.component.Dialog
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DialogRenderer
| Renderer Class | org.primefaces.component.dialog.DialogRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget
| header | null | String | Text of the header
| draggable | true | Boolean | Specifies draggability
| resizable | true | Boolean | Specifies resizability
| modal | false | Boolean | Enables modality.
| visible | false | Boolean | When enabled, dialog is visible by default.
| width | auto | Integer | Width of the dialog
| height | auto | Integer | Height of the dialog
| minWidth | 150 | Integer | Minimum width of a resizable dialog.
| minHeight | 0 | Integer | Minimum height of a resizable dialog.
| style | null | String | Inline style of the dialog.
| styleClass | null | String | Style class of the dialog
| showEffect | null | String | Effect to use when showing the dialog
| hideEffect | null | String | Effect to use when hiding the dialog
| position | null | String | Defines where the dialog should be displayed
| closable | true | Boolean | Defines if close icon should be displayed or not
| onShow | null | String | Client side callback to execute when dialog is displayed.
| onHide | null | String | Client side callback to execute when dialog is hidden.
| appendTo | null | String | Appends the dialog to the element defined by the given search expression.
| showHeader | true | Boolean | Defines visibility of the header content.
| footer | null | String | Text of the footer.
| dynamic | false | Boolean | Enables lazy loading of the content with ajax.
| minimizable | false | Boolean | Whether a dialog is minimizable or not.
| maximizable | false | Boolean | Whether a dialog is maximizable or not.
| closeOnEscape | false | Boolean | Defines if dialog should close on escape key.
| dir | ltr | String | Defines text direction, valid values are ltr and rtl.
| focus | null | String | Defines which component to apply focus by search expression.
| fitViewport | false | Boolean | Dialog size might exceeed viewport if content is bigger than viewport in terms of height. fitViewport option automatically adjusts height to fit dialog within the viewport.
| positionType | fixed | String | Defines whether dialog will be kept in viewport on scroll (fixed) or keep its position (absolute).
| responsive | false | Boolean | In responsive mode, dialog adjusts itself based on screen width.
| blockScroll | false | Boolean | Whether to block scrolling of the document when sidebar is active.
| my | center | String | Position of the dialog relative to the target.

## Getting started with the Dialog
Dialog is a panel component containing other components, note that by default dialog is not visible.


```xhtml
<p:dialog>
    <h:outputText value="Resistance to PrimeFaces is Futile!" />
    //Other content
</p:dialog>
```
## Show and Hide
Showing and hiding the dialog is easy using the client side api.

```xhtml
<p:dialog header="Header Text" widgetVar="dlg">//Content</p:dialog>
<p:commandButton value="Show" type="button" onclick="PF('dlg').show()" />
<p:commandButton value="Hide" type="button" onclick="PF('dlg').hide()" />
```
## Effects
There are various effect options to be used when displaying and closing the dialog. Use _showEffect_
and _hideEffect_ options to apply these effects; blind, bounce, clip, drop, explode, fade, fold,
highlight, puff, pulsate, scale, shake, size, slide and transfer.

```xhtml
<p:dialog showEffect="fade" hideEffect="explode" ...>
    //...
</p:dialog>
```
## Position
By default dialog is positioned at center of the viewport and _position_ option is used to change the
location of the dialog. Possible values are;

- Single string value like _‘center’, ‘left’, ‘right’, ‘top’, ‘bottom’_ representing the position within
    viewport.
- Comma separated x and y coordinate values like _200, 500_
- Comma separated position values like _‘top’,‘right’._ (Use single quotes when using a combination)

Some examples are described below;

```xhtml
<p:dialog position="top" ...>
```
```xhtml
<p:dialog position="left,top" ...>
```
```xhtml
<p:dialog position="200,50" ...>
```
## Focus
Dialog applies focus on first visible input on show by default which is useful for user friendliness
however in some cases this is not desirable. Assume the first input is a popup calendar and opening
the dialog shows a popup calendar. To customize default focus behavior, use focus attribute.


## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| open | javax.faces.event.AjaxBehaviorEvent | On open.
| close | org.primefaces.event.CloseEvent | On close.
| minimize | javax.faces.event.AjaxBehaviorEvent | On minimize.
| restoreMinimize | javax.faces.event.AjaxBehaviorEvent | On restore minimize.
| maximize | javax.faces.event.AjaxBehaviorEvent | On maximize.
| restoreMaximize | javax.faces.event.AjaxBehaviorEvent | On restore maximize.
| move | org.primefaces.event.MoveEvent | On move.
| loadContent | javax.faces.event.AjaxBehaviorEvent | On lazy loading the content when dynamic=true
| resizeStart | org.primefaces.event.ResizeEvent | On resize start.
| resizeStop | org.primefaces.event.ResizeEvent | On resize stop.

close** event is one of the ajax behavior events provided by dialog that is fired when the dialog is
hidden. If there is a listener defined it’ll be executed by passing an instance of
_org.primefaces.event.CloseEvent_.

Example below adds a FacesMessage when dialog is closed and updates the messages component to
display the added message.

```xhtml
<p:dialog>
    <p:ajax event="close" listener="#{dialogBean.handleClose}" update="msg" />
    //Content
</p:dialog>
<p:messages id="msg" />
```
```java
public class DialogBean {
    public void handleClose(CloseEvent event) {
        //Add facesmessage
    }
}
```


## Client Side Callbacks
Similar to close listener, onShow and onHide are handy callbacks for client side in case you need to
execute custom javascript.

```xhtml
<p:dialog onShow="alert(‘Visible’)" onHide="alert(‘Hidden’)">
    //Content
</p:dialog>
```
## Client Side API
Widget: _PrimeFaces.widget.Dialog_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show(duration) | duration: (optional) duration of the animation | void | Displays dialog.
| hide(duration) | duration: (optional) duration of the animation | void | Closes dialog.
| isVisible() | - | void | Returns visibility as a boolean.
| resetPosition | - | void | Reset the dialog position based on the configured "position".

## Skinning
Dialog resides in a main container element which _styleClass_ option apply. Following is the list of
structural style classes;



| Class | Applies |
| --- | --- |
| .ui-dialog | Container element of dialog
| .ui-dialog-titlebar | Title bar
| .ui-dialog-title-dialog | Header text
| .ui-dialog-titlebar-close | Close icon
| .ui-dialog-content | Dialog body

As skinning style classes are global, see the main theming section for more information.

**Tips**:

- Use appendTo with care as the page definition and html dom would be different, for example if
    dialog is inside an h:form component and appendTo is enabled, on the browser dialog would be
    outside of form and may cause unexpected results. In this case, nest a form inside a dialog.
- Do not place dialog inside tables, containers likes divs with relative positioning or with non-
    visible overflow defined, in cases like these functionality might be broken. This is not a limitation
    but a result of DOM model. For example dialog inside a layout unit, tabview, accordion are a
    couple of examples. Same applies to confirmDialog as well.
- A facet called “header” is available to provide custom content inside header instead of using
    header attribute.