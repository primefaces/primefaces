# Dialog

Dialog is a panel component that can overlay other elements on page.

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
highlight, puff, pulsate, scale, shake, size and slide.

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

## Common Mistakes

### Using `appendTo`

`appendTo` moves the dialog in the browser DOM to a different parent than where it appears in your
Facelets page. That is often intentional, but it changes how forms and Ajax updates behave.

**When to use `appendTo="@(body)"`**

- **Nested dialogs** — when a dialog opens another dialog that is larger than its parent, the child
  may be clipped or positioned incorrectly. Appending it to `body` lets it render above the parent
  dialog and use the full viewport.
- **Overflow containers** — when the dialog sits inside a parent with `overflow: hidden` or limited
  space (for example `p:tabView` or `p:accordionPanel`), `appendTo="@(body)"` avoids clipping and
  stacking issues.

**Form and Ajax behavior**

If `p:dialog` is declared inside an `h:form` and `appendTo` is set, the dialog is rendered outside
that form in the DOM. Ajax updates and submit actions inside the dialog may then fail or behave
unexpectedly — for example, a `p:commandButton` whose `disabled` state depends on server-side logic
may not update after a `p:ajax` request. See
[issue #8075](https://github.com/primefaces/primefaces/issues/8075).

Place an `h:form` **inside** the dialog so its contents stay in a valid form context after the
dialog is moved:

```xhtml
<h:form>
    <p:commandButton onclick="PF('dialog').show()" value="Open dialog" />
    <p:dialog widgetVar="dialog" appendTo="@(body)">
        <h:form>
            <p:inputText value="#{testBean.text}">
                <p:ajax event="keyup" update="button" delay="1000" />
            </p:inputText>
            <p:commandButton disabled="#{testBean.disabled}" id="button" />
        </h:form>
    </p:dialog>
</h:form>
```

### Restricted parent containers

Do not place `p:dialog` inside table cells or containers with `position: relative` or
`overflow: hidden`. Functionality can break because of how the dialog is positioned in the DOM —
this is not a component limitation. The same guidance applies to `p:confirmDialog`.


## Client Side Callbacks
Similar to close listener, onShow and onHide are handy callbacks for client side in case you need to
execute custom javascript.

```xhtml
<p:dialog onShow="alert(‘Visible’)" onHide="alert(‘Hidden’)">
    //Content
</p:dialog>
```

## Facets

| Name | Description |
| --- | --- |
| header | content which will be rendered in the header; alternative to the `header` attribute |
| footer | content which will be rendered in the footer; alternative to the `footer` attribute |

## Client Side API
Widget: _PrimeFaces.widget.Dialog_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show(duration) | duration: (optional) duration of the animation | void | Displays dialog.
| hide(duration) | duration: (optional) duration of the animation | void | Closes dialog.
| isVisible() | - | void | Returns visibility as a boolean.
| resetPosition | - | void | Reset the dialog position based on the configured "position".

## Custom Action
If you’d like to add custom actions to a dialog titlebar, use actions facet with icon markup;

```xhtml
<p:dialog>
    <f:facet name="actions">
        <h:commandLink styleClass="p-dialog-titlebar-icon">
            <i class="pi pi-question"></i>
        </h:commandLink>
    </f:facet>
    //content
</p:dialog>
```

## Skinning
Dialog resides in a main container element which _style_ and _styleClass_ options apply. This container is the
scrollable layer covering the viewport, the visible dialog is the box inside it which _boxStyle_ and _boxStyleClass_
options apply, and the dialog body is the content which _contentStyle_ and _contentStyleClass_ options apply. So use
_boxStyleClass_ to size the dialog with utility classes, e.g. `boxStyleClass="w-full"`. Following is the list of
structural style classes;

| Class | Applies |
| --- | --- |
| .p-dialog | Container element of dialog, the scrollable layer
| .p-dialog-box | Visible dialog box
| .p-dialog-titlebar | Title bar
| .p-dialog-title | Header text
| .p-dialog-titlebar-close | Close icon
| .p-dialog-close-icon, .p-dialog-minimize-icon, .p-dialog-maximize-icon | Icons of the title bar (tokens `--p-dialog-close-icon`, `--p-dialog-minimize-icon` / `-unminimize-icon`, `--p-dialog-maximize-icon` / `-unmaximize-icon`; the restore icon is shown while the dialog is minimized / maximized)
| .p-dialog-content | Dialog body

As skinning style classes are global, see the main theming section for more information.
    
## Modal
The dialog support for `modal='true'` works very well. However, there is a use case where the background area of the
screen is still accessible by keyboard from the browser address bar. See: https://github.com/primefaces/primefaces/issues/1991

To make a dialog truly modal so the keyboard focus is only ever in your modal dialog you have to re-arrange your page.
You must add a `<main>` element where your page content is and your dialog should live outside that main area.  You can then
use some clever JQuery to hide the area from screen readers and all keyboard and mouse interaction while the modal
dialog is being displayed. See example code below:

**JavaScript:**

```javascript
/**
 * Show the modal dialog while ensuring the <main> is no longer keyboard/mouse accessible 
 * and is marked hidden from screen readers.
 */
function showModal() {
    var main = $('main');
    main.attr('aria-hidden', true).css('pointer-events', 'none');
    main.find(':focusable').each(function(i) {
        var tabindex = String($(this).attr('tabindex') || 0);
        $(this).data('tabindex', tabindex).attr('tabindex', '-1');
    });
}

/**
 * Hide the modal dialog and reinstates the <main> keyboard/mouse and screen reader accessibility.
 */
function hideModal() {
    var main = $('main');
    main.removeAttr('aria-hidden').css('pointer-events', '');
    main.find(':focusable').each(function(i) {
        var tabindex = $(this).data('tabindex') || 0;
        $(this).attr('tabindex', tabindex).removeData('tabindex');
    });
}
``` 

**XHTML:**

```xhtml
<h:body>
    <!-- Main Page content -->
    <main>
        <h:form>
            <p:datePicker />
            <div>
                <p:commandButton value="Open Dialog" type="button" onclick="PF('modalDlg').show()" />
            </div>
        </h:form>
    </main>
    <!-- Dialogs outside of main -->
    <div>
        <h:form>
            <p:dialog widgetVar="modalDlg" modal="true" onShow="showModal();" onHide="hideModal();">
                <p:inputText value="test" />
                <h:outputLabel value="test" />
            </p:dialog>
        </h:form>
    </div>
</h:body>

```


